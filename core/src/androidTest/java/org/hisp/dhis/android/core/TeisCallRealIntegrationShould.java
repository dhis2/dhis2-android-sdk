package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TeisCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void download_tracked_entity_instances() throws Exception {
        d2.logout().call();
        d2.logIn("android", "Android123").call();

        d2.syncMetaData().call();

        List<TrackedEntityInstance> trackedEntityInstances =
                d2.downloadTrackedEntityInstances(5,  false).call();
        assertThat(trackedEntityInstances.size() == 5).isTrue();
    }
}