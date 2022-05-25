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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class AnalyticsVisualizationRepositoryIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val visualizationUid = "FAFa11yFeFe"

    @Test
    fun evaluate_visualization() {
        val result = d2.analyticsModule().visualizations()
            .withVisualization(visualizationUid)
            .blockingEvaluate()
            .getOrThrow()

        assertThat(result.dimensions.columns.size).isEqualTo(1)
        assertThat(result.dimensions.rows.size).isEqualTo(1)
        assertThat(result.dimensionItems[Dimension.Data]!!.size).isEqualTo(2)
        assertThat(result.dimensionItems[Dimension.OrganisationUnit]!!.size).isEqualTo(1)
        assertThat(result.dimensionItems[Dimension.Period]!!.size).isEqualTo(3)
        assertThat(result.metadata).isNotEmpty()
        assertThat(result.values.size).isEqualTo(3)
    }

    @Test
    fun evaluate_visualization_with_periods() {
        val result = d2.analyticsModule().visualizations()
            .withVisualization(visualizationUid)
            .withPeriods(listOf(DimensionItem.PeriodItem.Absolute("2018")))
            .blockingEvaluate()
            .getOrThrow()

        assertThat(result.dimensions.columns.size).isEqualTo(1)
        assertThat(result.dimensions.rows.size).isEqualTo(1)
        assertThat(result.dimensionItems[Dimension.Data]!!.size).isEqualTo(2)
        assertThat(result.dimensionItems[Dimension.OrganisationUnit]!!.size).isEqualTo(1)
        assertThat(result.dimensionItems[Dimension.Period]).isEqualTo(
            listOf(
                DimensionItem.PeriodItem.Absolute("2018")
            )
        )
        assertThat(result.metadata).isNotEmpty()
        assertThat(result.values.size).isEqualTo(1)
    }

    @Test
    fun evaluate_visualization_with_unordered_periods() {
        val result = d2.analyticsModule().visualizations()
            .withVisualization(visualizationUid)
            .withPeriods(
                listOf(
                    DimensionItem.PeriodItem.Absolute("2022"),
                    DimensionItem.PeriodItem.Absolute("2022"),
                    DimensionItem.PeriodItem.Absolute("2021")
                )
            )
            .blockingEvaluate()
            .getOrThrow()

        assertThat(result.dimensionItems[Dimension.Period]).isEqualTo(
            listOf(
                DimensionItem.PeriodItem.Absolute("2021"),
                DimensionItem.PeriodItem.Absolute("2022")
            )
        )
        assertThat(result.values.size).isEqualTo(2)
    }

    @Test
    fun evaluate_visualization_with_organisation_units() {
        val result = d2.analyticsModule().visualizations()
            .withVisualization(visualizationUid)
            .withOrganisationUnits(
                listOf(
                    DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT)
                )
            )
            .blockingEvaluate()
            .getOrThrow()

        assertThat(result.dimensions.columns.size).isEqualTo(1)
        assertThat(result.dimensions.rows.size).isEqualTo(1)
        assertThat(result.dimensionItems[Dimension.Data]!!.size).isEqualTo(2)
        assertThat(result.dimensionItems[Dimension.OrganisationUnit]).isEqualTo(
            listOf(
                DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT)
            )
        )
        assertThat(result.dimensionItems[Dimension.Period]!!.size).isEqualTo(3)
        assertThat(result.metadata).isNotEmpty()
        assertThat(result.values.size).isEqualTo(3)
    }

    @Test
    fun evaluate_invalid_visualization() {
        val result = d2.analyticsModule().visualizations()
            .withVisualization("invalid_visualization_uid")
            .blockingEvaluate()

        assertThat(result.succeeded).isFalse()
    }

    @Test
    fun return_data_elements_with_legend_by_DE_if_legend_strategy_is_by_data_item() {
        val result = d2.analyticsModule().visualizations()
            .withVisualization(visualizationUid)
            .blockingEvaluate()
            .getOrThrow()

        assertThat(result.values.size).isEqualTo(3)
        assertThat(result.values[0][0].legend).isEqualTo("rlXteEDaTpt")
        assertThat(result.values[1][0].legend).isEqualTo("rlXteEDaTpt")
        assertThat(result.values[2][0].legend).isEqualTo("rlXteEDaTpt")
    }
}
