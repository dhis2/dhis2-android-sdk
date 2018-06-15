/* * Copyright (c) 2017, University of Oslo
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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.calls.factories.QueryCallFactory;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.ListNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalNoResourceCallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;

public final class TrackedEntityAttributeReservedValueEndpointCall {

    public static final QueryCallFactory<TrackedEntityAttributeReservedValue,
            TrackedEntityAttributeReservedValueQuery> FACTORY =
            new QueryCallFactory<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueQuery>() {

                @Override
                protected CallFetcher<TrackedEntityAttributeReservedValue> fetcher(
                        GenericCallData data,
                        final TrackedEntityAttributeReservedValueQuery query) {

                    final TrackedEntityAttributeReservedValueService service
                            = data.retrofit().create(TrackedEntityAttributeReservedValueService.class);

                    return new ListNoResourceCallFetcher<TrackedEntityAttributeReservedValue>() {
                        @Override
                        protected retrofit2.Call<List<TrackedEntityAttributeReservedValue>> getCall() {
                            return service.generateAndReserve(
                                    query.trackedEntityAttributeUid(),
                                    query.numberToReserve(),
                                    query.organisationUnit().code());
                        }
                    };
                }

                @Override
                protected CallProcessor<TrackedEntityAttributeReservedValue> processor(
                        GenericCallData data,
                        final TrackedEntityAttributeReservedValueQuery query) {
                    return new TransactionalNoResourceCallProcessor<>(
                            data.databaseAdapter(),
                            TrackedEntityAttributeReservedValueHandler.create(data.databaseAdapter()),
                            new TrackedEntityAttributeReservedValueModelBuilder(
                                    query.organisationUnit(), query.trackedEntityAttributePattern())
                    );
                }
            };
}