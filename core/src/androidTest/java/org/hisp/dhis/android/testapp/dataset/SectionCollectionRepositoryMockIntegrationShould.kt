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
import org.hisp.dhis.android.core.dataset.SectionPivotMode
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class SectionCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all_objects() {
        val sections = d2.dataSetModule().sections().blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
        Truth.assertThat(sections[0].name()).isEqualTo("Immunization")
    }

    @Test
    fun filter_by_description() {
        val sections = d2.dataSetModule().sections()
            .byDescription().eq("Immunization dose administration")
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_sort_order() {
        val sections = d2.dataSetModule().sections()
            .bySortOrder().eq(1)
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_show_row_totals() {
        val sections = d2.dataSetModule().sections()
            .byShowRowTotals().isTrue
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_show_column_totals() {
        val sections = d2.dataSetModule().sections()
            .byShowColumnTotals().isFalse
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_set_uid() {
        val sections = d2.dataSetModule().sections()
            .byDataSetUid().eq("lyLU2wR22tC")
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_disable_data_element_autoGrouping() {
        val sections = d2.dataSetModule().sections()
            .byDisableDataElementAutoGroup().eq(true)
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_pivot_mode() {
        val sections = d2.dataSetModule().sections()
            .byPivotMode().eq(SectionPivotMode.MOVE_CATEGORIES)
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_pivoted_category() {
        val sections = d2.dataSetModule().sections()
            .byPivotedCategory().eq("pivoted category")
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_after_section_text() {
        val sections = d2.dataSetModule().sections()
            .byAfterSectionText().eq("Text after section")
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun filter_by_before_section_text() {
        val sections = d2.dataSetModule().sections()
            .byBeforeSectionText().eq("Text before section")
            .blockingGet()
        Truth.assertThat(sections.size).isEqualTo(1)
    }

    @Test
    fun return_greyed_fields_as_children() {
        val section = d2.dataSetModule().sections()
            .withGreyedFields().one().blockingGet()
        Truth.assertThat(section!!.greyedFields()!!.size).isEqualTo(1)
        Truth.assertThat(section.greyedFields()!![0].uid()).isEqualTo("ca8lfO062zg.Prlt0C1RF0s")
    }

    @Test
    fun return_data_element_as_children() {
        val section = d2.dataSetModule().sections()
            .withDataElements().one().blockingGet()
        Truth.assertThat(section!!.dataElements()!!.size).isEqualTo(1)
        Truth.assertThat(section.dataElements()!![0].uid()).isEqualTo("g9eOBujte1U")
        Truth.assertThat(section.dataElements()!![0].code()).isEqualTo("DE_2005735")
    }

    @Test
    fun return_indicators_as_children() {
        val section = d2.dataSetModule().sections()
            .withIndicators().one().blockingGet()
        Truth.assertThat(section!!.indicators()!!.size).isEqualTo(1)
        Truth.assertThat(section.indicators()!![0].uid()).isEqualTo("ReUHfIn0pTQ")
        Truth.assertThat(section.indicators()!![0].code()).isEqualTo("IN_52462")
    }
}
