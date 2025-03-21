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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.dataset.TabsDirection
import org.hisp.dhis.android.core.dataset.TextAlign
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class DataSetCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val dataSets = d2.dataSetModule().dataSets()
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(3)
    }

    @Test
    fun filter_by_period_type() {
        val dataSets = d2.dataSetModule().dataSets()
            .byPeriodType().eq(PeriodType.Monthly)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_category_combo() {
        val dataSets = d2.dataSetModule().dataSets()
            .byCategoryComboUid().eq("m2jTvAj5kkm")
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_mobile() {
        val dataSets = d2.dataSetModule().dataSets()
            .byMobile().isFalse
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_version_eq() {
        val dataSets = d2.dataSetModule().dataSets()
            .byVersion().eq(22)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_version_bigger_1() {
        val dataSets = d2.dataSetModule().dataSets()
            .byVersion().biggerThan(21)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_version_bigger_0() {
        val dataSets = d2.dataSetModule().dataSets()
            .byVersion().biggerThan(22)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_version_smaller_0() {
        val dataSets = d2.dataSetModule().dataSets()
            .byVersion().smallerThan(21)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_version_smaller_1() {
        val dataSets = d2.dataSetModule().dataSets()
            .byVersion().smallerThan(23)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_expiry_days() {
        val dataSets = d2.dataSetModule().dataSets()
            .byExpiryDays().eq(1)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_timely_days() {
        val dataSets = d2.dataSetModule().dataSets()
            .byTimelyDays().eq(3)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_notify_completing_user() {
        val dataSets = d2.dataSetModule().dataSets()
            .byNotifyCompletingUser().isFalse
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_open_future_periods() {
        val dataSets = d2.dataSetModule().dataSets()
            .byOpenFuturePeriods().eq(3)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_field_combination_required() {
        val dataSets = d2.dataSetModule().dataSets()
            .byFieldCombinationRequired().isFalse
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_valid_complete_only() {
        val dataSets = d2.dataSetModule().dataSets()
            .byValidCompleteOnly().isFalse
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_no_value_requires_comment() {
        val dataSets = d2.dataSetModule().dataSets()
            .byNoValueRequiresComment().isTrue
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_skip_offline() {
        val dataSets = d2.dataSetModule().dataSets()
            .bySkipOffline().isFalse
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_element_decoration() {
        val dataSets = d2.dataSetModule().dataSets()
            .byDataElementDecoration().isTrue
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_render_as_tabs() {
        val dataSets = d2.dataSetModule().dataSets()
            .byRenderAsTabs().isTrue
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_render_horizontally() {
        val dataSets = d2.dataSetModule().dataSets()
            .byRenderHorizontally().isFalse
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_access_data_write() {
        val dataSets = d2.dataSetModule().dataSets()
            .byAccessDataWrite().isTrue
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(3)
    }

    @Test
    fun filter_by_field_color() {
        val dataSets = d2.dataSetModule().dataSets()
            .byColor().eq("#000")
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_icon() {
        val dataSets = d2.dataSetModule().dataSets()
            .byIcon().eq("my-icon-name")
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_organisation_unit_uid() {
        val dataSets = d2.dataSetModule().dataSets()
            .byOrganisationUnitUid("DiszpKrYNg8")
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_organisation_unit_uid_list() {
        val dataSets = d2.dataSetModule().dataSets()
            .byOrganisationUnitList(listOf("DiszpKrYNg8"))
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_orgunit_scope() {
        val dataSetCapture = d2.dataSetModule().dataSets()
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .blockingGet()
        Truth.assertThat(dataSetCapture.size).isEqualTo(3)

        val dataSetSearch = d2.dataSetModule().dataSets()
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
            .blockingGet()
        Truth.assertThat(dataSetSearch.size).isEqualTo(0)
    }

    @Test
    fun filter_by_header() {
        val dataSets = d2.dataSetModule().dataSets()
            .byHeader().eq("Title")
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(2)
    }

    @Test
    fun filter_by_sub_header() {
        val dataSets = d2.dataSetModule().dataSets()
            .bySubHeader().eq("Subtitle")
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_custom_text_align() {
        val dataSets = d2.dataSetModule().dataSets()
            .byCustomTextAlign().eq(TextAlign.LINE_END)
            .blockingGet()
        Truth.assertThat(dataSets.size).isEqualTo(1)
    }

    @Test
    fun filter_by_tabs_direction() {
        val verticalDataSets = d2.dataSetModule().dataSets()
            .byTabsDirection().eq(TabsDirection.VERTICAL)
            .blockingGet()
        Truth.assertThat(verticalDataSets.size).isEqualTo(1)

        val horizontalDataSets = d2.dataSetModule().dataSets()
            .byTabsDirection().eq(TabsDirection.HORIZONTAL)
            .blockingGet()
        Truth.assertThat(horizontalDataSets.size).isEqualTo(1)
    }

    @Test
    fun include_compulsory_data_element_operands_as_children() {
        val dataSet = d2.dataSetModule().dataSets()
            .withCompulsoryDataElementOperands()
            .one().blockingGet()
        Truth.assertThat(dataSet!!.compulsoryDataElementOperands()!!.size).isEqualTo(2)
    }

    @Test
    fun include_data_input_periods_as_children() {
        val dataSet = d2.dataSetModule().dataSets()
            .withDataInputPeriods()
            .one().blockingGet()
        Truth.assertThat(dataSet!!.dataInputPeriods()!!.size).isEqualTo(1)
    }

    @Test
    fun include_data_set_elements_as_children() {
        val dataSet = d2.dataSetModule().dataSets()
            .withDataSetElements()
            .one().blockingGet()
        Truth.assertThat(dataSet!!.dataSetElements()!!.size).isEqualTo(2)
    }

    @Test
    fun get_child_with_data_set_elements() {
        val dataSet = d2.dataSetModule().dataSets()
            .withDataSetElements()
            .uid("lyLU2wR22tC")
            .blockingGet()
        Truth.assertThat(dataSet!!.dataSetElements()!!.size).isEqualTo(2)
    }

    @Test
    fun include_indicators_as_children() {
        val dataSet = d2.dataSetModule().dataSets()
            .withIndicators()
            .one().blockingGet()
        Truth.assertThat(dataSet!!.indicators()!!.size).isEqualTo(1)
    }
}
