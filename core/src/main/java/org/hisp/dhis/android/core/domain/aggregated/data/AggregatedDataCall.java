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
package org.hisp.dhis.android.core.domain.aggregated.data;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.calls.factories.QueryCallFactory;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationQuery;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueQuery;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

@SuppressWarnings("PMD.ExcessiveImports")
final class AggregatedDataCall implements Callable<Unit> {

    private final D2CallExecutor d2CallExecutor;

    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory;
    private final QueryCallFactory<DataSetCompleteRegistration,
            DataSetCompleteRegistrationQuery> dataSetCompleteRegistrationCallFactory;
    private final IdentifiableObjectStore<DataSet> dataSetStore;
    private final PeriodStore periodStore;
    private final UserOrganisationUnitLinkStore organisationUnitStore;
    private final ForeignKeyCleaner foreignKeyCleaner;

    @Inject
    AggregatedDataCall(@NonNull D2CallExecutor d2CallExecutor,
                       @NonNull ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
                       @NonNull QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory,
                       @NonNull QueryCallFactory<DataSetCompleteRegistration, DataSetCompleteRegistrationQuery>
                               dataSetCompleteRegistrationCallFactory,
                       @NonNull IdentifiableObjectStore<DataSet> dataSetStore,
                       @NonNull PeriodStore periodStore,
                       @NonNull UserOrganisationUnitLinkStore organisationUnitStore,
                       @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.d2CallExecutor = d2CallExecutor;
        this.systemInfoRepository = systemInfoRepository;
        this.dataValueCallFactory = dataValueCallFactory;
        this.dataSetCompleteRegistrationCallFactory = dataSetCompleteRegistrationCallFactory;
        this.dataSetStore = dataSetStore;
        this.periodStore = periodStore;
        this.organisationUnitStore = organisationUnitStore;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Unit call() throws Exception {
        return d2CallExecutor.executeD2CallTransactionally(() -> {
            systemInfoRepository.download().call();

            List<String> dataSetUids = Collections.unmodifiableList(dataSetStore.selectUids());
            Set<String> periodIds = Collections.unmodifiableSet(
                    selectPeriodIds(periodStore.selectAll()));
            List<String> organisationUnitUids = Collections.unmodifiableList(
                    organisationUnitStore.queryRootCaptureOrganisationUnitUids());

            DataValueQuery dataValueQuery = DataValueQuery.create(dataSetUids, periodIds, organisationUnitUids);

            dataValueCallFactory.create(dataValueQuery).call();

            DataSetCompleteRegistrationQuery dataSetCompleteRegistrationQuery =
                    DataSetCompleteRegistrationQuery.create(dataSetUids, periodIds, organisationUnitUids);

            Callable<List<DataSetCompleteRegistration>> dataSetCompleteRegistrationCall =
                    dataSetCompleteRegistrationCallFactory.create(dataSetCompleteRegistrationQuery);

            dataSetCompleteRegistrationCall.call();

            foreignKeyCleaner.cleanForeignKeyErrors();

            return new Unit();
        });

    }

    private Set<String> selectPeriodIds(Collection<Period> periods) {
        Set<String> periodIds = new HashSet<>();

        for (Period period : periods) {
            periodIds.add(period.periodId());
        }
        return periodIds;
    }
}