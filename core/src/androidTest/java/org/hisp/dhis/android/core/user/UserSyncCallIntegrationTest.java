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
package org.hisp.dhis.android.core.user;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.utils.HeaderUtils;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserSyncCallIntegrationTest extends AbsStoreTestCase {
    private static final String[] USER_PROJECTION = {
            UserModel.Columns.ID,
            UserModel.Columns.UID,
            UserModel.Columns.CODE,
            UserModel.Columns.NAME,
            UserModel.Columns.DISPLAY_NAME,
            UserModel.Columns.CREATED,
            UserModel.Columns.LAST_UPDATED,
            UserModel.Columns.BIRTHDAY,
            UserModel.Columns.EDUCATION,
            UserModel.Columns.GENDER,
            UserModel.Columns.JOB_TITLE,
            UserModel.Columns.SURNAME,
            UserModel.Columns.FIRST_NAME,
            UserModel.Columns.INTRODUCTION,
            UserModel.Columns.EMPLOYER,
            UserModel.Columns.INTERESTS,
            UserModel.Columns.LANGUAGES,
            UserModel.Columns.EMAIL,
            UserModel.Columns.PHONE_NUMBER,
            UserModel.Columns.NATIONALITY
    };

    private static final String[] USER_CREDENTIALS_PROJECTION = {
            UserCredentialsModel.Columns.ID,
            UserCredentialsModel.Columns.UID,
            UserCredentialsModel.Columns.CODE,
            UserCredentialsModel.Columns.NAME,
            UserCredentialsModel.Columns.DISPLAY_NAME,
            UserCredentialsModel.Columns.CREATED,
            UserCredentialsModel.Columns.LAST_UPDATED,
            UserCredentialsModel.Columns.USERNAME,
            UserCredentialsModel.Columns.USER,
    };

    // using table as a prefix in order to avoid ambiguity in queries against joined tables
    private static final String[] ORGANISATION_UNIT_PROJECTION = {
            OrganisationUnitModel.Columns.ID,
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
            OrganisationUnitModel.Columns.PARENT,
            OrganisationUnitModel.Columns.LEVEL
    };

    private static final String[] USER_ROLE_PROGRAM_LINK_PROJECTION = {
            UserRoleProgramLinkModel.Columns.USER_ROLE,
            UserRoleProgramLinkModel.Columns.PROGRAM
    };

    private static String[] USER_ORGANISATION_UNIT_PROJECTION = {
            UserOrganisationUnitLinkModel.Columns.ID,
            UserOrganisationUnitLinkModel.Columns.USER,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE,
    };

    private static String[] RESOURCE_PROJECTION = {
            ResourceModel.Columns.ID,
            ResourceModel.Columns.RESOURCE_TYPE,
            ResourceModel.Columns.LAST_SYNCED
    };

    private static final String[] USER_ROLE_PROJECTION = {
            UserRoleModel.Columns.UID,
            UserRoleModel.Columns.CODE,
            UserRoleModel.Columns.NAME,
            UserRoleModel.Columns.DISPLAY_NAME,
            UserRoleModel.Columns.CREATED,
            UserRoleModel.Columns.LAST_UPDATED
    };

    private MockWebServer mockWebServer;
    private UserSyncCall userSyncCall;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        MockResponse mockResponse = new MockResponse();
        mockResponse.setHeader(HeaderUtils.DATE, Calendar.getInstance().getTime());

        // JSON payload is returned from this api query:
        // https://play.dhis2.org/dev/api/me.json?fields=id,code,name,displayName,created,lastUpdated,birthday,
        // education,gender,jobTitle,surname,firstName,introduction,employer,interests,languages,email,
        // phoneNumber,nationality,teiSearchOrganisationUnits[id],organisationUnits[id,programs],
        // userCredentials[id,code,name,displayName,created,lastUpdated,username,userRoles[id,programs[id]]]
        mockResponse.setBody("{\n" +
                "\n" +
                "    \"created\": \"2015-03-31T13:31:09.324\",\n" +
                "    \"lastUpdated\": \"2017-02-01T14:32:33.771\",\n" +
                "    \"name\": \"John Barnes\",\n" +
                "    \"id\": \"DXyJmlo9rge\",\n" +
                "    \"displayName\": \"John Barnes\",\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"surname\": \"Barnes\",\n" +
                "    \"email\": \"john@hmail.com\",\n" +
                "    \"userCredentials\": {\n" +
                "        \"lastUpdated\": \"2017-02-01T14:31:54.370\",\n" +
                "        \"code\": \"android\",\n" +
                "        \"created\": \"2015-03-31T13:31:09.206\",\n" +
                "        \"name\": \"John Traore\",\n" +
                "        \"id\": \"M0fCOxtkURr\",\n" +
                "        \"displayName\": \"John Traore\",\n" +
                "        \"username\": \"android\",\n" +
                "        \"userRoles\": [\n" +
                "            {\n" +
                "                \"id\": \"Ufph3mGRmMo\",\n" +
                "                \"programs\": [\n" +
                "                    {\n" +
                "                        \"id\": \"uy2gU8kT1jF\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"q04UBOqq3rp\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"VBqh0ynB2wv\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"eBAyeGv0exc\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"kla3mAPgvCH\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"lxAQ7Zs9VYR\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"IpHINAT79UW\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"WSGAb5XwJ3Y\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"ur1Edk5Oe2n\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": \"fDd25txQckK\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"Euq3XfEIEbx\",\n" +
                "                \"programs\": [ ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"cUlTcejWree\",\n" +
                "                \"programs\": [\n" +
                "                    {\n" +
                "                        \"id\": \"ur1Edk5Oe2n\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"DRdaVRtwmG5\",\n" +
                "                \"programs\": [\n" +
                "                    {\n" +
                "                        \"id\": \"eBAyeGv0exc\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"jRWSNIHdKww\",\n" +
                "                \"programs\": [ ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"txB7vu1w2Pr\",\n" +
                "                \"programs\": [ ]\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"teiSearchOrganisationUnits\": [\n" +
                "        {\n" +
                "            \"id\": \"WAjjFMDJKcx\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"organisationUnits\": [\n" +
                "        {\n" +
                "            \"id\": \"DiszpKrYNg8\",\n" +
                "            \"programs\": [\n" +
                "                {\n" +
                "                    \"id\": \"uy2gU8kT1jF\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"q04UBOqq3rp\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"VBqh0ynB2wv\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"eBAyeGv0exc\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"kla3mAPgvCH\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"lxAQ7Zs9VYR\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"IpHINAT79UW\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"WSGAb5XwJ3Y\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"ur1Edk5Oe2n\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"fDd25txQckK\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "\n" +
                "}");

        mockWebServer.enqueue(mockResponse);

        // ToDo: consider moving this out
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        UserSyncService userSyncService = retrofit.create(UserSyncService.class);

        OrganisationUnitStore organisationUnitStore = new OrganisationUnitStoreImpl(database());
        UserOrganisationUnitLinkStore userOrganisationUnitStore = new UserOrganisationUnitLinkStoreImpl(database());
        UserCredentialsStore userCredentialsStore = new UserCredentialsStoreImpl(database());
        UserRoleStore userRoleStore = new UserRoleStoreImpl(database());
        UserStore userStore = new UserStoreImpl(database());
        UserRoleProgramLinkStore userRoleProgramLinkStore = new UserRoleProgramLinkStoreImpl(database());
        ResourceStore resourceStore = new ResourceStoreImpl(database());

        userSyncCall = new UserSyncCall(userSyncService, database(), organisationUnitStore,
                userOrganisationUnitStore, userCredentialsStore, userRoleStore,
                userStore, userRoleProgramLinkStore, resourceStore);

    }

    @Test
    public void stub_test() throws Exception {
        // dummy test

    }

    // this test is commented out until we finish sync the program sub graph.
    // This test will break since we try to insert userRoleProgramLink without having programs.
//    @Test
    public void call_shouldPersistInDatabase() throws Exception {
        userSyncCall.call();

        Cursor userCursor = database().query(UserModel.TABLE, USER_PROJECTION, null, null, null, null, null);
        Cursor userCredentialsCursor = database().query(UserCredentialsModel.TABLE, USER_CREDENTIALS_PROJECTION,
                null, null, null, null, null);
        Cursor userRoleCursor = database().query(UserRoleModel.TABLE, USER_ROLE_PROJECTION,
                null, null, null, null, null);
        Cursor organisationUnitCursor = database().query(OrganisationUnitModel.TABLE, ORGANISATION_UNIT_PROJECTION,
                null, null, null, null, null);
        Cursor userOrganisationUnitCursor = database().query(
                UserOrganisationUnitLinkModel.TABLE, USER_ORGANISATION_UNIT_PROJECTION, null, null, null, null, null
        );

        Cursor userRoleProgramCursor = database().query(
                UserRoleProgramLinkModel.TABLE, USER_ROLE_PROGRAM_LINK_PROJECTION, null, null, null, null, null
        );

        Cursor resourceProjection = database().query(ResourceModel.TABLE, RESOURCE_PROJECTION,
                null, null, null, null, null);

        assertThatCursor(userCursor).hasRow(
                1L,
                "DXyJmlo9rge",
                null,
                "John Barnes",
                "John Barnes",
                "2015-03-31T13:31:09.324",
                "2017-02-01T14:32:33.771",
                null,
                null,
                null,
                null,
                "Barnes",
                "John",
                null,
                null,
                null,
                null,
                "john@hmail.com",
                null,
                null
        ).isExhausted();

    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();
        mockWebServer.shutdown();
    }
}
