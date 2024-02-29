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
import org.hisp.dhis.android.core.visualization.TrackerVisualizationOutputType
import org.hisp.dhis.android.core.visualization.TrackerVisualizationType
import org.junit.Test

class TrackerVisualizationCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val visualizations = d2.visualizationModule().trackerVisualizations()
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun find_uids() {
        val visualizationUids = d2.visualizationModule().trackerVisualizations()
            .blockingGetUids()

        assertThat(visualizationUids.size).isEqualTo(1)
        assertThat(visualizationUids.contains("s85urBIkN0z")).isTrue()
    }

    @Test
    fun find_by_description() {
        val visualizations = d2.visualizationModule().trackerVisualizations()
            .byDescription().eq("Child line list description")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun find_by_display_description() {
        val visualizations = d2.visualizationModule().trackerVisualizations()
            .byDisplayDescription().eq("Child line list description")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun find_by_type() {
        val visualizations = d2.visualizationModule().trackerVisualizations()
            .byType().eq(TrackerVisualizationType.LINE_LIST)
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun find_by_output_type() {
        val visualizations = d2.visualizationModule().trackerVisualizations()
            .byOutputType().eq(TrackerVisualizationOutputType.ENROLLMENT)
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun find_by_program() {
        val visualizations = d2.visualizationModule().trackerVisualizations()
            .byProgram().eq("IpHINAT79UW")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(1)
    }

    @Test
    fun find_by_program_stage() {
        val visualizations = d2.visualizationModule().trackerVisualizations()
            .byProgramStage().eq("IpHINAT79UW")
            .blockingGet()

        assertThat(visualizations.size).isEqualTo(0)
    }

    @Test
    fun include_columns_and_filters_as_children() {
        val visualization = d2.visualizationModule().trackerVisualizations()
            .withColumnsAndFilters()
            .uid("s85urBIkN0z")
            .blockingGet()!!

        assertThat(visualization.columns()!!.size).isEqualTo(3)
        assertThat(visualization.columns()!![0].dimension()).isEqualTo("ou")
        assertThat(visualization.columns()!![0].dimensionType()).isEqualTo("ORGANISATION_UNIT")
        assertThat(visualization.columns()!![0].items()!!.size).isEqualTo(1)
        assertThat(visualization.columns()!![0].items()!![0].uid()).isEqualTo("USER_ORGUNIT")

        assertThat(visualization.filters()!!.size).isEqualTo(1)
        assertThat(visualization.filters()!![0].dimension()).isEqualTo("enrollmentDate")
        assertThat(visualization.filters()!![0].dimensionType()).isEqualTo("PERIOD")
        assertThat(visualization.filters()!![0].items()!!.size).isEqualTo(2)
        assertThat(visualization.filters()!![0].items()!![0].uid()).isEqualTo("2018")
        assertThat(visualization.filters()!![0].items()!![1].uid()).isEqualTo("2019")
    }
}
