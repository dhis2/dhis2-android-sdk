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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.common.SortingDirection
import org.hisp.dhis.android.network.trackervisualization.TrackerVisualizationDTO
import org.junit.Test

class TrackerVisualizationShould : BaseObjectKotlinxShould("visualization/tracker_visualization.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val visualizationDTO = deserialize(TrackerVisualizationDTO.serializer())
        val visualization = visualizationDTO.toDomain()

        assertThat(visualization.uid()).isEqualTo("s85urBIkN0z")
        assertThat(visualization.name()).isEqualTo("TB program")
        assertThat(visualization.displayName()).isEqualTo("TB program")
        assertThat(visualization.description()).isEqualTo("Line list for TB program")
        assertThat(visualization.displayDescription()).isEqualTo("Line list for TB program")
        assertThat(visualization.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2024-02-07T07:37:41.116"))
        assertThat(visualization.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2024-02-07T07:43:25.556"))
        assertThat(visualization.type()).isEqualTo(TrackerVisualizationType.LINE_LIST)
        assertThat(visualization.outputType()).isEqualTo(TrackerVisualizationOutputType.ENROLLMENT)
        assertThat(visualization.program()!!.uid()).isEqualTo("ur1Edk5Oe2n")

        assertThat(visualization.columns()?.size).isEqualTo(4)
        val dataElementColumn = visualization.columns()!!.find { it.dimensionType() == "PROGRAM_DATA_ELEMENT" }!!
        assertThat(dataElementColumn.dimension()).isEqualTo("fCXKBdc27Bt")
        assertThat(dataElementColumn.program()!!.uid()).isEqualTo("ur1Edk5Oe2n")
        assertThat(dataElementColumn.programStage()!!.uid()).isEqualTo("EPEcjy3FWmI")
        assertThat(dataElementColumn.filter()).isEqualTo("IN:1")
        assertThat(dataElementColumn.repetition()!!.indexes()).isEqualTo(listOf(1, 2, -2, -1, 0))

        assertThat(visualization.filters()?.size).isEqualTo(1)
        assertThat(visualization.filters()!![0].dimension()).isEqualTo("enrollmentDate")
        assertThat(visualization.filters()!![0].dimensionType()).isEqualTo("PERIOD")
        assertThat(visualization.filters()!![0].items()!!.size).isEqualTo(1)
        assertThat(visualization.filters()!![0].items()!![0].uid()).isEqualTo("LAST_5_YEARS")

        assertThat(visualization.sorting()?.size).isEqualTo(1)
        assertThat(visualization.sorting()!![0].dimension()).isEqualTo("w75KJ2mc4zz")
        assertThat(visualization.sorting()!![0].direction()).isEqualTo(SortingDirection.ASC)
    }
}
