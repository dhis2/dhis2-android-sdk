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

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

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

    private Dhis2MockServer dhis2MockServer;
    private GenericCallData genericCallData;

    //The return of the organisationUnitCall to be tested:
    private Call<List<OrganisationUnit>> organisationUnitCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();


        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        dhis2MockServer.enqueueMockResponse("admin/organisation_units.json");

        List<OrganisationUnit> organisationUnits = Collections.singletonList(OrganisationUnit.create("O6uvpzGd5pu",
                null, null, null, null, null, null, null, null, null, null, "/ImspTQPwCqd/O6uvpzGd5pu", null, null,
                null, null, null, null, false));
        //dependencies for the OrganisationUnitCall:
        OrganisationUnitService organisationUnitService = d2.retrofit().create(OrganisationUnitService.class);

        // Create a user with the root as assigned organisation unit (for the test):
        User user = User.builder().uid("user_uid").organisationUnits(organisationUnits).build();

        ContentValues userContentValues = new ContentValues();
        userContentValues.put(UserModel.Columns.ID, "user_uid");
        database().insert(UserModel.TABLE, null, userContentValues);

        // inserting programs for creating OrgUnitProgramLinks

        String programUid = "uy2gU8kT1jF";
        String programUid1 = "q04UBOqq3rp";
        String programUid2 = "VBqh0ynB2wv";
        String programUid3 = "eBAyeGv0exc";
        String programUid4 = "kla3mAPgvCH";
        String programUid5 = "lxAQ7Zs9VYR";
        String programUid6 = "IpHINAT79UW";
        String programUid7 = "WSGAb5XwJ3Y";
        String programUid8 = "ur1Edk5Oe2n";

        insertProgramWithUid(programUid);
        insertProgramWithUid(programUid1);
        insertProgramWithUid(programUid2);
        insertProgramWithUid(programUid3);
        insertProgramWithUid(programUid4);
        insertProgramWithUid(programUid5);
        insertProgramWithUid(programUid6);
        insertProgramWithUid(programUid7);
        insertProgramWithUid(programUid8);

        Set<String> programUids = Sets.newHashSet(Lists.newArrayList(programUid, programUid1, programUid2,
                programUid3, programUid4, programUid5, programUid6, programUid7, programUid8));

        GenericHandler<OrganisationUnit, OrganisationUnitModel> organisationUnitHandler =
                OrganisationUnitHandler.create(databaseAdapter(), programUids,
                        OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, user);

        genericCallData = GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date());
        organisationUnitCall = new OrganisationUnitCall(user, organisationUnitService,
                genericCallData, organisationUnitHandler);
    }

    private void insertProgramWithUid(String uid) {
        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, uid);
        database().insert(ProgramModel.TABLE, null, program);
    }

    @Test
    public void persist_organisation_unit_tree_in_data_base_after_call() throws Exception {
        //Insert User in the User tables, such that UserOrganisationUnitLink's foreign key is satisfied:
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

        assertThatCursor(organisationUnitCursor).hasRow("Rp268JB6Ne4", "OU_651071", "Adonkia CHP",
                "Adonkia CHP", "2012-02-17T15:54:39.987", "2017-05-22T15:21:48.515", "Adonkia CHP",
                "Adonkia CHP", null, null, "/ImspTQPwCqd/at6UHUQatSo/qtr8GGlm4gg/Rp268JB6Ne4",
                "2010-01-01T00:00:00.000", null, 4, "qtr8GGlm4gg");

        assertThatCursor(organisationUnitCursor).hasRow("cDw53Ej8rju", "OU_278371", "Afro Arab Clinic",
                "Afro Arab Clinic", "2012-02-17T15:54:39.987", "2017-05-22T15:21:48.518", "Afro Arab Clinic",
                "Afro Arab Clinic", null, null, "/ImspTQPwCqd/at6UHUQatSo/qtr8GGlm4gg/cDw53Ej8rju",
                "2008-01-01T00:00:00.000", null, 4, "qtr8GGlm4gg");

        //Link tables:
        assertThatCursor(userOrganisationUnitCursor).hasRow("user_uid", "Rp268JB6Ne4");
        assertThatCursor(userOrganisationUnitCursor).hasRow("user_uid", "cDw53Ej8rju").isExhausted();

        assertThatCursor(resourceCursor).hasRow(ResourceModel.Type.ORGANISATION_UNIT,
                BaseIdentifiableObject.DATE_FORMAT.format(genericCallData.serverDate()));
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
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
