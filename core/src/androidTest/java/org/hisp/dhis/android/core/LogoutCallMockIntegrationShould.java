package org.hisp.dhis.android.core;

import static com.google.common.truth.Truth.assertThat;

import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isDatabaseEmpty;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isTableEmpty;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.event.EventCall;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.UserModel;
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

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.wipeDB().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isTrue();
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        givenAEventInDatabase();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.wipeDB().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isTrue();
    }
    @Test
    public void delete_autenticate_user_table_only_when_log_out_after_sync_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        givenAEventInDatabase();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logout().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();
        assertThat(isTableEmpty(databaseAdapter(), EventModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), AuthenticatedUserModel.TABLE)).isTrue();
    }

    @Test
    public void delete_autenticate_user_table_only_when_log_out_after_sync_metadata() throws Exception {

        givenALoginInDatabase();

        givenAMetadataInDatabase();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        d2.logout().call();

        assertThat(isDatabaseEmpty(databaseAdapter())).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), EventModel.TABLE)).isTrue();

        assertThat(isTableEmpty(databaseAdapter(), AuthenticatedUserModel.TABLE)).isTrue();

        assertThat(isTableEmpty(databaseAdapter(), UserModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), OrganisationUnitModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), ProgramModel.TABLE)).isFalse();

        assertThat(isTableEmpty(databaseAdapter(), ResourceModel.TABLE)).isFalse();

    }
}
