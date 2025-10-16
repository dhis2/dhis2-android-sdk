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

import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.trackedentity.search.FilterOperatorHelper
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.koin.core.annotation.Singleton

@Singleton
internal class EventQueryOnlineAdapter(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper,
    private val filterOperatorHelper: FilterOperatorHelper,
) {
    fun scopeToOnlineQuery(scope: EventQueryRepositoryScope): TrackedEntityInstanceQueryOnline {
        return TrackedEntityInstanceQueryOnline(
            page = 1,
            pageSize = 50,
            paging = false,
            orgUnits = scope.orgUnits() ?: emptyList(),
            orgUnitMode = scope.orgUnitMode(),
            program = scope.program(),
            programStage = scope.programStage(),
            attributeFilter = emptyList(),
            dataValueFilter = scope.dataFilters().flatMap { dataFilter ->
                filterOperatorHelper.getFilterItems(dataFilter.dataItem(), dataFilter)
            },
            programStartDate = null,
            programEndDate = null,
            enrollmentStatus = null,
            incidentStartDate = null,
            incidentEndDate = null,
            followUp = scope.followUp(),
            eventStatus = scope.eventStatus()?.firstOrNull(),
            eventStartDate = scope.eventDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
            eventEndDate = scope.eventDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
            dueStartDate = scope.dueDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
            dueEndDate = scope.dueDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
            trackedEntityType = null,
            includeDeleted = scope.includeDeleted(),
            assignedUserMode = scope.assignedUserMode(),
            uids = scope.events(),
            lastUpdatedStartDate = scope.lastUpdatedDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
            lastUpdatedEndDate = scope.lastUpdatedDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
            order = scope.order(),
        )
    }
}
