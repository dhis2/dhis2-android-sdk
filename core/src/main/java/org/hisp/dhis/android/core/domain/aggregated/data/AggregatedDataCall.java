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

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore;
import org.hisp.dhis.android.core.dataapproval.DataApproval;
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalQuery;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationQuery;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.internal.DataValueQuery;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.period.internal.PeriodForDataSetManager;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;

@SuppressWarnings("PMD.ExcessiveImports")
final class AggregatedDataCall {

    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final DHISVersionManager dhisVersionManager;
    private final QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory;
    private final QueryCallFactory<DataSetCompleteRegistration,
            DataSetCompleteRegistrationQuery> dataSetCompleteRegistrationCallFactory;
    private final QueryCallFactory<DataApproval, DataApprovalQuery> dataApprovalCallFactory;
    private final UserOrganisationUnitLinkStore organisationUnitStore;
    private final CategoryOptionComboStore categoryOptionComboStore;
    private final RxAPICallExecutor rxCallExecutor;

    private final DataSetCollectionRepository dataSetCollectionRepository;
    private final PeriodForDataSetManager periodManager;

    @Inject
    AggregatedDataCall(@NonNull ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
                       @NonNull DHISVersionManager dhisVersionManager,
                       @NonNull QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory,
                       @NonNull QueryCallFactory<DataSetCompleteRegistration, DataSetCompleteRegistrationQuery>
                               dataSetCompleteRegistrationCallFactory,
                       @NonNull QueryCallFactory<DataApproval, DataApprovalQuery> dataApprovalCallFactory,
                       @NonNull UserOrganisationUnitLinkStore organisationUnitStore,
                       @NonNull CategoryOptionComboStore categoryOptionComboStore,
                       @NonNull RxAPICallExecutor rxCallExecutor,
                       @NonNull DataSetCollectionRepository dataSetCollectionRepository,
                       @NonNull PeriodForDataSetManager periodManager) {
        this.systemInfoRepository = systemInfoRepository;
        this.dhisVersionManager = dhisVersionManager;
        this.dataValueCallFactory = dataValueCallFactory;
        this.dataSetCompleteRegistrationCallFactory = dataSetCompleteRegistrationCallFactory;
        this.dataApprovalCallFactory = dataApprovalCallFactory;
        this.organisationUnitStore = organisationUnitStore;
        this.categoryOptionComboStore = categoryOptionComboStore;
        this.rxCallExecutor = rxCallExecutor;

        this.dataSetCollectionRepository = dataSetCollectionRepository;
        this.periodManager = periodManager;
    }

    Observable<D2Progress> download() {
        D2ProgressManager progressManager = new D2ProgressManager(null);

        Observable<D2Progress> observable = systemInfoRepository.download(true)
                .toSingle(() -> progressManager.increaseProgress(SystemInfo.class, false))
                .flatMapObservable(progress -> selectDataSetsAndDownload(progressManager, progress));
        return rxCallExecutor.wrapObservableTransactionally(observable, true);
    }

    private Observable<D2Progress> selectDataSetsAndDownload(D2ProgressManager progressManager,
                                                             D2Progress systemInfoProgress) {
        return Observable.fromArray(PeriodType.values()).flatMap(periodType ->
                dataSetCollectionRepository.byPeriodType().eq(periodType).get().flatMapObservable(dataSets -> {
                    if (dataSets.isEmpty()) {
                        return Observable.empty();
                    } else {
                        List<Period> periods = periodManager.getPeriodsForDataSets(periodType, dataSets);
                        List<String> periodIds = selectPeriodIds(periods);
                        return downloadInternal(dataSets, periodIds, progressManager, systemInfoProgress);
                    }
                }));
    }

    private Observable<D2Progress> downloadInternal(List<DataSet> dataSets,
                                                    List<String> periodIds,
                                                    D2ProgressManager progressManager,
                                                    D2Progress systemInfoProgress) {
        List<String> organisationUnitUids = Collections.unmodifiableList(
                organisationUnitStore.queryRootCaptureOrganisationUnitUids());

        List<String> dataSetUids
                = Collections.unmodifiableList(UidsHelper.getUidsList(dataSets));

        DataValueQuery dataValueQuery = DataValueQuery.create(dataSetUids, periodIds, organisationUnitUids);

        Single<D2Progress> dataValueSingle = Single.fromCallable(dataValueCallFactory.create(dataValueQuery))
                .map(dataValues -> progressManager.increaseProgress(DataValue.class, false));

        DataSetCompleteRegistrationQuery dataSetCompleteRegistrationQuery =
                DataSetCompleteRegistrationQuery.create(dataSetUids, periodIds, organisationUnitUids);

        Single<D2Progress> dataSetCompleteRegistrationSingle = Single.fromCallable(
                dataSetCompleteRegistrationCallFactory.create(dataSetCompleteRegistrationQuery)).map(dataValues ->
                progressManager.increaseProgress(DataSetCompleteRegistration.class, false));


        @SuppressWarnings("PMD.NonStaticInitializer")
        ArrayList<Single<D2Progress>> list = new ArrayList<Single<D2Progress>>() {{
            add(Single.just(systemInfoProgress));
            add(dataValueSingle);
            add(dataSetCompleteRegistrationSingle);
        }};

        if (!dhisVersionManager.is2_29()) {
            Single<D2Progress> approvalSingle = getApprovalSingle(dataSets, periodIds, progressManager);
            if (approvalSingle != null) {
                list.add(approvalSingle);
            }
        }

        return Single.merge(list).toObservable();
    }

    private Single<D2Progress> getApprovalSingle(List<DataSet> dataSets, List<String> periodIds,
                                                     D2ProgressManager progressManager) {
        List<DataSet> dataSetsWithWorkflow = new ArrayList<>();
        Set<String> workflowUids = new HashSet<>();
        for (DataSet ds : dataSets) {
            if (ds.workflow() != null) {
                dataSetsWithWorkflow.add(ds);
                workflowUids.add(ds.workflow().uid());
            }
        }

        if (workflowUids.isEmpty()) {
            return null;
        } else {
            Set<String> attributeOptionComboUids = getAttributeOptionCombosUidsFrom(dataSetsWithWorkflow);
            List<String> organisationUnitsUids = organisationUnitStore.queryOrganisationUnitUidsByScope(
                    OrganisationUnit.Scope.SCOPE_DATA_CAPTURE);


            DataApprovalQuery dataApprovalQuery = DataApprovalQuery.create(workflowUids,
                    organisationUnitsUids, periodIds, attributeOptionComboUids);

            return Single.fromCallable(
                    dataApprovalCallFactory.create(dataApprovalQuery)).map(dataApprovals ->
                    progressManager.increaseProgress(DataApproval.class, false));
        }
    }

    private List<String> selectPeriodIds(Collection<Period> periods) {
        List<String> periodIds = new ArrayList<>(periods.size());

        for (Period period : periods) {
            periodIds.add(period.periodId());
        }
        return periodIds;
    }

    private Set<String> getAttributeOptionCombosUidsFrom(Collection<DataSet> dataSetsWithWorkflow) {

        Set<String> dataSetsWithWorkflowCategoryCombos = new HashSet<>();
        for (DataSet dataSet : dataSetsWithWorkflow) {
            String uid = dataSet.categoryCombo().uid();
            dataSetsWithWorkflowCategoryCombos.add(uid);
        }

        List<CategoryOptionCombo> categoryOptionCombos =
                categoryOptionComboStore.selectWhere("categoryCombo IN ("
                        + CollectionsHelper.commaAndSpaceSeparatedArrayValues(
                        CollectionsHelper.withSingleQuotationMarksArray(dataSetsWithWorkflowCategoryCombos))
                        + ")");

        Set<String> attributeOptionCombosUids = new HashSet<>();
        for (CategoryOptionCombo categoryOptionCombo : categoryOptionCombos) {
            String uid = categoryOptionCombo.uid();
            attributeOptionCombosUids.add(uid);
        }
        return attributeOptionCombosUids;
    }
}