/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.data.visualization

import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.SortingDirection
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils
import org.hisp.dhis.android.core.visualization.*

internal object TrackerVisualizationSamples {

    private const val DATE_STR = "2021-06-16T14:26:50.195"
    private val DATE = FillPropertiesTestUtils.parseDate(DATE_STR)

    @JvmStatic
    fun trackerVisualization(): TrackerVisualization = TrackerVisualization.builder()
        .id(1L)
        .uid("PYBH8ZaAQnC")
        .name("Android SDK Tracker Visualization sample")
        .displayName("Android SDK Tracker Visualization sample")
        .created(DATE)
        .lastUpdated(DATE)
        .description("Sample tracker visualization for the Android SDK")
        .displayDescription("Sample tracker visualization for the Android SDK")
        .type(TrackerVisualizationType.LINE_LIST)
        .outputType(TrackerVisualizationOutputType.ENROLLMENT)
        .program(ObjectWithUid.create(""))
        .programStage(ObjectWithUid.create(""))
        .trackedEntityType(ObjectWithUid.create(""))
        .columns(
            listOf(
                TrackerVisualizationDimension.builder()
                    .dimension("ou")
                    .dimensionType("ORGANISATION_UNIT")
                    .items(listOf(ObjectWithUid.create("USER_ORGUNIT")))
                    .build(),
            ),
        )
        .filters(
            listOf(
                TrackerVisualizationDimension.builder()
                    .dimension("enrollmentDate")
                    .dimensionType("PERIOD")
                    .items(listOf(ObjectWithUid.create("LAST_5_YEARS")))
                    .build(),
            ),
        )
        .sorting(
            listOf(
                TrackerVisualizationSorting.builder()
                    .dimension("dimension")
                    .direction(SortingDirection.ASC)
                    .build(),
            ),
        )
        .build()
}
