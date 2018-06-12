package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class PeriodicSynchronizerRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to
     * the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }


    @Test
    public void only_sync_if_user_logged() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();
        d2.syncMetaData().call();
        d2.logout().call();
        d2.logIn(RealServerMother.user, RealServerMother.password).call();
    }
}