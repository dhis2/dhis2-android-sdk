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

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModelBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganisationUnitHandlerShould {
    @Mock
    private IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;

    @Mock
    private ObjectWithoutUidStore<UserOrganisationUnitLinkModel> userOrganisationUnitLinkStore;

    @Mock
    private ObjectWithoutUidStore<OrganisationUnitProgramLinkModel> organisationUnitProgramLinkStore;

    @Mock
    private LinkModelHandler<Program, OrganisationUnitProgramLinkModel> organisationUnitProgramLinkHandler;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private User user;

    @Mock
    private Program program;

    private OrganisationUnitHandler organisationUnitHandler;

    private List<OrganisationUnit> organisationUnits;

    private OrganisationUnitModel.Scope scope;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        scope = OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE;
        String PROGRAM_UID = "test_program_uid";
        Set<String> programUids = Sets.newHashSet(Lists.newArrayList(PROGRAM_UID));

        organisationUnitHandler = new OrganisationUnitHandler(
                organisationUnitStore, userOrganisationUnitLinkStore,
                organisationUnitProgramLinkHandler, programUids, scope, user);

        when(organisationUnit.uid()).thenReturn("test_organisation_unit_uid");
        when(user.uid()).thenReturn(PROGRAM_UID);

        organisationUnits = new ArrayList<>();
        organisationUnits.add(organisationUnit);

        when(program.uid()).thenReturn("test_program_uid");
        when(organisationUnit.programs()).thenReturn(Collections.singletonList(program));
    }

    @Test
    public void persist_user_organisation_unit_link() throws Exception {
        UserOrganisationUnitLinkModel userLinkModel = new UserOrganisationUnitLinkModelBuilder(scope, user)
                .buildModel(organisationUnit);
        organisationUnitHandler.handleMany(organisationUnits, new OrganisationUnitModelBuilder());
        verify(userOrganisationUnitLinkStore).updateOrInsertWhere(userLinkModel);
    }

    @Test
    public void persist_program_organisation_unit_link_when_programs_uids() throws Exception {
        organisationUnitHandler.handleMany(organisationUnits, new OrganisationUnitModelBuilder());
        verify(organisationUnitProgramLinkHandler).handleMany(anyString(), anyListOf(Program.class),
                any(OrganisationUnitProgramLinkModelBuilder.class));
    }

    @Test
    public void persist_program_organisation_unit_link_when_no_programs_uids() throws Exception {
        organisationUnitHandler = new OrganisationUnitHandler(
                organisationUnitStore, userOrganisationUnitLinkStore,
                organisationUnitProgramLinkHandler, null,
                scope, user);
        organisationUnitHandler.handleMany(organisationUnits, new OrganisationUnitModelBuilder());
        verifyNoMoreInteractions(organisationUnitProgramLinkStore);
    }
}