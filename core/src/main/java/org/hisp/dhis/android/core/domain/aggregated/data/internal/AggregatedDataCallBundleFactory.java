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

package org.hisp.dhis.android.core.domain.aggregated.data.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.period.internal.PeriodForDataSetManager;
import org.hisp.dhis.android.core.settings.DataSetSetting;
import org.hisp.dhis.android.core.settings.DataSetSettings;
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository;
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class AggregatedDataCallBundleFactory {

    private final IdentifiableObjectStore<DataSet> dataSetStore;
    private final UserOrganisationUnitLinkStore organisationUnitStore;
    private final DataSetSettingsObjectRepository dataSetSettingsObjectRepository;
    private final PeriodForDataSetManager periodManager;

    @Inject
    AggregatedDataCallBundleFactory(IdentifiableObjectStore<DataSet> dataSetStore,
                                    UserOrganisationUnitLinkStore organisationUnitStore,
                                    DataSetSettingsObjectRepository dataSetSettingsObjectRepository,
                                    PeriodForDataSetManager periodManager) {
        this.dataSetStore = dataSetStore;
        this.organisationUnitStore = organisationUnitStore;
        this.dataSetSettingsObjectRepository = dataSetSettingsObjectRepository;
        this.periodManager = periodManager;
    }

    List<AggregatedDataCallBundle> getDataValueQueries() {
        List<AggregatedDataCallBundle> queries = new ArrayList<>();

        DataSetSettings dataSetSettings = dataSetSettingsObjectRepository.blockingGet();

        List<String> organisationUnitUids = Collections.unmodifiableList(
                organisationUnitStore.queryRootCaptureOrganisationUnitUids());

        for (PeriodType periodType : PeriodType.values()) {
            List<DataSet> dataSets = getDataSetsInPeriodType(periodType);
            if (dataSets.isEmpty()) {
                continue;
            }

            queries.addAll(getDataValueQueriesForDataSets(dataSets, periodType, dataSetSettings, organisationUnitUids));
        }
        return queries;
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    List<AggregatedDataCallBundle> getDataValueQueriesForDataSets(Collection<DataSet> dataSets,
                                                                  PeriodType periodType,
                                                                  DataSetSettings dataSetSettings,
                                                                  List<String> organisationUnitUids) {
        Map<String, List<DataSet>> pastFutureKeyDataSet = new HashMap<>();
        for (DataSet dataSet : dataSets) {
            String pastFuturePair = getPastFuturePair(dataSetSettings, dataSet, periodType);

            if (!pastFutureKeyDataSet.containsKey(pastFuturePair)) {
                pastFutureKeyDataSet.put(pastFuturePair, new ArrayList<>());
            }
            pastFutureKeyDataSet.get(pastFuturePair).add(dataSet);
        }

        List<AggregatedDataCallBundle> queries = new ArrayList<>();
        for (Map.Entry<String, List<DataSet>> entry : pastFutureKeyDataSet.entrySet()) {
            PastFuturePair pair = new PastFuturePair(entry.getKey());

            List<Period> periods = periodManager.getPeriodsInRange(periodType, pair.past, pair.future);

            if (!periods.isEmpty()) {
                List<String> periodIds = selectPeriodIds(periods);

                AggregatedDataCallBundle bundle = AggregatedDataCallBundle.builder()
                        .dataSets(entry.getValue())
                        .periodIds(periodIds)
                        .orgUnitUids(organisationUnitUids)
                        .build();

                queries.add(bundle);
            }
        }
        return queries;
    }

    private String getPastFuturePair(DataSetSettings dataSetSettings, DataSet dataSet, PeriodType periodType) {
        int pastPeriods = getPastPeriods(dataSetSettings, dataSet, periodType);
        int futurePeriods = dataSet.openFuturePeriods() == null ? 1 : dataSet.openFuturePeriods();

        return new PastFuturePair(pastPeriods, futurePeriods).toKey();
    }

    private Integer getPastPeriods(DataSetSettings dataSetSettings, DataSet dataSet, PeriodType periodType) {
        if (dataSetSettings != null) {
            DataSetSetting specificSetting = dataSetSettings.specificSettings().get(dataSet.uid());
            DataSetSetting globalSetting = dataSetSettings.globalSettings();

            if (hasPeriodDSDownload(specificSetting)) {
                return specificSetting.periodDSDownload();
            } else if (hasPeriodDSDownload(globalSetting)) {
                return globalSetting.periodDSDownload();
            }
        }
        return periodType.getDefaultPastPeriods();
    }

    private boolean hasPeriodDSDownload(DataSetSetting dataSetSetting) {
        return dataSetSetting != null && dataSetSetting.periodDSDownload() != null;
    }

    private List<String> selectPeriodIds(Collection<Period> periods) {
        List<String> periodIds = new ArrayList<>(periods.size());

        for (Period period : periods) {
            periodIds.add(period.periodId());
        }
        return periodIds;
    }

    private List<DataSet> getDataSetsInPeriodType(PeriodType periodType) {
        String periodTypeClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataSetTableInfo.Columns.PERIOD_TYPE, periodType.name()).build();

        return dataSetStore.selectWhere(periodTypeClause);
    }

    static class PastFuturePair {
        Integer past;
        Integer future;

        private static String SEP = "x";

        PastFuturePair(Integer past, Integer future) {
            this.past = past;
            this.future = future;
        }

        PastFuturePair(String key) {
            this(Integer.parseInt(key.split(SEP)[0]), Integer.parseInt(key.split(SEP)[1]));
        }

        String toKey() {
            return past + SEP + future;
        }
    }
}