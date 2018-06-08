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

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2ErrorCode;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.utils.HeaderUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganisationUnitCallUnitShould {

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private Transaction transaction;

    //Mock return value of the mock service:
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<OrganisationUnit>> retrofitCall;

    @Mock
    private OrganisationUnitService organisationUnitService;

    //Captors for the organisationUnitService arguments:
    @Captor
    private ArgumentCaptor<String> uidCaptor;

    @Captor
    private ArgumentCaptor<Fields<OrganisationUnit>> fieldsCaptor;

    @Captor
    private ArgumentCaptor<Filter<OrganisationUnit, String>> filterCaptor;

    @Captor
    private ArgumentCaptor<Boolean> descendantsCaptor;

    @Captor
    private ArgumentCaptor<Boolean> pagingCaptor;


    @Mock
    private OrganisationUnit organisationUnit;

    private List<OrganisationUnit> organisationUnits;

    @Mock
    private Payload<OrganisationUnit> payload;

    @Mock
    private User user;

    @Mock
    private Date created;

    @Mock
    private Date lastUpdated;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private GenericCallData genericCallData;

    @Mock
    private GenericHandler<OrganisationUnit, OrganisationUnitModel> organisationUnitHandler;

    //the call we are testing:
    private OrganisationUnitCall organisationUnitCall;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        lastUpdated = new Date();

        //TODO: evaluate if only one org unit would suffice for the testing:
        String orgUnitUid = "orgUnitUid1";
        when(organisationUnit.uid()).thenReturn(orgUnitUid);
        when(organisationUnit.code()).thenReturn("organisation_unit_code");
        when(organisationUnit.name()).thenReturn("organisation_unit_name");
        when(organisationUnit.displayName()).thenReturn("organisation_unit_display_name");
        when(organisationUnit.deleted()).thenReturn(false);
        when(organisationUnit.created()).thenReturn(created);
        when(organisationUnit.lastUpdated()).thenReturn(lastUpdated);
        when(organisationUnit.shortName()).thenReturn("organisation_unit_short_name");
        when(organisationUnit.displayShortName()).thenReturn("organisation_unit_display_short_name");
        when(organisationUnit.description()).thenReturn("organisation_unit_description");
        when(organisationUnit.displayDescription()).thenReturn("organisation_unit_display_description");
        when(organisationUnit.path()).thenReturn("/root/orgUnitUid1");
        when(organisationUnit.openingDate()).thenReturn(created);
        when(organisationUnit.closedDate()).thenReturn(lastUpdated);
        when(organisationUnit.level()).thenReturn(4);
        when(organisationUnit.parent()).thenReturn(null);

        when(user.uid()).thenReturn("user_uid");
        when(user.code()).thenReturn("user_code");
        when(user.name()).thenReturn("user_name");
        when(user.displayName()).thenReturn("user_display_name");
        when(user.created()).thenReturn(created);
        when(user.lastUpdated()).thenReturn(lastUpdated);
        when(user.birthday()).thenReturn("user_birthday");
        when(user.education()).thenReturn("user_education");
        when(user.gender()).thenReturn("user_gender");
        when(user.jobTitle()).thenReturn("user_job_title");
        when(user.surname()).thenReturn("user_surname");
        when(user.firstName()).thenReturn("user_first_name");
        when(user.introduction()).thenReturn("user_introduction");
        when(user.employer()).thenReturn("user_employer");
        when(user.interests()).thenReturn("user_interests");
        when(user.languages()).thenReturn("user_languages");
        when(user.email()).thenReturn("user_email");
        when(user.phoneNumber()).thenReturn("user_phone_number");
        when(user.nationality()).thenReturn("user_nationality");


        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        organisationUnitCall = new OrganisationUnitCall(user, organisationUnitService,
                genericCallData, organisationUnitHandler);

        //Return only one organisationUnit.
        organisationUnits = Collections.singletonList(organisationUnit);
        when(user.organisationUnits()).thenReturn(organisationUnits);

        when(organisationUnitService.getOrganisationUnitWithDescendants(
                uidCaptor.capture(), fieldsCaptor.capture(), filterCaptor.capture(), descendantsCaptor.capture(),
                pagingCaptor.capture()
        )).thenReturn(retrofitCall);
        when(retrofitCall.execute()).thenReturn(Response.success(payload));

        when(payload.items()).thenReturn(organisationUnits);

        when(genericCallData.resourceHandler()).thenReturn(resourceHandler);
        when(genericCallData.databaseAdapter()).thenReturn(databaseAdapter);
        when(resourceHandler.getLastUpdated(ResourceModel.Type.ORGANISATION_UNIT)).thenReturn("lastUpdated");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invoke_server_with_correct_parameters() throws Exception {
        String date = "2014-11-25T09:37:53.358";
        when(genericCallData.resourceHandler().getLastUpdated(ResourceModel.Type.ORGANISATION_UNIT))
                .thenReturn(date);

        organisationUnitCall.call();

        assertThat(uidCaptor.getValue()).isEqualTo(organisationUnit.uid());
        assertThat(fieldsCaptor.getValue().fields()).contains(
                OrganisationUnit.uid, OrganisationUnit.code, OrganisationUnit.name,
                OrganisationUnit.displayName, OrganisationUnit.created, OrganisationUnit.lastUpdated,
                OrganisationUnit.shortName, OrganisationUnit.displayShortName,
                OrganisationUnit.description, OrganisationUnit.displayDescription,
                OrganisationUnit.path, OrganisationUnit.openingDate,
                OrganisationUnit.closedDate, OrganisationUnit.level, OrganisationUnit.deleted,
                OrganisationUnit.parent.with(OrganisationUnit.uid),
                OrganisationUnit.programs.with(ObjectWithUid.uid)
        );
        Filter<OrganisationUnit, String> filter = filterCaptor.getValue();
        assertThat(filter.operator()).isEqualTo("gt");
        assertThat(filter.field().name()).isEqualTo(OrganisationUnit.lastUpdated.name());
        assertThat(filter.values().size()).isEqualTo(1);
        assertThat(filter.values()).contains(date);
        assertThat(descendantsCaptor.getValue()).isTrue();
        assertThat(pagingCaptor.getValue()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void throw_exception_not_mark_transaction_successful() throws Exception {
        when(retrofitCall.execute()).thenThrow(IOException.class);
        try {
            organisationUnitCall.call();
            fail("Expecting an Exception");
        } catch (D2CallException d2E) {
            assertThat(d2E.errorCode()).isEqualTo(D2ErrorCode.API_RESPONSE_PROCESS_ERROR);
            verify(databaseAdapter, times(1)).beginNewTransaction();
            verify(transaction, times(1)).end();
            verifyNoMoreInteractions(transaction);
        } catch (Exception e) {
            fail("Exception has wrong type");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_mark_transaction_successful_if_request_fails() throws Exception {
        when(retrofitCall.execute()).thenReturn(Response.<Payload<OrganisationUnit>>error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}")));

        try {
            organisationUnitCall.call();
            fail("Call must fail");
        } catch (D2CallException d2E) {
            assertThat(d2E.errorCode()).isEqualTo(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE);
            verify(databaseAdapter, times(1)).beginNewTransaction();
            verify(transaction, times(1)).end();
            verifyNoMoreInteractions(transaction);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invoke_handler_if_request_succeeds() throws Exception {
        Headers headers = new Headers.Builder().add(HeaderUtils.DATE, lastUpdated.toString()).build();
        Response<Payload<OrganisationUnit>> response = Response.success(payload, headers);
        when(retrofitCall.execute()).thenReturn(response);

        organisationUnitCall.call();

        verify(databaseAdapter, times(1)).beginNewTransaction();
        verify(transaction, times(1)).setSuccessful();
        verify(transaction, times(1)).end();

        verify(organisationUnitHandler).handleMany(eq(organisationUnits), any(OrganisationUnitModelBuilder.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_fail_on_empty_input() {
        Set<String> uids = new HashSet<>();
        organisationUnitCall = new OrganisationUnitCall(user, organisationUnitService,
                genericCallData, organisationUnitHandler);

        try {
            organisationUnitCall.call();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception should not be thrown.");
        } finally {
            assertThat(organisationUnitCall.isExecuted()).isTrue();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void thrown_d2_call_exception_on_consecutive_calls() {
        try {
            organisationUnitCall.call();
            organisationUnitCall.call();
            fail("Expecting an Exception on Consecutive calls");
        } catch (Exception e) {
            assertThat(D2CallException.class.isInstance(e)).isTrue();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mark_as_executed_after_call() {
        /*when(organisationUnitHandler.handleMany(anyListOf(OrganisationUnit.class),
        TODO fix
                any(OrganisationUnitModelBuilder.class))
        ).thenThrow(IOException.class);
                 */

        try {
            organisationUnitCall.call();
        } catch (Exception e) {
            fail("Exception should not be thrown.");
        } finally {
            assertThat(organisationUnitCall.isExecuted()).isTrue();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mark_as_executed_after_call_with_io_exception() throws IOException {
        when(retrofitCall.execute()).thenThrow(new IOException());
        try {
            organisationUnitCall.call();
            fail("IOException should be thrown");
        } catch (Exception e) {
            assertThat(organisationUnitCall.isExecuted()).isTrue();
        }
    }
}
