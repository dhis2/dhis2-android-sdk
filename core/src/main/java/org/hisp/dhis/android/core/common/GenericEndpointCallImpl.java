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
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public abstract class GenericEndpointCallImpl<P extends BaseIdentifiableObject>
        implements Call<Response<Payload<P>>> {
    private GenericCallData data;
    private GenericHandler<P, ?> handler;
    private boolean isExecuted;

    private ResourceModel.Type resourceType;
    private Set<String> uids;
    private Integer limit;

    public GenericEndpointCallImpl(GenericCallData data, GenericHandler<P, ?> handler,
                                   ResourceModel.Type resourceType, Set<String> uids,
                                   Integer limit) {
        this.data = data;
        this.handler = handler;
        this.resourceType = resourceType;
        this.uids = uids;
        this.limit = limit;
    }

    @Override
    public final boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public final Response<Payload<P>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalArgumentException("Already executed");
            }

            isExecuted = true;
        }

        if (limit != null && uids.size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of objects of type " + resourceType +
                            ": " + uids.size() + ". " + "Max size is: " + MAX_UIDS);
        }

        String lastUpdated = data.resourceHandler().getLastUpdated(resourceType);
        Response<Payload<P>> response = getCall(uids, lastUpdated).execute();

        if (isValidResponse(response)) {
            persist(response);
        }
        return response;
    }

    protected abstract retrofit2.Call<Payload<P>> getCall(Set<String> uids,
                                                          String lastUpdated) throws IOException;

    private Response<Payload<P>> persist(Response<Payload<P>> response) {
        if (response == null) {
            throw new RuntimeException("Trying to process call without download data");
        }
        List<P> pojoList = response.body().items();
        if (pojoList != null && !pojoList.isEmpty()) {
            Transaction transaction = data.databaseAdapter().beginNewTransaction();

            try {
                handler.handleMany(pojoList);
                data.resourceHandler().handleResource(resourceType, data.serverDate());

                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
        return response;
    }

    private boolean isValidResponse(Response<Payload<P>> response) {
        return response.isSuccessful() && response.body().items() != null;
    }
}
