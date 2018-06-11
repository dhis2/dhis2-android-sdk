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
package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class EndpointPayloadCallAbstractShould<P> extends BaseCallShould {

    @Mock
    protected ListPersistor<P> persistor;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    protected retrofit2.Call<Payload<P>> retrofitCall;

    @Mock
    private Payload<P> payload;

    protected List<P> pojoList;

    protected Call<List<P>> endpointCall;

    protected ResourceModel.Type resourceType;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        when(payload.items()).thenReturn(pojoList);
        when(retrofitCall.execute()).thenReturn(Response.success(payload));
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test(expected = D2CallException.class)
    @SuppressWarnings("unchecked")
    public void fail_if_api_call_fails() throws Exception {
        when(retrofitCall.execute()).thenReturn(errorResponse);
        endpointCall.call();
    }

    @Test
    public void succeed_for_last_synced_null() throws Exception {
        endpointCall.call();
        verify(persistor).persist(pojoList);
    }

    @Test
    public void succeed_for_last_synced_not_null() throws Exception {
        when(resourceHandler.getLastUpdated(resourceType)).thenReturn("2017-02-09");

        endpointCall.call();

        verify(persistor).persist(pojoList);
    }

    @Test
    public void mark_call_as_executed_on_success() throws Exception {
        endpointCall.call();
        assertThat(endpointCall.isExecuted()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mark_call_as_executed_on_failure() throws Exception {
        when(retrofitCall.execute()).thenReturn(errorResponse);
        try {
            endpointCall.call();
            fail("Call must fail");
        } catch (D2CallException d2E) {
            assertThat(endpointCall.isExecuted()).isTrue();
        }
    }

    @Test()
    public void throw_d2_call_exception_when_executing_consecutive_calls() throws Exception {
        endpointCall.call();
        try {
            endpointCall.call();
        } catch (D2CallException d2E) {
            assertThat(d2E.errorCode()).isEqualTo(D2ErrorCode.ALREADY_EXECUTED);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void throw_d2_call_exception_when_call_is_executed() throws Exception {
        when(retrofitCall.execute()).thenThrow(IOException.class);
        try {
            endpointCall.call();
        } catch (D2CallException d2E) {
            assertThat(d2E.errorCode()).isEqualTo(D2ErrorCode.API_RESPONSE_PROCESS_ERROR);
        }
    }
}
