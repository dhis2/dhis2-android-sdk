package org.hisp.dhis.android.core;

import static com.google.common.truth.Truth.assertThat;

import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isDatabaseEmpty;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isTableEmpty;

import android.database.Cursor;

import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.event.EventCall;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.UserModel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class LogoutCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;
    Exception e;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse("https://play.dhis2.org/android-current/api/"))
                .build();

        d2 = new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter())
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter()))
                                .build()
                ).build();
    }

    //@Test
    public void have_empty_database_when_wipe_db_after_sync_metadata() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.wipeDB().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isTrue();
    }

    //@Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        EventCall eventCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        eventCall.call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.wipeDB().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isTrue();
    }

    //@Test
    public void delete_autenticate_user_table_only_when_log_out_after_sync_data() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        EventCall eventCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        eventCall.call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logout().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();
        assertThat(isTableEmpty(databaseAdapter(), EventModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), AuthenticatedUserModel.TABLE)).isTrue();
    }

    //@Test
    public void delete_autenticate_user_table_only_when_log_out_after_sync_metadata() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logout().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), EventModel.TABLE)).isTrue();

        assertThat(isTableEmpty(databaseAdapter(), UserModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), OrganisationUnitModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), ProgramModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), ResourceModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), AuthenticatedUserModel.TABLE)).isTrue();
    }
}
