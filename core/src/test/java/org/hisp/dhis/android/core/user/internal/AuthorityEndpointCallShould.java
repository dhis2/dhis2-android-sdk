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
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.call.internal.EndpointCall;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.user.Authority;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AuthorityEndpointCallShould extends BaseCallShould {

    @Mock
    private AuthorityService authorityService;

    @Mock
    private Handler<Authority> handler;

    @Mock
    private retrofit2.Call<List<String>> retrofitCall;

    @Mock
    private List<String> payload;

    @Mock
    private UserAccountDisabledErrorCatcher userAccountDisabledErrorCatcher;

    private Callable<List<Authority>> endpointCall;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(databaseAdapter, userAccountDisabledErrorCatcher);
        endpointCall = new AuthorityEndpointCallFactory(genericCallData, apiCallExecutor, handler,
                retrofit.create(AuthorityService.class)).create();
        when(retrofitCall.execute()).thenReturn(Response.success(payload));

        when(authorityService.getAuthorities()).thenReturn(retrofitCall);
    }

    private EndpointCall<Authority> castedEndpointCall() {
        return (EndpointCall<Authority>) endpointCall;
    }

    @Test
    public void extend_endpoint_call() {
        assertThat(endpointCall instanceof EndpointCall).isTrue();
    }

    @Test
    public void have_payload_no_resource_fetcher() {
        assertThat(castedEndpointCall().getFetcher() instanceof AuthorityCallFetcher).isTrue();
    }

    @Test
    public void have_transactional_no_resource_call_processor() {
        EndpointCall<Authority> castedEndpointCall = (EndpointCall<Authority>) endpointCall;
        assertThat(castedEndpointCall.getProcessor() instanceof AuthorityCallProcessor).isTrue();
    }
}