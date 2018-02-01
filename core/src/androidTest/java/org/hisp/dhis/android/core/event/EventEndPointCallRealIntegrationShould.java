package org.hisp.dhis.android.core.event;

import static junit.framework.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.support.test.filters.LargeTest;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class EventEndPointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    @LargeTest
    public void download_number_of_events_according_to_default_limit() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();


        response = d2.syncMetaData().call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        response = eventEndPointCall.call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        //TODO: we should create dependant server data verifications in other test suite
       /* verifyNumberOfDownloadedEvents(49);
        verifyNumberOfDownloadedTrackedEntityDataValue(335);*/
    }


    @Test
    @LargeTest
    public void download_event_with_category_combo_option() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();


        response = d2.syncMetaData().call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        eventEndPointCall.call();

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
        TrackedEntityDataValueStoreImpl eventStore = new TrackedEntityDataValueStoreImpl(d2.databaseAdapter());

        int numPersisted = eventStore.countAll();

        assertThat(numPersisted, is(num));
    }
}
