package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;

public class UserAuthenticateCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void not_wipe_after_second_login_with_same_user() throws Exception {
        d2.userModule().logIn("android", "Android123").call();

        d2.syncMetaData().call();

        d2.userModule().logOut().call();
        d2.userModule().logIn("android", "Android123").call();
    }

    //@Test
    public void wipe_after_second_login_with_different_user() throws Exception {
        d2.userModule().logIn("android", "Android123").call();

        d2.syncMetaData().call();

        d2.userModule().logOut().call();
        d2.userModule().logIn("admin", "district").call();
    }

    //@Test
    public void wipe_after_second_login_with_equivalent_user_in_different_server() throws Exception {
        d2 = D2Factory.create("https://play.dhis2.org/2.29/api/", databaseAdapter());

        d2.userModule().logIn("android", "Android123").call();

        d2.syncMetaData().call();

        d2 = D2Factory.create("https://play.dhis2.org/android-current/api/", databaseAdapter());

        d2.userModule().logOut().call();
        d2.userModule().logIn("android", "Android123").call();
    }
}
