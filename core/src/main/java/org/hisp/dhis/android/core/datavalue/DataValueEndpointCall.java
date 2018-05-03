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

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericEndpointCallImpl;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.io.IOException;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.android.core.utils.Utils.commaSeparatedCollectionValues;

public final class DataValueEndpointCall extends GenericEndpointCallImpl<DataValue, DataValueModel,
        DataValueQuery> {
    private final DataValueService dataValueService;

    private DataValueEndpointCall(GenericCallData data, DataValueService dataValueService,
                                  GenericHandler<DataValue, DataValueModel> dataValueHandler,
                                  DataValueQuery query) {
        super(data, dataValueHandler, ResourceModel.Type.DATA_VALUE, new DataValueModelBuilder(), query);
        this.dataValueService = dataValueService;
    }

    @Override
    protected Call<Payload<DataValue>> getCall(DataValueQuery query, String lastUpdated) throws IOException {
        return dataValueService.getDataValues(
                DataValue.allFields,
                DataValue.lastUpdated.gt(lastUpdated),
                commaSeparatedCollectionValues(query.dataSetUids()),
                commaSeparatedCollectionValues(query.periodIds()),
                commaSeparatedCollectionValues(query.orgUnitUids()),
                Boolean.FALSE);
    }

    public interface Factory {
        DataValueEndpointCall create(GenericCallData data, Set<String> dataSetUids, Set<String> periodIds,
                                     Set<String> orgUnitUids);
    }

    public static final DataValueEndpointCall.Factory FACTORY = new DataValueEndpointCall.Factory() {
        @Override
        public DataValueEndpointCall create(GenericCallData data, Set<String> dataSetUids, Set<String> periodIds,
                                            Set<String> orgUnitUids) {
            return new DataValueEndpointCall(data, data.retrofit().create(DataValueService.class),
                    DataValueHandler.create(data.databaseAdapter()), DataValueQuery.create(dataSetUids, periodIds,
                    orgUnitUids));
        }
    };
}
