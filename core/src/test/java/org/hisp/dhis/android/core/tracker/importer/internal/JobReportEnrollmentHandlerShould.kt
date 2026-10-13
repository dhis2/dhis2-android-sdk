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
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteStore
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.internal.RelationshipSamples
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.program.ProgramTrackedEntityAttributeTableInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.util.Date

@RunWith(JUnit4::class)
class JobReportEnrollmentHandlerShould {

    private val noteStore: NoteStore = mock {
        onBlocking { selectWhere(any<String>()) } doReturn emptyList()
    }
    private val enrollmentStore: EnrollmentStore = mock()
    private val conflictStore: TrackerImportConflictStore = mock()
    private val conflictHelper: TrackerConflictHelper = mock()
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore = mock()
    private val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore = mock()
    private val relationshipStore: RelationshipStore = mock()

    private val enrollmentUid = "enrollment_uid"
    private val teiUid = "tei_uid"
    private val programUid = "program_uid"
    private val attributeUids = listOf("attribute1", "attribute2")

    private val enrollment = Enrollment.builder()
        .uid(enrollmentUid)
        .attributeOptionCombo("aoc")
        .trackedEntityInstance(teiUid)
        .program(programUid)
        .build()

    private val jobObject = TrackerJobObject(
        trackerType = TrackerImporterObjectType.ENROLLMENT,
        objectUid = enrollmentUid,
        jobUid = "job_uid",
        lastUpdated = Date(),
        fileResources = emptyList(),
    )

    private lateinit var handler: JobReportEnrollmentHandler

    @Before
    fun setUp() {
        handler = JobReportEnrollmentHandler(
            noteStore,
            enrollmentStore,
            conflictStore,
            conflictHelper,
            trackedEntityAttributeValueStore,
            programTrackedEntityAttributeStore,
            relationshipStore,
        )
    }

    @Test
    fun delete_conflicts_and_set_state_on_success() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Update)

        handler.handleSuccess(jobObject)

        verify(conflictStore).deleteEnrollmentConflicts(enrollmentUid)
        verify(enrollmentStore).setSyncStateOrDelete(enrollmentUid, State.SYNCED)
    }

    @Test
    fun mark_pending_notes_as_synced_when_enrollment_is_updated() = runTest {
        val note = Note.builder().uid("note_uid").enrollment(enrollmentUid).syncState(State.TO_POST).build()
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Update)
        whenever(noteStore.selectWhere(any<String>())).doReturn(listOf(note))

        handler.handleSuccess(jobObject)

        verify(noteStore).selectWhere(argThat<String> { contains("'$enrollmentUid'") })
        verify(noteStore).update(note.toBuilder().syncState(State.SYNCED).build())
    }

    @Test
    fun mark_program_attributes_as_synced_when_enrollment_is_inserted() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Insert)
        givenStoredEnrollment(enrollment)
        givenProgramAttributes(attributeUids)

        handler.handleSuccess(jobObject)

        verify(programTrackedEntityAttributeStore).selectStringColumnsWhereClause(
            eq(ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE),
            argThat { contains("'$programUid'") },
        )
        verify(trackedEntityAttributeValueStore).setSyncStateByAttributes(teiUid, attributeUids, State.SYNCED)
        verify(trackedEntityAttributeValueStore)
            .removeDeletedAttributeValuesByInstanceAndAttributes(teiUid, attributeUids)
    }

    @Test
    fun not_touch_attribute_values_when_program_has_no_attributes() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Update)
        givenStoredEnrollment(enrollment)
        givenProgramAttributes(emptyList())

        handler.handleSuccess(jobObject)

        verifyNoInteractions(trackedEntityAttributeValueStore)
    }

    @Test
    fun not_touch_attribute_values_when_enrollment_has_no_program() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Update)
        givenStoredEnrollment(enrollment.toBuilder().program(null).build())

        handler.handleSuccess(jobObject)

        verifyNoInteractions(programTrackedEntityAttributeStore, trackedEntityAttributeValueStore)
    }

    @Test
    fun not_touch_attribute_values_when_enrollment_has_no_tei() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Update)
        givenStoredEnrollment(enrollment.toBuilder().trackedEntityInstance(null).build())

        handler.handleSuccess(jobObject)

        verifyNoInteractions(programTrackedEntityAttributeStore, trackedEntityAttributeValueStore)
    }

    @Test
    fun not_touch_attribute_values_when_enrollment_is_not_found() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Update)

        handler.handleSuccess(jobObject)

        verify(enrollmentStore).selectByUid(enrollmentUid)
        verifyNoInteractions(programTrackedEntityAttributeStore, trackedEntityAttributeValueStore)
    }

    @Test
    fun delete_related_relationships_when_enrollment_is_deleted() = runTest {
        val r1 = RelationshipSamples.get230("relationship1", "from1", "to1")
        val r2 = RelationshipSamples.get230("relationship2", "from2", "to2")
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Delete)
        whenever(relationshipStore.getRelationshipsByItem(RelationshipHelper.enrollmentItem(enrollmentUid)))
            .doReturn(listOf(r1, r2))

        handler.handleSuccess(jobObject)

        verify(relationshipStore).deleteByEntity(r1)
        verify(relationshipStore).deleteByEntity(r2)
        verifyNoInteractions(noteStore, programTrackedEntityAttributeStore, trackedEntityAttributeValueStore)
    }

    @Test
    fun not_delete_relationships_when_enrollment_is_updated() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.SYNCED)).doReturn(HandleAction.Update)

        handler.handleSuccess(jobObject)

        verify(relationshipStore, never()).getRelationshipsByItem(any<RelationshipItem>())
        verify(relationshipStore, never()).deleteByEntity(any())
    }

    @Test
    fun set_to_update_state_when_enrollment_is_not_present_in_report() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.TO_UPDATE)).doReturn(HandleAction.Update)

        handler.handleNotPresent(jobObject)

        verify(conflictStore).deleteEnrollmentConflicts(enrollmentUid)
        verify(enrollmentStore).setSyncStateOrDelete(enrollmentUid, State.TO_UPDATE)
        verifyNoInteractions(noteStore, programTrackedEntityAttributeStore, trackedEntityAttributeValueStore)
    }

    @Test
    fun set_error_state_and_store_conflict_on_error() = runTest {
        val error = validationError(ImporterError.E1000.name)
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.ERROR)).doReturn(HandleAction.Update)
        givenStoredEnrollment(enrollment)
        whenever(conflictHelper.getConflictBuilder(error)).doReturn(
            TrackerImportConflict.builder().errorCode(error.errorCode).conflict(error.message),
        )

        handler.handleError(jobObject, error)

        verify(enrollmentStore).setSyncStateOrDelete(enrollmentUid, State.ERROR)
        verifyNoInteractions(noteStore, programTrackedEntityAttributeStore, trackedEntityAttributeValueStore)

        val captor = argumentCaptor<TrackerImportConflict>()
        verify(conflictStore).updateOrInsertWhere(captor.capture())
        val conflict = captor.firstValue
        assertThat(conflict.tableReference()).isEqualTo(EnrollmentTableInfo.TABLE_INFO.name())
        assertThat(conflict.enrollment()).isEqualTo(enrollmentUid)
        assertThat(conflict.trackedEntityInstance()).isEqualTo(teiUid)
        assertThat(conflict.errorCode()).isEqualTo(ImporterError.E1000.name)
    }

    @Test
    fun delete_enrollment_instead_of_storing_conflict_when_deleted_enrollment_does_not_exist_in_server() = runTest {
        val deletedEnrollment = enrollment.toBuilder().deleted(true).build()
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.ERROR)).doReturn(HandleAction.Update)
        givenStoredEnrollment(deletedEnrollment)

        handler.handleError(jobObject, validationError(ImporterError.E1081.name))

        verify(enrollmentStore).deleteByEntity(deletedEnrollment)
        verify(conflictStore, never()).updateOrInsertWhere(any())
    }

    @Test
    fun store_conflict_when_not_deleted_enrollment_does_not_exist_in_server() = runTest {
        val error = validationError(ImporterError.E1081.name)
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.ERROR)).doReturn(HandleAction.Update)
        givenStoredEnrollment(enrollment.toBuilder().deleted(false).build())
        whenever(conflictHelper.getConflictBuilder(error)).doReturn(TrackerImportConflict.builder())

        handler.handleError(jobObject, error)

        verify(enrollmentStore, never()).deleteByEntity(any())
        verify(conflictStore).updateOrInsertWhere(any())
    }

    @Test
    fun not_store_conflict_when_enrollment_is_not_found() = runTest {
        whenever(enrollmentStore.setSyncStateOrDelete(enrollmentUid, State.ERROR)).doReturn(HandleAction.Update)

        handler.handleError(jobObject, validationError(ImporterError.E1000.name))

        verify(conflictStore, never()).updateOrInsertWhere(any())
        verifyNoInteractions(conflictHelper)
    }

    @Test
    fun set_notes_to_post_when_handling_notes_with_a_not_synced_state() = runTest {
        val note = Note.builder().uid("note_uid").enrollment(enrollmentUid).syncState(State.ERROR).build()
        whenever(noteStore.selectWhere(any<String>())).doReturn(listOf(note))

        handler.handleEnrollmentNotes(enrollmentUid, State.ERROR)

        verify(noteStore).update(note.toBuilder().syncState(State.TO_POST).build())
    }

    @Test
    fun only_select_uploadable_notes() = runTest {
        handler.handleEnrollmentNotes(enrollmentUid, State.SYNCED)

        verify(noteStore).selectWhere(
            argThat<String> {
                State.uploadableStatesIncludingError().all { contains("'${it.name}'") } &&
                    !contains("'${State.SYNCED.name}'")
            },
        )
        verify(noteStore, never()).update(any<Note>())
    }

    private suspend fun givenStoredEnrollment(e: Enrollment) {
        whenever(enrollmentStore.selectByUid(enrollmentUid)).doReturn(e)
    }

    private suspend fun givenProgramAttributes(uids: List<String>) {
        whenever(programTrackedEntityAttributeStore.selectStringColumnsWhereClause(any(), any())).doReturn(uids)
    }

    private fun validationError(errorCode: String) = JobValidationError(
        uid = enrollmentUid,
        trackerType = TrackerImporterObjectType.ENROLLMENT,
        errorCode = errorCode,
        message = "message",
    )
}
