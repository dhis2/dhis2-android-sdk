package org.hisp.dhis.android.core.enrollment;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
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
    private EventHandler eventHandler;

    @Mock
    private Enrollment enrollment;

    @Mock
    private Event event;

    // object to test
    private EnrollmentHandler enrollmentHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(enrollment.uid()).thenReturn("test_enrollment_uid");
        when(enrollment.events()).thenReturn(Collections.singletonList(event));

        enrollmentHandler = new EnrollmentHandler(enrollmentStore, eventHandler);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        enrollmentHandler.handle(null);

        // verify that store or event handler is never called
        verify(enrollmentStore, never()).delete(anyString());
        verify(enrollmentStore, never()).update(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class), anyString()
        );
        verify(enrollmentStore, never()).insert(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class)
        );

        verify(eventHandler, never()).handle(any(Event.class));
    }

    @Test
    public void invoke_only_delete_when_a_enrollment_is_set_as_deleted() throws Exception {
        when(enrollment.deleted()).thenReturn(Boolean.TRUE);

        enrollmentHandler.handle(enrollment);

        // verify that enrollment store is only invoked with delete
        verify(enrollmentStore, times(1)).delete(anyString());


        verify(enrollmentStore, never()).update(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class), anyString()
        );
        verify(enrollmentStore, never()).insert(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class)
        );

        // event handler should not be invoked
        verify(eventHandler, never()).handle(any(Event.class));
    }

    @Test
    public void invoke_only_update_when_handle_enrollment_inserted() throws Exception {
        when(enrollmentStore.update(anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class), anyString())
        ).thenReturn(1);

        enrollmentHandler.handle(enrollment);

        // verify that enrollment store is only invoked with update
        verify(enrollmentStore, times(1)).update(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class), anyString()
        );

        verify(enrollmentStore, never()).delete(anyString());

        verify(enrollmentStore, never()).insert(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class)
        );

        // event handler should be invoked once
        verify(eventHandler, times(1)).handle(event);
    }

    @Test
    public void invoke_update_and_insert_when_handle_enrollment_not_inserted() throws Exception {
        when(enrollmentStore.update(anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class), anyString())
        ).thenReturn(0);

        enrollmentHandler.handle(enrollment);

        // verify that enrollment store is only invoked with insert
        verify(enrollmentStore, times(1)).insert(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class)
        );

        // verify that update is also invoked since we're trying to update before we insert
        verify(enrollmentStore, times(1)).update(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyBoolean(),
                any(EnrollmentStatus.class), anyString(), anyString(),
                anyString(), any(State.class), anyString()
        );

        // verify that delete is never invoked
        verify(enrollmentStore, never()).delete(anyString());


        // event handler should be invoked once
        verify(eventHandler, times(1)).handle(event);
    }
}
