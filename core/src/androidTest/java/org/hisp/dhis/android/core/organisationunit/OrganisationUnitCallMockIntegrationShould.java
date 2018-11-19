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
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitCallMockIntegrationShould extends AbsStoreTestCase {
    private static String[] RESOURCE_PROJECTION = {
            ResourceModel.Columns.RESOURCE_TYPE,
            ResourceModel.Columns.LAST_SYNCED
    };

    private Dhis2MockServer dhis2MockServer;
    private GenericCallData genericCallData;

    //The return of the organisationUnitCall to be tested:
    private Call<List<OrganisationUnit>> organisationUnitCall;

    private OrganisationUnit expectedAfroArabicClinic = OrganisationUnitSamples.getAfroArabClinic();
    private OrganisationUnit expectedAdonkiaCHP = OrganisationUnitSamples.getAdonkiaCHP();

    public OrganisationUnitCallMockIntegrationShould() throws ParseException {
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        dhis2MockServer.enqueueMockResponse("organisationunit/admin_organisation_units.json");

        OrganisationUnit orgUnit = OrganisationUnit.builder().uid("O6uvpzGd5pu").path("/ImspTQPwCqd/O6uvpzGd5pu").build();
        List<OrganisationUnit> organisationUnits = Collections.singletonList(orgUnit);
        //dependencies for the OrganisationUnitCall:
        OrganisationUnitService organisationUnitService = d2.retrofit().create(OrganisationUnitService.class);

        // Create a user with the root as assigned organisation unit (for the test):
        User user = User.builder().uid("user_uid").organisationUnits(organisationUnits).build();
        database().insert(UserModel.TABLE, null, user.toContentValues());

        ContentValues userContentValues = new ContentValues();
        userContentValues.put(UserModel.Columns.ID, "user_uid");
        database().insert(UserModel.TABLE, null, userContentValues);

        // inserting programs for creating OrgUnitProgramLinks
        String programUid = "lxAQ7Zs9VYR";
        insertProgramWithUid(programUid);
        Set<String> programUids = Sets.newHashSet(Lists.newArrayList(programUid));

        SyncHandlerWithTransformer<OrganisationUnit> organisationUnitHandler =
                OrganisationUnitHandler.create(databaseAdapter(), programUids, null,
                        OrganisationUnit.Scope.SCOPE_DATA_CAPTURE, user);

        genericCallData = getGenericCallData(d2);
        organisationUnitCall = new OrganisationUnitCall(user, organisationUnitService,
                genericCallData, organisationUnitHandler);
    }

    private void insertProgramWithUid(String uid) {
        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, uid);
        database().insert(ProgramModel.TABLE, null, program);
    }

    @Test
    public void persist_organisation_unit_tree() throws Exception {
        organisationUnitCall.call();

        IdentifiableObjectStore<OrganisationUnit> organisationUnitStore = OrganisationUnitStore.create(databaseAdapter());
        OrganisationUnit dbAfroArabicClinic = organisationUnitStore.selectByUid(expectedAfroArabicClinic.uid());
        OrganisationUnit dbAdonkiaCHP = organisationUnitStore.selectByUid(expectedAdonkiaCHP.uid());

        assertThat(expectedAfroArabicClinic).isEqualTo(dbAfroArabicClinic.toBuilder().id(null).build());
        assertThat(expectedAdonkiaCHP).isEqualTo(dbAdonkiaCHP.toBuilder().id(null).build());
    }

    @Test
    public void persist_organisation_unit_user_links() throws Exception {
        organisationUnitCall.call();

        UserOrganisationUnitLinkStoreInterface userOrganisationUnitStore = UserOrganisationUnitLinkStore.create(databaseAdapter());
        List<UserOrganisationUnitLinkModel> linkList = userOrganisationUnitStore.selectAll();

        Set<String> linkOrganisationUnits = new HashSet<>(2);
        for (UserOrganisationUnitLinkModel linkModel: linkList) {
            assertThat(linkModel.user()).isEqualTo("user_uid");
            linkOrganisationUnits.add(linkModel.organisationUnit());
        }

        assertThat(linkOrganisationUnits.contains(expectedAfroArabicClinic.uid())).isTrue();
        assertThat(linkOrganisationUnits.contains(expectedAdonkiaCHP.uid())).isTrue();
    }

    @Test
    public void update_resource_handler() throws Exception {
       organisationUnitCall.call();

        Cursor resourceCursor = database().query(ResourceModel.TABLE,
                RESOURCE_PROJECTION, null, null, null, null, null);

        assertThatCursor(resourceCursor).hasRow(ResourceModel.Type.ORGANISATION_UNIT,
                BaseIdentifiableObject.DATE_FORMAT.format(genericCallData.serverDate()));

        resourceCursor.close();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }
}