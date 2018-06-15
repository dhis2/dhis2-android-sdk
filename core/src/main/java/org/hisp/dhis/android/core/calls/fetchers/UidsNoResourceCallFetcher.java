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

package org.hisp.dhis.android.core.calls.fetchers;

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.UidsQuery;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class UidsNoResourceCallFetcher<P> implements CallFetcher<P> {

    private final Set<String> uids;
    private final int limit;

    protected UidsNoResourceCallFetcher(Set<String> uids, int limit) {
        this.uids = uids;
        this.limit = limit;
    }

    protected abstract retrofit2.Call<Payload<P>> getCall(UidsQuery query);

    @Override
    public List<P> fetch() throws D2CallException {
        if (uids.isEmpty()) {
            return Collections.emptyList();
        }

        return executeCall(uids);
    }

    private List<P> executeCall(Set<String> uids) throws D2CallException {
        List<P> objects = new ArrayList<>();
        if (!uids.isEmpty()) {
            APICallExecutor executor = new APICallExecutor();
            List<Set<String>> partitions = Utils.setPartition(uids, limit);

            for (Set<String> partitionUids : partitions) {
                UidsQuery uidQuery = UidsQuery.create(partitionUids);
                List<P> callObjects = executor.executePayloadCall(getCall(uidQuery));
                objects.addAll(callObjects);
            }
        }
        return objects;
    }
}