package org.hisp.dhis.android.core.event;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EventEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //This test is commented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    //@Test
    public void download_number_of_events_according_to_default_limit() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        EventEndpointCall eventEndpointCall = EventCallFactory.create(
                d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        List<Event> events = eventEndpointCall.call();
        Truth.assertThat(events.isEmpty()).isFalse();

        //TODO: we should create dependant server data verifications in other test suite
       /* verifyNumberOfDownloadedEvents(49);
        verifyNumberOfDownloadedTrackedEntityDataValue(335);*/
    }


    //@Test
    public void download_event_with_category_combo_option() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        EventEndpointCall eventEndpointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        eventEndpointCall.call();

        assertTrue(verifyAtLeastOneEventWithCategoryOption());
    }

    private boolean verifyAtLeastOneEventWithCategoryOption() {
        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();
        for(Event event : downloadedEvents){
            if(event.attributeCategoryOptions()!=null && event.attributeOptionCombo()!=null){
                return true;
            }
        }
        return false;
    }

    private void verifyNumberOfDownloadedEvents(int numEvents) {
        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size(), is(numEvents));
    }

    private void verifyNumberOfDownloadedTrackedEntityDataValue(int num) {
        TrackedEntityDataValueStoreImpl eventStore = new TrackedEntityDataValueStoreImpl(
                d2.databaseAdapter());

        int numPersisted = eventStore.countAll();

        assertThat(numPersisted, is(num));
    }
}
