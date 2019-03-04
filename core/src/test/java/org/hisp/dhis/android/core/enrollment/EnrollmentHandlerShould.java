/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.enrollment;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.Transformer;
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteDHISVersionManager;
import org.hisp.dhis.android.core.enrollment.note.NoteUniquenessManager;
import org.hisp.dhis.android.core.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EnrollmentHandlerShould {
    @Mock
    private EnrollmentStore enrollmentStore;

    @Mock
    private SyncHandlerWithTransformer<Event> eventHandler;

    @Mock
    private SyncHandler<Note> noteHandler;

    @Mock
    private NoteUniquenessManager noteUniquenessManager;

    @Mock
    private Enrollment enrollment;

    @Mock
    private Event event;

    @Mock
    private Note note;

    @Mock
    private NoteDHISVersionManager noteVersionManager;

    @Mock
    private OrphanCleaner<Enrollment, Event> eventCleaner;

    // object to test
    private EnrollmentHandler enrollmentHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(enrollment.uid()).thenReturn("test_enrollment_uid");
        when(enrollment.events()).thenReturn(Collections.singletonList(event));
        when(enrollment.notes()).thenReturn(Collections.singletonList(note));
        when(note.storedDate()).thenReturn(FillPropertiesTestUtils.LAST_UPDATED_STR);

        enrollmentHandler = new EnrollmentHandler(noteVersionManager, enrollmentStore, eventHandler,
                eventCleaner, noteHandler, noteUniquenessManager);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        enrollmentHandler.handle(null);

        // verify that store or event handler is never called
        verify(enrollmentStore, never()).deleteIfExists(anyString());
        verify(enrollmentStore, never()).updateOrInsert(any(Enrollment.class));

        verify(eventHandler, never()).handle(any(Event.class));
        verify(eventCleaner, never()).deleteOrphan(any(Enrollment.class), any(ArrayList.class));
        verify(noteHandler, never()).handleMany(anyCollectionOf(Note.class));
    }

    @Test
    public void invoke_only_delete_when_a_enrollment_is_set_as_deleted() throws Exception {
        when(enrollment.deleted()).thenReturn(Boolean.TRUE);

        enrollmentHandler.handleMany(Collections.singletonList(enrollment));

        // verify that enrollment store is only invoked with delete
        verify(enrollmentStore, times(1)).deleteIfExists(anyString());


        verify(enrollmentStore, never()).updateOrInsert(any(Enrollment.class));

        // event handler should not be invoked
        verify(eventHandler, never()).handle(any(Event.class));
        verify(eventCleaner, times(1)).deleteOrphan(any(Enrollment.class), any(ArrayList.class));
        verify(noteHandler, never()).handleMany(anyCollectionOf(Note.class));
    }

    @Test
    public void invoke_only_update_or_insert_when_handle_enrollment_is_valid() throws Exception {
        when(enrollment.deleted()).thenReturn(Boolean.FALSE);
        when(enrollmentStore.updateOrInsert(any(Enrollment.class))).thenReturn(HandleAction.Update);

        enrollmentHandler.handleMany(Collections.singletonList(enrollment));

        // verify that enrollment store is only invoked with update
        verify(enrollmentStore, times(1)).updateOrInsert(any(Enrollment.class));

        verify(enrollmentStore, never()).deleteIfExists(anyString());

        // event handler should be invoked once
        verify(eventHandler, times(1)).handleMany(any(ArrayList.class), any(Transformer.class));
        verify(eventCleaner, times(1)).deleteOrphan(any(Enrollment.class), any(ArrayList.class));
        verify(noteHandler, times(1)).handleMany(anyCollectionOf(Note.class));
    }
}
