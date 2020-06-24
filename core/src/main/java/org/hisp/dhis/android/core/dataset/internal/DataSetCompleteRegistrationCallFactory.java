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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactoryImpl;
import org.hisp.dhis.android.core.arch.call.fetchers.internal.CallFetcher;
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData;
import org.hisp.dhis.android.core.arch.call.processors.internal.CallProcessor;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Call;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.commaSeparatedCollectionValues;

@Reusable
final class DataSetCompleteRegistrationCallFactory extends QueryCallFactoryImpl<DataSetCompleteRegistration,
        DataSetCompleteRegistrationQuery> {

    private final DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;
    private final Handler<DataSetCompleteRegistration> handler;
    private final DataSetCompleteRegistrationService service;

    @Inject
    DataSetCompleteRegistrationCallFactory(GenericCallData genericCallData,
                                           APICallExecutor apiCallExecutor,
                                           DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore,
                                           Handler<DataSetCompleteRegistration> handler,
                                           DataSetCompleteRegistrationService service) {
        super(genericCallData, apiCallExecutor);
        this.dataSetCompleteRegistrationStore = dataSetCompleteRegistrationStore;
        this.handler = handler;
        this.service = service;
    }

    @Override
    protected CallFetcher<DataSetCompleteRegistration> fetcher(
            final DataSetCompleteRegistrationQuery query) {

        return new DataSetCompleteRegistrationCallFetcher(
                query.dataSetUids(),
                query.periodIds(),
                query.rootOrgUnitUids(),
                query.lastUpdatedStr(), apiCallExecutor) {

            @Override
            protected Call<DataSetCompleteRegistrationPayload> getCall(
                    DataSetCompleteRegistrationQuery query) {
                return service.getDataSetCompleteRegistrations(
                        DataSetCompleteRegistrationFields.allFields,
                        query.lastUpdatedStr(),
                        commaSeparatedCollectionValues(query.dataSetUids()),
                        commaSeparatedCollectionValues(query.periodIds()),
                        commaSeparatedCollectionValues(query.rootOrgUnitUids()),
                        Boolean.TRUE,
                        Boolean.FALSE);
            }
        };
    }

    @Override
    protected CallProcessor<DataSetCompleteRegistration> processor(DataSetCompleteRegistrationQuery query) {
        return new DataSetCompleteRegistrationCallProcessor(data.databaseAdapter(), dataSetCompleteRegistrationStore,
                handler, query);
    }
}
