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

package org.hisp.dhis.android.testapp.visualization;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.hisp.dhis.android.core.visualization.DataDimensionItemType;
import org.hisp.dhis.android.core.visualization.HideEmptyItemStrategy;
import org.hisp.dhis.android.core.visualization.Visualization;
import org.hisp.dhis.android.core.visualization.VisualizationType;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(D2JunitRunner.class)
public class VisualizationCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations().blockingGet();
        assertThat(visualizations.size()).isEqualTo(2);
    }

    @Test
    public void find_uids() {
        List<String> visualizationUids = d2.visualizationModule().visualizations()
                .blockingGetUids();
        assertThat(visualizationUids.size()).isEqualTo(2);
        assertThat(visualizationUids.contains("PYBH8ZaAQnC")).isTrue();
        assertThat(visualizationUids.contains("FAFa11yFeFe")).isTrue();
    }

    @Test
    public void find_by_description() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byDescription().eq("Sample visualization for the Android SDK")
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
        assertThat(visualizations.get(0).uid()).isEqualTo("PYBH8ZaAQnC");
    }

    @Test
    public void find_by_display_description() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byDisplayDescription().eq("Sample visualization for the Android SDK")
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
        assertThat(visualizations.get(0).uid()).isEqualTo("PYBH8ZaAQnC");
    }

    @Test
    public void find_by_display_form_name() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byDisplayFormName().eq("Android SDK Visualization sample")
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
        assertThat(visualizations.get(0).uid()).isEqualTo("PYBH8ZaAQnC");
    }

    @Test
    public void find_by_display_title() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byDisplayTitle().eq("Sample title")
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
        assertThat(visualizations.get(0).uid()).isEqualTo("PYBH8ZaAQnC");
    }

    @Test
    public void filter_by_legend_id() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
            .byLegendUid()
            .eq("Yf6UHoPkd57")
            .blockingGet();

        assertThat(visualizations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_legend_style() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byLegendStyle()
                .eq("FILL")
                .blockingGet();

        assertThat(visualizations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_legend_show_key() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
            .byLegendShowKey()
             .isFalse()
            .blockingGet();

        assertThat(visualizations.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_legend_strategy() {
        List<Visualization> indicators = d2.visualizationModule().visualizations()
            .byLegendStrategy()
            .eq("FIXED")
            .blockingGet();

        assertThat(indicators.size()).isEqualTo(2);
    }

    @Test
    public void find_by_display_subtitle() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byDisplaySubtitle().eq("Sample subtitle")
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
        assertThat(visualizations.get(0).uid()).isEqualTo("PYBH8ZaAQnC");
    }

    @Test
    public void find_by_visualization_type() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byType().eq(VisualizationType.COLUMN)
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
        assertThat(visualizations.get(0).uid()).isEqualTo("FAFa11yFeFe");
    }

    @Test
    public void find_by_hide_title() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byHideTitle().isFalse()
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
    }

    @Test
    public void find_by_hide_empty_row_items() {
        List<Visualization> visualizations = d2.visualizationModule().visualizations()
                .byHideEmptyRowItems().eq(HideEmptyItemStrategy.AFTER_LAST)
                .blockingGet();
        assertThat(visualizations.size()).isEqualTo(1);
    }

    @Test
    public void include_category_dimensions_as_children() {
        Visualization visualization = d2.visualizationModule().visualizations()
                .withCategoryDimensions().one().blockingGet();
        assertThat(visualization.categoryDimensions().get(0).category().uid()).isEqualTo("KfdsGBcoiCa");
        assertThat(visualization.categoryDimensions().get(0).categoryOptions().get(0).uid()).isEqualTo("TNYQzTHdoxL");
        assertThat(visualization.categoryDimensions().get(0).categoryOptions().get(1).uid()).isEqualTo("TXGfLxZlInA");
        assertThat(visualization.categoryDimensions().get(0).categoryOptions().get(2).uid()).isEqualTo("uZUnebiT5DI");
    }

    @Test
    public void include_data_dimension_item_as_children() {
        Visualization visualization = d2.visualizationModule().visualizations()
                .withDataDimensionItems().one().blockingGet();
        assertThat(visualization.dataDimensionItems().get(0).dataDimensionItemType()).isEqualTo(DataDimensionItemType.INDICATOR);
        assertThat(visualization.dataDimensionItems().get(0).dataDimensionItem()).isEqualTo("Uvn6LCg7dVU");
    }
}