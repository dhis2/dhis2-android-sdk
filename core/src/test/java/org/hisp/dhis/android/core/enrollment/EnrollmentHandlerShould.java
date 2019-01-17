package org.hisp.dhis.android.core.enrollment;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
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
    private ObjectWithoutUidStore<Note> noteStore;

    @Mock
    private Enrollment enrollment;

    @Mock
    private Event event;

    @Mock
    private Note note;

    @Mock
    private DHISVersionManager versionManager;

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

        enrollmentHandler = new EnrollmentHandler(versionManager, enrollmentStore, eventHandler,
                eventCleaner, noteHandler, noteStore);
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
        verify(eventHandler, times(1)).handleMany(any(ArrayList.class), any(ModelBuilder.class));
        verify(eventCleaner, times(1)).deleteOrphan(any(Enrollment.class), any(ArrayList.class));
        verify(noteHandler, times(1)).handleMany(anyCollectionOf(Note.class));
    }
}
