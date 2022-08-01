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

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.util.concurrent.Callable;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserCallShould extends BaseCallShould {

    @Mock
    private UserService userService;

    @Mock
    private APICallExecutor apiCallExecutor;

    @Mock
    private Handler<User> userHandler;

    @Mock
    private retrofit2.Call<User> userCall;

    @Mock
    private User user;

    private Callable<User> userSyncCall;

    @Override
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        userSyncCall = new UserCall(genericCallData, apiCallExecutor, userService, userHandler);
        when(userService.getUser(any(Fields.class))).thenReturn(userCall);
    }

    @Test
    public void not_invoke_stores_on_call_io_exception() throws D2Error {
        when(apiCallExecutor.executeObjectCall(userCall)).thenThrow(d2Error);

        try {
            userSyncCall.call();
            fail("Exception was not thrown");
        } catch (Exception ex) {

            // verify that handlers was not touched
            verify(databaseAdapter, never()).beginNewTransaction();
            verify(transaction, never()).setSuccessful();
            verify(transaction, never()).end();

            verify(userHandler, never()).handle(eq(user));
        }
    }

    @Test
    public void not_invoke_handler_after_call_failure() throws Exception {
        when(apiCallExecutor.executeObjectCall(userCall)).thenThrow(d2Error);

        try {
            userSyncCall.call();
            fail("Call should't succeed");
        } catch (D2Error d2Exception) {
        }

        // verify that database was not touched
        verify(databaseAdapter, never()).beginNewTransaction();
        verify(transaction, never()).setSuccessful();
        verify(transaction, never()).end();
        verify(userHandler, never()).handle(eq(user));
    }

    @Test
    public void invoke_handlers_on_success() throws Exception {
        when(apiCallExecutor.executeObjectCall(userCall)).thenReturn(user);
        userSyncCall.call();
        verify(userHandler).handle(eq(user));
    }
}
