/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2ProgressSyncStatus
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.dataapproval.DataApproval
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalCall
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalQuery
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationCall
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationQuery
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueCall
import org.hisp.dhis.android.core.datavalue.internal.DataValueQuery
import org.hisp.dhis.android.core.domain.aggregated.data.AggregatedD2Progress
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class AggregatedDataCall constructor(
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader,
    private val dataValueCall: DataValueCall,
    private val dsCompleteRegistrationCall: DataSetCompleteRegistrationCall,
    private val dataApprovalCall: DataApprovalCall,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val coroutineCallExecutor: CoroutineAPICallExecutor,
    private val aggregatedDataSyncStore: AggregatedDataSyncStore,
    private val aggregatedDataCallBundleFactory: AggregatedDataCallBundleFactory,
    private val resourceHandler: ResourceHandler,
    private val hashHelper: AggregatedDataSyncHashHelper,
) {
    fun download(): Flow<AggregatedD2Progress> = flow {
        val progressManager = AggregatedD2ProgressManager(null)
        systemInfoModuleDownloader.downloadWithProgressManager(progressManager)
        selectDataSetsAndDownload(progressManager).collect { emit(it) }
    }

    private fun selectDataSetsAndDownload(
        progressManager: AggregatedD2ProgressManager,
    ): Flow<AggregatedD2Progress> {
        return flow {
            val bundles = aggregatedDataCallBundleFactory.bundles
            val dataSets = bundles.flatMap { it.dataSets }.mapNotNull { it.uid() }

            progressManager.setTotalCalls(bundles.size + 2)
            progressManager.setDataSets(dataSets)
            emit(progressManager.getProgress())

            for (bundle in bundles) {
                downloadBundle(bundle)

                bundle.dataSets.forEach {
                    progressManager.completeDataSet(it.uid(), D2ProgressSyncStatus.SUCCESS)
                    progressManager.increaseProgress(DataValue::class.java, false)
                }

                emit(progressManager.getProgress())
            }
            emit(progressManager.complete())
        }
    }

    private suspend fun downloadBundle(
        bundle: AggregatedDataCallBundle,
    ) {
        return try {
            coroutineCallExecutor.wrapTransactionally(cleanForeignKeyErrors = true) {
                downloadDataValues(bundle)
                downloadCompleteRegistration(bundle)
                downloadApproval(bundle)
                updateAggregatedDataSync(bundle)
            }
        } catch (_: D2Error) {
        }
    }

    private suspend fun downloadDataValues(
        bundle: AggregatedDataCallBundle,
    ): List<DataValue> {
        val dataValueQuery = DataValueQuery(bundle)
        return dataValueCall.download(dataValueQuery)
    }

    private suspend fun downloadCompleteRegistration(
        bundle: AggregatedDataCallBundle,
    ): List<DataSetCompleteRegistration> {
        val completeRegistrationQuery = DataSetCompleteRegistrationQuery(
            dataSetUids = getUids(bundle.dataSets),
            periodIds = bundle.periodIds,
            rootOrgUnitUids = bundle.rootOrganisationUnitUids,
            lastUpdatedStr = bundle.key.lastUpdatedStr(),
        )

        return dsCompleteRegistrationCall.download(completeRegistrationQuery)
    }

    private fun updateAggregatedDataSync(
        bundle: AggregatedDataCallBundle,
    ) {
        for (dataSet in bundle.dataSets) {
            aggregatedDataSyncStore.updateOrInsertWhere(
                AggregatedDataSync.builder()
                    .dataSet(dataSet.uid())
                    .periodType(dataSet.periodType())
                    .pastPeriods(bundle.key.pastPeriods)
                    .futurePeriods(dataSet.openFuturePeriods())
                    .dataElementsHash(hashHelper.getDataSetDataElementsHash(dataSet))
                    .organisationUnitsHash(bundle.allOrganisationUnitUidsSet.hashCode())
                    .lastUpdated(resourceHandler.serverDate)
                    .build(),
            )
        }
    }

    private suspend fun downloadApproval(
        bundle: AggregatedDataCallBundle,
    ): List<DataApproval> {
        val dataSetsWithWorkflow = bundle.dataSets.filter { it.workflow() != null }
        val workflowUids = dataSetsWithWorkflow.mapNotNull { it.workflow()?.uid() }

        return if (workflowUids.isEmpty()) {
            emptyList()
        } else {
            val attributeOptionComboUids = getAttributeOptionCombosUidsFrom(dataSetsWithWorkflow)
            val dataApprovalQuery = DataApprovalQuery(
                workflowsUids = workflowUids,
                organisationUnistUids = bundle.allOrganisationUnitUidsSet,
                periodIds = bundle.periodIds,
                attributeOptionCombosUids = attributeOptionComboUids,
                lastUpdatedStr = bundle.key.lastUpdatedStr(),
            )
            dataApprovalCall.download(dataApprovalQuery)
        }
    }

    private fun getAttributeOptionCombosUidsFrom(dataSetsWithWorkflow: Collection<DataSet>): Set<String> {
        val categoryComboUids = dataSetsWithWorkflow.mapNotNull { it.categoryCombo()?.uid() }.toSet()

        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO,
                categoryComboUids,
            ).build()

        return categoryOptionComboStore.selectWhere(whereClause).map { it.uid() }.toSet()
    }
}
