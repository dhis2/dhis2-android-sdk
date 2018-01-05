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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class IsUserLoggedInCallableShould {

    @Mock
    private AuthenticatedUserStore authenticatedUserStore;

    @Mock
    private AuthenticatedUserModel authenticatedUser;

    private Callable<Boolean> isUserLoggedInCallable;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        isUserLoggedInCallable = new IsUserLoggedInCallable(authenticatedUserStore);
    }

    @Test
    public void return_true_if_any_users_are_persisted_after_call() throws Exception {
        when(authenticatedUserStore.query()).thenReturn(Arrays.asList(authenticatedUser));

        Boolean isUserLoggedIn = isUserLoggedInCallable.call();

        assertThat(isUserLoggedIn).isTrue();
    }

    @Test
    public void return_false_if_any_users_are_not_persisted_after_call() throws Exception {
        when(authenticatedUserStore.query()).thenReturn(new ArrayList<AuthenticatedUserModel>());

        Boolean isUserLoggedIn = isUserLoggedInCallable.call();

        assertThat(isUserLoggedIn).isFalse();
    }
}
