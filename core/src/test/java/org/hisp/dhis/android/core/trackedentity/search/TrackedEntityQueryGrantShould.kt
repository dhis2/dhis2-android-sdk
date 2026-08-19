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
package org.hisp.dhis.android.core.trackedentity.search

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Tracker search does not get the append-only guarantee the rest of the SDK has — its scope fields
 * are replaced by `by*()` rather than accumulated — so the grant has to be re-applied on every
 * repository construction. These tests cover what that re-application must do.
 */
@RunWith(JUnit4::class)
class TrackedEntityQueryGrantShould {

    private val grant = TrackedEntityQueryGrant(
        programs = setOf("granted1", "granted2"),
        orgUnits = setOf("ouA", "ouB"),
        trackedEntityTypes = setOf("teType"),
    )

    private fun granted(
        block: TrackedEntityInstanceQueryRepositoryScope.Builder.() -> Unit = {},
    ): TrackedEntityInstanceQueryRepositoryScope =
        TrackedEntityInstanceQueryRepositoryScope.builder()
            .apply { block() }
            .mandatory(grant)
            .build()
            .applyGrant()

    @Test
    fun leave_an_ungranted_scope_untouched() {
        val scope = TrackedEntityInstanceQueryRepositoryScope.builder()
            .mode(RepositoryMode.ONLINE_ONLY)
            .program("anything")
            .build()

        assertThat(scope.applyGrant()).isSameInstanceAs(scope)
    }

    @Test
    fun force_offline_mode_so_the_server_is_never_asked() {
        // The online modes are answered by the server, where none of these restrictions exist.
        for (mode in listOf(RepositoryMode.ONLINE_ONLY, RepositoryMode.ONLINE_FIRST, RepositoryMode.OFFLINE_FIRST)) {
            assertThat(granted { mode(mode) }.mode()).isEqualTo(RepositoryMode.OFFLINE_ONLY)
        }
    }

    @Test
    fun keep_a_program_that_is_inside_the_grant() {
        assertThat(granted { program("granted1") }.program()).isEqualTo("granted1")
    }

    @Test
    fun reduce_a_program_outside_the_grant_to_no_match() {
        assertThat(granted { program("other") }.program()).isEqualTo(TrackedEntityQueryGrant.NO_MATCH)
    }

    @Test
    fun leave_an_unset_program_for_the_query_layer_to_bound() {
        // A single `program` field cannot express "one of these", so a null program stays null here
        // and the local query helper adds the sub-select instead.
        assertThat(granted().program()).isNull()
    }

    @Test
    fun reduce_a_tracked_entity_type_outside_the_grant_to_no_match() {
        assertThat(granted { trackedEntityType("other") }.trackedEntityType())
            .isEqualTo(TrackedEntityQueryGrant.NO_MATCH)
        assertThat(granted { trackedEntityType("teType") }.trackedEntityType()).isEqualTo("teType")
    }

    @Test
    fun fall_back_to_the_granted_org_units_when_none_were_requested() {
        val scope = granted()

        assertThat(scope.orgUnits()).containsExactly("ouA", "ouB")
        assertThat(scope.orgUnitMode()).isEqualTo(OrganisationUnitMode.SELECTED)
    }

    @Test
    fun intersect_requested_org_units_with_the_grant() {
        val scope = granted { orgUnits(listOf("ouA", "ouOutside")) }

        assertThat(scope.orgUnits()).containsExactly("ouA")
    }

    @Test
    fun reduce_org_units_entirely_outside_the_grant_to_no_match() {
        // An empty list would read as "no filter" downstream, which would leak the whole database.
        assertThat(granted { orgUnits(listOf("ouOutside")) }.orgUnits())
            .containsExactly(TrackedEntityQueryGrant.NO_MATCH)
    }

    @Test
    fun pin_org_unit_mode_so_a_hierarchy_mode_cannot_widen_the_grant() {
        // The granted set is already expanded to leaves, so DESCENDANTS or ACCESSIBLE could only
        // reach further than intended.
        val scope = granted { orgUnitMode(OrganisationUnitMode.ACCESSIBLE) }

        assertThat(scope.orgUnitMode()).isEqualTo(OrganisationUnitMode.SELECTED)
    }

    @Test
    fun survive_a_rebuild_through_to_builder() {
        // Every by*() call rebuilds the scope this way; the grant has to come along.
        val rebuilt = granted().toBuilder().program("other").build()

        assertThat(rebuilt.mandatory).isSameInstanceAs(grant)
        assertThat(rebuilt.applyGrant().program()).isEqualTo(TrackedEntityQueryGrant.NO_MATCH)
    }
}
