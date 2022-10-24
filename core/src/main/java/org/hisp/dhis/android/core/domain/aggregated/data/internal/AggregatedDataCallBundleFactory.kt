/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.domain.aggregated.data.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.PeriodForDataSetManager
import org.hisp.dhis.android.core.settings.DataSetSetting
import org.hisp.dhis.android.core.settings.DataSetSettings
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository

@Reusable
internal class AggregatedDataCallBundleFactory @Inject constructor(
    private val dataSetRepository: DataSetCollectionRepository,
    private val organisationUnitRepository: OrganisationUnitCollectionRepository,
    private val dataSetSettingsObjectRepository: DataSetSettingsObjectRepository,
    private val periodManager: PeriodForDataSetManager,
    private val aggregatedDataSyncStore: ObjectWithoutUidStore<AggregatedDataSync>,
    private val lastUpdatedCalculator: AggregatedDataSyncLastUpdatedCalculator
) {
    val bundles: List<AggregatedDataCallBundle>
        get() {
            val rootOrganisationUnitUids = organisationUnitRepository
                .byRootOrganisationUnit(true)
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .blockingGetUids()
            val allOrganisationUnitUids = organisationUnitRepository
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .blockingGetUids()
            return getBundlesInternal(
                dataSets,
                dataSetSettingsObjectRepository.blockingGet(),
                rootOrganisationUnitUids,
                HashSet(allOrganisationUnitUids),
                syncValuesByDataSetUid
            )
        }

    private val syncValuesByDataSetUid: Map<String, AggregatedDataSync>
        get() {
            val syncValues = aggregatedDataSyncStore.selectAll()
            return syncValues.associateBy { it.dataSet() }
        }

    fun getBundlesInternal(
        dataSets: Collection<DataSet>,
        dataSetSettings: DataSetSettings?,
        rootOrganisationUnitUids: List<String>,
        allOrganisationUnitUids: Set<String>,
        syncValues: Map<String, AggregatedDataSync>
    ): List<AggregatedDataCallBundle> {
        val organisationUnitsHash = allOrganisationUnitUids.hashCode()
        val keyDataSetMap = dataSets.groupBy { getBundleKey(dataSetSettings, it, syncValues, organisationUnitsHash) }

        return keyDataSetMap.mapNotNull { (key, dataSets) ->
            val periods = periodManager.getPeriodsInRange(
                key.periodType,
                -key.pastPeriods,
                key.futurePeriods
            )
            if (periods.isNotEmpty()) {
                val periodIds = selectPeriodIds(periods)
                AggregatedDataCallBundle(
                    key,
                    dataSets,
                    periodIds,
                    rootOrganisationUnitUids,
                    allOrganisationUnitUids
                )
            } else {
                null
            }
        }
    }

    private fun getBundleKey(
        dataSetSettings: DataSetSettings?,
        dataSet: DataSet,
        syncValues: Map<String, AggregatedDataSync>,
        organisationUnitsHash: Int
    ): AggregatedDataCallBundleKey {
        val pastPeriods = getPastPeriods(dataSetSettings, dataSet)
        val futurePeriods = if (dataSet.openFuturePeriods() == null) 1 else dataSet.openFuturePeriods()!!
        val syncValue = syncValues[dataSet.uid()]

        return AggregatedDataCallBundleKey(
            periodType = dataSet.periodType()!!,
            pastPeriods = pastPeriods,
            futurePeriods = futurePeriods,
            lastUpdated = lastUpdatedCalculator.getLastUpdated(
                syncValue, dataSet, pastPeriods, futurePeriods,
                organisationUnitsHash
            )
        )
    }

    private fun getPastPeriods(dataSetSettings: DataSetSettings?, dataSet: DataSet): Int {
        val settingsPastPeriod = dataSetSettings?.let {
            val specificSetting = dataSetSettings.specificSettings()[dataSet.uid()]
            val globalSetting = dataSetSettings.globalSettings()
            when {
                hasPeriodDSDownload(specificSetting) -> specificSetting!!.periodDSDownload()!!
                hasPeriodDSDownload(globalSetting) -> globalSetting.periodDSDownload()!!
                else -> null
            }
        }

        return settingsPastPeriod ?: -dataSet.periodType()!!.defaultStartPeriods
    }

    private fun hasPeriodDSDownload(dataSetSetting: DataSetSetting?): Boolean {
        return dataSetSetting?.periodDSDownload() != null
    }

    private fun selectPeriodIds(periods: Collection<Period>): List<String> {
        return periods.mapNotNull { it.periodId() }
    }

    private val dataSets: List<DataSet>
        get() = dataSetRepository
            .withDataSetElements()
            .blockingGet()
}
