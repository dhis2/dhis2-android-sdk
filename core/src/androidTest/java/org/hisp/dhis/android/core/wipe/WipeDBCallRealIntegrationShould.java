package org.hisp.dhis.android.core.wipe;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DatabaseAssert;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.event.EventEndpointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.junit.Before;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class WipeDBCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void have_empty_database_when_wipe_db_after_sync_metadata() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.wipeModule().wipeEverything();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isEmpty();
    }

    //@Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        EventEndpointCall eventCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        eventCall.call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.wipeModule().wipeEverything();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isEmpty();
    }

    //@Test
    public void do_not_have_metadata_when_wipe_metadata_after_sync_metadata() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.wipeModule().wipeMetadata();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isEmpty();
    }

    //@Test
    public void do_not_have_data_when_wipe_data_after_sync() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        d2.downloadTrackedEntityInstances(5, false).call();

        TrackedEntityInstanceStoreImpl trackedEntityInstanceStore =
                new TrackedEntityInstanceStoreImpl(databaseAdapter());

        boolean hasTrackedEntities = trackedEntityInstanceStore.queryAll().values().iterator().hasNext();

        assertThat(hasTrackedEntities).isTrue();

        d2.wipeModule().wipeData();

        hasTrackedEntities = trackedEntityInstanceStore.queryAll().values().iterator().hasNext();

        assertThat(hasTrackedEntities).isFalse();
    }
}