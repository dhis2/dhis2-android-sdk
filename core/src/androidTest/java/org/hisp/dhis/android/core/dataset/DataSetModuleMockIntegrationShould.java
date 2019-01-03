package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

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
}