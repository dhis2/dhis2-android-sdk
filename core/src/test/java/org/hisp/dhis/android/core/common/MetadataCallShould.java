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
package org.hisp.dhis.android.core.common;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.hisp.dhis.android.core.arch.modules.Downloader;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.calls.factories.GenericCallFactory;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboUidsSeeker;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.organisationunit.SearchOrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.Authority;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserDownloadModule;
import org.hisp.dhis.android.core.user.UserRole;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MetadataCallShould extends BaseCallShould {

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private SystemSetting systemSetting;

    @Mock
    private Date serverDateTime;

    @Mock
    private User user;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private UserRole userRole;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private Authority authority;

    @Mock
    private Category category;

    @Mock
    private CategoryCombo categoryCombo;

    @Mock
    private Program program;

    @Mock
    private DataSet dataSet;

    @Mock
    private Call<SystemInfo> systemInfoEndpointCall;

    @Mock
    private Call<SystemSetting> systemSettingEndpointCall;

    @Mock
    private Call<User> userCall;

    @Mock
    private Call<List<Authority>> authorityEndpointCall;

    @Mock
    private Call<List<Category>> categoryEndpointCall;

    @Mock
    private Call<List<CategoryCombo>> categoryComboEndpointCall;

    @Mock
    private Call<List<Program>> programParentCall;

    @Mock
    private Call<List<DataSet>> dataSetParentCall;

    @Mock
    private Call<List<OrganisationUnit>> organisationUnitEndpointCall;

    @Mock
    private Call<List<OrganisationUnit>> searchOrganisationUnitCall;

    @Mock
    private Downloader<SystemInfo> systemInfoCallDownloader;

    @Mock
    private Downloader<SystemSetting> systemSettingDownloader;

    @Mock
    private UserDownloadModule userDownloadModule;

    @Mock
    private UidsCallFactory<Category> categoryCallFactory;

    @Mock
    private UidsCallFactory<CategoryCombo> categoryComboCallFactory;

    @Mock
    private CategoryComboUidsSeeker categoryComboUidsSeeker;

    @Mock
    private GenericCallFactory<List<Program>> programParentCallFactory;

    @Mock
    private OrganisationUnitCall.Factory organisationUnitCallFactory;

    @Mock
    private SearchOrganisationUnitCall.Factory searchOrganisationUnitCallFactory;

    @Mock
    private GenericCallFactory<List<DataSet>> dataSetParentCallFactory;

    @Mock
    private ForeignKeyCleaner foreignKeyCleaner;

    // object to test
    private MetadataCall metadataCall;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        // Payload data
        when(systemInfo.serverDate()).thenReturn(serverDateTime);
        when(userCredentials.userRoles()).thenReturn(Collections.singletonList(userRole));
        when(organisationUnit.uid()).thenReturn("unit");
        when(organisationUnit.path()).thenReturn("path/to/org/unit");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.organisationUnits()).thenReturn(Collections.singletonList(organisationUnit));

        // Call factories
        when(systemInfoCallDownloader.download()).thenReturn(systemInfoEndpointCall);
        when(systemSettingDownloader.download()).thenReturn(systemSettingEndpointCall);
        when(userDownloadModule.downloadUser()).thenReturn(userCall);
        when(userDownloadModule.downloadAuthority()).thenReturn(authorityEndpointCall);
        when(programParentCallFactory.create(any(GenericCallData.class))).thenReturn(programParentCall);
        when(categoryCallFactory.create(anySetOf(String.class))).thenReturn(categoryEndpointCall);
        when(categoryComboCallFactory.create(anySetOf(String.class))).thenReturn(categoryComboEndpointCall);
        when(organisationUnitCallFactory.create(any(GenericCallData.class), same(user), anySetOf(String.class),
                anySetOf(String.class))).thenReturn(organisationUnitEndpointCall);
        when(searchOrganisationUnitCallFactory.create(any(GenericCallData.class), same(user))).thenReturn(
                searchOrganisationUnitCall);
        when(dataSetParentCallFactory.create(any(GenericCallData.class))).thenReturn(dataSetParentCall);

        // Calls
        when(systemInfoEndpointCall.call()).thenReturn(systemInfo);
        when(systemSettingEndpointCall.call()).thenReturn(systemSetting);
        when(userCall.call()).thenReturn(user);
        when(authorityEndpointCall.call()).thenReturn(Lists.newArrayList(authority));
        when(categoryEndpointCall.call()).thenReturn(Lists.newArrayList(category));
        when(categoryComboEndpointCall.call()).thenReturn(Lists.newArrayList(categoryCombo));
        when(programParentCall.call()).thenReturn(Lists.newArrayList(program));
        when(organisationUnitEndpointCall.call()).thenReturn(Lists.newArrayList(organisationUnit));
        when(searchOrganisationUnitCall.call()).thenReturn(Lists.newArrayList(organisationUnit));
        when(dataSetParentCall.call()).thenReturn(Lists.newArrayList(dataSet));
        when(categoryComboUidsSeeker.seekUids()).thenReturn(
                Sets.newHashSet(Lists.newArrayList("category_combo_uid")));

        // Metadata call
        metadataCall = new MetadataCall(
                genericCallData,
                systemInfoCallDownloader,
                systemSettingDownloader,
                userDownloadModule,
                categoryCallFactory,
                categoryComboCallFactory,
                categoryComboUidsSeeker,
                programParentCallFactory,
                organisationUnitCallFactory,
                searchOrganisationUnitCallFactory,
                dataSetParentCallFactory,
                foreignKeyCleaner);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    public void succeed_when_endpoint_calls_succeed() throws Exception {
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_system_info_call_fail() throws Exception {
        when(systemInfoEndpointCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_system_setting_call_fail() throws Exception {
        when(systemSettingEndpointCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_user_call_fail() throws Exception {
        when(userCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_authority_call_fail() throws Exception {
        when(authorityEndpointCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_category_call_fail() throws Exception {
        when(categoryEndpointCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_category_combo_call_fail() throws Exception {
        when(categoryComboEndpointCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_program_call_fail() throws Exception {
        when(programParentCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_organisation_unit_call_fail() throws Exception {
        when(organisationUnitEndpointCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_search_organisation_unit_call_fail() throws Exception {
        when(searchOrganisationUnitCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test(expected = D2Error.class)
    public void fail_when_dataset_parent_call_fail() throws Exception {
        when(dataSetParentCall.call()).thenThrow(d2Error);
        metadataCall.call();
    }

    @Test
    public void call_foreign_key_cleaner() throws Exception {
        metadataCall.call();
        verify(foreignKeyCleaner).cleanForeignKeyErrors();
    }
}