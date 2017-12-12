package org.hisp.dhis.android.core;

import static com.google.common.truth.Truth.assertThat;

import android.database.Cursor;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.event.EventCall;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class LogoutCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    private void givenALoginInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("login.json");

        d2.logIn("user", "password").call();
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("user.json");
        dhis2MockServer.enqueueMockResponse("organisationUnits.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        d2.syncMetaData().call();
    }

    private void givenAEventInDatabase() throws Exception {
        EventCall eventCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("events_1.json");

        eventCall.call();
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_meta_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        Truth.assertThat(isDatabaseEmpty()).isFalse();

        d2.logOut().call();

        Truth.assertThat(isDatabaseEmpty()).isTrue();
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        givenAEventInDatabase();

        Truth.assertThat(isDatabaseEmpty()).isFalse();

        d2.logOut().call();

        Truth.assertThat(isDatabaseEmpty()).isTrue();
    }

    private boolean isDatabaseEmpty() {
        Cursor res = databaseAdapter().query(" SELECT name FROM sqlite_master WHERE type='table' and name!='android_metadata' and name!='sqlite_sequence'");
        int value = res.getColumnIndex("name");
        if (value != -1) {
            while (res.moveToNext()){
                String tableName = res.getString(value);
                Cursor resTable = databaseAdapter().query(
                        "SELECT * from " + tableName , null);
                if (resTable.getCount() > 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
