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

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Filter;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserInternalAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganisationUnitCallUnitShould {

    @Mock
    private Payload<OrganisationUnit> organisationUnitPayload;

    @Mock
    private OrganisationUnitService organisationUnitService;

    //Captors for the organisationUnitService arguments:
    @Captor
    private ArgumentCaptor<Fields<OrganisationUnit>> fieldsCaptor;

    @Captor
    private ArgumentCaptor<Filter<OrganisationUnit, String>> filtersCaptor;

    @Captor
    private ArgumentCaptor<Boolean> pagingCaptor;

    @Captor
    private ArgumentCaptor<Integer> pageCaptor;

    @Captor
    private ArgumentCaptor<Integer> pageSizeCaptor;


    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private User user;

    @Mock
    private Date created;

    @Mock
    private Date lastUpdated;

    @Mock
    private OrganisationUnitHandler organisationUnitHandler;

    @Mock
    private OrganisationUnitDisplayPathTransformer organisationUnitDisplayPathTransformer;

    //the call we are testing:
    private Single<List<OrganisationUnit>> organisationUnitCall;

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

        organisationUnitCall = new OrganisationUnitCall(organisationUnitService, organisationUnitHandler,
                organisationUnitDisplayPathTransformer)
                .download(user);

        //Return only one organisationUnit.
        List<OrganisationUnit> organisationUnits = Collections.singletonList(organisationUnit);
        when(UserInternalAccessor.accessOrganisationUnits(user)).thenReturn(new ArrayList<>(organisationUnits));

        when(organisationUnitService.getOrganisationUnits(
                fieldsCaptor.capture(), filtersCaptor.capture(), pagingCaptor.capture(),
                pageSizeCaptor.capture(), pageCaptor.capture()
        )).thenReturn(Single.just(organisationUnitPayload));
        when(organisationUnitPayload.items()).thenReturn(organisationUnits);
    }

    @Test
    public void invoke_server_with_correct_parameters() {
        organisationUnitCall.blockingGet();

        assertThat(fieldsCaptor.getValue()).isEqualTo(OrganisationUnitFields.allFields);
        assertThat(filtersCaptor.getValue().operator()).isEqualTo("like");
        assertThat(filtersCaptor.getValue().field()).isEqualTo(OrganisationUnitFields.path);
        assertThat(pagingCaptor.getValue()).isTrue();
        assertThat(pageCaptor.getValue()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invoke_handler_if_request_succeeds() {
        organisationUnitCall.blockingGet();

        verify(organisationUnitHandler,  times(1)).handleMany(anyCollectionOf(OrganisationUnit.class),
                any(Transformer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void perform_call_twice_on_consecutive_calls() {
        organisationUnitCall.blockingGet();
        organisationUnitCall.blockingGet();

        verify(organisationUnitHandler, times(2)).handleMany(anyCollectionOf(OrganisationUnit.class),
                any(Transformer.class));
    }
}