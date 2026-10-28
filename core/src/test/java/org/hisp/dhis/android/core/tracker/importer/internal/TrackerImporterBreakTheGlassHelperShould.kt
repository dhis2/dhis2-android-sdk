/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.tracker.importer.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.TEIWebResponseHandlerSummary
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipManagerImpl
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerStore
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackerImporterBreakTheGlassHelperShould {

    private val conflictStore: TrackerImportConflictStore = mock()
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val programStore: ProgramStore = mock()
    private val programOwnerStore: ProgramOwnerStore = mock()
    private val ownershipManagerImpl: OwnershipManagerImpl = mock()

    private lateinit var helper: TrackerImporterBreakTheGlassHelper

    private val programUid = "program_uid"
    private val orgUnitUid = "orgunit_uid"
    private val teiUid = "tei_uid"

    @Before
    fun setUp() {
        helper = TrackerImporterBreakTheGlassHelper(
            conflictStore,
            userOrganisationUnitLinkStore,
            enrollmentStore,
            programStore,
            programOwnerStore,
            ownershipManagerImpl,
        )
    }

    @Test
    fun return_true_when_program_is_protected_and_orgunit_is_not_capture_scope() = runTest {
        mockProtectedProgram()
        mockSearchScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isTrue()
    }

    @Test
    fun return_false_when_program_is_protected_but_orgunit_is_in_capture_scope() = runTest {
        mockProtectedProgram()
        mockCaptureScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_is_open_and_orgunit_is_not_capture_scope() = runTest {
        mockOpenProgram()
        mockSearchScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_is_open_and_orgunit_is_capture_scope() = runTest {
        mockOpenProgram()
        mockCaptureScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_is_null() = runTest {
        val result = helper.isProtectedInSearchScope(null, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_orgunit_is_null() = runTest {
        mockProtectedProgram()

        val result = helper.isProtectedInSearchScope(programUid, null)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_both_are_null() = runTest {
        val result = helper.isProtectedInSearchScope(null, null)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_not_found() = runTest {
        whenever(programStore.selectByUid(programUid)).doReturn(null)
        mockSearchScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_tei_with_only_the_ignored_enrollment_protected_in_search_scope() = runTest {
        givenProtectedOwnership()
        val protectedEnrollment = enrollment("enrollment1")
        val otherEnrollment = enrollment("enrollment2")
        val instance = tei(teiUid, listOf(protectedEnrollment, otherEnrollment))
        val summary = TEIWebResponseHandlerSummary().apply { enrollments.ignored.add(protectedEnrollment) }

        val result = helper.getGlassErrors(summary, listOf(tei("other_tei", emptyList()), instance))

        assertThat(result).containsExactly(instance.toBuilder().enrollments(listOf(protectedEnrollment)).build())
    }

    @Test
    fun not_return_ignored_enrollments_in_capture_scope() = runTest {
        givenProgramOwner()
        mockProtectedProgram()
        mockCaptureScope()
        val enrollment = enrollment("enrollment1")
        val summary = TEIWebResponseHandlerSummary().apply { enrollments.ignored.add(enrollment) }

        val result = helper.getGlassErrors(summary, listOf(tei(teiUid, listOf(enrollment))))

        assertThat(result).isEmpty()
    }

    @Test
    fun not_return_ignored_enrollments_without_program_owner() = runTest {
        mockProtectedProgram()
        mockSearchScope()
        val enrollment = enrollment("enrollment1")
        val summary = TEIWebResponseHandlerSummary().apply { enrollments.ignored.add(enrollment) }

        val result = helper.getGlassErrors(summary, listOf(tei(teiUid, listOf(enrollment))))

        assertThat(result).isEmpty()
        verify(programOwnerStore).selectForTeiProgram(teiUid, programUid)
    }

    @Test
    fun not_return_ignored_enrollments_without_tei_or_program() = runTest {
        val withoutTei = enrollment("enrollment1").toBuilder().trackedEntityInstance(null).build()
        val withoutProgram = enrollment("enrollment2").toBuilder().program(null).build()
        val summary = TEIWebResponseHandlerSummary().apply {
            enrollments.ignored.addAll(listOf(withoutTei, withoutProgram))
        }

        val result = helper.getGlassErrors(summary, listOf(tei(teiUid, listOf(withoutTei, withoutProgram))))

        assertThat(result).isEmpty()
        verifyNoInteractions(programOwnerStore)
    }

    @Test
    fun not_return_protected_enrollments_not_present_in_the_instances() = runTest {
        givenProtectedOwnership()
        val summary = TEIWebResponseHandlerSummary().apply { enrollments.ignored.add(enrollment("enrollment1")) }

        val result = helper.getGlassErrors(summary, listOf(tei(teiUid, listOf(enrollment("enrollment2")))))

        assertThat(result).isEmpty()
    }

    @Test
    fun only_consider_ignored_enrollments() = runTest {
        givenProtectedOwnership()
        val enrollment = enrollment("enrollment1")
        val summary = TEIWebResponseHandlerSummary().apply {
            enrollments.error.add(enrollment)
            enrollments.success.add(enrollment)
        }

        val result = helper.getGlassErrors(summary, listOf(tei(teiUid, listOf(enrollment))))

        assertThat(result).isEmpty()
        verifyNoInteractions(programOwnerStore)
    }

    @Test
    fun return_protected_enrollment_conflicts_with_their_tei_and_events() = runTest {
        givenProtectedOwnership()
        givenConflicts(enrollmentConflicts = listOf(enrollmentConflict("enrollment1")))
        val trackedEntity = newTrackedEntity(teiUid)
        val enrollment = newEnrollment("enrollment1")
        val event = newEvent("event1", "enrollment1")
        val payload = NewTrackerImporterPayload(
            trackedEntities = mutableListOf(trackedEntity, newTrackedEntity("other_tei")),
            enrollments = mutableListOf(enrollment, newEnrollment("enrollment2")),
            events = mutableListOf(event, newEvent("event2", "enrollment2")),
        )

        val result = helper.getGlassErrors(payload)

        assertThat(result.trackedEntities).containsExactly(trackedEntity)
        assertThat(result.enrollments).containsExactly(enrollment)
        assertThat(result.events).containsExactly(event)
        assertThat(result.relationships).isEmpty()
    }

    @Test
    fun query_conflicts_by_error_code_and_payload_uids() = runTest {
        givenConflicts()
        val payload = NewTrackerImporterPayload(
            enrollments = mutableListOf(newEnrollment("enrollment1")),
            events = mutableListOf(newEvent("event1", "enrollment1")),
        )

        helper.getGlassErrors(payload)

        verify(conflictStore).selectWhere(
            argThat<String> { contains("'${ImporterError.E1102.name}'") && contains("'enrollment1'") },
        )
        verify(conflictStore).selectWhere(
            argThat<String> { contains("'${ImporterError.E1000.name}'") && contains("'event1'") },
        )
    }

    @Test
    fun not_return_enrollment_conflicts_that_are_not_protected() = runTest {
        givenProgramOwner()
        mockOpenProgram()
        mockSearchScope()
        givenConflicts(enrollmentConflicts = listOf(enrollmentConflict("enrollment1")))
        val payload = NewTrackerImporterPayload(
            trackedEntities = mutableListOf(newTrackedEntity(teiUid)),
            enrollments = mutableListOf(newEnrollment("enrollment1")),
            events = mutableListOf(newEvent("event1", "enrollment1")),
        )

        val result = helper.getGlassErrors(payload)

        assertThat(result.isEmpty()).isTrue()
    }

    @Test
    fun ignore_conflicts_for_objects_not_present_in_the_payload() = runTest {
        givenProtectedOwnership()
        givenConflicts(
            enrollmentConflicts = listOf(enrollmentConflict("missing_enrollment"), enrollmentConflict(null)),
            eventConflicts = listOf(eventConflict("missing_event"), eventConflict(null)),
        )
        val payload = NewTrackerImporterPayload(
            enrollments = mutableListOf(newEnrollment("enrollment1")),
            events = mutableListOf(newEvent("event1", "enrollment1")),
        )

        val result = helper.getGlassErrors(payload)

        assertThat(result.isEmpty()).isTrue()
    }

    @Test
    fun return_protected_event_conflicts_using_the_stored_enrollment() = runTest {
        givenProtectedOwnership()
        givenConflicts(eventConflicts = listOf(eventConflict("event1")))
        whenever(enrollmentStore.selectByUid("enrollment1")).doReturn(enrollment("enrollment1"))
        val event = newEvent("event1", "enrollment1")
        val payload = NewTrackerImporterPayload(events = mutableListOf(event))

        val result = helper.getGlassErrors(payload)

        assertThat(result.events).containsExactly(event)
        assertThat(result.enrollments).isEmpty()
        assertThat(result.trackedEntities).isEmpty()
    }

    @Test
    fun not_return_event_conflicts_whose_enrollment_is_missing_or_not_protected() = runTest {
        givenProgramOwner()
        mockProtectedProgram()
        mockCaptureScope()
        givenConflicts(
            eventConflicts = listOf(eventConflict("event1"), eventConflict("event2"), eventConflict("event3")),
        )
        whenever(enrollmentStore.selectByUid("enrollment1")).doReturn(enrollment("enrollment1"))
        val payload = NewTrackerImporterPayload(
            events = mutableListOf(
                newEvent("event1", "enrollment1"),
                newEvent("event2", "missing_enrollment"),
                newEvent("event3", null),
            ),
        )

        val result = helper.getGlassErrors(payload)

        assertThat(result.events).isEmpty()
    }

    @Test
    fun not_duplicate_event_conflicts_already_included_by_their_enrollment() = runTest {
        givenProtectedOwnership()
        givenConflicts(
            enrollmentConflicts = listOf(enrollmentConflict("enrollment1")),
            eventConflicts = listOf(eventConflict("event1")),
        )
        val event = newEvent("event1", "enrollment1")
        val payload = NewTrackerImporterPayload(
            enrollments = mutableListOf(newEnrollment("enrollment1")),
            events = mutableListOf(event),
        )

        val result = helper.getGlassErrors(payload)

        assertThat(result.events).containsExactly(event)
        verify(enrollmentStore, never()).selectByUid(any())
    }

    @Test
    fun fake_break_glass_for_every_enrollment_with_program_in_v1_instances() = runTest {
        val instances = listOf(
            tei(teiUid, listOf(enrollment("enrollment1"), enrollment("enrollment2").toBuilder().program(null).build())),
            tei("tei2", listOf(enrollment("enrollment3").toBuilder().program("program2").build())),
            tei("tei3", null),
        )

        helper.fakeBreakGlass(instances)

        verify(ownershipManagerImpl).fakeBreakGlass(teiUid, programUid)
        verify(ownershipManagerImpl).fakeBreakGlass("tei2", "program2")
        verify(ownershipManagerImpl, times(2)).fakeBreakGlass(any(), any())
    }

    @Test
    fun fake_break_glass_once_per_tei_and_program_in_v2_payload() = runTest {
        whenever(enrollmentStore.selectByUid("stored_enrollment1")).doReturn(
            enrollment("stored_enrollment1").toBuilder().trackedEntityInstance("tei2").program("program2").build(),
        )
        whenever(enrollmentStore.selectByUid("stored_enrollment2")).doReturn(enrollment("stored_enrollment2"))
        whenever(enrollmentStore.selectByUid("stored_enrollment3")).doReturn(
            enrollment("stored_enrollment3").toBuilder().program(null).build(),
        )
        val payload = NewTrackerImporterPayload(
            enrollments = mutableListOf(
                newEnrollment("enrollment1"),
                newEnrollment("enrollment2").copy(trackedEntity = null),
                newEnrollment("enrollment3").copy(program = null),
            ),
            events = mutableListOf(
                newEvent("event1", "enrollment1"),
                newEvent("event2", "stored_enrollment1"),
                newEvent("event3", "stored_enrollment2"),
                newEvent("event4", "stored_enrollment3"),
                newEvent("event5", "missing_enrollment"),
                newEvent("event6", null),
            ),
        )

        helper.fakeBreakGlass(payload)

        verify(ownershipManagerImpl).fakeBreakGlass(teiUid, programUid)
        verify(ownershipManagerImpl).fakeBreakGlass("tei2", "program2")
        verify(ownershipManagerImpl, times(2)).fakeBreakGlass(any(), any())
        verify(enrollmentStore, never()).selectByUid("enrollment1")
    }

    @Test
    fun not_fake_break_glass_for_empty_v2_payload() = runTest {
        helper.fakeBreakGlass(NewTrackerImporterPayload())

        verifyNoInteractions(ownershipManagerImpl, enrollmentStore)
    }

    private suspend fun mockProtectedProgram() {
        val program = Program.builder()
            .uid(programUid)
            .accessLevel(AccessLevel.PROTECTED)
            .categoryCombo(mock())
            .enrollmentCategoryCombo(mock())
            .build()
        whenever(programStore.selectByUid(programUid)).doReturn(program)
    }

    private suspend fun mockOpenProgram() {
        val program = Program.builder()
            .uid(programUid)
            .accessLevel(AccessLevel.OPEN)
            .categoryCombo(mock())
            .enrollmentCategoryCombo(mock())
            .build()
        whenever(programStore.selectByUid(programUid)).doReturn(program)
    }

    private suspend fun mockCaptureScope() {
        whenever(userOrganisationUnitLinkStore.isCaptureScope(orgUnitUid)).doReturn(true)
        whenever(userOrganisationUnitLinkStore.isSearchScope(orgUnitUid)).doReturn(true)
    }

    private suspend fun mockSearchScope() {
        whenever(userOrganisationUnitLinkStore.isCaptureScope(orgUnitUid)).doReturn(false)
        whenever(userOrganisationUnitLinkStore.isSearchScope(orgUnitUid)).doReturn(true)
    }

    private suspend fun givenProgramOwner() {
        whenever(programOwnerStore.selectForTeiProgram(teiUid, programUid)).doReturn(
            ProgramOwner.builder()
                .trackedEntityInstance(teiUid)
                .program(programUid)
                .ownerOrgUnit(orgUnitUid)
                .build(),
        )
    }

    private suspend fun givenProtectedOwnership() {
        givenProgramOwner()
        mockProtectedProgram()
        mockSearchScope()
    }

    private suspend fun givenConflicts(
        enrollmentConflicts: List<TrackerImportConflict> = emptyList(),
        eventConflicts: List<TrackerImportConflict> = emptyList(),
    ) {
        whenever(conflictStore.selectWhere(argThat<String> { contains(ImporterError.E1102.name) }))
            .doReturn(enrollmentConflicts)
        whenever(conflictStore.selectWhere(argThat<String> { contains(ImporterError.E1000.name) }))
            .doReturn(eventConflicts)
    }

    private fun enrollmentConflict(enrollment: String?) = TrackerImportConflict.builder()
        .errorCode(ImporterError.E1102.name)
        .enrollment(enrollment)
        .build()

    private fun eventConflict(event: String?) = TrackerImportConflict.builder()
        .errorCode(ImporterError.E1000.name)
        .event(event)
        .build()

    private fun tei(uid: String, enrollments: List<Enrollment>?) = TrackedEntityInstance.builder()
        .uid(uid)
        .enrollments(enrollments)
        .build()

    private fun enrollment(uid: String) = Enrollment.builder()
        .uid(uid)
        .trackedEntityInstance(teiUid)
        .program(programUid)
        .attributeOptionCombo("aoc")
        .build()

    private fun newTrackedEntity(uid: String) = NewTrackerImporterTrackedEntity(
        uid = uid,
        deleted = false,
        syncState = State.TO_UPDATE,
        createdAt = null,
        updatedAt = null,
        createdAtClient = null,
        updatedAtClient = null,
        organisationUnit = null,
        trackedEntityType = null,
        geometry = null,
        aggregatedSyncState = State.TO_UPDATE,
    )

    private fun newEnrollment(uid: String) = NewTrackerImporterEnrollment(
        uid = uid,
        deleted = false,
        syncState = State.TO_UPDATE,
        createdAt = null,
        updatedAt = null,
        createdAtClient = null,
        updatedAtClient = null,
        organisationUnit = null,
        program = programUid,
        enrolledAt = null,
        occurredAt = null,
        completedAt = null,
        followUp = null,
        status = null,
        trackedEntity = teiUid,
        geometry = null,
        attributeOptionCombo = null,
        aggregatedSyncState = State.TO_UPDATE,
    )

    private fun newEvent(uid: String, enrollment: String?) = NewTrackerImporterEvent(
        uid = uid,
        deleted = false,
        syncState = State.TO_UPDATE,
        enrollment = enrollment,
        createdAt = null,
        updatedAt = null,
        createdAtClient = null,
        updatedAtClient = null,
        program = programUid,
        programStage = null,
        organisationUnit = null,
        occurredAt = null,
        status = null,
        geometry = null,
        completedAt = null,
        completedBy = null,
        scheduledAt = null,
        attributeOptionCombo = null,
        assignedUser = null,
        aggregatedSyncState = State.TO_UPDATE,
    )
}
