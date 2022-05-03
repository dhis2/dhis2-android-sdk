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

package org.hisp.dhis.android.testapp.program;

import org.hisp.dhis.android.core.common.AnalyticsType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryType;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ProgramIndicatorCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_display_in_form() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byDisplayInForm()
                        .isTrue()
                        .blockingGet();
        
        assertThat(indicators.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_expression() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byExpression()
                        .eq("1")
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_dimension_item() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byDimensionItem()
                        .eq("rXoaHGAXWy9")
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_filter() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byFilter()
                        .eq("#{edqlbukwRfQ.vANAXwtLwcT} < 11")
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_decimals() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byDecimals()
                        .eq(2)
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_aggregation_type() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byAggregationType()
                        .eq("AVERAGE")
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_analytics_type() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byAnalyticsType()
                        .eq(AnalyticsType.EVENT)
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_program() {
        List<ProgramIndicator> indicators =
                d2.programModule().programIndicators()
                        .byProgramUid()
                        .eq("lxAQ7Zs9VYR")
                        .blockingGet();

        assertThat(indicators.size()).isEqualTo(2);
    }

    @Test
    public void include_legend_sets_as_children() {
        ProgramIndicator programIndicators = d2.programModule().programIndicators()
                .withLegendSets().one().blockingGet();
        assertThat(programIndicators.legendSets().size()).isEqualTo(1);
        assertThat(programIndicators.legendSets().get(0).uid()).isEqualTo("TiOkbpGEud4");
    }

    @Test
    public void include_analytics_period_boundaries_as_children() {
        ProgramIndicator programIndicators = d2.programModule().programIndicators()
                .withAnalyticsPeriodBoundaries().one().blockingGet();
        assertThat(programIndicators.analyticsPeriodBoundaries().size()).isEqualTo(2);
        assertThat(programIndicators.analyticsPeriodBoundaries().get(0).offsetPeriodType()).
                isEqualTo(PeriodType.SixMonthly);
        assertThat(programIndicators.analyticsPeriodBoundaries().get(1).analyticsPeriodBoundaryType())
                .isEqualTo(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD);
    }
}