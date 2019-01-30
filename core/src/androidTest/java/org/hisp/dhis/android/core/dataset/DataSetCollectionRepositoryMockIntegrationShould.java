package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class DataSetCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_period_type() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byPeriodType().eq(PeriodType.Monthly)
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_category_combo() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byCategoryComboUid().eq("m2jTvAj5kkm")
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_mobile() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byMobile().isFalse()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_version_eq() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byVersion().eq(22)
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_version_bigger_1() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byVersion().biggerThan(21)
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_version_bigger_0() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byVersion().biggerThan(22)
                .get();
        assertThat(dataSets.size(), is(0));
    }

    @Test
    public void filter_by_version_smaller_0() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byVersion().smallerThan(21)
                .get();
        assertThat(dataSets.size(), is(0));
    }

    @Test
    public void filter_by_version_smaller_1() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byVersion().smallerThan(23)
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_expiry_days() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byExpiryDays().eq(1)
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_timely_days() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byTimelyDays().eq(3)
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_notify_completing_user() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byNotifyCompletingUser().isFalse()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_open_future_periods() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byOpenFuturePeriods().eq(2)
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_field_combination_required() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byFieldCombinationRequired().isFalse()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_valid_complete_only() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byValidCompleteOnly().isFalse()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_no_value_requires_comment() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byNoValueRequiresComment().isTrue()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_skip_offline() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .bySkipOffline().isFalse()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_data_element_decoration() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byDataElementDecoration().isTrue()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_render_as_tabs() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byRenderAsTabs().isTrue()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_render_horizontally() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byRenderHorizontally().isFalse()
                .get();
        assertThat(dataSets.size(), is(1));
    }

    @Test
    public void filter_by_access_data_write() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets
                .byAccessDataWrite().isTrue()
                .get();
        assertThat(dataSets.size(), is(1));
    }
}