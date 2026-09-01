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
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.scopedaccess.internal.ScopeResolver
import org.hisp.dhis.android.core.scopedaccess.internal.ScopedAccessGuard
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Read filters cannot bound a write: a create projection or value object carries its own
 * organisation unit, program and data element regardless of the query that produced the repository.
 * This is the check that closes that gap.
 */
@RunWith(JUnit4::class)
class ScopedAccessGuardShould {

    private val resolver: ScopeResolver = mock {
        on { writableOrgUnits() } doReturn setOf("grantedOu")
        on { writableDataElements() } doReturn setOf("grantedDe")
    }

    private val scope = D2DataScope(
        programs = UidScope.of("grantedProgram"),
        dataSets = UidScope.of("grantedDataSet"),
        writable = WritableScope(
            programs = UidScope.of("grantedProgram"),
            dataSets = UidScope.of("grantedDataSet"),
        ),
        trackedEntityTypes = UidScope.of("grantedType"),
        capabilities = setOf(
            D2Capability.WRITE_EVENT,
            D2Capability.WRITE_DATA_VALUE,
            D2Capability.WRITE_TRACKED_ENTITY,
            D2Capability.WRITE_ENROLLMENT,
        ),
    )

    private val guard = ScopedAccessGuard(scope, resolver)

    private fun event(program: String, orgUnit: String): Event = mock {
        on { uid() } doReturn "eventUid"
        on { program() } doReturn program
        on { organisationUnit() } doReturn orgUnit
    }

    private fun dataValue(dataElement: String, orgUnit: String): DataValue = mock {
        on { dataElement() } doReturn dataElement
        on { organisationUnit() } doReturn orgUnit
    }

    private fun denialFor(block: suspend () -> Unit): D2Error = runBlocking {
        try {
            block()
            null
        } catch (error: D2Error) {
            error
        }
    } ?: throw AssertionError("Expected the write to be refused")

    private fun trackedEntity(orgUnit: String, type: String): TrackedEntityInstance = mock {
        on { uid() } doReturn "teiUid"
        on { organisationUnit() } doReturn orgUnit
        on { trackedEntityType() } doReturn type
    }

    private fun enrollment(program: String, orgUnit: String): Enrollment = mock {
        on { uid() } doReturn "enrollmentUid"
        on { program() } doReturn program
        on { organisationUnit() } doReturn orgUnit
    }

    private fun eventDataValue(event: String): TrackedEntityDataValue = mock {
        on { event() } doReturn event
    }

    private fun attributeValue(tei: String): TrackedEntityAttributeValue = mock {
        on { trackedEntityInstance() } doReturn tei
    }

    @Test
    fun allow_a_write_fully_inside_the_grant() = runBlocking {
        guard.checkWrite(event("grantedProgram", "grantedOu"))
        guard.checkWrite(dataValue("grantedDe", "grantedOu"))
    }

    @Test
    fun refuse_an_event_in_a_program_outside_the_grant() {
        val error = denialFor { guard.checkWrite(event("otherProgram", "grantedOu")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_an_event_in_an_organisation_unit_outside_the_grant() {
        val error = denialFor { guard.checkWrite(event("grantedProgram", "otherOu")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_a_data_value_for_a_data_element_outside_the_granted_data_sets() {
        // The bug this replaces: validating the data set argument while writing whichever data
        // element the caller supplied.
        val error = denialFor { guard.checkWrite(dataValue("otherDe", "grantedOu")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_a_write_whose_capability_was_not_granted() {
        val readOnly = ScopedAccessGuard(scope.copy(capabilities = emptySet()), resolver)

        val error = denialFor { readOnly.checkWrite(event("grantedProgram", "grantedOu")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_a_model_type_it_does_not_know() {
        // Deny by default, so a model type added to the SDK later cannot become silently writable.
        val error = denialFor { guard.checkWrite("an unexpected object") }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_a_null_target() {
        val error = denialFor { guard.checkWrite(null) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    // ── Tracked entity instances ─────────────────────────────────────────────

    @Test
    fun allow_a_tracked_entity_inside_the_grant() = runBlocking {
        guard.checkWrite(trackedEntity("grantedOu", "grantedType"))
    }

    @Test
    fun refuse_a_tracked_entity_in_an_organisation_unit_outside_the_grant() {
        val error = denialFor { guard.checkWrite(trackedEntity("otherOu", "grantedType")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_a_tracked_entity_of_a_type_outside_the_grant() {
        // The type dimension is checked on writes as well as reads; a grant of one entity type must
        // not let another be created in an otherwise granted org unit.
        val error = denialFor { guard.checkWrite(trackedEntity("grantedOu", "otherType")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_a_tracked_entity_when_its_capability_was_not_granted() {
        val withoutTeiWrite = ScopedAccessGuard(
            scope.copy(capabilities = setOf(D2Capability.WRITE_EVENT)),
            resolver,
        )

        val error = denialFor { withoutTeiWrite.checkWrite(trackedEntity("grantedOu", "grantedType")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    // ── Enrollments ──────────────────────────────────────────────────────────

    @Test
    fun allow_an_enrollment_inside_the_grant() = runBlocking {
        guard.checkWrite(enrollment("grantedProgram", "grantedOu"))
    }

    @Test
    fun refuse_an_enrollment_in_a_program_outside_the_grant() {
        val error = denialFor { guard.checkWrite(enrollment("otherProgram", "grantedOu")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_an_enrollment_in_an_organisation_unit_outside_the_grant() {
        val error = denialFor { guard.checkWrite(enrollment("grantedProgram", "otherOu")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_an_enrollment_when_its_capability_was_not_granted() {
        val withoutEnrollmentWrite = ScopedAccessGuard(
            scope.copy(capabilities = setOf(D2Capability.WRITE_EVENT)),
            resolver,
        )

        val error = denialFor {
            withoutEnrollmentWrite.checkWrite(enrollment("grantedProgram", "grantedOu"))
        }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    // ── Event data values, checked through their parent event ────────────────

    @Test
    fun allow_an_event_data_value_whose_event_is_inside_the_grant() = runBlocking {
        whenever(resolver.programOfEvent("grantedEvent")) doReturn "grantedProgram"
        whenever(resolver.orgUnitOfEvent("grantedEvent")) doReturn "grantedOu"

        guard.checkWrite(eventDataValue("grantedEvent"))
    }

    @Test
    fun refuse_an_event_data_value_whose_event_is_in_an_ungranted_program() {
        // This row carries no program of its own, so the guard has to go and look the event up.
        whenever(resolver.programOfEvent("foreignEvent")) doReturn "otherProgram"
        whenever(resolver.orgUnitOfEvent("foreignEvent")) doReturn "grantedOu"

        val error = denialFor { guard.checkWrite(eventDataValue("foreignEvent")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_an_event_data_value_for_an_event_it_cannot_find() {
        // An unknown parent means the guard cannot prove the write is in scope, so it must refuse
        // rather than assume.
        whenever(resolver.programOfEvent("ghostEvent")) doReturn null

        val error = denialFor { guard.checkWrite(eventDataValue("ghostEvent")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    // ── Attribute values, checked through the entity's enrollments ───────────

    @Test
    fun allow_an_attribute_value_for_an_entity_enrolled_in_a_writable_program() = runBlocking {
        whenever(resolver.programsOfTrackedEntity("grantedTei")) doReturn setOf("grantedProgram")

        guard.checkWrite(attributeValue("grantedTei"))
    }

    @Test
    fun refuse_an_attribute_value_for_an_entity_in_no_writable_program() {
        whenever(resolver.programsOfTrackedEntity("foreignTei")) doReturn setOf("otherProgram")

        val error = denialFor { guard.checkWrite(attributeValue("foreignTei")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }

    @Test
    fun refuse_an_attribute_value_for_an_entity_with_no_enrollments_at_all() {
        whenever(resolver.programsOfTrackedEntity("orphanTei")) doReturn emptySet()

        val error = denialFor { guard.checkWrite(attributeValue("orphanTei")) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }
}
