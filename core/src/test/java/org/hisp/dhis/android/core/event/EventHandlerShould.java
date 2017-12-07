package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventHandlerShould {

    @Mock
    private EventStore eventStore;

    @Mock
    private TrackedEntityDataValueHandler trackedEntityDataValueHandler;

    @Mock
    private Event event;

    // object to test
    private EventHandler eventHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(event.uid()).thenReturn("test_event_uid");

        eventHandler = new EventHandler(eventStore, trackedEntityDataValueHandler);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        eventHandler.handle(null);

        // verify that store is never invoked
        verify(eventStore, never()).delete(anyString());
        verify(eventStore, never()).update(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class), anyString());

        verify(eventStore, never()).insert(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class));
    }

    @Test
    public void invoke_only_delete_when_a_event_is_set_as_deleted() throws Exception {
        when(event.deleted()).thenReturn(Boolean.TRUE);

        eventHandler.handle(event);

        // verify that delete is invoked once
        verify(eventStore, times(1)).delete(event.uid());

        // verify that update and insert is never invoked
        verify(eventStore, never()).update(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class), anyString());
        verify(eventStore, never()).insert(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class));
    }

    @Test
    public void invoke_only_update_when_handle_event_inserted() throws Exception {
        when(eventStore.update(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class), anyString())
        ).thenReturn(1);

        eventHandler.handle(event);

        // verify that update is invoked once
        verify(eventStore, times(1)).update(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class), anyString());

        // verify that insert and delete is never invoked
        verify(eventStore, never()).insert(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class));
        verify(eventStore, never()).delete(anyString());
    }

    @Test
    public void invoke_update_and_insert_when_handle_event_not_inserted() throws Exception {
        when(eventStore.update(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class), anyString())
        ).thenReturn(0);

        eventHandler.handle(event);

        // verify that update and insert is invoked, since we're updating before inserting
        verify(eventStore, times(1)).insert(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class));

        verify(eventStore, times(1)).update(anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),
                any(EventStatus.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), any(Date.class), any(State.class), anyString());

        // verify that delete is never invoked
        verify(eventStore, never()).delete(anyString());
    }
}