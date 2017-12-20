package org.hisp.dhis.android.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DatabaseAssert;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.event.EventEndPointCall;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.core.user.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import retrofit2.Response;

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

    @Test
    public void have_empty_database_when_wipe_db_after_sync_meta_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.wipeDB().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isEmpty();
    }

    @Test
    public void have_empty_database_when_wipe_db_after_sync_data() throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        givenAEventInDatabase();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.wipeDB().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isEmpty();
    }

    @Test
    public void delete_authenticate_user_table_only_when_log_out_after_sync_metadata()
            throws Exception {

        givenALoginInDatabase();

        givenAMetadataInDatabase();

        givenAEventInDatabase();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        d2.logout().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter()).isNotEmpty();

        DatabaseAssert.assertThatDatabase(databaseAdapter())
                .isNotEmpty()
                .isEmptyTable(AuthenticatedUserModel.TABLE)
                .isNotEmptyTable(EventModel.TABLE)
                .isNotEmptyTable(UserModel.TABLE)
                .isNotEmptyTable(UserCredentialsModel.TABLE)
                .isNotEmptyTable(OrganisationUnitModel.TABLE)
                .isNotEmptyTable(ProgramModel.TABLE)
                .isNotEmptyTable(ResourceModel.TABLE);
    }

    @Test
    public void have_organisation_units_descendants_after_login_wipe_and_login()
            throws Exception {
        givenALoginWithSierraLeonaOUInDatabase();

        givenAMetadataWithDescendantsInDatabase();

        verifyExistsAsignedOrgUnitAndDescendants();

        d2.wipeDB().call();

        givenALoginWithSierraLeonaOUInDatabase();

        givenAMetadataWithDescendantsInDatabase();

        verifyExistsAsignedOrgUnitAndDescendants();
    }

    @Test
    public void realize_login_and_sync_metadata_successfully_after_logout()
            throws Exception {
        givenALoginInDatabase();

        givenAMetadataInDatabase();

        d2.logout().call();

        DatabaseAssert.assertThatDatabase(databaseAdapter())
                .isNotEmpty()
                .isEmptyTable(AuthenticatedUserModel.TABLE)
                .isNotEmptyTable(UserModel.TABLE)
                .isNotEmptyTable(UserCredentialsModel.TABLE)
                .isNotEmptyTable(ResourceModel.TABLE);

        givenALoginInDatabase();

        givenAMetadataInDatabase();

        DatabaseAssert.assertThatDatabase(databaseAdapter())
                .isNotEmpty()
                .isNotEmptyTable(AuthenticatedUserModel.TABLE)
                .isNotEmptyTable(UserModel.TABLE)
                .isNotEmptyTable(UserCredentialsModel.TABLE)
                .isNotEmptyTable(OrganisationUnitModel.TABLE)
                .isNotEmptyTable(ProgramModel.TABLE)
                .isNotEmptyTable(ResourceModel.TABLE);
    }


    private void givenALoginInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("login.json", new Date());

        Response<User> response = d2.logIn("user", "password").call();

        assertThat(response.isSuccessful(), is(true));
    }

    private void givenALoginWithSierraLeonaOUInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("admin/login.json", new Date());

        Response<User> response = d2.logIn("user", "password").call();

        assertThat(response.isSuccessful(), is(true));
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("user.json");
        dhis2MockServer.enqueueMockResponse("organisationUnits.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        Response response = d2.syncMetaData().call();

        assertThat(response.isSuccessful(), is(true));
    }

    private void givenAMetadataWithDescendantsInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("admin/user.json");
        dhis2MockServer.enqueueMockResponse("admin/organisation_units.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        Response response = d2.syncMetaData().call();

        assertThat(response.isSuccessful(), is(true));
    }

    private void givenAEventInDatabase() throws Exception {
        EventEndPointCall eventCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("events_1.json");

        Response response = eventCall.call();

        assertThat(response.isSuccessful(), is(true));
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
                        "OU_278371");
    }
}
