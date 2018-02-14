/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.organisationunit;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.utils.HeaderUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitCallMockIntegrationShould extends AbsStoreTestCase {
    private static final String[] ORGANISATION_UNIT_PROJECTION = {
            OrganisationUnitModel.Columns.UID,
            OrganisationUnitModel.Columns.CODE,
            OrganisationUnitModel.Columns.NAME,
            OrganisationUnitModel.Columns.DISPLAY_NAME,
            OrganisationUnitModel.Columns.CREATED,
            OrganisationUnitModel.Columns.LAST_UPDATED,
            OrganisationUnitModel.Columns.SHORT_NAME,
            OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME,
            OrganisationUnitModel.Columns.DESCRIPTION,
            OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION,
            OrganisationUnitModel.Columns.PATH,
            OrganisationUnitModel.Columns.OPENING_DATE,
            OrganisationUnitModel.Columns.CLOSED_DATE,
            OrganisationUnitModel.Columns.LEVEL,
            OrganisationUnitModel.Columns.PARENT
    };
    private static String[] USER_ORGANISATION_UNIT_PROJECTION = {
            UserOrganisationUnitLinkModel.Columns.USER,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT,
    };
    private static String[] RESOURCE_PROJECTION = {
            ResourceModel.Columns.RESOURCE_TYPE,
            ResourceModel.Columns.LAST_SYNCED
    };

    private MockWebServer server;

    private String dateString;

    //The return of the organisationUnitCall to be tested:
    private Call<Response<Payload<OrganisationUnit>>> organisationUnitCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        server = new MockWebServer();
        server.start();

        //TODO: Consider moving the json out to a separate file:
        MockResponse response = new MockResponse();
        response.setHeader(HeaderUtils.DATE, "Tue, 21 Feb 2017 15:44:46 GMT");
        response.setResponseCode(200);
        response.setBody("{\n" +
                "  \"organisationUnits\": [\n" +
                "    {\n" +
                "      \"code\": \"OU_264\",\n" +
                "      \"level\": 2,\n" +
                "      \"created\": \"2012-02-17T15:54:39.987\",\n" +
                "      \"lastUpdated\": \"2014-12-15T11:56:16.767\",\n" +
                "      \"name\": \"Bo\",\n" +
                "      \"id\": \"O6uvpzGd5pu\",\n" +
                "      \"deleted\": false,\n" +
                "      \"shortName\": \"Bo\",\n" +
                "      \"displayName\": \"Bo\",\n" +
                "     \"displayShortName\": \"Bo\",\n" +
                "      \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu\",\n" +
                "      \"openingDate\": \"1990-02-01T00:00:00.000\",\n" +
                "      \"parent\": {\n" +
                "        \"id\": \"ImspTQPwCqd\"\n" +
                "      },\n" +
                "      \"programs\": [\n" +
                "        {\n" +
                "          \"id\": \"kla3mAPgvCH\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"OU_544\",\n" +
                "      \"level\": 3,\n" +
                "      \"created\": \"2012-02-17T15:54:39.987\",\n" +
                "      \"lastUpdated\": \"2014-11-25T09:37:53.358\",\n" +
                "      \"name\": \"Gbo\",\n" +
                "      \"id\": \"YmmeuGbqOwR\",\n" +
                "      \"deleted\": false,\n" +
                "      \"shortName\": \"Gbo\",\n" +
                "      \"displayName\": \"Gbo\",\n" +
                "     \"displayShortName\": \"Gbo\",\n" +
                "      \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu/YmmeuGbqOwR\",\n" +
                "      \"openingDate\": \"1970-01-01T00:00:00.000\",\n" +
                "      \"parent\": {\n" +
                "        \"id\": \"O6uvpzGd5pu\"\n" +
                "      },\n" +
                "      \"programs\": [ ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"OU_550\",\n" +
                "      \"level\": 3,\n" +
                "      \"created\": \"2012-02-17T15:54:39.987\",\n" +
                "      \"lastUpdated\": \"2014-11-25T09:37:53.364\",\n" +
                "      \"name\": \"Selenga\",\n" +
                "      \"id\": \"KctpIIucige\",\n" +
                "      \"deleted\": false,\n" +
                "      \"shortName\": \"Selenga\",\n" +
                "      \"displayName\": \"Selenga\",\n" +
                "     \"displayShortName\": \"Selenga\",\n" +
                "      \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu/KctpIIucige\",\n" +
                "      \"openingDate\": \"1970-01-01T00:00:00.000\",\n" +
                "      \"parent\": {\n" +
                "        \"id\": \"O6uvpzGd5pu\"\n" +
                "      },\n" +
                "      \"programs\": [ ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"OU_636\",\n" +
                "      \"level\": 4,\n" +
                "      \"created\": \"2012-02-17T15:54:39.987\",\n" +
                "      \"lastUpdated\": \"2014-11-25T09:37:53.553\",\n" +
                "      \"name\": \"Yengema CHP\",\n" +
                "      \"id\": \"EFTcruJcNmZ\",\n" +
                "      \"deleted\": false,\n" +
                "      \"shortName\": \"Yengema CHP\",\n" +
                "      \"displayName\": \"Yengema CHP\",\n" +
                "     \"displayShortName\": \"Yengema CHP\",\n" +
                "      \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu/BGGmAwx33dj/EFTcruJcNmZ\",\n" +
                "      \"openingDate\": \"1994-01-01T00:00:00.000\",\n" +
                "      \"parent\": {\n" +
                "        \"id\": \"BGGmAwx33dj\"\n" +
                "      },\n" +
                "      \"programs\": [\n" +
                "        {\n" +
                "          \"id\": \"uy2gU8kT1jF\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"q04UBOqq3rp\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"OU_678886\",\n" +
                "      \"level\": 4,\n" +
                "      \"created\": \"2012-02-17T15:54:39.987\",\n" +
                "      \"lastUpdated\": \"2014-11-25T09:37:54.900\",\n" +
                "      \"name\": \"Wallehun MCHP\",\n" +
                "      \"id\": \"tZxqVn3xNrA\",\n" +
                "      \"deleted\": false,\n" +
                "      \"shortName\": \"Wallehun MCHP\",\n" +
                "      \"displayName\": \"Wallehun MCHP\",\n" +
                "     \"displayShortName\": \"Wallehun MCHP\",\n" +
                "      \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu/BGGmAwx33dj/tZxqVn3xNrA\",\n" +
                "      \"openingDate\": \"2010-01-01T00:00:00.000\",\n" +
                "      \"parent\": {\n" +
                "        \"id\": \"BGGmAwx33dj\"\n" +
                "      },\n" +
                "      \"programs\": [\n" +
                "        {\n" +
                "          \"id\": \"uy2gU8kT1jF\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"q04UBOqq3rp\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"VBqh0ynB2wv\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"eBAyeGv0exc\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"kla3mAPgvCH\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"lxAQ7Zs9VYR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"IpHINAT79UW\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"WSGAb5XwJ3Y\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"ur1Edk5Oe2n\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "    ]\n" +
                "}");
        server.enqueue(response);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/")) //?
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        List<OrganisationUnit> organisationUnits = Collections.singletonList(
                OrganisationUnit.builder()
                        .uid("O6uvpzGd5pu").path("/ImspTQPwCqd/O6uvpzGd5pu")
                        .deleted(false).build());

        UserCredentials userCredentials = UserCredentials.builder().uid("credentials_uid").code(
                "code")
                .name("name").build();
        //dependencies for the OrganisationUnitCall:
        OrganisationUnitService organisationUnitService = retrofit.create(
                OrganisationUnitService.class);
        OrganisationUnitStore organisationUnitStore = new OrganisationUnitStoreImpl(
                databaseAdapter());
        UserOrganisationUnitLinkStore userOrganisationUnitLinkStore =
                new UserOrganisationUnitLinkStoreImpl(databaseAdapter());
        OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore =
                new OrganisationUnitProgramLinkStoreImpl(databaseAdapter());
        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        // Create a user with the root as assigned organisation unit (for the test):
        User user = User.builder()
                .uid("user_uid").code("code").name("name").displayName("display_name")
                .created(new Date()).lastUpdated(new Date()).birthday("birthday")
                .education("education").gender("gender").jobTitle("job_title").surname(
                        "surname").firstName("firstName")
                .introduction("introduction").employer("employer").interests("interests").languages(
                        "languages")
                .email("email").phoneNumber("phoneNumber").nationality("nationality")
                .userCredentials(userCredentials).organisationUnits(organisationUnits)
                .teiSearchOrganisationUnits(organisationUnits).dataViewOrganisationUnits(
                        organisationUnits)
                .deleted(false)
                .build();

        OrganisationUnitQuery organisationUnitQuery = OrganisationUnitQuery.defaultQuery(user);

        ContentValues userContentValues = new ContentValues();
        userContentValues.put(UserModel.Columns.ID, "user_uid");
        database().insert(UserModel.TABLE, null, userContentValues);

        // inserting programs for creating OrgUnitProgramLinks
        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "uy2gU8kT1jF");
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues program1 = new ContentValues();
        program1.put(ProgramModel.Columns.UID, "q04UBOqq3rp");
        database().insert(ProgramModel.TABLE, null, program1);

        ContentValues program2 = new ContentValues();
        program2.put(ProgramModel.Columns.UID, "VBqh0ynB2wv");
        database().insert(ProgramModel.TABLE, null, program2);

        ContentValues program3 = new ContentValues();
        program3.put(ProgramModel.Columns.UID, "eBAyeGv0exc");
        database().insert(ProgramModel.TABLE, null, program3);

        ContentValues program4 = new ContentValues();
        program4.put(ProgramModel.Columns.UID, "kla3mAPgvCH");
        database().insert(ProgramModel.TABLE, null, program4);

        ContentValues program5 = new ContentValues();
        program5.put(ProgramModel.Columns.UID, "lxAQ7Zs9VYR");
        database().insert(ProgramModel.TABLE, null, program5);

        ContentValues program6 = new ContentValues();
        program6.put(ProgramModel.Columns.UID, "IpHINAT79UW");
        database().insert(ProgramModel.TABLE, null, program6);

        ContentValues program7 = new ContentValues();
        program7.put(ProgramModel.Columns.UID, "WSGAb5XwJ3Y");
        database().insert(ProgramModel.TABLE, null, program7);

        ContentValues program8 = new ContentValues();
        program8.put(ProgramModel.Columns.UID, "ur1Edk5Oe2n");
        database().insert(ProgramModel.TABLE, null, program8);

        Date serverDate = new Date();

        dateString = BaseIdentifiableObject.DATE_FORMAT.format(serverDate);

        OrganisationUnitHandler organisationUnitHandler =
                new OrganisationUnitHandler(organisationUnitStore, userOrganisationUnitLinkStore,
                        organisationUnitProgramLinkStore, resourceHandler);

        organisationUnitCall = new OrganisationUnitCall(organisationUnitService, databaseAdapter(),
                resourceHandler, new Date(), organisationUnitHandler, organisationUnitQuery);
    }


    @Test
    @MediumTest
    public void persist_organisation_unit_tree_in_data_base_after_call() throws Exception {
        //Insert User in the User tables, such that UserOrganisationUnitLink's foreign key is
        // satisfied:
        ContentValues userContentValues = new ContentValues();
        userContentValues.put(UserModel.Columns.UID, "user_uid");
        userContentValues.put(UserModel.Columns.CODE, "code");
        userContentValues.put(UserModel.Columns.NAME, "name");
        userContentValues.put(UserModel.Columns.DISPLAY_NAME, "displayName");
        userContentValues.put(UserModel.Columns.LAST_UPDATED, "dateString");
        userContentValues.put(UserModel.Columns.CREATED, "dateString");
        userContentValues.put(UserModel.Columns.BIRTHDAY, "birthday");
        userContentValues.put(UserModel.Columns.EDUCATION, "education");
        userContentValues.put(UserModel.Columns.GENDER, "gender");
        userContentValues.put(UserModel.Columns.JOB_TITLE, "jobTitle");
        userContentValues.put(UserModel.Columns.SURNAME, "surname");
        userContentValues.put(UserModel.Columns.FIRST_NAME, "firstName");
        userContentValues.put(UserModel.Columns.INTRODUCTION, "introduction");
        userContentValues.put(UserModel.Columns.EMPLOYER, "employer");
        userContentValues.put(UserModel.Columns.INTERESTS, "interests");
        userContentValues.put(UserModel.Columns.LANGUAGES, "languages");
        userContentValues.put(UserModel.Columns.EMAIL, "email");
        userContentValues.put(UserModel.Columns.PHONE_NUMBER, "phoneNumber");
        userContentValues.put(UserModel.Columns.NATIONALITY, "nationality");
        database().insert(UserModel.TABLE, null, userContentValues);


        organisationUnitCall.call();

        Cursor organisationUnitCursor = database().query(OrganisationUnitModel.TABLE,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        Cursor userOrganisationUnitCursor = database().query(UserOrganisationUnitLinkModel.TABLE,
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);

        Cursor resourceCursor = database().query(ResourceModel.TABLE,
                RESOURCE_PROJECTION, null, null, null, null, null);
        //BO:
        assertThatCursor(organisationUnitCursor).hasRow("O6uvpzGd5pu", "OU_264", "Bo", "Bo",
                "2012-02-17T15:54:39.987", "2014-12-15T11:56:16.767", "Bo", "Bo", null, null,
                "/ImspTQPwCqd/O6uvpzGd5pu", "1990-02-01T00:00:00.000", null, 2, "ImspTQPwCqd");
        //GBO:
        assertThatCursor(organisationUnitCursor).hasRow("YmmeuGbqOwR", "OU_544", "Gbo", "Gbo",
                "2012-02-17T15:54:39.987", "2014-11-25T09:37:53.358", "Gbo", "Gbo", null, null,
                "/ImspTQPwCqd/O6uvpzGd5pu/YmmeuGbqOwR", "1970-01-01T00:00:00.000", null, 3,
                "O6uvpzGd5pu");
        //Selenga:
        assertThatCursor(organisationUnitCursor).hasRow("KctpIIucige", "OU_550", "Selenga",
                "Selenga",
                "2012-02-17T15:54:39.987", "2014-11-25T09:37:53.364", "Selenga", "Selenga", null,
                null,
                "/ImspTQPwCqd/O6uvpzGd5pu/KctpIIucige", "1970-01-01T00:00:00.000", null, 3,
                "O6uvpzGd5pu");
        //Yengema CHP:
        assertThatCursor(organisationUnitCursor).hasRow("EFTcruJcNmZ", "OU_636", "Yengema CHP",
                "Yengema CHP",
                "2012-02-17T15:54:39.987", "2014-11-25T09:37:53.553", "Yengema CHP", "Yengema CHP",
                null, null,
                "/ImspTQPwCqd/O6uvpzGd5pu/BGGmAwx33dj/EFTcruJcNmZ", "1994-01-01T00:00:00.000", null,
                4, "BGGmAwx33dj");
        //Wallehun MCHP:
        assertThatCursor(organisationUnitCursor).hasRow("tZxqVn3xNrA", "OU_678886", "Wallehun MCHP",
                "Wallehun MCHP",
                "2012-02-17T15:54:39.987", "2014-11-25T09:37:54.900", "Wallehun MCHP",
                "Wallehun MCHP", null, null,
                "/ImspTQPwCqd/O6uvpzGd5pu/BGGmAwx33dj/tZxqVn3xNrA", "2010-01-01T00:00:00.000", null,
                4, "BGGmAwx33dj");
        //Link tables:
        assertThatCursor(userOrganisationUnitCursor).hasRow("user_uid", "O6uvpzGd5pu");
        assertThatCursor(userOrganisationUnitCursor).hasRow("user_uid", "YmmeuGbqOwR");
        assertThatCursor(userOrganisationUnitCursor).hasRow("user_uid", "KctpIIucige");
        assertThatCursor(userOrganisationUnitCursor).hasRow("user_uid", "EFTcruJcNmZ");
        assertThatCursor(userOrganisationUnitCursor).hasRow("user_uid",
                "tZxqVn3xNrA").isExhausted();

        // TODO: make sure this date is correctly formated:
        //assertThatCursor(resourceCursor).hasRow(ORGANISATION_UNIT, dateString);

    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();
        server.shutdown();
    }

/*//TODO: consider testing these cases when we decide to write more thorough Integration Tests:
    @Test
    public void call_shouldReturnCorrectOrganisationUnitModel() {
    }

    @Test
    public void call_shouldReturnCorrectOrganisationUnitTreeModel() {
    }
        @Test
    public void call_shouldInsertOrganisationUnitInDatabase() {
    }

    @Test
    public void call_shouldUpdateOrganisationUnitInDatabase() {
    }

    @Test
    public void call_shouldDeleteOrganisationUnitInDatabase() {
    }*/
}
