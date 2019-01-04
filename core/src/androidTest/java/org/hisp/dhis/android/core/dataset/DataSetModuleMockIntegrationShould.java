package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
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
}