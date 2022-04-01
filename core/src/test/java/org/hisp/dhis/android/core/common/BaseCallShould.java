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
package org.hisp.dhis.android.core.common;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hisp.dhis.android.core.arch.api.testutils.RetrofitFactory;
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public abstract class BaseCallShould {

    @Mock
    protected DatabaseAdapter databaseAdapter;

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
    protected D2Error d2Error;

    protected Response errorResponse;

    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        retrofit = RetrofitFactory.fromServerUrl("https://fake.dhis.org");

        when(genericCallData.databaseAdapter()).thenReturn(databaseAdapter);
        when(genericCallData.retrofit()).thenReturn(retrofit);
        when(genericCallData.resourceHandler()).thenReturn(resourceHandler);

        when(resourceHandler.getLastUpdated(any(Resource.Type.class))).thenReturn(null);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));
    }

    protected void whenEndpointCallFails(Callable<?> endpointCall) throws Exception {
        when(endpointCall.call()).thenThrow(new Exception());
    }

    protected void verifyNoTransactionCompleted() {
        verify(databaseAdapter, never()).beginNewTransaction();
        verify(transaction, never()).setSuccessful();
        verify(transaction, never()).end();
    }

    protected void verifyTransactionComplete() {
        InOrder transactionMethodsOrder = inOrder(databaseAdapter);
        transactionMethodsOrder.verify(databaseAdapter).beginNewTransaction();
        verify(transaction).setSuccessful();
        verify(transaction).end();
    }
}
