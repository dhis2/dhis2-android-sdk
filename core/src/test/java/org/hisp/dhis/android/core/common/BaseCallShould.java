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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseCallShould {

    @Mock
    protected DatabaseAdapter databaseAdapter;

    protected Dhis2MockServer dhis2MockServer;

    protected Retrofit retrofit;

    @Mock
    protected Date serverDate;

    @Mock
    protected ResourceHandler resourceHandler;

    @Mock
    protected GenericCallData genericCallData;

    @Mock
    protected Transaction transaction;

    @Mock
    protected D2CallException d2CallException;

    protected Response errorResponse;

    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        retrofit = new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        when(genericCallData.databaseAdapter()).thenReturn(databaseAdapter);
        when(genericCallData.retrofit()).thenReturn(retrofit);
        when(genericCallData.serverDate()).thenReturn(serverDate);
        when(genericCallData.resourceHandler()).thenReturn(resourceHandler);

        when(resourceHandler.getLastUpdated(any(ResourceModel.Type.class))).thenReturn(null);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));
    }

    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    protected void whenEndpointCallFails(Call<?> endpointCall) throws Exception {
        when(endpointCall.call()).thenThrow(new Exception());
    }

    protected <T> void verifyFail(Response<T> response) throws Exception {
        assertThat(response).isEqualTo(errorResponse);
        verify(databaseAdapter).beginNewTransaction();
        verify(transaction).end();
        verify(transaction, never()).setSuccessful();
    }

    protected void verifyNoTransactionStarted() {
        verify(databaseAdapter, never()).beginNewTransaction();
        verify(transaction, never()).begin();
        verify(transaction, never()).setSuccessful();
        verify(transaction, never()).end();
    }

    protected void verifyTransactionComplete() {
        InOrder transactionMethodsOrder = inOrder(databaseAdapter);
        transactionMethodsOrder.verify(databaseAdapter).beginNewTransaction();
        verify(transaction).begin();
        verify(transaction).setSuccessful();
        verify(transaction).end();
    }
}
