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
package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.program.Program;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserRoleHandlerShould {
    @Mock
    private UserRoleStore userRoleStore;

    @Mock
    private UserRoleProgramLinkStore userRoleProgramLinkStore;

    @Mock
    private UserRole userRole;

    @Mock
    private Program program;

    @Mock
    private Date created;

    @Mock
    private Date lastUpdated;

    // object to test
    private UserRoleHandler userRoleHandler;

    // list of user roles
    private List<UserRole> userRoles;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRoleHandler = new UserRoleHandler(userRoleStore, userRoleProgramLinkStore);

        when(program.uid()).thenReturn("program_uid");

        when(userRole.uid()).thenReturn("user_role_uid");
        when(userRole.code()).thenReturn("user_role_code");
        when(userRole.name()).thenReturn("user_role_name");
        when(userRole.displayName()).thenReturn("user_role_display_name");
        when(userRole.created()).thenReturn(created);
        when(userRole.lastUpdated()).thenReturn(lastUpdated);
        when(userRole.programs()).thenReturn(Collections.singletonList(program));

        userRoles = new ArrayList<>();
        userRoles.add(userRole);
    }

    @Test
    public void invoke_update_and_insert_when_handle_user_credentials_not_updatable() throws Exception {
        when(userRoleStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString())).thenReturn(0);

        userRoleHandler.handleUserRoles(userRoles);


        // verify that userRoleStore was called once with insert
        verify(userRoleStore, times(1)).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class));

        // verify that update was called once (Because we try to update before we can insert)
        verify(userRoleStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString());

        // verify that delete was not called
        verify(userRoleStore, never()).delete(anyString());
    }

    @Test
    public void invoke_only_update_when_handle_user_roles_inserted() throws Exception {

        when(userRoleStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString())).thenReturn(1);

        userRoleHandler.handleUserRoles(userRoles);


        verify(userRoleStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString());

        verify(userRoleStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class));

        verify(userRoleStore, never()).delete(anyString());
    }

    @Test
    public void invoke_update_and_insert_when_handle_user_roles_not_updatable() throws Exception {
        when(userRoleProgramLinkStore.update(anyString(), anyString(), anyString(), anyString())).thenReturn(0);

        userRoleHandler.handleUserRoles(userRoles);

        // verify that insert is called once
        verify(userRoleProgramLinkStore, times(1)).insert(anyString(), anyString());

        // verify that updateWithSection is called once since we try to updateWithSection before we insert
        verify(userRoleProgramLinkStore, times(1)).update(anyString(), anyString(), anyString(), anyString());

        // verify that delete is never called
        verify(userRoleProgramLinkStore, never()).delete(anyString(), anyString());
    }

    @Test
    public void invoke_only_update_when_handle_user_roles_inserted_with_uids() throws Exception {
        when(userRole.uid()).thenReturn("new_user_role_uid");
        when(program.uid()).thenReturn("new_program_uid");
        when(userRoleProgramLinkStore.update(anyString(), anyString(), anyString(), anyString())).thenReturn(1);

        userRoleHandler.handleUserRoles(userRoles);

        // verify that updateWithSection is called once
        verify(userRoleProgramLinkStore, times(1)).update(anyString(), anyString(), anyString(), anyString());

        // verify that insert and delete is never called

        verify(userRoleProgramLinkStore, never()).delete(anyString(), anyString());
        verify(userRoleProgramLinkStore, never()).insert(anyString(), anyString());
    }

    @Test
    public void invoke_delete_when_handle_user_credentials_set_as_deleted() throws Exception {
        when(userRole.deleted()).thenReturn(Boolean.TRUE);

        userRoleHandler.handleUserRoles(userRoles);

        // verify that delete is called once
        verify(userRoleStore, times(1)).delete(anyString());

        // verify that updateWithSection and insert is never called
        verify(userRoleStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString());

        verify(userRoleStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class));
    }
}
