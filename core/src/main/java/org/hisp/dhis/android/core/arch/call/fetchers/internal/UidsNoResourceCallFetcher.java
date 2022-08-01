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

package org.hisp.dhis.android.core.arch.call.fetchers.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.call.queries.internal.UidsQuery;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class UidsNoResourceCallFetcher<P> implements CallFetcher<P> {

    private final Set<String> uids;
    private final int limit;
    private final APICallExecutor apiCallExecutor;

    protected UidsNoResourceCallFetcher(Set<String> uids, int limit, APICallExecutor apiCallExecutor) {
        this.uids = uids;
        this.limit = limit;
        this.apiCallExecutor = apiCallExecutor;
    }

    protected abstract retrofit2.Call<Payload<P>> getCall(UidsQuery query);

    @Override
    public final List<P> fetch() throws D2Error {
        if (uids.isEmpty()) {
            return Collections.emptyList();
        }

        List<P> objects = new ArrayList<>();
        if (!uids.isEmpty()) {
            List<Set<String>> partitions = CollectionsHelper.setPartition(uids, limit);

            for (Set<String> partitionUids : partitions) {
                UidsQuery uidQuery = UidsQuery.create(partitionUids);
                List<P> callObjects = apiCallExecutor.executePayloadCall(getCall(uidQuery));
                objects.addAll(transform(callObjects));
            }
        }
        return objects;
    }

    protected List<P> transform(List<P> list) {
        return list;
    }
}