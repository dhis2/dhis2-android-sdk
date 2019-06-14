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

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCallFactory;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore;
import org.hisp.dhis.android.core.dataapproval.DataApproval;
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalQuery;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationQuery;
import org.hisp.dhis.android.core.dataset.internal.DataSetFields;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.internal.DataValueQuery;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.internal.PeriodStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

@SuppressWarnings("PMD.ExcessiveImports")
final class AggregatedDataCall {

    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory;
    private final QueryCallFactory<DataSetCompleteRegistration,
            DataSetCompleteRegistrationQuery> dataSetCompleteRegistrationCallFactory;
    private final QueryCallFactory<DataApproval, DataApprovalQuery> dataApprovalCallFactory;
    private final IdentifiableObjectStore<DataSet> dataSetStore;
    private final PeriodStore periodStore;
    private final UserOrganisationUnitLinkStore organisationUnitStore;
    private final CategoryOptionComboStore categoryOptionComboStore;
    private final RxAPICallExecutor rxCallExecutor;

    @Inject
    AggregatedDataCall(@NonNull ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
                       @NonNull QueryCallFactory<DataValue, DataValueQuery> dataValueCallFactory,
                       @NonNull QueryCallFactory<DataSetCompleteRegistration, DataSetCompleteRegistrationQuery>
                               dataSetCompleteRegistrationCallFactory,
                       @NonNull QueryCallFactory<DataApproval, DataApprovalQuery> dataApprovalCallFactory,
                       @NonNull IdentifiableObjectStore<DataSet> dataSetStore,
                       @NonNull PeriodStore periodStore,
                       @NonNull UserOrganisationUnitLinkStore organisationUnitStore,
                       @NonNull CategoryOptionComboStore categoryOptionComboStore,
                       @NonNull RxAPICallExecutor rxCallExecutor) {
        this.systemInfoRepository = systemInfoRepository;
        this.dataValueCallFactory = dataValueCallFactory;
        this.dataSetCompleteRegistrationCallFactory = dataSetCompleteRegistrationCallFactory;
        this.dataApprovalCallFactory = dataApprovalCallFactory;
        this.dataSetStore = dataSetStore;
        this.periodStore = periodStore;
        this.organisationUnitStore = organisationUnitStore;
        this.categoryOptionComboStore = categoryOptionComboStore;
        this.rxCallExecutor = rxCallExecutor;
    }

    Observable<D2Progress> download() {
        D2ProgressManager progressManager = new D2ProgressManager(4);

        Observable<D2Progress> observable = systemInfoRepository.download()
                .toSingle(() -> progressManager.increaseProgressAndCompleteWithCount(SystemInfo.class))
                .flatMapObservable(progress -> downloadInternal(progressManager, progress));
        return rxCallExecutor.wrapObservableTransactionally(observable, true);
    }

    private Observable<D2Progress> downloadInternal(D2ProgressManager progressManager, D2Progress systemInfoProgress) {
        List<String> dataSetUids = Collections.unmodifiableList(dataSetStore.selectUids());
        Set<String> periodIds = Collections.unmodifiableSet(selectPeriodIds(periodStore.selectAll()));
        List<String> organisationUnitUids = Collections.unmodifiableList(
                organisationUnitStore.queryRootCaptureOrganisationUnitUids());

        DataValueQuery dataValueQuery = DataValueQuery.create(dataSetUids, periodIds, organisationUnitUids);

        Single<D2Progress> dataValueSingle = Single.fromCallable(dataValueCallFactory.create(dataValueQuery))
                .map(dataValues -> progressManager.increaseProgressAndCompleteWithCount(DataValue.class));

        DataSetCompleteRegistrationQuery dataSetCompleteRegistrationQuery =
                DataSetCompleteRegistrationQuery.create(dataSetUids, periodIds, organisationUnitUids);

        Single<D2Progress> dataSetCompleteRegistrationSingle = Single.fromCallable(
                dataSetCompleteRegistrationCallFactory.create(dataSetCompleteRegistrationQuery)).map(dataValues ->
                        progressManager.increaseProgressAndCompleteWithCount(DataSetCompleteRegistration.class));


        List<DataSet> dataSetsWithWorkflow =
                dataSetStore.selectWhere(DataSetFields.WORKFLOW + " IS NOT NULL");

        List<UserOrganisationUnitLink> userOrganisationUnitLinks = organisationUnitStore.selectAll();

        Set<String> workflowUids = getWorkflowsUidsFrom(dataSetsWithWorkflow);
        Set<String> attributeOptionComboUids = getAttributeOptionCombosUidsFrom(dataSetsWithWorkflow);
        Set<String> organisationUnitsUids = getOrganisationUnitsUidsFrom(userOrganisationUnitLinks);

        Date startDate = periodStore.getOldestPeriodStartDate();
        Date endDate = new Date();

        DataApprovalQuery dataApprovalQuery = DataApprovalQuery.create(workflowUids,
                organisationUnitsUids, startDate, endDate, attributeOptionComboUids);

        Single<D2Progress> dataApprovalSingle = Single.fromCallable(
                dataApprovalCallFactory.create(dataApprovalQuery)).map(dataApprovals ->
                progressManager.increaseProgressAndCompleteWithCount(DataApproval.class));


        @SuppressWarnings("PMD.NonStaticInitializer")
        ArrayList<Single<D2Progress>> list = new ArrayList<Single<D2Progress>>() {{
            add(Single.just(systemInfoProgress));
            add(dataValueSingle);
            add(dataSetCompleteRegistrationSingle);
            add(dataApprovalSingle);
        }};

        return Single.merge(list).toObservable();
    }

    private Set<String> selectPeriodIds(Collection<Period> periods) {
        Set<String> periodIds = new HashSet<>();

        for (Period period : periods) {
            periodIds.add(period.periodId());
        }
        return periodIds;
    }

    private Set<String> getWorkflowsUidsFrom(Collection<DataSet> dataSetsWithWorkflow) {

        return dataSetsWithWorkflow.stream()
                .map(dataSet -> dataSet.workflow().uid())
                .collect(Collectors.toSet());
    }

    private Set<String> getAttributeOptionCombosUidsFrom(Collection<DataSet> dataSetsWithWorkflow) {

        Set<String> dataSetsWithWorkflowCategoryCombos = dataSetsWithWorkflow.stream()
                .map(dataSet -> dataSet.categoryCombo().uid())
                .collect(Collectors.toSet());

        List<CategoryOptionCombo> categoryOptionCombos =
                categoryOptionComboStore.selectWhere("categoryCombo IN ("
                        + Utils.commaAndSpaceSeparatedArrayValues(
                        Utils.withSingleQuotationMarksArray(dataSetsWithWorkflowCategoryCombos))
                        + ")");

        return categoryOptionCombos.stream()
                .map(categoryOptionCombo -> categoryOptionCombo.uid())
                .collect(Collectors.toSet());
    }

    private Set<String> getOrganisationUnitsUidsFrom(Collection<UserOrganisationUnitLink> userOrganisationUnitLinks) {

        return userOrganisationUnitLinks.stream()
                .map(userOrganisationUnitLink -> userOrganisationUnitLink.organisationUnit())
                .collect(Collectors.toSet());
    }
}