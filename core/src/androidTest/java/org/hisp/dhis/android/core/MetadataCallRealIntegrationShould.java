package org.hisp.dhis.android.core;

import static com.google.common.truth.Truth.assertThat;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@RunWith(AndroidJUnit4.class)
public class MetadataCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    Exception e;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse("https://play.dhis2.org/demo/api/"))
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


   /* How to extract database from tests:
    edit: AbsStoreTestCase.java (adding database name.)
    DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext().getApplicationContext(),
    "test.db");
    make a debugger break point where desired (after sync complete)

    Then while on the breakpoint :
    Android/platform-tools/adb pull /data/user/0/org.hisp.dhis.android.test/databases/test.db test.db

    in datagrip:
    pragma foreign_keys = on;
    pragma foreign_key_check;*/

    //This test is uncommented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    //@Test
    public void response_successful_on_sync_meta_data_two_times() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        //first sync:
        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        //second sync:
        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        //TODO: add aditional sync + break point.
        //when debugger stops at the new break point manually change metadata online & resume.
        //This way I can make sure that additive (updates) work as well.
        //The changes could be to one of the programs, adding stuff to it.
        // adding a new program..etc.
    }

    @Test
    public void stub() {
    }
}
