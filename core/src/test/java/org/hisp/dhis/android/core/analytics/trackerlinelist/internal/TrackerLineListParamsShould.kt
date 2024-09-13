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
package org.hisp.dhis.android.core.analytics.trackerlinelist.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.trackerlinelist.DataFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.DateFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackerLineListParamsShould {

    @Test
    fun should_add_two_params() {
        val params1 = TrackerLineListParams(
            trackerVisualization = null,
            outputType = TrackerLineListOutputType.EVENT,
            programId = null,
            programStageId = "program_stage_uid",
            columns = listOf(
                TrackerLineListItem.ProgramAttribute("attribute", listOf(DataFilter.GreaterThan("5"))),
                TrackerLineListItem.ProgramIndicator("indicator"),
                TrackerLineListItem.EventDate(),
            ),
            filters = listOf(),
        )

        val params2 = TrackerLineListParams(
            trackerVisualization = null,
            outputType = null,
            programId = "program_uid",
            programStageId = null,
            columns = listOf(
                TrackerLineListItem.ProgramAttribute("attribute", listOf(DataFilter.NotEqualTo("10"))),
            ),
            filters = listOf(
                TrackerLineListItem.EventDate(listOf(DateFilter.Absolute("202405"))),
            ),
        )

        val params = params1 + params2

        assertThat(params.trackerVisualization).isNull()
        assertThat(params.outputType).isEqualTo(TrackerLineListOutputType.EVENT)
        assertThat(params.programId).isEqualTo("program_uid")
        assertThat(params.programStageId).isEqualTo("program_stage_uid")
        assertThat(params.columns).containsExactly(
            TrackerLineListItem.ProgramAttribute("attribute", listOf(DataFilter.NotEqualTo("10"))),
            TrackerLineListItem.ProgramIndicator("indicator"),
        )
        assertThat(params.filters).containsExactly(
            TrackerLineListItem.EventDate(listOf(DateFilter.Absolute("202405"))),
        )
    }

    @Test
    fun should_flatten_repeated_data_elements() {
        val params = TrackerLineListParams(
            trackerVisualization = null,
            outputType = TrackerLineListOutputType.ENROLLMENT,
            programId = "programId",
            programStageId = null,
            columns = listOf(
                TrackerLineListItem.ProgramDataElement(
                    "dataElement",
                    "programStage",
                    listOf(),
                    listOf(0, -1, -2, 1, 2),
                ),
            ),
            filters = emptyList(),
        )

        val flattenedParams = params.flattenRepeatedDataElements()

        assertThat(flattenedParams.columns.size).isEqualTo(5)

        flattenedParams.columns.map { it as TrackerLineListItem.ProgramDataElement }.forEachIndexed { index, item ->
            when (index) {
                0 -> assertIndex(item, 1)
                1 -> assertIndex(item, 2)
                2 -> assertIndex(item, -2)
                3 -> assertIndex(item, -1)
                4 -> assertIndex(item, 0)
            }
        }
    }

    private fun assertIndex(item: TrackerLineListItem.ProgramDataElement, idx: Int) {
        assertThat(item.repetitionIndexes!!.first()).isEqualTo(idx)
    }
}
