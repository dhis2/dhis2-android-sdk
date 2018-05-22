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

import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public abstract class AbstractEndpointCall<P, M extends Model, Q extends BaseQuery, C> extends SyncCall<Response<C>> {
    private final GenericCallData data;
    private final GenericHandler<P, M> handler;

    private final ResourceModel.Type resourceType;
    private final ModelBuilder<P, M> modelBuilder;
    public final Q query;

    AbstractEndpointCall(GenericCallData data, GenericHandler<P, M> handler,
                         ResourceModel.Type resourceType,
                         ModelBuilder<P, M> modelBuilder, Q query) {
        this.data = data;
        this.handler = handler;
        this.resourceType = resourceType;
        this.modelBuilder = modelBuilder;
        this.query = query;
    }

    protected abstract retrofit2.Call<C> getCall(Q query, String lastUpdated) throws IOException;
    protected abstract List<P> getPojoList(Response<C> response);
    protected abstract boolean isValidResponse(Response<C> response);

    @Override
    public final Response<C> call() throws Exception {
        super.setExecuted();

        if (!query.isValid()) {
            throw new IllegalArgumentException("Invalid query");
        }

        String lastUpdated = data.resourceHandler().getLastUpdated(resourceType);
        Response<C> response = getCall(query, lastUpdated).execute();

        if (isValidResponse(response)) {
            persist(response);
            return response;
        } else {
            throw CallException.create(response);
        }
    }

    private void persist(Response<C> response) {
        if (response == null) {
            throw new RuntimeException("Trying to process call without download data");
        }
        List<P> pojoList = getPojoList(response);
        if (pojoList != null && !pojoList.isEmpty()) {
            Transaction transaction = data.databaseAdapter().beginNewTransaction();

            try {
                handler.handleMany(pojoList, modelBuilder);
                data.resourceHandler().handleResource(resourceType, data.serverDate());

                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
    }
}