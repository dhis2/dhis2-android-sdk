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

internal object AnalyticsBoundaryParser {

    private val dataElementRegex = "^#\\{(\\w{11})\\.(\\w{11})\\}$".toRegex()
    private val attributeRegex = "^A\\{(\\w{11})\\}$".toRegex()
    private val psEventDateRegex = "^PS_EVENTDATE:(\\w{11})$".toRegex()

    fun parseBoundaryTarget(target: String?): AnalyticsBoundaryTarget? {
        return if (target == null) {
            null
        } else if (target == "EVENT_DATE") {
            AnalyticsBoundaryTarget.EventDate
        } else if (target == "ENROLLMENT_DATE") {
            AnalyticsBoundaryTarget.EnrollmentDate
        } else if (target == "INCIDENT_DATE") {
            AnalyticsBoundaryTarget.IncidentDate
        } else {
            dataElementRegex.find(target)?.let { match ->
                val (programStageUid, dataElementUid) = match.destructured
                AnalyticsBoundaryTarget.Custom.DataElement(programStageUid, dataElementUid)
            } ?: attributeRegex.find(target)?.let { match ->
                val (attributeUid) = match.destructured
                AnalyticsBoundaryTarget.Custom.Attribute(attributeUid)
            } ?: psEventDateRegex.find(target)?.let { match ->
                val (programStageUid) = match.destructured
                AnalyticsBoundaryTarget.Custom.PSEventDate(programStageUid)
            }
        }
    }
}
