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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.junit.Test

class ProgramIndicatorShould : BaseObjectShould("program/program_indicator.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val programIndicator = objectMapper.readValue(jsonStream, ProgramIndicator::class.java)
        Truth.assertThat(programIndicator.created()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-21T23:35:50.945")
        )
        Truth.assertThat(programIndicator.lastUpdated()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-21T23:47:57.820")
        )
        Truth.assertThat(programIndicator.uid()).isEqualTo("GSae40Fyppf")
        Truth.assertThat(programIndicator.name()).isEqualTo("Age at visit")
        Truth.assertThat(programIndicator.displayName()).isEqualTo("Age at visit")
        Truth.assertThat(programIndicator.displayInForm()).isTrue()
        Truth.assertThat(programIndicator.expression()).isEqualTo("d2:yearsBetween(A{iESIqZ0R0R0},V{event_date})")
        Truth.assertThat(programIndicator.dimensionItem()).isEqualTo("GSae40Fyppf")
        Truth.assertThat(programIndicator.filter()).isNull()
        Truth.assertThat(programIndicator.decimals()).isNull()
        Truth.assertThat(programIndicator.aggregationType()).isEqualTo(AggregationType.AVERAGE)
        Truth.assertThat(programIndicator.analyticsType()).isEqualTo(AnalyticsType.EVENT)
        Truth.assertThat(programIndicator.analyticsPeriodBoundaries()!!.size).isEqualTo(5)
        Truth.assertThat(programIndicator.analyticsPeriodBoundaries()!![0].boundaryTarget())
            .isEqualTo("Custom boundary")
        Truth.assertThat(programIndicator.analyticsPeriodBoundaries()!![1].boundaryTarget()).isEqualTo("INCIDENT_DATE")

        Truth.assertThat(programIndicator.analyticsPeriodBoundaries()!![0].boundaryTargetType())
            .isEqualTo(BoundaryTargetType.Custom("Custom boundary"))
        Truth.assertThat(programIndicator.analyticsPeriodBoundaries()!![1].boundaryTargetType())
            .isEqualTo(BoundaryTargetType.IncidentDate)

        Truth.assertThat(programIndicator.analyticsPeriodBoundaries()!![0].analyticsPeriodBoundaryType())
            .isEqualTo(AnalyticsPeriodBoundaryType.AFTER_END_OF_REPORTING_PERIOD)
        Truth.assertThat(programIndicator.analyticsPeriodBoundaries()!![1].offsetPeriods()).isEqualTo(-3)
    }
}
