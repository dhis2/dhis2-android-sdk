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

import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OrganisationUnitHandlerShould {
    @Mock
    private OrganisationUnitStore organisationUnitStore;

    @Mock
    private UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @Mock
    private OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private User user;

    @Mock
    private Program program;

    // object to test
    private OrganisationUnitHandler organisationUnitHandler;

    // list of organisation units
    private List<OrganisationUnit> organisationUnits;


    // scope of org units
    private OrganisationUnitModel.Scope scope;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        organisationUnitHandler = new OrganisationUnitHandler(
                organisationUnitStore, userOrganisationUnitLinkStore,
                organisationUnitProgramLinkStore);

        when(organisationUnit.uid()).thenReturn("test_organisation_unit_uid");
        when(user.uid()).thenReturn("test_user_uid");

        organisationUnits = new ArrayList<>();
        organisationUnits.add(organisationUnit);

        scope = OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE;
        when(program.uid()).thenReturn("test_program_uid");
        when(organisationUnit.programs()).thenReturn(Collections.singletonList(program));
    }

    public void do_nothing_when_passing_in_null_organisation_units() throws Exception {
        organisationUnitHandler.handleOrganisationUnits(
                null, scope, user.uid()
        );

        // verify that stores is never invoked

        verify(organisationUnitStore, never()).delete(anyString());
        verify(organisationUnitStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt());
        verify(organisationUnitStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString()
        );

        verify(userOrganisationUnitLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userOrganisationUnitLinkStore, never()).insert(anyString(), anyString(), anyString());

        verify(organisationUnitProgramLinkStore, never()).insert(anyString(), anyString());
    }

    @Test
    public void invoke_delete_when_handle_organisation_unit_set_as_delete() throws Exception {
        when(organisationUnit.deleted()).thenReturn(Boolean.TRUE);

        // passing in null args to user uid and org unit scope. We don't want to invoke link store
        organisationUnitHandler.handleOrganisationUnits(
                organisationUnits, scope, user.uid());

        verify(organisationUnitStore, times(1)).delete(organisationUnit.uid());

        verify(organisationUnitStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString()
        );
        verify(organisationUnitStore, never()).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt());

        // verify that link store is never invoked
        verify(userOrganisationUnitLinkStore, never()).insert(anyString(), anyString(), anyString());
        verify(userOrganisationUnitLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );
        verify(organisationUnitProgramLinkStore, never()).insert(anyString(), anyString());
    }

    @Test
    public void invoke_only_update_when_handle_updatable_organisation_unit_and_link_store() throws Exception {
        when(organisationUnitStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString())).thenReturn(1);

        when(userOrganisationUnitLinkStore.update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString())
        ).thenReturn(1);

        organisationUnitHandler.handleOrganisationUnits(organisationUnits, scope, user.uid());

        // verify that update is called once
        verify(organisationUnitStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString()
        );

        // verify that insert and delete is never called
        verify(organisationUnitStore, never()).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt()
        );

        verify(organisationUnitStore, never()).delete(anyString());

        // verify that link store #update method is called once
        verify(userOrganisationUnitLinkStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );

        // verify that insert in link store is never called
        verify(userOrganisationUnitLinkStore, never()).insert(anyString(), anyString(), anyString());
        verify(organisationUnitProgramLinkStore, times(1)).insert(anyString(), anyString());
    }

    @Test
    public void invoke_only_update_when_handle_organisation_units_inserted() throws Exception {
        when(organisationUnitStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString())).thenReturn(1);

        // we pass in null as scope parameter for not invoking the link store
        organisationUnitHandler.handleOrganisationUnits(organisationUnits, null, user.uid());

        // verify that update is called once
        verify(organisationUnitStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString()
        );

        // verify that insert and delete is never called
        verify(organisationUnitStore, never()).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt()
        );

        verify(organisationUnitStore, never()).delete(anyString());

        // verify that link store #update method is called once
        verify(userOrganisationUnitLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );

        // verify that insert in link store is never called
        verify(userOrganisationUnitLinkStore, never()).insert(anyString(), anyString(), anyString());

        verify(organisationUnitProgramLinkStore, times(1)).insert(anyString(), anyString());

    }

    @Test
    public void invoke_update_and_insert_when_handle_insertable_organisation_unit_and_link_store() throws Exception {
        when(organisationUnitStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString())).thenReturn(0);

        when(userOrganisationUnitLinkStore.update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString())
        ).thenReturn(0);

        organisationUnitHandler.handleOrganisationUnits(organisationUnits, scope, user.uid());

        // verify that insert is called once
        verify(organisationUnitStore, times(1)).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt()
        );

        // verify that update is called once since we try to update before we insert
        verify(organisationUnitStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString()
        );

        // verify that delete is never called
        verify(organisationUnitStore, never()).delete(anyString());

        // verify that insert in link store is called once
        verify(userOrganisationUnitLinkStore, times(1)).insert(anyString(), anyString(), anyString());


        // verify that link store #update method is called once since we try to update before inserting
        verify(userOrganisationUnitLinkStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );

        verify(organisationUnitProgramLinkStore, times(1)).insert(anyString(), anyString());
    }

    @Test
    public void invoke_update_and_insert_when_handle_insertable_organisation_unit() throws Exception {
        when(organisationUnitStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString())).thenReturn(0);


        organisationUnitHandler.handleOrganisationUnits(organisationUnits, null, null);

        // verify that insert is called once
        verify(organisationUnitStore, times(1)).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt()
        );

        // verify that update is called once since we try to update before we insert
        verify(organisationUnitStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyInt(), anyString()
        );

        // verify that delete is never called
        verify(organisationUnitStore, never()).delete(anyString());

        // verify that link store is never called
        verify(userOrganisationUnitLinkStore, never()).insert(anyString(), anyString(), anyString());

        verify(userOrganisationUnitLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );

        verify(organisationUnitProgramLinkStore, times(1)).insert(anyString(), anyString());
    }
}
