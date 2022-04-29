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

package org.hisp.dhis.android.core.arch.api.executors.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.internal.UserAccountDisabledErrorCatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class APICallExecutorShould {

    @Mock
    private ObjectStore<D2Error> errorStore;

    @Mock
    private User user;
    private List<User> users;

    @Mock
    private Call<User> objectAPICall;

    @Mock
    private Call<Payload<User>> payloadAPICall;

    @Mock
    private Payload<User> payload;

    @Mock
    private IOException ioException;

    @Mock
    private UserAccountDisabledErrorCatcher userAccountDisabledErrorCatcher;

    private Response conflictResponse;

    private APICallExecutor apiCallExecutor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        users = Collections.singletonList(user);

        when(errorStore.isReady()).thenReturn(true);

        when(objectAPICall.execute()).thenReturn(Response.success(user));
        when(payloadAPICall.execute()).thenReturn(Response.success(payload));
        when(payload.items()).thenReturn(users);

        conflictResponse = Response.error(409, ResponseBody.create(MediaType.get("application/text"),
                "error_response"));

        apiCallExecutor = new APICallExecutorImpl(errorStore, userAccountDisabledErrorCatcher);
    }

    @Test
    public void return_object_when_object_api_call_succeeds() throws D2Error {
        User responseUser = apiCallExecutor.executeObjectCall(objectAPICall);
        assertThat(responseUser).isSameInstanceAs(user);
    }

    @Test
    public void throw_d2_error_when_object_api_call_fails() throws IOException {
        when(objectAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executeObjectCall(objectAPICall);
        } catch (D2Error d2Error) {
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.API_RESPONSE_PROCESS_ERROR);
        }
    }

    @Test
    public void persist_thrown_d2_error_when_object_api_call_fails() throws IOException {
        when(objectAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executeObjectCall(objectAPICall);
        } catch (D2Error d2Error) {
            // Empty block
        }
        verify(errorStore).isReady();
        verify(errorStore).insert(any(D2Error.class));
        verifyNoMoreInteractions(errorStore);
    }

    @Test
    public void return_list_when_payload_api_call_succeeds() throws D2Error {
        List<User> responseUsers = apiCallExecutor.executePayloadCall(payloadAPICall);
        assertThat(responseUsers).isSameInstanceAs(users);
    }

    @Test
    public void throw_d2_error_when_payload_api_call_fails() throws IOException {
        when(payloadAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executePayloadCall(payloadAPICall);
        } catch (D2Error d2Error) {
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.API_RESPONSE_PROCESS_ERROR);
        }
    }

    @Test
    public void persist_thrown_d2_error_when_payload_api_call_fails() throws IOException {
        when(payloadAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executePayloadCall(payloadAPICall);
        } catch (D2Error d2Error) {
            // Empty block
        }
        verify(errorStore).isReady();
        verify(errorStore).insert(any(D2Error.class));
        verifyNoMoreInteractions(errorStore);
    }

    @Test
    public void persist_thrown_d2_error_when_payload_call_is_conflict() throws IOException {
        when(payloadAPICall.execute()).thenReturn(conflictResponse);

        try {
            apiCallExecutor.executePayloadCall(payloadAPICall);
        } catch (D2Error d2Error) {
            //Empty block
        }
        verify(errorStore).isReady();
        verify(errorStore).insert(any(D2Error.class));
        verifyNoMoreInteractions(errorStore);
    }

    @Test
    public void persist_thrown_d2_error_when_object_call_is_conflict() throws IOException {
        when(objectAPICall.execute()).thenReturn(conflictResponse);

        try {
            apiCallExecutor.executeObjectCall(objectAPICall);
        } catch (D2Error d2Error) {
            //Empty block
        }
        verify(errorStore).isReady();
        verify(errorStore).insert(any(D2Error.class));
        verifyNoMoreInteractions(errorStore);
    }

    @Test
    public void call_error_catcher_when_account_disabled() throws IOException {
        Response disabledResponse = Response.error(401, ResponseBody.create(MediaType.get("application/text"),
                "Account disabled"));

        when(objectAPICall.execute()).thenReturn(disabledResponse);
        when(userAccountDisabledErrorCatcher.isUserAccountLocked(any(), any())).thenReturn(true);

        try {
            apiCallExecutor.executeObjectCall(objectAPICall);
        } catch (D2Error d2Error) {
            //Empty block
        }

        verify(userAccountDisabledErrorCatcher).catchError(any(), any());
    }
}