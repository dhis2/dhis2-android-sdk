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

package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class DataSetModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void allow_access_to_all_data_sets_without_children() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets.get();
        assertThat(dataSets.size(), is(1));
        for (DataSet dataSet : dataSets) {
            assertThat(dataSet.sections() == null, is(true));
            assertThat(dataSet.style() == null, is(true));
        }
    }

    @Test
    public void allow_access_to_one_data_set_without_children() {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").get();
        assertThat(dataSet.sections() == null, is(true));
        assertThat(dataSet.style() == null, is(true));
    }

    @Test
    public void allow_access_to_all_data_sets_with_children() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets.getWithAllChildren();
        assertThat(dataSets.size(), is(1));
        for (DataSet dataSet : dataSets) {
            assertThat(dataSet.sections() == null, is(false));
            assertThat(dataSet.style() == null, is(false));
        }
    }

    @Test
    public void allow_access_to_one_data_set_with_children() {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").getWithAllChildren();
        assertThat(dataSet.sections() == null, is(false));
        assertThat(dataSet.style() == null, is(false));
    }

    @Test
    public void allow_access_to_object_style() {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").getWithAllChildren();
        assertThat(dataSet.style().color(), is("#000"));
        assertThat(dataSet.style().icon(), is("my-icon-name"));
    }

    @Test
    public void allow_access_to_sections() {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").getWithAllChildren();
        List<Section> sections = dataSet.sections();
        assertThat(sections.size(), is(1));

        Section section = sections.get(0);
        assertThat(section.uid(), is("Y2rk0vzgvAx"));
        assertThat(section.name(), is("Immunization"));
    }

    @Test
    public void allow_access_to_compulsory_data_element_operands() {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").getWithAllChildren();
        List<DataElementOperand> dataElementOperands = dataSet.compulsoryDataElementOperands();
        assertThat(dataElementOperands.size(), is(1));

        DataElementOperand operand = dataElementOperands.get(0);
        assertThat(operand.uid(), is("g9eOBujte1U.Gmbgme7z9BF"));
        assertThat(operand.dataElement().uid(), is("g9eOBujte1U"));
        assertThat(operand.categoryOptionCombo().uid(), is("Gmbgme7z9BF"));
    }

    @Test
    public void allow_access_data_input_periods() throws ParseException {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").getWithAllChildren();
        List<DataInputPeriod> dataInputPeriods = dataSet.dataInputPeriods();
        assertThat(dataInputPeriods.size(), is(1));

        DataInputPeriod diPeriod = dataInputPeriods.get(0);
        assertThat(diPeriod.period().uid(), is("2019"));
        assertThat(BaseIdentifiableObject.dateToDateStr(diPeriod.openingDate()), is("2017-12-31T23:00:00.000"));
        assertThat(BaseIdentifiableObject.dateToDateStr(diPeriod.closingDate()), is("2018-01-09T23:00:00.000"));
    }

    @Test
    public void allow_access_data_set_elements() {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").getWithAllChildren();
        List<DataSetElement> dataSetElements = dataSet.dataSetElements();
        assertThat(dataSetElements.size(), is(1));

        DataSetElement dataSetElement = dataSetElements.get(0);
        assertThat(dataSetElement.dataSet().uid(), is("lyLU2wR22tC"));
        assertThat(dataSetElement.dataElement().uid(), is("g9eOBujte1U"));
        assertThat(dataSetElement.categoryCombo().uid(), is("m2jTvAj5kkm"));
    }

    @Test
    public void allow_access_indicators() {
        DataSet dataSet = d2.dataSetModule().dataSets.uid("lyLU2wR22tC").getWithAllChildren();
        List<Indicator> indicators = dataSet.indicators();
        assertThat(indicators.size(), is(1));

        Indicator indicator = indicators.get(0);
        assertThat(indicator.uid(), is("ReUHfIn0pTQ"));
        assertThat(indicator.name(), is("ANC 1-3 Dropout Rate"));
    }
}