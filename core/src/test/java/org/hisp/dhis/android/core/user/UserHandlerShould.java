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

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserHandlerShould {

    @Mock
    private UserStore userStore;

    @Mock
    private User user;

    @Mock
    private Date created;

    @Mock
    private Date lastUpdated;

    @Mock
    private UserCredentials userCredentials;

    // object to test
    private UserHandler userHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userHandler = new UserHandler(userStore);

        when(user.uid()).thenReturn("user_uid");
        when(user.code()).thenReturn("user_code");
        when(user.name()).thenReturn("user_name");
        when(user.displayName()).thenReturn("user_display_name");
        when(user.created()).thenReturn(created);
        when(user.lastUpdated()).thenReturn(lastUpdated);
        when(user.birthday()).thenReturn("user_birthday");
        when(user.education()).thenReturn("user_education");
        when(user.gender()).thenReturn("male");
        when(user.jobTitle()).thenReturn("job_title");
        when(user.surname()).thenReturn("user_surname");
        when(user.firstName()).thenReturn("user_first_name");
        when(user.introduction()).thenReturn("user_introduction");
        when(user.employer()).thenReturn("user_employer");
        when(user.interests()).thenReturn("user_interests");
        when(user.languages()).thenReturn("user_languages");
        when(user.email()).thenReturn("user_email");
        when(user.phoneNumber()).thenReturn("user_phone_number");
        when(user.nationality()).thenReturn("user_nationality");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.userCredentials().userRoles()).thenReturn(new ArrayList<UserRole>());
        when(user.organisationUnits()).thenReturn(new ArrayList<OrganisationUnit>());
    }

    @Test
    public void invoke_only_update_when_handle_user_inserted() throws Exception {
        when(userStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(1);

        userHandler.handleUser(user);


        verify(userStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());

        // verify that insert and delete is never called
        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        verify(userStore, never()).delete(anyString());
    }


    @Test
    public void invoke_delete_when_handle_user_set_as_deleted() throws Exception {
        when(user.deleted()).thenReturn(Boolean.TRUE);

        userHandler.handleUser(user);
        // verify that delete is called once
        verify(userStore, times(1)).delete(anyString());

        // verify that insert and update is never called
        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        verify(userStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        userHandler.handleUser(null);

        // verify that store is never called
        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        verify(userStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());

        verify(userStore, never()).delete(anyString());
    }

    @Test
    public void invoke_update_and_insert_when_handle_user_not_inserted() throws Exception {
        when(userStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(0);

        userHandler.handleUser(user);


        // verify that insert is called once
        verify(userStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        // verify that update is called once since we try to update before we insert
        verify(userStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());

        // verify that delete is never called
        verify(userStore, never()).delete(anyString());

    }
}
