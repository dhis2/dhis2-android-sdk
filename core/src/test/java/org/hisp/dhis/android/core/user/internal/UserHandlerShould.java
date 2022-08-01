/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.user.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserInternalAccessor;
import org.hisp.dhis.android.core.user.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserHandlerShould {

    @Mock
    private IdentifiableObjectStore<User> userStore;

    @Mock
    private Handler<UserCredentials> userCredentialsHandler;

    @Mock
    private Handler<UserRole> userRoleHandler;

    @Mock
    private CollectionCleaner<UserRole> userRoleCollectionCleaner;

    @Mock
    private User user;

    private UserCredentials userCredentials;

    // object to test
    private UserHandler userHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userHandler = new UserHandler(userStore, userCredentialsHandler, userRoleHandler, userRoleCollectionCleaner);
        userCredentials = UserCredentials.builder().uid("credentialsUid").build();
        when(UserInternalAccessor.accessUserCredentials(user)).thenReturn(userCredentials);
        when(user.uid()).thenReturn("userUid");
        when(userStore.updateOrInsert(user)).thenReturn(HandleAction.Insert);
    }

    @Test
    public void extend_identifiable_sync_handler_impl() {
        IdentifiableHandlerImpl<User> genericHandler = new UserHandler(userStore, userCredentialsHandler, userRoleHandler, userRoleCollectionCleaner);
    }

    @Test
    public void call_user_credentials_handler() {
        userHandler.handle(user);
        UserCredentials credentialsWithUser = userCredentials.toBuilder().user(ObjectWithUid.create(user.uid())).build();
        verify(userCredentialsHandler).handle(credentialsWithUser);
    }
}