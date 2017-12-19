package org.hisp.dhis.android.core;

import static com.google.common.truth.Truth.assertThat;

import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isDatabaseEmpty;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.event.EventEndPointCall;
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

    @Test
    public void have_empty_database_when_wipe_db_after_sync_metadata() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        Truth.assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logOut().call();

        Truth.assertThat(isDatabaseEmpty(databaseAdapter())).isTrue();
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        EventEndPointCall eventCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        eventCall.call();

        Truth.assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logOut().call();

        Truth.assertThat(isDatabaseEmpty(databaseAdapter())).isTrue();
    }
}
