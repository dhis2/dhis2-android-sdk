/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.trackedentity.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class TrackerPostStateManagerShould {

    private val trackedEntityInstanceStore: TrackedEntityInstanceStore = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val eventStore: EventStore = mock()
    private val relationshipStore: RelationshipStore = mock()
    private val fileResourceStore: FileResourceStore = mock()
    private val noteStore: NoteStore = mock()

    private val enrollmentNote = note("enrollment_note_uid")
    private val enrollmentEventNote = note("enrollment_event_note_uid")
    private val singleEventNote = note("single_event_note_uid")
    private val noteUids = listOf(enrollmentNote.uid(), enrollmentEventNote.uid(), singleEventNote.uid())

    private lateinit var trackedEntityInstance: TrackedEntityInstance
    private lateinit var singleEvent: Event

    private lateinit var stateManager: TrackerPostStateManager

    @Before
    fun setUp() {
        stateManager = TrackerPostStateManager(
            trackedEntityInstanceStore,
            enrollmentStore,
            eventStore,
            relationshipStore,
            fileResourceStore,
            noteStore,
            StatePersistorHelper(),
        )

        val enrollmentEvent = Event.builder()
            .uid("enrollment_event_uid")
            .syncState(State.TO_POST)
            .notes(listOf(enrollmentEventNote))
            .build()
        val enrollment = Enrollment.builder()
            .uid("enrollment_uid")
            .attributeOptionCombo("attribute_option_combo_uid")
            .syncState(State.TO_POST)
            .events(listOf(enrollmentEvent))
            .notes(listOf(enrollmentNote))
            .build()
        trackedEntityInstance = TrackedEntityInstance.builder()
            .uid("tei_uid")
            .syncState(State.TO_POST)
            .enrollments(listOf(enrollment))
            .build()
        singleEvent = Event.builder()
            .uid("single_event_uid")
            .syncState(State.TO_POST)
            .notes(listOf(singleEventNote))
            .build()
    }

    @Test
    fun set_uploading_state_to_enrollment_and_event_notes() = runTest {
        stateManager.setPayloadStates(
            trackedEntityInstances = listOf(trackedEntityInstance),
            events = listOf(singleEvent),
            forcedState = State.UPLOADING,
        )

        verify(noteStore).setSyncState(noteUids, State.UPLOADING)
    }

    @Test
    fun restore_notes_to_their_original_state() = runTest {
        stateManager.restorePayloadStates(
            trackedEntityInstances = listOf(trackedEntityInstance),
            events = listOf(singleEvent),
        )

        verify(noteStore).setSyncState(noteUids, State.TO_POST)
    }

    @Test
    fun restore_uploading_notes_to_to_post() = runTest {
        stateManager.restoreUploadingNotes(
            trackedEntityInstances = listOf(trackedEntityInstance),
            events = listOf(singleEvent),
        )

        noteUids.forEach { verify(noteStore).setSyncStateIfUploading(it, State.TO_POST) }
    }

    private fun note(uid: String): Note {
        return Note.builder()
            .uid(uid)
            .noteType(Note.NoteType.ENROLLMENT_NOTE)
            .syncState(State.TO_POST)
            .build()
    }
}
