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
package org.hisp.dhis.android.core.datavalue.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundle
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataCallBundleKey
import org.hisp.dhis.android.core.period.PeriodType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class DataValueCallShould {

    private val networkHandler: DataValueNetworkHandler = mock()
    private val handler: DataValueHandler = mock()
    private val apiDownloader: APIDownloader = mock()
    private val estimator: DataValueDownloadEstimator = mock()

    private val dataSet1: DataSet = mock()

    private val dataSetCall: DataValueCall by lazy {
        DataValueCall(networkHandler, handler, apiDownloader, estimator)
    }

    @Before
    fun setUp() {
        whenever(dataSet1.uid()) doReturn DS1

        apiDownloader.stub {
            onBlocking { downloadListAsCoroutine(any<Handler<DataValue>>(), any()) } doSuspendableAnswer {
                it.getArgument<suspend () -> List<DataValue>>(1).invoke()
            }
        }
        networkHandler.stub {
            onBlocking { getDataValuesForDataSet(any(), any(), anyOrNull()) } doReturn emptyList()
        }
    }

    @Test
    fun send_one_request_per_data_set() = runTest {
        estimator.stub {
            onBlocking { estimates(any(), any()) } doReturn listOf(
                estimate(DS1, periodIds = listOf(P1)),
                estimate(DS2, periodIds = listOf(P1)),
            )
        }

        dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1))))

        verifyBlocking(networkHandler) { getDataValuesForDataSet(eq(DS1), any(), eq(LAST_UPDATED)) }
        verifyBlocking(networkHandler) { getDataValuesForDataSet(eq(DS2), any(), eq(LAST_UPDATED)) }
    }

    @Test
    fun split_a_data_set_that_exceeds_the_limit_into_several_requests() = runTest {
        // 2 periods x 1 orgunit x 100_000 option combos x 1 aoc doubles the default limit.
        estimator.stub {
            onBlocking { estimates(any(), any()) } doReturn listOf(
                estimate(
                    dataSetUid = DS1,
                    periodIds = listOf(P1, P2),
                    categoryOptionCombosPerCell =
                        DataValueDownloadPartitioner.DEFAULT_MAX_POTENTIAL_VALUES.toInt(),
                ),
            )
        }

        dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1))))

        verifyBlocking(networkHandler) {
            getDataValuesForDataSet(eq(DS1), argThat { periodIds == listOf(P1) }, eq(LAST_UPDATED))
        }
        verifyBlocking(networkHandler) {
            getDataValuesForDataSet(eq(DS1), argThat { periodIds == listOf(P2) }, eq(LAST_UPDATED))
        }
    }

    @Test
    fun send_the_partition_dimensions_to_the_network_handler() = runTest {
        estimator.stub {
            onBlocking { estimates(any(), any()) } doReturn listOf(estimate(DS1, periodIds = listOf(P1)))
        }

        dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1))))

        verifyBlocking(networkHandler) {
            getDataValuesForDataSet(
                eq(DS1),
                eq(
                    DataValuePartition(
                        periodIds = listOf(P1),
                        orgUnitUids = listOf(OU1),
                        includeDescendants = true,
                        attributeOptionComboUids = listOf(AOC1),
                    ),
                ),
                eq(LAST_UPDATED),
            )
        }
    }

    @Test
    fun return_the_data_values_of_every_request() = runTest {
        estimator.stub {
            onBlocking { estimates(any(), any()) } doReturn listOf(
                estimate(DS1, periodIds = listOf(P1)),
                estimate(DS2, periodIds = listOf(P1)),
            )
        }
        whenever(networkHandler.getDataValuesForDataSet(eq(DS1), any(), anyOrNull()))
            .doReturn(listOf(dataValue("de1")))
        whenever(networkHandler.getDataValuesForDataSet(eq(DS2), any(), anyOrNull()))
            .doReturn(listOf(dataValue("de2")))

        val dataValues = dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1))))

        assertThat(dataValues.map { it.dataElement() }).containsExactly("de1", "de2")
    }

    @Test
    fun not_issue_any_request_when_there_is_nothing_to_download() = runTest {
        estimator.stub {
            onBlocking { estimates(any(), any()) } doReturn emptyList()
        }

        val dataValues = dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1))))

        assertThat(dataValues).isEmpty()
        verifyNoInteractions(networkHandler)
    }

    private fun estimate(
        dataSetUid: String,
        periodIds: List<String>,
        attributeOptionComboUids: List<String> = listOf(AOC1),
        categoryOptionCombosPerCell: Int = 1,
    ) = DataValueDownloadEstimate(
        dataSetUid = dataSetUid,
        periodIds = periodIds,
        rootOrgUnitUids = listOf(OU1),
        attributeOptionComboUids = attributeOptionComboUids,
        categoryOptionCombosPerCell = categoryOptionCombosPerCell,
        orgUnits = DataValueFlatOrgUnits(1),
        maxPotentialValues = DataValueDownloadPartitioner.DEFAULT_MAX_POTENTIAL_VALUES,
    )

    private fun bundle(dataSets: List<DataSet>) =
        AggregatedDataCallBundle(
            key = AggregatedDataCallBundleKey(PeriodType.Monthly, 1, 1, null),
            dataSets = dataSets,
            periodIds = listOf(P1),
            rootOrganisationUnitUids = listOf(OU1),
            allOrganisationUnitUidsSet = setOf(OU1),
        )

    private fun dataValue(dataElementUid: String): DataValue =
        DataValue.builder()
            .dataElement(dataElementUid)
            .period(P1)
            .organisationUnit(OU1)
            .categoryOptionCombo("categoryOptCom1")
            .attributeOptionCombo(AOC1)
            .build()

    companion object {
        private const val DS1 = "dataSet1"
        private const val DS2 = "dataSet2"
        private const val AOC1 = "attributeOptCom1"
        private const val OU1 = "organisationUni1"
        private const val P1 = "202001"
        private const val P2 = "202002"
        private val LAST_UPDATED: String? = null
    }
}
