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

package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.dataset.DataSet;
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