/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.organisationunit.internal;

import android.content.ContentValues;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserTableInfo;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class OrganisationUnitCallMockIntegrationShould extends BaseMockIntegrationTestEmptyEnqueable {
    
    //The return of the organisationUnitCall to be tested:
    private Callable<Unit> organisationUnitCall;

    private OrganisationUnit expectedAfroArabicClinic = OrganisationUnitSamples.getAfroArabClinic();
    private OrganisationUnit expectedAdonkiaCHP = OrganisationUnitSamples.getAdonkiaCHP();

    public OrganisationUnitCallMockIntegrationShould() throws ParseException {
    }

    @Before
    public void setUp() throws IOException {
        dhis2MockServer.enqueueMockResponse("organisationunit/admin_organisation_units.json");

        OrganisationUnit orgUnit = OrganisationUnit.builder().uid("O6uvpzGd5pu").path("/ImspTQPwCqd/O6uvpzGd5pu").build();
        List<OrganisationUnit> organisationUnits = Collections.singletonList(orgUnit);
        //dependencies for the OrganisationUnitCall:
        OrganisationUnitService organisationUnitService = d2.retrofit().create(OrganisationUnitService.class);

        // Create a user with the root as assigned organisation unit (for the test):
        User user = User.builder().uid("user_uid").organisationUnits(organisationUnits).build();
        database.insert(UserTableInfo.TABLE_INFO.name(), null, user.toContentValues());

        ContentValues userContentValues = new ContentValues();
        userContentValues.put(BaseIdentifiableObjectModel.Columns.UID, "user_uid");
        database.insert(UserTableInfo.TABLE_INFO.name(), null, userContentValues);

        // inserting programs for creating OrgUnitProgramLinks
        String programUid = "lxAQ7Zs9VYR";
        insertProgramWithUid(programUid);
        Set<String> programUids = Sets.newHashSet(Lists.newArrayList(programUid));

        OrganisationUnitHandler organisationUnitHandler =
                OrganisationUnitHandlerImpl.create(databaseAdapter);

        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(databaseAdapter);

        organisationUnitCall = new OrganisationUnitCallFactory(organisationUnitService,
                organisationUnitHandler, apiCallExecutor, objects.resourceHandler).create(user, programUids, null);
    }

    private void insertProgramWithUid(String uid) {
        ContentValues program = new ContentValues();
        program.put(BaseIdentifiableObjectModel.Columns.UID, uid);
        database.insert(ProgramTableInfo.TABLE_INFO.name(), null, program);
    }

    @Test
    public void persist_organisation_unit_tree() throws Exception {
        organisationUnitCall.call();

        IdentifiableObjectStore<OrganisationUnit> organisationUnitStore = OrganisationUnitStore.create(databaseAdapter);
        OrganisationUnit dbAfroArabicClinic = organisationUnitStore.selectByUid(expectedAfroArabicClinic.uid());
        OrganisationUnit dbAdonkiaCHP = organisationUnitStore.selectByUid(expectedAdonkiaCHP.uid());

        assertThat(expectedAfroArabicClinic).isEqualTo(dbAfroArabicClinic.toBuilder().id(null).build());
        assertThat(expectedAdonkiaCHP).isEqualTo(dbAdonkiaCHP.toBuilder().id(null).build());
    }

    @Test
    public void persist_organisation_unit_user_links() throws Exception {
        organisationUnitCall.call();

        UserOrganisationUnitLinkStore userOrganisationUnitStore = UserOrganisationUnitLinkStoreImpl.create(databaseAdapter);
        List<UserOrganisationUnitLink> userOrganisationUnitLinks = userOrganisationUnitStore.selectAll();

        Set<String> linkOrganisationUnits = new HashSet<>(2);
        for (UserOrganisationUnitLink userOrganisationUnitLink: userOrganisationUnitLinks) {
            assertThat(userOrganisationUnitLink.user()).isEqualTo("user_uid");
            linkOrganisationUnits.add(userOrganisationUnitLink.organisationUnit());
        }

        assertThat(linkOrganisationUnits.contains(expectedAfroArabicClinic.uid())).isTrue();
        assertThat(linkOrganisationUnits.contains(expectedAdonkiaCHP.uid())).isTrue();
    }
}