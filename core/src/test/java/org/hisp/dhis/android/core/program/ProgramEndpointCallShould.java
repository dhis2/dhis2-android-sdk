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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.common.CallException;
import org.hisp.dhis.android.core.common.EmptyQuery;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramEndpointCallShould extends BaseCallShould {

    @Mock
    private ProgramService programService;

    @Mock
    private GenericHandler<Program, ProgramModel> programHandler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<Program>> programCall;

    @Mock
    private Program program;

    @Captor
    private ArgumentCaptor<Fields<Program>> fieldsCaptor;

    @Captor
    private ArgumentCaptor<String> accessDataReadFilter;

    @Mock
    private Payload<Program> payload;

    private List<Program> programList;

    // the call we are testing
    private Call<Response<Payload<Program>>> programEndpointCall;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        programEndpointCall = new ProgramEndpointCall(genericCallData, programService, programHandler,
                EmptyQuery.create());

        when(program.uid()).thenReturn("test_program_uid");

        programList = Collections.singletonList(program);
        when(payload.items()).thenReturn(programList);

        when(programService.getPrograms(any(Fields.class), anyString(), anyBoolean())
        ).thenReturn(programCall);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void return_correct_fields_when_invoke_server() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));

        when(programService.getPrograms(
                fieldsCaptor.capture(), accessDataReadFilter.capture(), anyBoolean())
        ).thenReturn(programCall);


        programEndpointCall.call();

        assertThat(fieldsCaptor.getValue()).isEqualTo(Program.allFields);
    }

    @Test(expected = CallException.class)
    @SuppressWarnings("unchecked")
    public void not_invoke_program_handler_if_request_fail() throws Exception {
        when(programCall.execute()).thenReturn(errorResponse);
        Response<Payload<Program>> response = programEndpointCall.call();
        verifyFail(response);
        verifyNoMoreInteractions(programHandler);
    }

    @Test
    public void invoke_program_handler_and_update_resource_into_table_if_request_succeeds() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        List<Program> threePrograms = Arrays.asList(program, program, program);
        when(payload.items()).thenReturn(threePrograms);

        programEndpointCall.call();

        // verify that transactions is created also in the correct order
        verify(databaseAdapter, times(1)).beginNewTransaction();
        InOrder transactionMethodsOrder = inOrder(transaction);
        transactionMethodsOrder.verify(transaction, times(1)).setSuccessful();
        transactionMethodsOrder.verify(transaction, times(1)).end();

        // assert that payload contains 3 times and all is handled by ProgramHandler
        assertThat(payload.items().size()).isEqualTo(3);

        verify(programHandler).handleMany(same(threePrograms), any(ProgramModelBuilder.class));

        verify(resourceHandler, times(1)).handleResource(eq(ResourceModel.Type.PROGRAM),
                any(Date.class));
    }

    @Test
    public void invoke_program_handler_and_insert_resource_into_table_if_request_succeeds() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        List<Program> threePrograms = Arrays.asList(program, program, program);
        when(payload.items()).thenReturn(threePrograms);

        programEndpointCall.call();

        // verify that transactions is created also in the correct order
        verify(databaseAdapter, times(1)).beginNewTransaction();
        InOrder transactionMethodsOrder = inOrder(transaction);
        transactionMethodsOrder.verify(transaction, times(1)).setSuccessful();
        transactionMethodsOrder.verify(transaction, times(1)).end();

        // assert that payload contains 3 times and all is handled by ProgramHandler
        assertThat(payload.items().size()).isEqualTo(3);

        // verify that insert is called 3 times in program store
        verify(programHandler).handleMany(same(threePrograms), any(ProgramModelBuilder.class));

        // we need to verify that resource store is invoked with update since we update before we insert
        verify(resourceHandler, times(1)).handleResource(eq(ResourceModel.Type.PROGRAM),
                any(Date.class));
    }

    @Test
    public void invoke_program_handler_if_last_synced_program_is_not_null() throws Exception {
        when(resourceHandler.getLastUpdated(ResourceModel.Type.PROGRAM)).thenReturn("2017-02-09");
        when(programCall.execute()).thenReturn(Response.success(payload));

        programEndpointCall.call();

        // verify that transactions is created also in the correct order

        verify(databaseAdapter, times(1)).beginNewTransaction();
        InOrder transactionMethodsOrder = inOrder(transaction);
        transactionMethodsOrder.verify(transaction, times(1)).setSuccessful();
        transactionMethodsOrder.verify(transaction, times(1)).end();

        // only 1 program in payload (See setUp method)
        assertThat(payload.items().size()).isEqualTo(1);

        // verify that insert is called once in program store
        verify(programHandler, times(1)).handleMany(same(programList),
                any(ProgramModelBuilder.class));
    }

    @Test
    public void mark_call_as_executed_on_success() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        programEndpointCall.call();

        assertThat(programEndpointCall.isExecuted()).isTrue();
    }


    @Test
    public void throw_exception_when_executing_consecutive_call() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        programEndpointCall.call();

        try {
            programEndpointCall.call();
            fail("Invoking the programEndpointCall multiple times should throw exception");
        } catch (Exception ex) {
            // do nothing
        }
    }
    @Test
    @SuppressWarnings("unchecked")
    public void throw_io_exception_when_call_is_executed() throws Exception {
        when(programCall.execute()).thenThrow(IOException.class);

        try {
            programEndpointCall.call();
        } catch (IOException ioe) {
            // do nothing
        }

        assertThat(programEndpointCall.isExecuted()).isTrue();

    }
}
