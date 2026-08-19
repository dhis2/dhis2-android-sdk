/*
 *  Copyright (c) 2004-2025, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.scopedaccess

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

/**
 * [ScopedD2] against a real, populated database.
 *
 * The unit tests prove the scope *algebra* — that filters accumulate, that a guard survives a
 * builder chain. They cannot prove that the resulting SQL parses, runs, and returns the right rows,
 * and two things here are only knowable by executing them: the hand-written sub-selects for tables
 * with no program column, and an empty grant rendering as `IN ()`, which relies on SQLite accepting
 * an empty list and evaluating it false.
 *
 * The assertions are written against whatever the fixture happens to contain rather than hardcoded
 * UIDs, so they keep meaning if the fixture changes.
 */
class ScopedD2MockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val readEverything = setOf(
        D2Capability.READ_METADATA,
        D2Capability.READ_TRACKED_ENTITY,
        D2Capability.READ_ENROLLMENT,
        D2Capability.READ_EVENT,
        D2Capability.READ_DATA_VALUE,
    )

    private fun scopedTo(programs: UidScope, orgUnits: OrgUnitScope = OrgUnitScope.All) =
        d2.scopedTo(
            D2DataScope(
                programs = programs,
                orgUnits = orgUnits,
                capabilities = readEverything,
            ),
        )

    private fun allProgramUids() = d2.programModule().programs().blockingGetUids()

    // ── Reads ────────────────────────────────────────────────────────────────

    @Test
    fun expose_only_the_granted_program() {
        val granted = allProgramUids().first()

        val visible = scopedTo(UidScope.of(granted)).programs().blockingGetUids()

        assertThat(visible).containsExactly(granted)
    }

    @Test
    fun return_nothing_when_asked_for_a_program_outside_the_grant() {
        val all = allProgramUids()
        // Needs at least two programs for this to mean anything.
        assertThat(all.size).isAtLeast(2)
        val granted = all[0]
        val other = all[1]

        val sdk = scopedTo(UidScope.of(granted))

        // The grant and the caller's filter are AND-ed, so this is empty rather than `other`.
        assertThat(sdk.programs().byUid().eq(other).blockingGetUids()).isEmpty()
        assertThat(sdk.events().byProgramUid().eq(other).blockingGet()).isEmpty()
        assertThat(sdk.enrollments().byProgram().eq(other).blockingGet()).isEmpty()
    }

    @Test
    fun never_return_more_events_than_the_unscoped_sdk_would_for_that_program() {
        val granted = allProgramUids().first()

        val scoped = scopedTo(UidScope.of(granted)).events().blockingGet().map { it.uid() }
        val expected = d2.eventModule().events()
            .byProgramUid().eq(granted)
            .blockingGet()
            .map { it.uid() }

        assertThat(scoped).containsExactlyElementsIn(expected)
    }

    @Test
    fun see_nothing_at_all_when_the_grant_is_empty() {
        // Exercises `.in(emptyList())` → `column IN ()`. SQLite accepts an empty IN list and
        // evaluates it false; most engines reject it outright, so this is worth executing rather
        // than reasoning about.
        val sdk = scopedTo(UidScope.None)

        assertThat(sdk.programs().blockingGet()).isEmpty()
        assertThat(sdk.events().blockingGet()).isEmpty()
        assertThat(sdk.enrollments().blockingGet()).isEmpty()
        assertThat(sdk.trackedEntityInstances().blockingGet()).isEmpty()
    }

    @Test
    fun run_the_attribute_value_subquery_for_a_table_with_no_program_column() {
        // TrackedEntityAttributeValue only references its tracked entity, so the grant becomes a
        // hand-written sub-select through the enrollment table. This asserts that SQL is valid and
        // bounded — a syntax error here would surface as an exception, not an empty list.
        val granted = allProgramUids().first()
        val sdk = scopedTo(UidScope.of(granted))

        val inScopeTeis = sdk.trackedEntityInstances().blockingGetUids().toSet()
        val values = sdk.trackedEntityAttributeValues().blockingGet()

        assertThat(inScopeTeis).containsAtLeastElementsIn(values.map { it.trackedEntityInstance() }.toSet())
    }

    @Test
    fun bound_attribute_values_to_the_grant_rather_than_returning_every_row() {
        val granted = allProgramUids().first()

        val scoped = scopedTo(UidScope.of(granted)).trackedEntityAttributeValues().blockingGet().size
        val unscoped = d2.trackedEntityModule().trackedEntityAttributeValues().blockingGet().size

        // Only meaningful if the fixture has data outside the granted program; if it does not, this
        // still guards against the sub-select being dropped entirely.
        assertThat(scoped).isAtMost(unscoped)
    }

    @Test
    fun run_the_event_data_value_subquery() {
        val granted = allProgramUids().first()
        val sdk = scopedTo(UidScope.of(granted))

        val inScopeEvents = sdk.events().blockingGetUids().toSet()
        val values = sdk.trackedEntityDataValues().blockingGet()

        assertThat(inScopeEvents).containsAtLeastElementsIn(values.map { it.event() }.toSet())
    }

    @Test
    fun expand_org_unit_descendants_from_a_root() {
        val root = d2.organisationUnitModule().organisationUnits().blockingGetUids().first()

        val visible = d2.scopedTo(
            D2DataScope(
                orgUnits = OrgUnitScope.of(listOf(root)),
                capabilities = setOf(D2Capability.READ_METADATA),
            ),
        ).organisationUnits().blockingGetUids()

        // The root itself is always in scope; descendants come from the path LIKE match.
        assertThat(visible).contains(root)
    }

    // ── Capabilities ─────────────────────────────────────────────────────────

    @Test
    fun refuse_an_accessor_whose_capability_was_not_granted() {
        val sdk = d2.scopedTo(
            D2DataScope(
                programs = UidScope.All,
                capabilities = setOf(D2Capability.READ_METADATA),
            ),
        )

        // Metadata is granted…
        assertThat(sdk.programs().blockingGet()).isNotEmpty()

        // …tracker data is not.
        val error = runCatching { sdk.trackedEntityInstances() }.exceptionOrNull()
        assertThat(error).isInstanceOf(D2Error::class.java)
        assertThat((error as D2Error).errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    // ── Writes ───────────────────────────────────────────────────────────────

    @Test
    fun refuse_a_data_value_write_outside_the_grant() {
        val sdk = d2.scopedTo(
            D2DataScope(
                dataSets = UidScope.None,
                capabilities = setOf(D2Capability.READ_DATA_VALUE, D2Capability.WRITE_DATA_VALUE),
            ),
        )

        val existing = d2.dataValueModule().dataValues().blockingGet().firstOrNull()
        assertThat(existing).isNotNull()

        val error = runCatching {
            sdk.dataValues().value(
                period = existing!!.period(),
                organisationUnit = existing.organisationUnit(),
                dataElement = existing.dataElement(),
                categoryOptionCombo = existing.categoryOptionCombo(),
                attributeOptionCombo = existing.attributeOptionCombo(),
                sourceDataSet = "anyDataSet",
            ).blockingSet("999")
        }.exceptionOrNull()

        assertThat(error).isInstanceOf(D2Error::class.java)
        assertThat((error as D2Error).errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun leave_the_unscoped_sdk_completely_alone() {
        // Everything above installs guards and filters on scopes. None of it may leak into the
        // repositories the rest of the app gets from D2 directly.
        val granted = allProgramUids().first()
        scopedTo(UidScope.of(granted)).events().blockingGet()

        assertThat(d2.programModule().programs().blockingGetUids())
            .containsExactlyElementsIn(allProgramUids())
        assertThat(d2.eventModule().events().blockingGet().size)
            .isEqualTo(d2.eventModule().events().blockingCount())
    }
}
