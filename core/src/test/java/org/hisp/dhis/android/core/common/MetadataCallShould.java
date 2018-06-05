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

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
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

import retrofit2.Response;

import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.same;
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
    private DataElement dataElement;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private UserRole userRole;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private Payload<Category> categoryPayload;

    @Mock
    private Payload<CategoryCombo> categoryComboPayload;

    @Mock
    private Payload<OrganisationUnit> organisationUnitPayload;

    @Mock
    private Payload<Program> programPayload;

    @Mock
    private Payload<DataElement> dataElementPayload;

    @Mock
    private Category category;

    @Mock
    private CategoryCombo categoryCombo;

    @Mock
    private Call<SystemInfo> systemInfoEndpointCall;

    @Mock
    private Call<SystemSetting> systemSettingEndpointCall;

    @Mock
    private Call<User> userCall;

    @Mock
    private Call<Response<Payload<Category>>> categoryEndpointCall;

    @Mock
    private Call<Response<Payload<CategoryCombo>>> categoryComboEndpointCall;

    @Mock
    private Call<Response<Payload<Program>>> programParentCall;

    @Mock
    private Call<Response> dataSetParentCall;

    @Mock
    private Call<Response<Payload<OrganisationUnit>>> organisationUnitEndpointCall;

    @Mock
    private BasicCallFactory<SystemInfo> systemInfoCallFactory;

    @Mock
    private GenericCallFactory<SystemSetting> systemSettingCallFactory;

    @Mock
    private GenericCallFactory<User> userCallFactory;

    @Mock
    private SimpleCallFactory<Payload<Category>> categoryCallFactory;

    @Mock
    private SimpleCallFactory<Payload<CategoryCombo>> categoryComboCallFactory;

    @Mock
    private SimpleCallFactory<Payload<Program>> programParentCallFactory;

    @Mock
    private OrganisationUnitCall.Factory organisationUnitCallFactory;

    @Mock
    private DataSetParentCall.Factory dataSetParentCallFactory;

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

        // Payloads
        when(categoryPayload.items()).thenReturn(Collections.singletonList(category));
        when(categoryComboPayload.items()).thenReturn(Collections.singletonList(categoryCombo));
        when(organisationUnitPayload.items()).thenReturn(Collections.singletonList(organisationUnit));
        when(dataElementPayload.items()).thenReturn(Collections.singletonList(dataElement));

        // Call factories
        when(systemInfoCallFactory.create(databaseAdapter, retrofit)).thenReturn(systemInfoEndpointCall);
        when(systemSettingCallFactory.create(any(GenericCallData.class))).thenReturn(systemSettingEndpointCall);
        when(userCallFactory.create(any(GenericCallData.class))).thenReturn(userCall);
        when(programParentCallFactory.create(any(GenericCallData.class))).thenReturn(programParentCall);
        when(categoryCallFactory.create(any(GenericCallData.class))).thenReturn(categoryEndpointCall);
        when(categoryComboCallFactory.create(any(GenericCallData.class))).thenReturn(categoryComboEndpointCall);
        when(organisationUnitCallFactory.create(any(GenericCallData.class), same(user), anySetOf(String.class)))
                .thenReturn(organisationUnitEndpointCall);
        when(dataSetParentCallFactory.create(same(user), any(GenericCallData.class), any(List.class)))
                .thenReturn(dataSetParentCall);

        // Calls
        when(systemInfoEndpointCall.call()).thenReturn(systemInfo);
        when(systemSettingEndpointCall.call()).thenReturn(systemSetting);
        when(userCall.call()).thenReturn(user);
        when(categoryEndpointCall.call()).thenReturn(Response.success(categoryPayload));
        when(categoryComboEndpointCall.call()).thenReturn(Response.success(categoryComboPayload));
        when(programParentCall.call()).thenReturn(Response.success(programPayload));
        when(organisationUnitEndpointCall.call()).thenReturn(Response.success(organisationUnitPayload));
        when(dataSetParentCall.call()).thenReturn(Response.success(dataElementPayload));

        // Metadata call
        metadataCall = new MetadataCall(
                databaseAdapter,
                retrofit,
                systemInfoCallFactory,
                systemSettingCallFactory,
                userCallFactory,
                categoryCallFactory,
                categoryComboCallFactory,
                programParentCallFactory,
                organisationUnitCallFactory,
                dataSetParentCallFactory);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    public void succeed_when_endpoint_calls_succeed() throws Exception {
        Response response = metadataCall.call();
        assertTrue(response.isSuccessful());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void return_last_response_items() throws Exception {
        Response response = metadataCall.call();
        Payload<DataElement> payload = (Payload<DataElement>) response.body();
        assertTrue(!payload.items().isEmpty());
        assertThat(payload.items().get(0)).isEqualTo(dataElement);
    }

    /* TODO will be fixed in ANDROSDK-186
    @Test
    @SuppressWarnings("unchecked")
    public void fail_when_system_info_call_fail() throws Exception {
        when(systemInfoEndpointCall.call()).thenThrow(d2CallException);
        verifyFail(metadataCall.call());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fail_when_system_setting_call_fail() throws Exception {
        when(systemInfoEndpointCall.call()).thenThrow(d2CallException);
        verifyFail(metadataCall.call());
    }*/

    /* TODO will be fixed in ANDROSDK-186
    @Test
    @SuppressWarnings("unchecked")
    public void fail_when_user_call_fail() throws Exception {
        when(userCall.call()).thenThrow(d2CallException);
        verifyFail(metadataCall.call());
    }*/

    @Test
    @SuppressWarnings("unchecked")
    public void fail_when_category_call_fail() throws Exception {
        when(categoryEndpointCall.call()).thenReturn(errorResponse);
        verifyFail(metadataCall.call());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fail_when_category_combo_call_fail() throws Exception {
        when(categoryComboEndpointCall.call()).thenReturn(errorResponse);
        verifyFail(metadataCall.call());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fail_when_program_call_fail() throws Exception {
        when(programParentCall.call()).thenReturn(errorResponse);
        verifyFail(metadataCall.call());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fail_when_organisation_unit_call_fail() throws Exception {
        when(organisationUnitEndpointCall.call()).thenReturn(errorResponse);
        verifyFail(metadataCall.call());
    }

    @Test
    public void fail_when_dataset_parent_call_call_fail() throws Exception {
        when(dataSetParentCall.call()).thenReturn(errorResponse);
        verifyFail(metadataCall.call());
    }
}
