package org.hisp.dhis.android.core.audit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.audit.broker.RealBrokerMother;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MetadataAuditRealIntegrationShould extends AbsStoreTestCase {

    private final CountDownLatch lock = new CountDownLatch(1);

    private D2 d2;
    private SyncedMetadata syncedMetadataFromServer;

    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter(),
                MetadataAuditConnection.builder()
                        .setHost(RealBrokerMother.hostIP)
                        .setVirtualHost(RealBrokerMother.virtualHost)
                        .setUsername(RealBrokerMother.user)
                        .setPassword(RealBrokerMother.password)
                        .setPort(RealBrokerMother.port)
                        .build());
    }

    // The goal of this test is verify the end to end the whole rabbitmq sync feature
    // this test must to be commented because it needs a message broker (server), rabbitmq
    // configuration in dhis2 server and manual metadata change on server.
    // To execute this test:
    // 1) Uncomment @Test annotation
    // 2) Change MetadataAuditConnection to match your broker IP, username and port
    // 3) Eventually setup a breakpoint
    // 4) Launch the test
    // 5) Manually Modify some piece of metadata on a DHIS2 instance connected to the same broker
    //@Test
    public void notify_metadata_synced_in_local_when_a_change_occurs_on_the_server()
            throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();
        d2.startListeningSyncedMetadata(
                new MetadataSyncedListener() {
                    @Override
                    public void onSynced(SyncedMetadata syncedMetadata) {
                        syncedMetadataFromServer = syncedMetadata;
                        lock.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        lock.countDown();
                    }
                });

        lock.await();

        assertThat(syncedMetadataFromServer, is(notNullValue()));

        d2.stopListeningSyncedMetadata();
    }
}
