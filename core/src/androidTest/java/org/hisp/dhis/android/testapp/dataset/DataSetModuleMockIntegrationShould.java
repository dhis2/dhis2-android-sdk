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

package org.hisp.dhis.android.testapp.dataset;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataset.DataInputPeriod;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class DataSetModuleMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void allow_access_to_all_data_sets_without_children() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets().blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
        for (DataSet dataSet : dataSets) {
            assertThat(dataSet.dataSetElements() == null).isTrue();
        }
    }

    @Test
    public void allow_access_to_one_data_set_without_children() {
        DataSet dataSet = d2.dataSetModule().dataSets().uid("lyLU2wR22tC").blockingGet();
        assertThat(dataSet.dataSetElements() == null).isTrue();
    }

    @Test
    public void allow_access_to_all_data_sets_with_children() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets().withDataSetElements().blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
        for (DataSet dataSet : dataSets) {
            assertThat(dataSet.dataSetElements() == null).isFalse();
        }
    }

    @Test
    public void allow_access_to_one_data_set_with_children() {
        DataSet dataSet = d2.dataSetModule().dataSets().withDataSetElements().uid("lyLU2wR22tC").blockingGet();
        assertThat(dataSet.dataSetElements() == null).isFalse();
    }

    @Test
    public void allow_access_to_compulsory_data_element_operands() {
        DataSet dataSet = d2.dataSetModule().dataSets().withCompulsoryDataElementOperands().uid("lyLU2wR22tC").blockingGet();
        List<DataElementOperand> dataElementOperands = dataSet.compulsoryDataElementOperands();
        assertThat(dataElementOperands.size()).isEqualTo(1);

        DataElementOperand operand = dataElementOperands.get(0);
        assertThat(operand.uid()).isEqualTo("g9eOBujte1U.Gmbgme7z9BF");
        assertThat(operand.dataElement().uid()).isEqualTo("g9eOBujte1U");
        assertThat(operand.categoryOptionCombo().uid()).isEqualTo("Gmbgme7z9BF");
    }

    @Test
    public void allow_access_data_input_periods() {
        DataSet dataSet = d2.dataSetModule().dataSets().withDataInputPeriods().uid("lyLU2wR22tC").blockingGet();
        List<DataInputPeriod> dataInputPeriods = dataSet.dataInputPeriods();
        assertThat(dataInputPeriods.size()).isEqualTo(1);

        DataInputPeriod diPeriod = dataInputPeriods.get(0);
        assertThat(diPeriod.period().uid()).isEqualTo("2019");
        assertThat(BaseIdentifiableObject.dateToDateStr(diPeriod.openingDate())).isEqualTo("2017-12-31T23:00:00.000");
        assertThat(BaseIdentifiableObject.dateToDateStr(diPeriod.closingDate())).isEqualTo("2018-01-09T23:00:00.000");
    }

    @Test
    public void allow_access_data_set_elements() {
        DataSet dataSet = d2.dataSetModule().dataSets().withDataSetElements().uid("lyLU2wR22tC").blockingGet();
        List<DataSetElement> dataSetElements = dataSet.dataSetElements();
        assertThat(dataSetElements.size()).isEqualTo(2);
    }

    @Test
    public void allow_access_indicators() {
        DataSet dataSet = d2.dataSetModule().dataSets().withIndicators().uid("lyLU2wR22tC").blockingGet();
        List<Indicator> indicators = dataSet.indicators();
        assertThat(indicators.size()).isEqualTo(1);

        Indicator indicator = indicators.get(0);
        assertThat(indicator.uid()).isEqualTo("ReUHfIn0pTQ");
        assertThat(indicator.name()).isEqualTo("ANC 1-3 Dropout Rate");
    }
}