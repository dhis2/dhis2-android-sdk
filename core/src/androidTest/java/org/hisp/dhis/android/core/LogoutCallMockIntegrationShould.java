package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DatabaseAssert;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.event.EventEndPointCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
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

    private void givenALoginWithSierraLeonaOUInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("sierra_leona_login.json");

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

    private void givenAMetadataWithDescendantsInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("sierra_leona_login.json");
        dhis2MockServer.enqueueMockResponse("sierra_leona_organisationUnits.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        d2.syncMetaData().call();
    }

    private void givenAEventInDatabase() throws Exception {
        EventEndPointCall eventCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("events_1.json");

        eventCall.call();
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_meta_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.logOut().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isEmpty();
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        givenAEventInDatabase();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.logOut().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isEmpty();
    }

    @Test
    public void have_organisation_units_descendants_after_login_logout_and_login()
            throws Exception {
        givenALoginWithSierraLeonaOUInDatabase();

        givenAMetadataWithDescendantsInDatabase();

        verifyExistsAsignedOrgUnitAndDescendants();

        d2.logOut().call();

        givenALoginWithSierraLeonaOUInDatabase();

        givenAMetadataWithDescendantsInDatabase();

        verifyExistsAsignedOrgUnitAndDescendants();
    }

    private void verifyExistsAsignedOrgUnitAndDescendants() {
        //Sierra leona
        DatabaseAssert.assertThatDatabase(databaseAdapter())
                .ifValueExist(OrganisationUnitModel.TABLE,
                        OrganisationUnitModel.Columns.UID,
                        "ImspTQPwCqd");

        //Sierra leona descendant
        DatabaseAssert.assertThatDatabase(databaseAdapter())
                .ifValueExist(OrganisationUnitModel.TABLE,
                        OrganisationUnitModel.Columns.CODE,
                        "OU_260382");
    }
}
