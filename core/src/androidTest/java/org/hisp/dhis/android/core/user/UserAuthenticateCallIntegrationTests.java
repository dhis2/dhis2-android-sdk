package org.hisp.dhis.android.core.user;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.models.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// ToDo: implement integration tests for user authentication task
// ToDo: what will happen if the same user will be inserted twice?
// ToDo: more tests to verify correct store behaviour
@RunWith(AndroidJUnit4.class)
public class UserAuthenticateCallIntegrationTests extends AbsStoreTestCase {
    private MockWebServer mockWebServer;
    private Call<Response<User>> authenticateUserCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(JacksonConverterFactory.create())
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        UserService userService = retrofit.create(UserService.class);

        UserStore userStore = new UserStoreImpl(database());
        UserCredentialsStore userCredentialsStore = new UserCredentialsStoreImpl(database());
        OrganisationUnitStore organisationUnitStore = new OrganisationUnitStoreImpl(database());
        AuthenticatedUserStore authenticatedUserStore = new AuthenticatedUserStoreImpl(database());
        UserOrganisationUnitLinkStore userOrganisationUnitLinkStore = new UserOrganisationUnitLinkStoreImpl(database());

        authenticateUserCall = new UserAuthenticateCall(
                userService, database(), userStore, userCredentialsStore,
                userOrganisationUnitLinkStore, authenticatedUserStore, organisationUnitStore,
                "test_user", "test_password");
    }

    @Test
    public void call_shouldAuthenticateUser() throws Exception {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody("{\n" +
                "\n" +
                "    \"created\": \"2015-03-31T13:31:09.324\",\n" +
                "    \"lastUpdated\": \"2016-04-06T00:05:57.495\",\n" +
                "    \"name\": \"John Barnes\",\n" +
                "    \"id\": \"DXyJmlo9rge\",\n" +
                "    \"displayName\": \"John Barnes\",\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"surname\": \"Barnes\",\n" +
                "    \"email\": \"john@hmail.com\",\n" +
                "    \"userCredentials\": {\n" +
                "        \"lastUpdated\": \"2016-12-20T15:04:21.254\",\n" +
                "        \"code\": \"android\",\n" +
                "        \"created\": \"2015-03-31T13:31:09.206\",\n" +
                "        \"name\": \"John Traore\",\n" +
                "        \"id\": \"M0fCOxtkURr\",\n" +
                "        \"displayName\": \"John Traore\",\n" +
                "        \"username\": \"android\"\n" +
                "    },\n" +
                "    \"organisationUnits\": [\n" +
                "        {\n" +
                "            \"code\": \"OU_559\",\n" +
                "            \"level\": 4,\n" +
                "            \"created\": \"2012-02-17T15:54:39.987\",\n" +
                "            \"lastUpdated\": \"2014-11-25T09:37:54.924\",\n" +
                "            \"name\": \"Ngelehun CHC\",\n" +
                "            \"id\": \"DiszpKrYNg8\",\n" +
                "            \"shortName\": \"Ngelehun CHC\",\n" +
                "            \"displayName\": \"Ngelehun CHC\",\n" +
                "            \"displayShortName\": \"Ngelehun CHC\",\n" +
                "            \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu/YuQRtpLP10I/DiszpKrYNg8\",\n" +
                "            \"openingDate\": \"1970-01-01T00:00:00.000\",\n" +
                "            \"parent\": {\n" +
                "                \"id\": \"YuQRtpLP10I\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "\n" +
                "}");

        mockWebServer.enqueue(mockResponse);

        Response<User> userResponse = authenticateUserCall.call();
    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();

        mockWebServer.shutdown();
    }
}
