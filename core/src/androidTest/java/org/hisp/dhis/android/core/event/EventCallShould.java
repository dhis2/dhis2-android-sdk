package org.hisp.dhis.android.core.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.program.Program;
import org.junit.Test;

import java.util.List;

import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;

public class EventCallShould extends AbsStoreTestCase{

    private MockWebServer mockWebServer;
    private Call<Response<Payload<Program>>> eventCall;

    @Test
    public void download_number_of_events_according_to_default_limit() throws Exception {

        eventCall.call();

        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents =  eventStore.querySingleEventsToPost();

        assertThat(downloadedEvents.size(),is(50));

        deleteDatabase();
    }
}
