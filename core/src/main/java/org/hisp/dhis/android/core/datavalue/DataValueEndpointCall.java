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

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.calls.factories.QueryCallFactory;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.PayloadResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalResourceCallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.resource.ResourceModel;

import static org.hisp.dhis.android.core.utils.Utils.commaSeparatedCollectionValues;

public final class DataValueEndpointCall {

    public static final QueryCallFactory<DataValue, DataValueQuery> FACTORY = new QueryCallFactory<DataValue, DataValueQuery>() {

        private final ResourceModel.Type resourceType = ResourceModel.Type.DATA_VALUE;

        @Override
        protected CallFetcher<DataValue> fetcher(GenericCallData data, final DataValueQuery query) {

            final DataValueService dataValueService = data.retrofit().create(DataValueService.class);

            return new PayloadResourceCallFetcher<DataValue>(data.resourceHandler(), resourceType) {
                @Override
                protected retrofit2.Call<Payload<DataValue>> getCall(String lastUpdated) {
                    return dataValueService.getDataValues(
                            DataValue.allFields,
                            DataValue.lastUpdated.gt(lastUpdated),
                            commaSeparatedCollectionValues(query.dataSetUids()),
                            commaSeparatedCollectionValues(query.periodIds()),
                            commaSeparatedCollectionValues(query.orgUnitUids()),
                            Boolean.FALSE);
                }
            };
        }

        @Override
        protected CallProcessor<DataValue> processor(GenericCallData data, DataValueQuery query) {
            return new TransactionalResourceCallProcessor<>(
                    data,
                    DataValueHandler.create(data.databaseAdapter()),
                    ResourceModel.Type.DATA_VALUE,
                    new DataValueModelBuilder()
            );
        }
    };
}
