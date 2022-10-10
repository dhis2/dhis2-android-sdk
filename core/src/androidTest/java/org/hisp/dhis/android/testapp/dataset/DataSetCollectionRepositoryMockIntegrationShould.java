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

import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class DataSetCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_period_type() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byPeriodType().eq(PeriodType.Monthly)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_category_combo() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byCategoryComboUid().eq("m2jTvAj5kkm")
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_mobile() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byMobile().isFalse()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_version_eq() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byVersion().eq(22)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_version_bigger_1() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byVersion().biggerThan(21)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_version_bigger_0() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byVersion().biggerThan(22)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(0);
    }

    @Test
    public void filter_by_version_smaller_0() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byVersion().smallerThan(21)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_version_smaller_1() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byVersion().smallerThan(23)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_expiry_days() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byExpiryDays().eq(1)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_timely_days() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byTimelyDays().eq(3)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_notify_completing_user() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byNotifyCompletingUser().isFalse()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_open_future_periods() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byOpenFuturePeriods().eq(3)
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_field_combination_required() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byFieldCombinationRequired().isFalse()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_valid_complete_only() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byValidCompleteOnly().isFalse()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_no_value_requires_comment() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byNoValueRequiresComment().isTrue()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_skip_offline() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .bySkipOffline().isFalse()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_data_element_decoration() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byDataElementDecoration().isTrue()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_render_as_tabs() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byRenderAsTabs().isTrue()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_render_horizontally() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byRenderHorizontally().isFalse()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_access_data_write() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byAccessDataWrite().isTrue()
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_field_color() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byColor().eq("#000")
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_field_icon() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byIcon().eq("my-icon-name")
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_organisation_unit_uid() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byOrganisationUnitUid("DiszpKrYNg8")
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_organisation_unit_uid_list() {
        List<DataSet> dataSets = d2.dataSetModule().dataSets()
                .byOrganisationUnitList(Collections.singletonList("DiszpKrYNg8"))
                .blockingGet();
        assertThat(dataSets.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_orgunit_scope() {
        List<DataSet> dataSetCapture = d2.dataSetModule().dataSets()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .blockingGet();
        assertThat(dataSetCapture.size()).isEqualTo(2);

        List<DataSet> dataSetSearch = d2.dataSetModule().dataSets()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
                .blockingGet();
        assertThat(dataSetSearch.size()).isEqualTo(0);
    }

    @Test
    public void include_compulsory_data_element_operands_as_children() {
        DataSet dataSet = d2.dataSetModule().dataSets()
                .withCompulsoryDataElementOperands()
                .one().blockingGet();
        assertThat(dataSet.compulsoryDataElementOperands().size()).isEqualTo(1);
    }

    @Test
    public void include_data_input_periods_as_children() {
        DataSet dataSet = d2.dataSetModule().dataSets()
                .withDataInputPeriods()
                .one().blockingGet();
        assertThat(dataSet.dataInputPeriods().size()).isEqualTo(1);
    }

    @Test
    public void include_data_set_elements_as_children() {
        DataSet dataSet = d2.dataSetModule().dataSets()
                .withDataSetElements()
                .one().blockingGet();
        assertThat(dataSet.dataSetElements().size()).isEqualTo(2);
    }

    @Test
    public void get_child_with_data_set_elements() {
        DataSet dataSet = d2.dataSetModule().dataSets()
                .withDataSetElements()
                .uid("lyLU2wR22tC")
                .blockingGet();
        assertThat(dataSet.dataSetElements().size()).isEqualTo(2);
    }

    @Test
    public void include_indicators_as_children() {
        DataSet dataSet = d2.dataSetModule().dataSets()
                .withIndicators()
                .one().blockingGet();
        assertThat(dataSet.indicators().size()).isEqualTo(1);
    }
}