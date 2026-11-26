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

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.COUNT_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.DATA_SET_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.ORGANISATION_UNIT_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.PARENT_COLUMN
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper.UID_COLUMN
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

/**
 * The estimator drives the download partitioning from raw SQL, and its unit test mocks the
 * database adapter, so these queries are only ever exercised against a real schema here. Each one
 * is checked against the equivalent answer built from the public repositories, which read the same
 * tables through a different code path.
 */
class DataValueDownloadEstimatorMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val dataSetUids: List<String>
        get() = d2.dataSetModule().dataSets().blockingGetUids()

    @Test
    fun count_the_option_combos_a_cell_can_hold_in_every_data_set() = runTest {
        val counts = countsByDataSet(
            DataValueDownloadEstimationQueryHelper.categoryOptionComboCountByDataSetQuery(dataSetUids),
        )

        val dataSets = d2.dataSetModule().dataSets().withDataSetElements().blockingGet()
        assertThat(dataSets).isNotEmpty()

        dataSets.forEach { dataSet ->
            val expected = dataSet.dataSetElements().orEmpty().sumOf { dataSetElement ->
                val categoryCombo = dataSetElement.categoryCombo()?.uid()
                    ?: d2.dataElementModule().dataElements()
                        .uid(dataSetElement.dataElement().uid()).blockingGet()
                        ?.categoryCombo()?.uid()
                categoryCombo?.let {
                    d2.categoryModule().categoryOptionCombos().byCategoryComboUid().eq(it).blockingCount()
                } ?: 0
            }

            assertWithMessage("option combos per cell of ${dataSet.uid()}")
                .that(counts[dataSet.uid()] ?: 0)
                .isEqualTo(expected)
        }
    }

    @Test
    fun count_the_org_units_assigned_to_every_data_set() = runTest {
        val counts = countsByDataSet(
            DataValueDownloadEstimationQueryHelper.assignedOrgUnitCountByDataSetQuery(dataSetUids),
        )

        dataSetUids.forEach { uid ->
            assertWithMessage("assigned org units of $uid")
                .that(counts[uid] ?: 0)
                .isEqualTo(assignedOrgUnits(uid).size)
        }
    }

    @Test
    fun list_the_org_units_assigned_to_every_data_set() = runTest {
        val uidsByDataSet = databaseAdapter
            .rawQuery(DataValueDownloadEstimationQueryHelper.assignedOrgUnitsByDataSetQuery(dataSetUids))
            .mapNotNull { row ->
                val dataSet = row[DATA_SET_COLUMN]
                val orgUnit = row[ORGANISATION_UNIT_COLUMN]
                if (dataSet != null && orgUnit != null) dataSet to orgUnit else null
            }
            .groupBy({ it.first }, { it.second })

        dataSetUids.forEach { uid ->
            assertWithMessage("assigned org units of $uid")
                .that(uidsByDataSet[uid].orEmpty().toSet())
                .isEqualTo(assignedOrgUnits(uid).toSet())
        }
    }

    /**
     * The precondition the hierarchy descent relies on: walking down from the roots must reach
     * every unit of the capture scope, so no unit may have its parent outside the scope unless it
     * is a root itself.
     */
    @Test
    fun expose_a_capture_scope_tree_that_is_closed_downwards() = runTest {
        val rows = databaseAdapter.rawQuery(
            DataValueDownloadEstimationQueryHelper.captureScopeOrganisationUnitTreeQuery(),
        )

        val captureScope = d2.organisationUnitModule().organisationUnits()
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .blockingGet()
        val captureScopeUids = captureScope.map { it.uid() }.toSet()
        val roots = d2.organisationUnitModule().organisationUnits()
            .byRootOrganisationUnit(true)
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .blockingGetUids()
            .toSet()

        assertThat(captureScopeUids).isNotEmpty()
        assertThat(rows.mapNotNull { it[UID_COLUMN] }.toSet()).isEqualTo(captureScopeUids)

        val childrenByParent = rows.mapNotNull { row ->
            val uid = row[UID_COLUMN]
            val parent = row[PARENT_COLUMN]
            if (uid != null && parent != null) parent to uid else null
        }.groupBy({ it.first }, { it.second })

        val reachable = mutableSetOf<String>()
        val pending = ArrayDeque(roots)
        while (pending.isNotEmpty()) {
            val uid = pending.removeFirst()
            if (reachable.add(uid)) {
                pending.addAll(childrenByParent[uid].orEmpty())
            }
        }

        assertWithMessage("units of the capture scope not reachable from its roots")
            .that(captureScopeUids - reachable)
            .isEmpty()
    }

    private suspend fun countsByDataSet(query: String): Map<String, Int> {
        return databaseAdapter.rawQuery(query).mapNotNull { row ->
            val dataSet = row[DATA_SET_COLUMN]
            val count = row[COUNT_COLUMN]?.toIntOrNull()
            if (dataSet != null && count != null) dataSet to count else null
        }.toMap()
    }

    /**
     * Not narrowed to the capture scope on purpose: the estimate counts every assignment, because
     * reading an incomplete assignment table as "nothing to download" would cost data, while
     * counting an unreachable assignment only overestimates the volume.
     */
    private fun assignedOrgUnits(dataSetUid: String): List<String> {
        return d2.organisationUnitModule().organisationUnits()
            .byDataSetUids(listOf(dataSetUid))
            .blockingGetUids()
    }
}
