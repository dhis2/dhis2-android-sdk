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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseEndpointCall;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SimpleCallFactory;
import org.hisp.dhis.android.core.resource.ResourceModel;

import retrofit2.Response;

public final class DataSetAccessEndpointCall extends BaseEndpointCall<DataSet> {
    private final GenericCallData data;
    private final DataSetService dataSetService;

    private DataSetAccessEndpointCall(GenericCallData data, DataSetService dataSetService) {
        this.data = data;
        this.dataSetService = dataSetService;
    }

    @Override
    protected Response<Payload<DataSet>> callBody() throws Exception {
        String lastUpdated = data.resourceHandler().getLastUpdated(ResourceModel.Type.DATA_SET);
        return dataSetService.getDataSetsForAccess(DataSet.uidAndAccessRead, DataSet.lastUpdated.gt(lastUpdated),
                Boolean.FALSE).execute();
    }

    static final SimpleCallFactory<Payload<DataSet>> FACTORY = new SimpleCallFactory<Payload<DataSet>>() {
        @Override
        public Call<Response<Payload<DataSet>>> create(GenericCallData data) {
            return new DataSetAccessEndpointCall(data, data.retrofit().create(DataSetService.class));
        }
    };
}