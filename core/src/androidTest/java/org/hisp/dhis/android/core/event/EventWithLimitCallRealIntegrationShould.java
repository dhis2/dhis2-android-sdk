package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class EventWithLimitCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void download_tracked_entity_instances() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

         d2.downloadSingleEvents(20,  false).call();
        assertThat(new EventStoreImpl(databaseAdapter()).queryAll().size()).isEqualTo(20);
    }
}