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
package org.hisp.dhis.android.testapp.visualization

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.visualization.HideEmptyItemStrategy
import org.hisp.dhis.android.core.visualization.VisualizationType
import org.junit.Test

class VisualizationCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val visualizations = d2.visualizationModule().visualizations()
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(2)
    }

    @Test
    fun find_uids() {
        val visualizationUids = d2.visualizationModule().visualizations()
            .blockingGetUids()

        assertThat(visualizationUids.size).isEqualTo(2)
        assertThat(visualizationUids.contains("PYBH8ZaAQnC")).isTrue()
        assertThat(visualizationUids.contains("FAFa11yFeFe")).isTrue()
    }

    @Test
    fun find_by_description() {
        val visualizations = d2.visualizationModule().visualizations()
            .byDescription().eq("Sample visualization for the Android SDK")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
        assertThat(visualizations[0].uid()).isEqualTo("PYBH8ZaAQnC")
    }

    @Test
    fun find_by_display_description() {
        val visualizations = d2.visualizationModule().visualizations()
            .byDisplayDescription().eq("Sample visualization for the Android SDK")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
        assertThat(visualizations[0].uid()).isEqualTo("PYBH8ZaAQnC")
    }

    @Test
    fun find_by_display_form_name() {
        val visualizations = d2.visualizationModule().visualizations()
            .byDisplayFormName().eq("Android SDK Visualization sample")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
        assertThat(visualizations[0].uid()).isEqualTo("PYBH8ZaAQnC")
    }

    @Test
    fun find_by_display_title() {
        val visualizations = d2.visualizationModule().visualizations()
            .byDisplayTitle().eq("Sample title")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
        assertThat(visualizations[0].uid()).isEqualTo("PYBH8ZaAQnC")
    }

    @Test
    fun filter_by_legend_id() {
        val visualizations = d2.visualizationModule().visualizations()
            .byLegendUid()
            .eq("Yf6UHoPkd57")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_legend_style() {
        val visualizations = d2.visualizationModule().visualizations()
            .byLegendStyle().eq("FILL")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_legend_show_key() {
        val visualizations = d2.visualizationModule().visualizations()
            .byLegendShowKey().isFalse
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(2)
    }

    @Test
    fun filter_by_legend_strategy() {
        val indicators = d2.visualizationModule().visualizations()
            .byLegendStrategy().eq("FIXED")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(2)
    }

    @Test
    fun find_by_display_subtitle() {
        val visualizations = d2.visualizationModule().visualizations()
            .byDisplaySubtitle().eq("Sample subtitle")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
        assertThat(visualizations[0].uid()).isEqualTo("PYBH8ZaAQnC")
    }

    @Test
    fun find_by_visualization_type() {
        val visualizations = d2.visualizationModule().visualizations()
            .byType().eq(VisualizationType.COLUMN)
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
        assertThat(visualizations[0].uid()).isEqualTo("FAFa11yFeFe")
    }

    @Test
    fun find_by_hide_title() {
        val visualizations = d2.visualizationModule().visualizations()
            .byHideTitle().isFalse
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun find_by_hide_empty_row_items() {
        val visualizations = d2.visualizationModule().visualizations()
            .byHideEmptyRowItems().eq(HideEmptyItemStrategy.AFTER_LAST)
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun include_columns_rows_and_filters_as_children() {
        val visualization = d2.visualizationModule().visualizations()
            .withColumnsRowsAndFilters()
            .uid("PYBH8ZaAQnC")
            .blockingGet()!!

        assertThat(visualization.columns()!![0].id()).isEqualTo("dx")
        assertThat(visualization.columns()!![0].items()!!.size).isEqualTo(3)
        assertThat(visualization.rows()!![0].id()).isEqualTo("pe")
        assertThat(visualization.rows()!![0].items()!!.size).isEqualTo(4)
        assertThat(visualization.filters()!![0].id()).isEqualTo("ou")
        assertThat(visualization.filters()!![0].items()!!.size).isEqualTo(3)
    }
}
