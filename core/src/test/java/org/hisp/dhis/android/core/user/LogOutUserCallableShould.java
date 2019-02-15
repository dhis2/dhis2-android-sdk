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

package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LogOutUserCallableShould {

    @Mock
    private ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Captor
    private ArgumentCaptor<AuthenticatedUser> loggedOutUser;

    private Callable<Unit> logOutUserCallable;

    static final String USER = "user";
    static final String HASH = "hash";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(authenticatedUser.user()).thenReturn(USER);
        when(authenticatedUser.credentials()).thenReturn("credentials");
        when(authenticatedUser.hash()).thenReturn(HASH);

        logOutUserCallable = new LogOutUserCallable(authenticatedUserStore);
    }

    @Test
    public void clear_user_credentials() throws Exception {
        when(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser);

        logOutUserCallable.call();

        verify(authenticatedUserStore).updateOrInsertWhere(loggedOutUser.capture());

        assertThat(loggedOutUser.getValue().user()).isEqualTo(USER);
        assertThat(loggedOutUser.getValue().credentials()).isNull();
        assertThat(loggedOutUser.getValue().hash()).isEqualTo(HASH);
    }

    @Test
    public void throw_d2_exception_if_no_authenticated_user() throws Exception {
        when(authenticatedUserStore.selectFirst()).thenReturn(null);

        try {
            logOutUserCallable.call();
            fail("D2Exception must be thrown if no authenticated user");
        } catch (D2Error d2Exception) {
            assertThat(d2Exception.errorCode()).isEqualTo(D2ErrorCode.NO_AUTHENTICATED_USER);
        }
    }
}