package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.event.EventEndpointCall;
import org.hisp.dhis.android.core.event.EventModel;
import org.junit.Before;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isDatabaseEmpty;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isTableEmpty;

public class LogoutCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;

    private ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        authenticatedUserStore = AuthenticatedUserStore.create(databaseAdapter());
    }

    //@Test
    public void delete_credentials_when_log_out_after_sync_data() throws Exception {
        d2.logIn("android", "Android123").call();

        d2.syncMetaData().call();

        EventEndpointCall eventCall = EventCallFactory.create(d2.retrofit(), "DiszpKrYNg8", 0);

        eventCall.call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logout().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();
        assertThat(isTableEmpty(databaseAdapter(), EventModel.TABLE)).isFalse();

        AuthenticatedUserModel authenticatedUser = authenticatedUserStore.selectFirst(AuthenticatedUserModel.factory);

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.credentials()).isNull();
    }

    //@Test
    public void recreate_credentials_when_login_again()
            throws Exception {
        d2.logIn("android", "Android123").call();

        d2.syncMetaData().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logout().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        AuthenticatedUserModel authenticatedUser = authenticatedUserStore.selectFirst(AuthenticatedUserModel.factory);

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.credentials()).isNull();

        d2.logIn("android", "Android123").call();

        authenticatedUser = authenticatedUserStore.selectFirst(AuthenticatedUserModel.factory);

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.credentials()).isNotNull();
    }

    //@Test
    public void response_successful_on_login_logout_and_login() throws Exception {
        d2.logIn("android", "Android123").call();
        d2.logout().call();
        d2.logIn("android", "Android123").call();
    }
}
