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

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.calls.factories.NoArgumentsCallFactory;
import org.hisp.dhis.android.core.calls.factories.QueryCallFactory;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCall;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationQuery;
import org.hisp.dhis.android.core.dataset.DataSetStore;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueEndpointCall;
import org.hisp.dhis.android.core.datavalue.DataValueQuery;
import org.hisp.dhis.android.core.period.PeriodModel;
import org.hisp.dhis.android.core.period.PeriodStore;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

@SuppressWarnings("PMD.ExcessiveImports")
public final class AggregatedDataCall extends SyncCall<Unit> {

    private final Retrofit retrofit;
    private final DatabaseAdapter databaseAdapter;

    private final NoArgumentsCallFactory<SystemInfo> systemInfoCallFactory;
    private final DHISVersionManager versionManager;
    private final QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory;
    private final QueryCallFactory<DataSetCompleteRegistration,
            DataSetCompleteRegistrationQuery> dataSetCompleteRegistrationCallFactory;
    private final IdentifiableObjectStore<DataSet> dataSetStore;
    private final ObjectWithoutUidStore<PeriodModel> periodStore;
    private final UserOrganisationUnitLinkStoreInterface organisationUnitStore;

    private AggregatedDataCall(@NonNull DatabaseAdapter databaseAdapter,
                               @NonNull Retrofit retrofit,
                               @NonNull NoArgumentsCallFactory<SystemInfo> systemInfoCallFactory,
                               @NonNull DHISVersionManager versionManager,
                               @NonNull QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory,
                               @NonNull QueryCallFactory<DataSetCompleteRegistration, DataSetCompleteRegistrationQuery>
                                       dataSetCompleteRegistrationCallFactory,
                               @NonNull IdentifiableObjectStore<DataSet> dataSetStore,
                               @NonNull ObjectWithoutUidStore<PeriodModel> periodStore,
                               @NonNull UserOrganisationUnitLinkStoreInterface organisationUnitStore) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.systemInfoCallFactory = systemInfoCallFactory;
        this.versionManager = versionManager;
        this.dataValueCallFactory = dataValueCallFactory;
        this.dataSetCompleteRegistrationCallFactory = dataSetCompleteRegistrationCallFactory;
        this.dataSetStore = dataSetStore;
        this.periodStore = periodStore;
        this.organisationUnitStore = organisationUnitStore;
    }

    @Override
    public Unit call() throws Exception {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor();

        return executor.executeD2CallTransactionally(databaseAdapter, new Callable<Unit>() {

            @Override
            public Unit call() throws D2CallException {
                SystemInfo systemInfo = executor.executeD2Call(systemInfoCallFactory.create());
                GenericCallData genericCallData = GenericCallData.create(databaseAdapter, retrofit,
                        systemInfo.serverDate(), versionManager);

                Set<String> dataSetUids = Collections.unmodifiableSet(dataSetStore.selectUids());
                Set<String> periodIds = Collections.unmodifiableSet(
                        selectPeriodIds(periodStore.selectAll()));
                Set<String> organisationUnitUids = Collections.unmodifiableSet(
                        organisationUnitStore.queryRootOrganisationUnitUids());

                DataValueQuery dataValueQuery = DataValueQuery.create(dataSetUids, periodIds, organisationUnitUids);

                Call<List<DataValue>> dataValueEndpointCall = dataValueCallFactory
                        .create(genericCallData, dataValueQuery);
                executor.executeD2Call(dataValueEndpointCall);

                DataSetCompleteRegistrationQuery dataSetCompleteRegistrationQuery =
                        DataSetCompleteRegistrationQuery.create(dataSetUids, periodIds, organisationUnitUids);

                Call<List<DataSetCompleteRegistration>> dataSetCompleteRegistrationCall =
                        dataSetCompleteRegistrationCallFactory.create(genericCallData,
                                dataSetCompleteRegistrationQuery);

                executor.executeD2Call(dataSetCompleteRegistrationCall);

                return new Unit();
            }
        });

    }

    private Set<String> selectPeriodIds(Collection<PeriodModel> periodModels) {
        Set<String> periodIds = new HashSet<>();

        for (PeriodModel period : periodModels) {
            periodIds.add(period.periodId());
        }
        return periodIds;
    }

    public static AggregatedDataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                            D2InternalModules internalModules) {
        return new AggregatedDataCall(
                databaseAdapter,
                retrofit,
                internalModules.systemInfo.callFactory,
                internalModules.systemInfo.publicModule.versionManager,
                DataValueEndpointCall.FACTORY,
                DataSetCompleteRegistrationCall.FACTORY,
                DataSetStore.create(databaseAdapter),
                PeriodStore.create(databaseAdapter),
                UserOrganisationUnitLinkStore.create(databaseAdapter));
    }
}
