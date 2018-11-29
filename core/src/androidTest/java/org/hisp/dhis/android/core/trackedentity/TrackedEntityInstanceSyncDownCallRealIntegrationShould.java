package org.hisp.dhis.android.core.trackedentity;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TrackedEntityInstanceSyncDownCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
    private TrackedEntityInstanceStoreImpl store;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
        store = new TrackedEntityInstanceStoreImpl(databaseAdapter());
    }

    //This test is commented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.

    //@Test
    public void sync_down_synced_tracked_entity_instances() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        d2.downloadTrackedEntityInstances(2, false).call();

        Map<String, TrackedEntityInstance> trackedEntityInstances =
                new TrackedEntityInstanceStoreImpl(databaseAdapter()).queryAll();

        Truth.assertThat(trackedEntityInstances.size()).isEqualTo(2);

        Iterator<Map.Entry<String, TrackedEntityInstance>> tEIIterator = trackedEntityInstances.entrySet().iterator();

        TrackedEntityInstance trackedEntityInstance = tEIIterator.next().getValue();

        store.setState(trackedEntityInstance.uid(), State.TO_UPDATE);

        Callable<List<TrackedEntityInstance>> syncDownSyncedTrackedEntityInstanceCall =
                d2.syncDownSyncedTrackedEntityInstances();
        List<TrackedEntityInstance> syncedTeiResponse = syncDownSyncedTrackedEntityInstanceCall.call();

        Truth.assertThat(syncedTeiResponse.size()).isEqualTo(1);
    }
}
