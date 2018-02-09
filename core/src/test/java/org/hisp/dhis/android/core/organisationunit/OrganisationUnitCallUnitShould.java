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

import static junit.framework.Assert.fail;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.Cursor;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
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
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class OrganisationUnitCallUnitShould {

    @Mock
    private Cursor cursor;

    @Mock
    private DatabaseAdapter database;

    @Mock
    private Transaction transaction;


    @Mock
    private OrganisationUnitHandler organisationUnitHandler;

    @Mock
    private OrganisationUnitStore organisationUnitStore;

    @Mock
    private UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @Mock
    private OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore;

    @Mock
    private ResourceStore resourceStore;

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

    @Mock
    private OrganisationUnit organisationUnit2;

    @Mock
    private Payload<OrganisationUnit> payload;

    @Mock
    private User user;

    @Mock
    private Date created;

    @Mock
    private Date lastUpdated;

    @Mock
    private Date serverDate;

    //the call we are testing:
    private OrganisationUnitCall organisationUnitCall;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        lastUpdated = new Date();

        //TODO: evaluate if only one org unit would suffice for the testing:
        when(organisationUnit.uid()).thenReturn("orgUnitUid1");
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


        when(database.beginNewTransaction()).thenReturn(transaction);
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        OrganisationUnitHandler organisationUnitHandler = new OrganisationUnitHandler(
                organisationUnitStore,
                userOrganisationUnitLinkStore, organisationUnitProgramLinkStore, resourceHandler);

        organisationUnitCall = new OrganisationUnitCall(user, organisationUnitService, database,
                resourceHandler, serverDate, organisationUnitHandler, "");

        //Return only one organisationUnit.
        when(user.organisationUnits()).thenReturn(Collections.singletonList(organisationUnit));

        when(organisationUnitService.getOrganisationUnits(
                uidCaptor.capture(), fieldsCaptor.capture(), filterCaptor.capture(), descendantsCaptor.capture(),
                pagingCaptor.capture()
        )).thenReturn(retrofitCall);
        when(retrofitCall.execute()).thenReturn(Response.success(payload));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invoke_server_with_correct_parameters() throws Exception {
        when(payload.items()).thenReturn(Collections.singletonList(organisationUnit));

        organisationUnitCall.call();

        assertThat(uidCaptor.getValue()).isEqualTo(organisationUnit.uid());
        assertThat(fieldsCaptor.getValue().fields()).contains(
                OrganisationUnit.uid, OrganisationUnit.code, OrganisationUnit.name,
                OrganisationUnit.displayName, OrganisationUnit.created, OrganisationUnit.lastUpdated,
                OrganisationUnit.shortName, OrganisationUnit.displayShortName,
                OrganisationUnit.description, OrganisationUnit.displayDescription,
                OrganisationUnit.path, OrganisationUnit.openingDate,
                OrganisationUnit.closedDate, OrganisationUnit.level, OrganisationUnit.deleted,
                OrganisationUnit.parent.with(OrganisationUnit.uid), OrganisationUnit.programs.with(Program.uid)
        );
        assertThat(filterCaptor.getValue()).isNull();
        assertThat(descendantsCaptor.getValue()).isTrue();
        assertThat(pagingCaptor.getValue()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invoke_server_with_correct_parameters_with_last_updated() throws Exception {
        String date = "2014-11-25T09:37:53.358";
        when(resourceStore.getLastUpdated(any(ResourceModel.Type.class))).thenReturn(date);
        when(payload.items()).thenReturn(Collections.singletonList(organisationUnit));

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
                OrganisationUnit.programs.with(Program.uid)
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
        } catch (Exception e) {
            assertThat(IOException.class.isInstance(e)).isTrue();

            verify(database, times(1)).beginNewTransaction();
            verify(transaction, times(1)).end();
            verify(transaction, never()).setSuccessful();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_mark_transaction_successful_if_request_fails() throws Exception {
        when(retrofitCall.execute()).thenReturn(Response.<Payload<OrganisationUnit>>error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}")));

        Response<Payload<OrganisationUnit>> response = organisationUnitCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(database, times(1)).beginNewTransaction();
        verify(transaction, times(1)).end();
        verify(transaction, never()).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invoke_store_if_request_succeeds() throws Exception {
        Headers headers = new Headers.Builder().add(HeaderUtils.DATE, lastUpdated.toString()).build();
        when(payload.items()).thenReturn(Collections.singletonList(organisationUnit));
        Response<Payload<OrganisationUnit>> response = Response.success(payload, headers);
        when(retrofitCall.execute()).thenReturn(response);

        organisationUnitCall.call();

        verify(database, times(1)).beginNewTransaction();
        verify(transaction, times(1)).setSuccessful();
        verify(transaction, times(1)).end();

        verify(organisationUnitStore, times(1)).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt()
        );
        verify(resourceStore, times(1)).insert(anyString(), any(Date.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_fail_on_empty_input() {
        when(user.organisationUnits()).thenReturn(null); //no organisation units in user
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
    public void thrown_illegal_state_exception_on_consecutive_calls() {
        try {
            organisationUnitCall.call();
            organisationUnitCall.call();
            fail("Expecting an Exception on Consecutive calls");
        } catch (Exception e) {
            assertThat(IllegalStateException.class.isInstance(e)).isTrue();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mark_as_executed_after_call() {
        when(organisationUnitStore.insert(any(String.class), any(String.class), any(String.class), any(String.class),
                any(Date.class), any(Date.class), any(String.class), any(String.class), any(String.class),
                any(String.class), any(String.class), any(Date.class), any(Date.class), any(String.class),
                any(Integer.class))
        ).thenThrow(IOException.class);

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
