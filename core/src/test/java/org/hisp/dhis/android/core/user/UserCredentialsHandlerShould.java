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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class UserCredentialsHandlerShould {

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private User user;

    @Mock
    private Date created;

    @Mock
    private Date lastUpdated;

    // object to test
    private UserCredentialsHandler userCredentialHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userCredentialHandler = new UserCredentialsHandler(userCredentialsStore);

        when(userCredentials.uid()).thenReturn("user_credentials_uid");
        when(userCredentials.code()).thenReturn("user_credentials_code");
        when(userCredentials.name()).thenReturn("user_credentials_name");
        when(userCredentials.displayName()).thenReturn("user_credentials_displayName");
        when(userCredentials.created()).thenReturn(created);
        when(userCredentials.lastUpdated()).thenReturn(lastUpdated);
        when(userCredentials.username()).thenReturn("user_credentials_username");
    }

    @Test
    public void invoke_update_and_insert_when_handle_user_credentials_not_inserted() throws Exception {
        // insert userCredentials first to check the updateWithSection mechanism
        when(userCredentialsStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString())).thenReturn(1);

        userCredentialHandler.handleUserCredentials(userCredentials, user);

        // verify that update is called once
        verify(userCredentialsStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString());

        // verify that insert and delete is never called
        verify(userCredentialsStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString());

        verify(userCredentialsStore, never()).delete(anyString());
    }

    @Test
    public void invoke_delete_when_handle_user_credentials_set_as_deleted() throws Exception {
        when(userCredentials.deleted()).thenReturn(Boolean.TRUE);

        userCredentialHandler.handleUserCredentials(userCredentials, user);

        verify(userCredentialsStore, times(1)).delete(anyString());

        // verify that insert and update is never called
        verify(userCredentialsStore, never()).update(anyString(), anyString(),
                anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(),
                anyString(), anyString());

        verify(userCredentialsStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString());

    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        userCredentialHandler.handleUserCredentials(null, user);

        verify(userCredentialsStore, never()).delete(anyString());
        verify(userCredentialsStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString());
        verify(userCredentialsStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString());

    }

    @Test
    public void invoke_only_update_when_handle_user_credentials_inserted() throws Exception {
        when(userCredentialsStore.update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString())
        ).thenReturn(0);

        userCredentialHandler.handleUserCredentials(userCredentials, user);

        // verify that insert is called once
        verify(userCredentialsStore, times(1)).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString());

        // verify that update is called once since we try to update before we insert
        verify(userCredentialsStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString());

        // verify that delete is never called
        verify(userCredentialsStore, never()).delete(anyString());
    }
}
