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
package org.hisp.dhis.android.core.program

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.programindicator.ProgramIndicatorDTO
import org.junit.Test

class ProgramIndicatorShould : CoreObjectShould("program/program_indicator.json") {

    @Test
    override fun map_from_json_string() {
        val programIndicatorDTO = deserialize(ProgramIndicatorDTO.serializer())
        val programIndicator = programIndicatorDTO.toDomain()

        assertThat(programIndicator.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-09-21T23:35:50.945"))
        assertThat(programIndicator.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-09-21T23:47:57.820"))
        assertThat(programIndicator.uid()).isEqualTo("GSae40Fyppf")
        assertThat(programIndicator.name()).isEqualTo("Age at visit")
        assertThat(programIndicator.displayName()).isEqualTo("Age at visit")
        assertThat(programIndicator.displayInForm()).isTrue()
        assertThat(programIndicator.expression()).isEqualTo("d2:yearsBetween(A{iESIqZ0R0R0},V{event_date})")
        assertThat(programIndicator.dimensionItem()).isEqualTo("GSae40Fyppf")
        assertThat(programIndicator.filter()).isNull()
        assertThat(programIndicator.decimals()).isNull()
        assertThat(programIndicator.aggregationType()).isEqualTo(AggregationType.AVERAGE)
        assertThat(programIndicator.analyticsType()).isEqualTo(AnalyticsType.EVENT)
        assertThat(programIndicator.analyticsPeriodBoundaries()!!.size).isEqualTo(5)
        assertThat(programIndicator.analyticsPeriodBoundaries()!![0].boundaryTarget())
            .isEqualTo("Custom boundary")
        assertThat(programIndicator.analyticsPeriodBoundaries()!![1].boundaryTarget()).isEqualTo("INCIDENT_DATE")

        assertThat(programIndicator.analyticsPeriodBoundaries()!![0].boundaryTargetType())
            .isEqualTo(BoundaryTargetType.Custom("Custom boundary"))
        assertThat(programIndicator.analyticsPeriodBoundaries()!![1].boundaryTargetType())
            .isEqualTo(BoundaryTargetType.IncidentDate)

        assertThat(programIndicator.analyticsPeriodBoundaries()!![0].analyticsPeriodBoundaryType())
            .isEqualTo(AnalyticsPeriodBoundaryType.AFTER_END_OF_REPORTING_PERIOD)
        assertThat(programIndicator.analyticsPeriodBoundaries()!![1].offsetPeriods()).isEqualTo(-3)
    }
}
