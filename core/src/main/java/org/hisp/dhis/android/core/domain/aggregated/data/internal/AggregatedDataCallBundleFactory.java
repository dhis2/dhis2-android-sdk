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

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.internal.PeriodForDataSetManager;
import org.hisp.dhis.android.core.settings.DataSetSetting;
import org.hisp.dhis.android.core.settings.DataSetSettings;
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class AggregatedDataCallBundleFactory {

    private final DataSetCollectionRepository dataSetRepository;
    private final OrganisationUnitCollectionRepository organisationUnitRepository;
    private final DataSetSettingsObjectRepository dataSetSettingsObjectRepository;
    private final PeriodForDataSetManager periodManager;
    private final ObjectWithoutUidStore<AggregatedDataSync> aggregatedDataSyncStore;
    private final AggregatedDataSyncLastUpdatedCalculator lastUpdatedCalculator;

    @Inject
    AggregatedDataCallBundleFactory(DataSetCollectionRepository dataSetRepository,
                                    OrganisationUnitCollectionRepository organisationUnitRepository,
                                    DataSetSettingsObjectRepository dataSetSettingsObjectRepository,
                                    PeriodForDataSetManager periodManager,
                                    ObjectWithoutUidStore<AggregatedDataSync> aggregatedDataSyncStore,
                                    AggregatedDataSyncLastUpdatedCalculator lastUpdatedCalculator) {
        this.dataSetRepository = dataSetRepository;
        this.organisationUnitRepository = organisationUnitRepository;
        this.dataSetSettingsObjectRepository = dataSetSettingsObjectRepository;
        this.periodManager = periodManager;
        this.aggregatedDataSyncStore = aggregatedDataSyncStore;
        this.lastUpdatedCalculator = lastUpdatedCalculator;
    }

    List<AggregatedDataCallBundle> getBundles() {
        List<String> rootOrganisationUnitUids = organisationUnitRepository
                .byRootOrganisationUnit(true)
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .blockingGetUids();
        List<String> allOrganisationUnitUids = organisationUnitRepository
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .blockingGetUids();

        return getBundlesInternal(
                getDataSets(),
                dataSetSettingsObjectRepository.blockingGet(),
                rootOrganisationUnitUids,
                new HashSet<>(allOrganisationUnitUids),
                getSyncValuesByDataSetUid());
    }

    private Map<String, AggregatedDataSync> getSyncValuesByDataSetUid() {
        List<AggregatedDataSync> syncValues = aggregatedDataSyncStore.selectAll();
        Map<String, AggregatedDataSync> map = new HashMap<>(syncValues.size());
        for (AggregatedDataSync v : syncValues) {
            map.put(v.dataSet(), v);
        }
        return map;
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    List<AggregatedDataCallBundle> getBundlesInternal(Collection<DataSet> dataSets,
                                                      DataSetSettings dataSetSettings,
                                                      List<String> rootOrganisationUnitUids,
                                                      Set<String> allOrganisationUnitUids,
                                                      Map<String, AggregatedDataSync> syncValues) {
        int organisationUnitsHash = allOrganisationUnitUids.hashCode();
        Map<AggregatedDataCallBundleKey, List<DataSet>> keyDataSetMap = new HashMap<>();
        for (DataSet dataSet : dataSets) {
            AggregatedDataCallBundleKey key = getBundleKey(dataSetSettings, dataSet, syncValues, organisationUnitsHash);
            if (!keyDataSetMap.containsKey(key)) {
                keyDataSetMap.put(key, new ArrayList<>());
            }
            keyDataSetMap.get(key).add(dataSet);
        }

        List<AggregatedDataCallBundle> bundles = new ArrayList<>();
        for (Map.Entry<AggregatedDataCallBundleKey, List<DataSet>> entry : keyDataSetMap.entrySet()) {
            AggregatedDataCallBundleKey key = entry.getKey();
            List<Period> periods = periodManager.getPeriodsInRange(key.periodType(), key.pastPeriods(),
                    key.futurePeriods());

            if (!periods.isEmpty()) {
                List<String> periodIds = selectPeriodIds(periods);

                AggregatedDataCallBundle bundle = AggregatedDataCallBundle.builder()
                        .key(key)
                        .dataSets(entry.getValue())
                        .periodIds(periodIds)
                        .rootOrganisationUnitUids(rootOrganisationUnitUids)
                        .allOrganisationUnitUidsSet(allOrganisationUnitUids)
                        .build();

                bundles.add(bundle);
            }
        }
        return bundles;
    }

    private AggregatedDataCallBundleKey getBundleKey(DataSetSettings dataSetSettings,
                                                     DataSet dataSet,
                                                     Map<String, AggregatedDataSync> syncValues,
                                                     int organisationUnitsHash) {
        int pastPeriods = getPastPeriods(dataSetSettings, dataSet);
        int futurePeriods = dataSet.openFuturePeriods() == null ? 1 : dataSet.openFuturePeriods();
        AggregatedDataSync syncValue = syncValues.get(dataSet.uid());
        return AggregatedDataCallBundleKey.builder()
                .periodType(dataSet.periodType())
                .pastPeriods(pastPeriods)
                .futurePeriods(futurePeriods)
                .lastUpdated(lastUpdatedCalculator.getLastUpdated(syncValue, dataSet, pastPeriods, futurePeriods,
                        organisationUnitsHash))
            .build();
    }

    private Integer getPastPeriods(DataSetSettings dataSetSettings, DataSet dataSet) {
        if (dataSetSettings != null) {
            DataSetSetting specificSetting = dataSetSettings.specificSettings().get(dataSet.uid());
            DataSetSetting globalSetting = dataSetSettings.globalSettings();

            if (hasPeriodDSDownload(specificSetting)) {
                return specificSetting.periodDSDownload();
            } else if (hasPeriodDSDownload(globalSetting)) {
                return globalSetting.periodDSDownload();
            }
        }
        return dataSet.periodType().getDefaultPastPeriods();
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

    private List<DataSet> getDataSets() {
        return dataSetRepository
                .withDataSetElements()
                .blockingGet();
    }
}