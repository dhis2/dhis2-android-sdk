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
package org.hisp.dhis.android.testapp.dataset

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DataSetModuleMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun allow_access_to_all_data_sets_without_children() {
        val dataSets = d2.dataSetModule().dataSets().blockingGet()
        assertThat(dataSets.size).isEqualTo(3)
        dataSets.forEach { dataSet ->
            assertThat(dataSet.dataSetElements()).isNull()
        }
    }

    @Test
    fun allow_access_to_one_data_set_without_children() {
        val dataSet = d2.dataSetModule().dataSets().uid("lyLU2wR22tC").blockingGet()
        assertThat(dataSet!!.dataSetElements()).isNull()
    }

    @Test
    fun allow_access_to_all_data_sets_with_children() {
        val dataSets = d2.dataSetModule().dataSets().withDataSetElements().blockingGet()
        assertThat(dataSets.size).isEqualTo(3)
        dataSets.forEach { dataSet ->
            assertThat(dataSet.dataSetElements()).isNotNull()
        }
    }

    @Test
    fun allow_access_to_one_data_set_with_children() {
        val dataSet = d2.dataSetModule().dataSets().withDataSetElements().uid("lyLU2wR22tC").blockingGet()
        assertThat(dataSet!!.dataSetElements()).isNotNull()
    }

    @Test
    fun allow_access_to_compulsory_data_element_operands() {
        val dataSet = d2.dataSetModule().dataSets().withCompulsoryDataElementOperands().uid("lyLU2wR22tC").blockingGet()
        val dataElementOperands = dataSet!!.compulsoryDataElementOperands()
        assertThat(dataElementOperands!!.size).isEqualTo(3)

        val operand = dataElementOperands[0]
        assertThat(operand.uid()).isNotNull()
        assertThat(operand.dataElement()!!.uid()).isNotNull()
    }

    @Test
    fun allow_access_data_input_periods() {
        val dataSet = d2.dataSetModule().dataSets().withDataInputPeriods().uid("lyLU2wR22tC").blockingGet()
        val dataInputPeriods = dataSet!!.dataInputPeriods()
        assertThat(dataInputPeriods!!.size).isEqualTo(1)

        val diPeriod = dataInputPeriods[0]
        assertThat(diPeriod.period().uid()).isEqualTo("2019")
        assertThat(diPeriod.openingDate().dateFormat()).isEqualTo("2017-12-31T23:00:00.000")
        assertThat(diPeriod.closingDate().dateFormat()).isEqualTo("2018-01-09T23:00:00.000")
    }

    @Test
    fun allow_access_data_set_elements() {
        val dataSet = d2.dataSetModule().dataSets().withDataSetElements().uid("lyLU2wR22tC").blockingGet()
        val dataSetElements = dataSet!!.dataSetElements()
        assertThat(dataSetElements!!.size).isEqualTo(2)
    }

    @Test
    fun allow_access_indicators() {
        val dataSet = d2.dataSetModule().dataSets().withIndicators().uid("lyLU2wR22tC").blockingGet()
        val indicators = dataSet!!.indicators()
        assertThat(indicators!!.size).isEqualTo(1)

        val indicator = indicators[0]
        assertThat(indicator.uid()).isEqualTo("ReUHfIn0pTQ")
        assertThat(indicator.name()).isEqualTo("ANC 1-3 Dropout Rate")
    }
}
