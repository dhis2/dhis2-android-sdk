/*
 *  Copyright (c) 2004-2025, University of Oslo
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
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.common.ObjectWithUid
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class DataValueCallShould {

    private val networkHandler: DataValueNetworkHandler = mock()
    private val handler: DataValueHandler = mock()
    private val apiDownloader: APIDownloader = mock()
    private val categoryOptionComboStore: CategoryOptionComboStore = mock()

    private val dataSet1: DataSet = mock()
    private val dataSet2: DataSet = mock()

    private val dataSetCall: DataValueCall by lazy {
        DataValueCall(networkHandler, handler, apiDownloader, categoryOptionComboStore)
    }

    @Before
    fun setUp() {
        whenever(dataSet1.uid()) doReturn DS1
        whenever(dataSet1.categoryCombo()) doReturn ObjectWithUid.create(CC1)
        whenever(dataSet2.uid()) doReturn DS2
        whenever(dataSet2.categoryCombo()) doReturn ObjectWithUid.create(CC2)

        apiDownloader.stub {
            onBlocking { downloadListAsCoroutine(any<Handler<DataValue>>(), any()) } doSuspendableAnswer {
                it.getArgument<suspend () -> List<DataValue>>(1).invoke()
            }
        }
        networkHandler.stub {
            onBlocking { getDataValuesForDataSet(any(), any(), any()) } doReturn emptyList()
        }
    }

    @Test
    fun send_the_attribute_option_combos_of_the_data_set() = runTest {
        whenever(categoryOptionComboStore.getForCategoryCombo(CC1)) doReturn categoryOptionCombos(AOC1, AOC2)

        dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1))))

        verifyBlocking(networkHandler) {
            getDataValuesForDataSet(eq(DS1), eq(listOf(AOC1, AOC2)), any())
        }
    }

    @Test
    fun send_one_request_per_data_set_with_its_own_attribute_option_combos() = runTest {
        whenever(categoryOptionComboStore.getForCategoryCombo(CC1)) doReturn categoryOptionCombos(AOC1)
        whenever(categoryOptionComboStore.getForCategoryCombo(CC2)) doReturn categoryOptionCombos(AOC2)

        dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1, dataSet2))))

        verifyBlocking(networkHandler) { getDataValuesForDataSet(eq(DS1), eq(listOf(AOC1)), any()) }
        verifyBlocking(networkHandler) { getDataValuesForDataSet(eq(DS2), eq(listOf(AOC2)), any()) }
    }

    @Test
    fun omit_the_attribute_option_combos_if_they_do_not_fit_in_the_url() = runTest {
        whenever(categoryOptionComboStore.getForCategoryCombo(CC1)) doReturn categoryOptionCombos(AOC1, AOC2)
        val periodIds = (1..500).map { "20200$it" }

        dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1), periodIds)))

        verifyBlocking(networkHandler) { getDataValuesForDataSet(eq(DS1), eq(emptyList()), any()) }
    }

    @Test
    fun return_the_data_values_of_every_data_set() = runTest {
        whenever(categoryOptionComboStore.getForCategoryCombo(any())) doReturn emptyList()
        whenever(networkHandler.getDataValuesForDataSet(eq(DS1), any(), any())) doReturn listOf(dataValue("de1"))
        whenever(networkHandler.getDataValuesForDataSet(eq(DS2), any(), any())) doReturn listOf(dataValue("de2"))

        val dataValues = dataSetCall.download(DataValueQuery(bundle(listOf(dataSet1, dataSet2))))

        assertThat(dataValues.map { it.dataElement() }).containsExactly("de1", "de2")
    }

    private fun bundle(dataSets: List<DataSet>, periodIds: List<String> = listOf("202001")) =
        AggregatedDataCallBundle(
            key = AggregatedDataCallBundleKey(PeriodType.Monthly, 1, 1, null),
            dataSets = dataSets,
            periodIds = periodIds,
            rootOrganisationUnitUids = listOf(OU1),
            allOrganisationUnitUidsSet = setOf(OU1),
        )

    private fun categoryOptionCombos(vararg uids: String): List<CategoryOptionCombo> =
        uids.map { CategoryOptionCombo.builder().uid(it).build() }

    private fun dataValue(dataElementUid: String): DataValue =
        DataValue.builder()
            .dataElement(dataElementUid)
            .period("202001")
            .organisationUnit(OU1)
            .categoryOptionCombo("categoryOptCom1")
            .attributeOptionCombo(AOC1)
            .build()

    companion object {
        private const val DS1 = "dataSet1"
        private const val DS2 = "dataSet2"
        private const val CC1 = "categoryCombo1"
        private const val CC2 = "categoryCombo2"
        private const val AOC1 = "attributeOptCom1"
        private const val AOC2 = "attributeOptCom2"
        private const val OU1 = "organisationUni1"
    }
}
