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
package org.hisp.dhis.android.core.visualization

import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.text.ParseException
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.junit.Test

class VisualizationAPI36Should : BaseObjectShould("visualization/visualization_api_36.json"), ObjectShould {

    @Test
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val visualizationAPI36 = objectMapper.readValue(jsonStream, VisualizationAPI36::class.java)

        assertThat(visualizationAPI36.id).isEqualTo("PYBH8ZaAQnC")
        assertThat(visualizationAPI36.type).isEqualTo(VisualizationType.PIVOT_TABLE)

        assertThat(visualizationAPI36.legendDisplayStrategy).isEquivalentAccordingToCompareTo(LegendStrategy.FIXED)
        assertThat(visualizationAPI36.legendDisplayStyle).isEquivalentAccordingToCompareTo(LegendStyle.FILL)
    }

    @Test
    fun convert_to_visualization() {
        val visualizationAPI36 = objectMapper.readValue(jsonStream, VisualizationAPI36::class.java)

        val visualizationStream = this.javaClass.classLoader!!.getResourceAsStream("visualization/visualization.json")
        val visualization = objectMapper.readValue(visualizationStream, Visualization::class.java)

        assertThat(visualizationAPI36.toVisualization()).isEqualTo(visualization)
    }
}
