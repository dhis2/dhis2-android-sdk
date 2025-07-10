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
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.visualization.Visualization36DTO
import org.hisp.dhis.android.network.visualization.VisualizationDTO
import org.junit.Test

class Visualization36DTOShould : CoreObjectShould("visualization/visualization_api_36.json") {

    @Test
    override fun map_from_json_string() {
        val visualization36 = deserialize(Visualization36DTO.serializer())

        assertThat(visualization36.id).isEqualTo("PYBH8ZaAQnC")
        assertThat(visualization36.type).isEqualTo(VisualizationType.PIVOT_TABLE.name)

        assertThat(visualization36.legendDisplayStrategy).isEqualTo(LegendStrategy.FIXED.name)
        assertThat(visualization36.legendDisplayStyle).isEqualTo(LegendStyle.FILL.name)
    }

    @Test
    fun convert_to_visualization() {
        val visualization36 = deserialize(Visualization36DTO.serializer())
        val visualization = deserializePath("visualization/visualization.json", VisualizationDTO.serializer())

        assertThat(visualization36.toDomain()).isEqualTo(visualization.toDomain())
    }
}
