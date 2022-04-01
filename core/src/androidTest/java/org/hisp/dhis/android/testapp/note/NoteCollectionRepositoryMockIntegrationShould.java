/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.testapp.note;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class NoteCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<Note> notes = d2.noteModule().notes().blockingGet();
        assertThat(notes.size()).isEqualTo(10);
    }

    @Test
    public void filter_by_uid() {
        List<Note> notes = d2.noteModule().notes().byUid().eq("enrollmentNote1").blockingGet();
        assertThat(notes.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_note_type() {
        List<Note> enrollmentNotes = d2.noteModule().notes()
                .byNoteType().eq(Note.NoteType.ENROLLMENT_NOTE).blockingGet();
        assertThat(enrollmentNotes.size()).isEqualTo(4);

        List<Note> eventNotes = d2.noteModule().notes()
                .byNoteType().eq(Note.NoteType.EVENT_NOTE).blockingGet();
        assertThat(eventNotes.size()).isEqualTo(6);
    }

    @Test
    public void filter_by_event_uid() {
        List<Note> notes = d2.noteModule().notes().byEventUid().eq("event1").blockingGet();
        assertThat(notes.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_enrollment_uid() {
        List<Note> notes = d2.noteModule().notes().byEnrollmentUid().eq("enroll1").blockingGet();
        assertThat(notes.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_value() {
        List<Note> notes = d2.noteModule().notes().byValue().eq("TEI enrollment note 3").blockingGet();
        assertThat(notes.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_stored_by() {
        List<Note> notes = d2.noteModule().notes().byStoredBy().eq("android").blockingGet();
        assertThat(notes.size()).isEqualTo(10);
    }

    @Test
    public void filter_by_state() {
        List<Note> notes = d2.noteModule().notes().bySyncState().eq(State.SYNCED).blockingGet();
        assertThat(notes.size()).isEqualTo(10);
    }

    @Test
    public void filter_by_stored_date() {
        List<Note> notes = d2.noteModule().notes().byStoredDate().eq("2018-03-19T15:20:55.058").blockingGet();
        assertThat(notes.size()).isEqualTo(8);
    }
}