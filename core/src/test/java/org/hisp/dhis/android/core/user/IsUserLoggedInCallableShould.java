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

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class IsUserLoggedInCallableShould {

    @Mock
    private ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore;

    @Mock
    private AuthenticatedUser authenticatedUser;

    private Single<Boolean> isUserLoggedInSingle;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(authenticatedUser.user()).thenReturn("user");
        when(authenticatedUser.credentials()).thenReturn("credentials");
        when(authenticatedUser.hash()).thenReturn("hash");

        isUserLoggedInSingle = new IsUserLoggedInCallableFactory(authenticatedUserStore).isLogged();
    }

    @Test
    public void return_true_if_any_users_are_persisted_after_call() {
        when(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser);

        Boolean isUserLoggedIn = isUserLoggedInSingle.blockingGet();

        assertThat(isUserLoggedIn).isTrue();
    }

    @Test
    public void return_false_if_any_users_are_not_persisted_after_call() {
        when(authenticatedUserStore.selectFirst()).thenReturn(null);

        Boolean isUserLoggedIn = isUserLoggedInSingle.blockingGet();

        assertThat(isUserLoggedIn).isFalse();
    }

    @Test
    public void return_false_if_users_persisted_but_without_credentials() {
        when(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser);
        when(authenticatedUser.credentials()).thenReturn(null);

        Boolean isUserLoggedIn = isUserLoggedInSingle.blockingGet();

        assertThat(isUserLoggedIn).isFalse();
    }
}