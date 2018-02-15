package org.hisp.dhis.android.core;

import static com.google.common.truth.Truth.assertThat;

import android.support.test.filters.LargeTest;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

public class MetadataCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to
     * the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    Exception e;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter(), Locale.ENGLISH);
    }


   /* How to extract database from tests:
    edit: AbsStoreTestCase.java (adding database name.)
    DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext()
    .getApplicationContext(),
    "test.db");
    make a debugger break point where desired (after sync complete)

    Then while on the breakpoint :
    Android/platform-tools/adb pull /data/user/0/org.hisp.dhis.android.test/databases/test.db
    test.db

    in datagrip:
    pragma foreign_keys = on;
    pragma foreign_key_check;*/

    @Test
    @LargeTest
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
    @LargeTest
    public void response_successful_on_login_wipe_db_and_login() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        d2.wipeDB().call();

        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    @LargeTest
    public void response_successful_on_login_logout_and_login() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();

        d2.logout().call();

        response = d2.logIn("android", "Android123").call();
        assertThat(response.isSuccessful()).isTrue();
    }
}
