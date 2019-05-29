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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.PayloadNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.TransactionalNoResourceSyncCallProcessor;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramEndpointCallShould extends BaseCallShould {

    @Mock
    private ProgramService programService;

    @Mock
    private SyncHandler<Program> programHandler;

    @Captor
    private ArgumentCaptor<Fields<Program>> fieldsCaptor;

    @Captor
    private ArgumentCaptor<String> accessDataReadFilter;

    @Mock
    private retrofit2.Call<Payload<Program>> retrofitCall;

    @Mock
    private Payload<Program> payload;

    private Callable<List<Program>> endpointCall;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(databaseAdapter);
        endpointCall = new ProgramEndpointCallFactory(genericCallData, apiCallExecutor,
                programService, programHandler).create();
        when(retrofitCall.execute()).thenReturn(Response.success(payload));

        when(programService.getPrograms(any(Fields.class), anyString(), anyBoolean())
        ).thenReturn(retrofitCall);
    }

    private EndpointCall<Program> castedEndpointCall() {
        return (EndpointCall<Program>) endpointCall;
    }

    @Test
    public void return_correct_fields_when_invoke_server() throws Exception {
        when(programService.getPrograms(
                fieldsCaptor.capture(), accessDataReadFilter.capture(), anyBoolean())
        ).thenReturn(retrofitCall);

        endpointCall.call();

        assertThat(fieldsCaptor.getValue()).isEqualTo(ProgramFields.allFields);
        assertThat(accessDataReadFilter.getValue()).isEqualTo("access.data.read:eq:true");
    }

    @Test
    public void extend_endpoint_call() {
        assertThat(endpointCall instanceof EndpointCall).isTrue();
    }

    @Test
    public void have_payload_no_resource_fetcher() {
        assertThat(castedEndpointCall().getFetcher() instanceof PayloadNoResourceCallFetcher).isTrue();
    }

    @Test
    public void have_transactional_no_resource_call_processor() {
        EndpointCall<Program> castedEndpointCall = (EndpointCall<Program>) endpointCall;
        assertThat(castedEndpointCall.getProcessor() instanceof TransactionalNoResourceSyncCallProcessor).isTrue();
    }
}
