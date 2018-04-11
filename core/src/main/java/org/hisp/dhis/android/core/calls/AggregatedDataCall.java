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
package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.DataSetModel;
import org.hisp.dhis.android.core.dataset.DataSetStore;
import org.hisp.dhis.android.core.datavalue.DataValueEndpointCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.period.PeriodModel;
import org.hisp.dhis.android.core.period.PeriodStore;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Response;
import retrofit2.Retrofit;

public class AggregatedDataCall extends TransactionalCall {

    private final DataValueEndpointCall.Factory dataValueCallFactory;
    private final IdentifiableObjectStore<DataSetModel> dataSetStore;
    private final ObjectWithoutUidStore<PeriodModel> periodStore;
    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;

    public AggregatedDataCall(@NonNull GenericCallData genericCallData,
                              @NonNull DataValueEndpointCall.Factory dataValueCallFactory,
                              @NonNull IdentifiableObjectStore<DataSetModel> dataSetStore,
                              @NonNull ObjectWithoutUidStore<PeriodModel> periodStore,
                              @NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore) {
        super(genericCallData);
        this.dataValueCallFactory = dataValueCallFactory;
        this.dataSetStore = dataSetStore;
        this.periodStore = periodStore;
        this.organisationUnitStore = organisationUnitStore;
    }

    @Override
    public Response callBody() throws Exception {
        DataValueEndpointCall dataValueEndpointCall = dataValueCallFactory.create(data, dataSetStore.selectUids(),
                selectPeriodIds(periodStore.selectAll(PeriodModel.factory)),
                organisationUnitStore.selectUids());
        return dataValueEndpointCall.call();
    }

    private Set<String> selectPeriodIds(Set<PeriodModel> periodModels) {
        Set<String> periodIds = new HashSet<>();

        for (PeriodModel period : periodModels) {
            periodIds.add(period.periodId());
        }
        return periodIds;
    }

    public static AggregatedDataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit) {
        return new AggregatedDataCall(
                GenericCallData.create(databaseAdapter, retrofit),
                DataValueEndpointCall.FACTORY,
                DataSetStore.create(databaseAdapter),
                PeriodStore.create(databaseAdapter),
                OrganisationUnitStore.create(databaseAdapter));
    }
}
