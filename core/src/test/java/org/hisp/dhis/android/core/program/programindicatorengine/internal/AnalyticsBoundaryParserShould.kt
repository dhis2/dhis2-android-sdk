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
package org.hisp.dhis.android.core.program.programindicatorengine.internal

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AnalyticsBoundaryParserShould {

    @Test
    fun should_parse_targets() {
        mapOf(
            "EVENT_DATE" to AnalyticsBoundaryTarget.EventDate,
            "ENROLLMENT_DATE" to AnalyticsBoundaryTarget.EnrollmentDate,
            "INCIDENT_DATE" to AnalyticsBoundaryTarget.IncidentDate,
            "#{A03MvHHogjR.a3kGcGDCuk6}" to AnalyticsBoundaryTarget.Custom.DataElement("A03MvHHogjR", "a3kGcGDCuk6"),
            "A{GPkGfbmArby}" to AnalyticsBoundaryTarget.Custom.Attribute("GPkGfbmArby"),
            "PS_EVENTDATE:A03MvHHogjR" to AnalyticsBoundaryTarget.Custom.PSEventDate("A03MvHHogjR"),

            "INVALID_STRING" to null,
            "#{a3kGcGDCuk6}" to null,
            "A{GPkGfbmy}" to null,
            "PS_EVENTDATE:3MvHHogjR" to null
        )
            .forEach {
                assertThat(AnalyticsBoundaryParser.parseBoundaryTarget(it.key)).isEqualTo(it.value)
            }
    }
}
