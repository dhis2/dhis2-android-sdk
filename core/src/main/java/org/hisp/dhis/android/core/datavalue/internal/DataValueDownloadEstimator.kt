/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.datavalue.internal

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.COUNT_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.DATA_SET_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.ORGANISATION_UNIT_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.PARENT_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.UID_COLUMN
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundle
import org.koin.core.annotation.Singleton

/**
 * Estimates, from local metadata, the volume every data set of a bundle can potentially download.
 *
 * The capture scope hierarchy is only loaded when some data set may end up being split by
 * organisation unit.
 */
@Singleton
internal class DataValueDownloadEstimator(
    private val databaseAdapter: DatabaseAdapter,
    private val categoryOptionComboStore: CategoryOptionComboStore,
) {

    suspend fun estimates(
        bundle: AggregatedDataCallBundle,
        maxPotentialValues: Long,
    ): List<DataValueDownloadEstimate> {
        if (bundle.dataSets.isEmpty()) {
            return emptyList()
        }
        val dataSetUids = bundle.dataSets.map { it.uid() }

        val categoryOptionCombosPerCell = countByDataSet(
            DataValueDownloadEstimationQueryHelper.categoryOptionComboCountByDataSetQuery(dataSetUids),
        )
        val captureScopeOrgUnits = orgUnitUids(DataValueDownloadEstimationQueryHelper.captureScopeOrgUnitsQuery())
        val assignedOrgUnitCounts = countByDataSet(
            DataValueDownloadEstimationQueryHelper.assignedOrgUnitCountByDataSetQuery(dataSetUids),
        ).withCaptureScopeFallback(dataSetUids, captureScopeOrgUnits.size)
        val attributeOptionComboUids = attributeOptionComboUidsByCategoryCombo(bundle)

        val orgUnitsByDataSet = orgUnitHierarchiesOrEmpty(
            dataSetUids = dataSetUids,
            categoryOptionCombosPerCell = categoryOptionCombosPerCell,
            assignedOrgUnitCounts = assignedOrgUnitCounts,
            captureScopeOrgUnits = captureScopeOrgUnits,
            maxPotentialValues = maxPotentialValues,
        )

        return bundle.dataSets.map { dataSet ->
            val uid = dataSet.uid()
            DataValueDownloadEstimate(
                dataSetUid = uid,
                periodIds = bundle.periodIds,
                rootOrgUnitUids = bundle.rootOrganisationUnitUids.toList(),
                attributeOptionComboUids = attributeOptionComboUids[dataSet.categoryCombo().uid()].orEmpty(),
                categoryOptionCombosPerCell = categoryOptionCombosPerCell[uid] ?: 0,
                orgUnits = orgUnitsByDataSet[uid]
                    ?: DataValueFlatOrgUnits((assignedOrgUnitCounts[uid] ?: 0).toLong()),
                maxPotentialValues = maxPotentialValues,
            )
        }
    }

    /**
     * A data set with no assignment of its own falls back to the whole capture scope rather than to
     * zero. A zero would drop every partition and skip the download silently, so an assignment
     * table that is empty or not yet populated would cost data instead of a request.
     */
    private fun Map<String, Int>.withCaptureScopeFallback(
        dataSetUids: List<String>,
        captureScopeSize: Int,
    ): Map<String, Int> {
        return dataSetUids.associateWith { uid -> this[uid]?.takeIf { it > 0 } ?: captureScopeSize }
    }

    private suspend fun orgUnitHierarchiesOrEmpty(
        dataSetUids: List<String>,
        categoryOptionCombosPerCell: Map<String, Int>,
        assignedOrgUnitCounts: Map<String, Int>,
        captureScopeOrgUnits: Set<String>,
        maxPotentialValues: Long,
    ): Map<String, DataValueOrgUnits> {
        val required = dataSetUids.any { uid ->
            DataValueDownloadPartitioner.requiresOrgUnitSplit(
                categoryOptionCombosPerCell = categoryOptionCombosPerCell[uid] ?: 0,
                assignedOrgUnitCount = assignedOrgUnitCounts[uid] ?: 0,
                maxPotentialValues = maxPotentialValues,
            )
        }
        if (!required) {
            return emptyMap()
        }

        val childrenByParent = captureScopeChildrenByParent()
        val assignedByDataSet = assignedOrgUnitsByDataSet(dataSetUids)

        return dataSetUids.associateWith { uid ->
            val assigned = assignedByDataSet[uid].orEmpty().ifEmpty { captureScopeOrgUnits }
            DataValueOrgUnitHierarchy(childrenByParent, assigned)
        }
    }

    private suspend fun captureScopeChildrenByParent(): Map<String, List<String>> {
        return databaseAdapter
            .rawQuery(DataValueDownloadEstimationQueryHelper.captureScopeOrganisationUnitTreeQuery())
            .mapNotNull { row ->
                val uid = row[UID_COLUMN]
                val parent = row[PARENT_COLUMN]
                if (uid != null && parent != null) parent to uid else null
            }
            .groupBy({ it.first }, { it.second })
    }

    private suspend fun assignedOrgUnitsByDataSet(dataSetUids: List<String>): Map<String, Set<String>> {
        return databaseAdapter
            .rawQuery(DataValueDownloadEstimationQueryHelper.assignedOrgUnitsByDataSetQuery(dataSetUids))
            .mapNotNull { row ->
                val dataSet = row[DATA_SET_COLUMN]
                val orgUnit = row[ORGANISATION_UNIT_COLUMN]
                if (dataSet != null && orgUnit != null) dataSet to orgUnit else null
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, orgUnits) -> orgUnits.toSet() }
    }

    private suspend fun orgUnitUids(query: String): Set<String> {
        return databaseAdapter.rawQuery(query).mapNotNull { it[ORGANISATION_UNIT_COLUMN] }.toSet()
    }

    private suspend fun attributeOptionComboUidsByCategoryCombo(
        bundle: AggregatedDataCallBundle,
    ): Map<String, List<String>> {
        return bundle.dataSets
            .map { it.categoryCombo().uid() }
            .distinct()
            .associateWith { categoryComboUid ->
                categoryOptionComboStore.getForCategoryCombo(categoryComboUid).map { it.uid() }
            }
    }

    private suspend fun countByDataSet(query: String): Map<String, Int> {
        return databaseAdapter.rawQuery(query).mapNotNull { row ->
            val dataSet = row[DATA_SET_COLUMN]
            val count = row[COUNT_COLUMN]?.toIntOrNull()
            if (dataSet != null && count != null) dataSet to count else null
        }.toMap()
    }
}
