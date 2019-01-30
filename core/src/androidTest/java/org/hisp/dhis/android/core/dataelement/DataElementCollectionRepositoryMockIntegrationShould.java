package org.hisp.dhis.android.core.dataelement;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class DataElementCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .get();
        assertThat(dataElements.size(), is(4));
    }

    @Test
    public void filter_by_name() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byValueType().eq("TEXT")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_zero_is_significant() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byZeroIsSignificant().isFalse()
                .get();
        assertThat(dataElements.size(), is(3));
    }

    @Test
    public void filter_by_aggregation_type() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byAggregationType().eq("AVERAGE")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_form_name() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byFormName().eq("ANC Visit")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_number_type() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byNumberType().eq("numTy")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_domain_type() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byDomainType().eq("TRACKER")
                .get();
        assertThat(dataElements.size(), is(4));
    }

    @Test
    public void filter_by_dimension() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byDimension().eq("dim")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_display_form_name() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byDisplayFormName().eq("ANC Visit")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_option_set() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byOptionSetUid().eq("VQ2lai3OfVG")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_category_combo() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byCategoryComboUid().eq("m2jTvAj5kkm")
                .get();
        assertThat(dataElements.size(), is(1));
    }
}