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
package org.hisp.dhis.android.core.trackedentity.search

import dagger.Reusable
import java.text.ParseException
import java.util.concurrent.Callable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService

@Reusable
internal class TrackedEntityInstanceQueryCallFactory @Inject constructor(
    private val service: TrackedEntityInstanceService,
    private val mapper: SearchGridMapper,
    private val apiCallExecutor: APICallExecutor,
    private val dhisVersionManager: DHISVersionManager
) {
    fun getCall(query: TrackedEntityInstanceQueryOnline): Callable<List<TrackedEntityInstance>> {
        return Callable { queryTrackedEntityInstances(query) }
    }

    @Throws(D2Error::class)
    private fun queryTrackedEntityInstances(query: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        val uidsStr =
            if (query.uids() == null) null else CollectionsHelper.joinCollectionWithSeparator(query.uids(), ";")
        val orgUnitModeStr = if (query.orgUnitMode() == null) null else query.orgUnitMode().toString()
        val assignedUserModeStr = if (query.assignedUserMode() == null) null else query.assignedUserMode().toString()
        val enrollmentStatus = if (query.enrollmentStatus() == null) null else query.enrollmentStatus().toString()
        val eventStatus = getEventStatus(query)

        val orgUnits =
            if (query.orgUnits().isEmpty()) null
            else CollectionsHelper.joinCollectionWithSeparator(query.orgUnits(), ";")

        val searchGridCall = service.query(
            uidsStr,
            orgUnits,
            orgUnitModeStr,
            query.program(),
            query.programStage(),
            query.formattedProgramStartDate(),
            query.formattedProgramEndDate(),
            enrollmentStatus,
            query.formattedIncidentStartDate(),
            query.formattedIncidentEndDate(),
            query.followUp(),
            query.formattedEventStartDate(),
            query.formattedEventEndDate(),
            eventStatus,
            query.trackedEntityType(),
            query.query(),
            query.attribute(),
            query.filter(),
            assignedUserModeStr,
            query.formattedLastUpdatedStartDate(),
            query.formattedLastUpdatedEndDate(),
            query.order(),
            query.paging(),
            query.page(),
            query.pageSize()
        )

        return try {
            val searchGrid = apiCallExecutor.executeObjectCallWithErrorCatcher(
                searchGridCall,
                TrackedEntityInstanceQueryErrorCatcher()
            )
            mapper.transform(searchGrid)
        } catch (pe: ParseException) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.SEARCH_GRID_PARSE)
                .errorComponent(D2ErrorComponent.SDK)
                .errorDescription("Search Grid mapping exception")
                .originalException(pe)
                .build()
        }
    }

    private fun getEventStatus(query: TrackedEntityInstanceQueryOnline): String? {
        return if (query.eventStatus() == null) {
            null
        } else if (!dhisVersionManager.isGreaterThan(DHISVersion.V2_33) && query.eventStatus() == EventStatus.ACTIVE) {
            EventStatus.VISITED.toString()
        } else {
            query.eventStatus().toString()
        }
    }
}
