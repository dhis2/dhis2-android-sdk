/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.event.search

import org.hisp.dhis.android.core.tracker.TrackerExporterVersion

sealed class EventQueryScopeOrderApiName(
    private val v1Name: String?,
    private val v2Name: String?,
) {
    data object Event : EventQueryScopeOrderApiName("event", "event")
    data object Program : EventQueryScopeOrderApiName("program", "program")
    data object ProgramStage : EventQueryScopeOrderApiName("programStage", "programStage")
    data object Enrollment : EventQueryScopeOrderApiName("enrollment", "enrollment")
    data object EnrollmentStatus : EventQueryScopeOrderApiName("enrollmentStatus", "enrollmentStatus")
    data object OrgUnit : EventQueryScopeOrderApiName("orgUnit", "orgUnit")
    data object OrgUnitName : EventQueryScopeOrderApiName("orgUnitName", "orgUnitName")
    data object TrackedEntityInstance : EventQueryScopeOrderApiName("trackedEntityInstance", "trackedEntity")
    data object EventDate : EventQueryScopeOrderApiName("eventDate", "occurredAt")
    data object FollowUp : EventQueryScopeOrderApiName("followup", "followup")
    data object Status : EventQueryScopeOrderApiName("status", "status")
    data object DueDate : EventQueryScopeOrderApiName("dueDate", "scheduledAt")
    data object StoredBy : EventQueryScopeOrderApiName("storedBy", "storedBy")
    data object Created : EventQueryScopeOrderApiName("created", "createdAt")
    data object LastUpdated : EventQueryScopeOrderApiName("lastUpdated", "updatedAt")
    data object CompletedBy : EventQueryScopeOrderApiName("completedBy", "completedBy")
    data object CompletedDate : EventQueryScopeOrderApiName("completedDate", "completedAt")
    class DataElement(dataElementId: String) : EventQueryScopeOrderApiName(dataElementId, dataElementId)

    fun getApiName(version: TrackerExporterVersion): String? {
        return when (version) {
            TrackerExporterVersion.V1 -> v1Name
            TrackerExporterVersion.V2 -> v2Name
        }
    }
}
