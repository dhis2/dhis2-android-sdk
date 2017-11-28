package org.hisp.dhis.android.core.event;

import static org.hisp.dhis.android.core.calls.Call.MAX_UIDS;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Response;

public class EventCallShould {
    private Call<Response<Payload<Event>>> eventCall;

    @Mock
    private EventService eventService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private EventStore eventStore;

    @Mock
    private Date serverDate;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_uids_size_exceeds_the_limit() {
        EventCall eventCall = givenAEventCallByUIds(MAX_UIDS + 1);
    }

    @Test
    public void create_event_call_if_uIds_size_does_not_exceeds_the_limit() {
        EventCall eventCall = givenAEventCallByUIds(MAX_UIDS);
    }

    private EventCall givenAEventCallByUIds(int numUIds) {
        Set<String> uIds = new HashSet<>();

        for (int i = 0; i < numUIds; i++) {
            uIds.add("uid" + i);
        }

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withUIds(uIds)
                .build();

        EventCall eventCall =
                new EventCall(eventService, databaseAdapter, resourceStore,
                        eventStore, serverDate, eventQuery);

        return eventCall;
    }
}
