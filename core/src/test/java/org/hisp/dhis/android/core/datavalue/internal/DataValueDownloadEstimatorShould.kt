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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.datavalue.internal.DataValueDownloadEstimationQueryHelper
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundle
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundleKey
import org.hisp.dhis.android.core.period.PeriodType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class DataValueDownloadEstimatorShould {

    private val databaseAdapter: DatabaseAdapter = mock()
    private val categoryOptionComboStore: CategoryOptionComboStore = mock()

    private val estimator = DataValueDownloadEstimator(databaseAdapter, categoryOptionComboStore)

    private val dataSet: DataSet = mock()

    @Before
    fun setUp() {
        whenever(dataSet.uid()) doReturn DS1
        whenever(dataSet.categoryCombo()) doReturn ObjectWithUid.create(CC1)

        databaseAdapter.stub {
            onBlocking { rawQuery(any(), anyOrNullArray()) } doReturn emptyList()
        }
        categoryOptionComboStore.stub {
            onBlocking { getForCategoryCombo(CC1) } doReturn listOf(
                CategoryOptionCombo.builder().uid(AOC1).build(),
            )
        }
    }

    @Test
    fun break_the_estimate_down_by_dimension() = runTest {
        stubCount(DataValueDownloadEstimationQueryHelper.categoryOptionComboCountByDataSetQuery(listOf(DS1)), 7)
        stubCount(DataValueDownloadEstimationQueryHelper.assignedOrgUnitCountByDataSetQuery(listOf(DS1)), 3)

        val estimate = estimator.estimates(bundle(), MAX).single()

        assertThat(estimate.dataSetUid).isEqualTo(DS1)
        assertThat(estimate.periodIds).containsExactly(P1, P2)
        assertThat(estimate.rootOrgUnitUids).containsExactly(OU1)
        assertThat(estimate.attributeOptionComboUids).containsExactly(AOC1)
        assertThat(estimate.categoryOptionCombosPerCell).isEqualTo(7)
        assertThat(estimate.orgUnits.assignedCount(listOf(OU1), includeDescendants = true)).isEqualTo(3)
    }

    @Test
    fun not_load_the_hierarchy_when_no_data_set_can_be_split_by_organisation_unit() = runTest {
        stubCount(DataValueDownloadEstimationQueryHelper.categoryOptionComboCountByDataSetQuery(listOf(DS1)), 10)
        stubCount(DataValueDownloadEstimationQueryHelper.assignedOrgUnitCountByDataSetQuery(listOf(DS1)), 10)

        estimator.estimates(bundle(), MAX).single()

        verifyBlocking(databaseAdapter, never()) {
            rawQuery(
                eq(
                    DataValueDownloadEstimationQueryHelper.captureScopeOrganisationUnitTreeQuery(),
                ),
                anyOrNullArray(),
            )
        }
    }

    @Test
    fun load_the_hierarchy_when_a_data_set_can_be_split_by_organisation_unit() = runTest {
        stubCount(DataValueDownloadEstimationQueryHelper.categoryOptionComboCountByDataSetQuery(listOf(DS1)), 10)
        stubCount(DataValueDownloadEstimationQueryHelper.assignedOrgUnitCountByDataSetQuery(listOf(DS1)), 20)
        stubRows(
            DataValueDownloadEstimationQueryHelper.captureScopeOrganisationUnitTreeQuery(),
            listOf(
                mapOf("uid" to OU1, "parent" to null),
                mapOf("uid" to CHILD, "parent" to OU1),
            ),
        )
        stubRows(
            DataValueDownloadEstimationQueryHelper.assignedOrgUnitsByDataSetQuery(listOf(DS1)),
            listOf(
                mapOf("dataSet" to DS1, "organisationUnit" to OU1),
                mapOf("dataSet" to DS1, "organisationUnit" to CHILD),
            ),
        )

        val estimate = estimator.estimates(bundle(), MAX).single()

        assertThat(estimate.orgUnits.assignedCount(listOf(OU1), includeDescendants = true)).isEqualTo(2)
        assertThat(estimate.orgUnits.assignedCount(listOf(OU1), includeDescendants = false)).isEqualTo(1)
        assertThat(estimate.orgUnits.children(listOf(OU1))).containsExactly(CHILD)
    }

    @Test
    fun report_no_option_combos_when_the_data_set_has_none_locally() = runTest {
        val estimate = estimator.estimates(bundle(), MAX).single()

        assertThat(estimate.categoryOptionCombosPerCell).isEqualTo(0)
        assertThat(estimate.orgUnits.assignedCount(listOf(OU1), includeDescendants = true)).isEqualTo(0)
    }

    private suspend fun stubCount(query: String, count: Int) {
        stubRows(query, listOf(mapOf("dataSet" to DS1, "count" to count.toString())))
    }

    private suspend fun stubRows(query: String, rows: List<Map<String, String?>>) {
        whenever(databaseAdapter.rawQuery(eq(query), anyOrNullArray())) doReturn rows
    }

    private fun anyOrNullArray(): Array<Any>? = anyOrNull()

    private fun bundle() = AggregatedDataCallBundle(
        key = AggregatedDataCallBundleKey(PeriodType.Monthly, 1, 1, null),
        dataSets = listOf(dataSet),
        periodIds = listOf(P1, P2),
        rootOrganisationUnitUids = listOf(OU1),
        allOrganisationUnitUidsSet = setOf(OU1, CHILD),
    )

    companion object {
        private const val DS1 = "dataSet1"
        private const val CC1 = "categoryCombo1"
        private const val AOC1 = "attributeOptCom1"
        private const val OU1 = "organisationUni1"
        private const val CHILD = "organisationUni2"
        private const val P1 = "202001"
        private const val P2 = "202002"
        private const val MAX = 100L
    }
}
