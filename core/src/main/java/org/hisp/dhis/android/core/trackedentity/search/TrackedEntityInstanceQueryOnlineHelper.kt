/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.event.EventStatus

internal class TrackedEntityInstanceQueryOnlineHelper @Inject constructor(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper
) {

    fun fromScope(scope: TrackedEntityInstanceQueryRepositoryScope): List<TrackedEntityInstanceQueryOnline> {
        return if (scope.eventFilters().isEmpty()) {
            listOf(getBaseBuilder(scope).build())
        } else {
            scope.eventFilters().map { eventFilter ->
                val baseBuilder = getBaseBuilder(scope)

                val eventStatus = getEventStatus(eventFilter)

                baseBuilder
                    .eventStatus(eventStatus)
                    .assignedUserMode(eventFilter.assignedUserMode())

                if (eventFilter.eventDate() != null) {
                    baseBuilder.eventStartDate(dateFilterPeriodHelper.getStartDate(eventFilter.eventDate()!!))
                    baseBuilder.eventEndDate(dateFilterPeriodHelper.getEndDate(eventFilter.eventDate()!!))
                }

                baseBuilder.build()
            }
        }
    }

    private fun getBaseBuilder(
        scope: TrackedEntityInstanceQueryRepositoryScope
    ): TrackedEntityInstanceQueryOnline.Builder {

        val query = scope.query()?.let { query -> query.operator().apiUpperOperator + ":" + query.value() }

        // EnrollmentStatus does not accepts a list of status but a single value in web API.
        val enrollmentStatus = scope.enrollmentStatus()?.getOrNull(0)

        val builder = TrackedEntityInstanceQueryOnline.builder()
            .query(query)
            .attribute(toAPIFilterFormat(scope.attribute()))
            .filter(toAPIFilterFormat(scope.filter()))
            .orgUnits(scope.orgUnits())
            .orgUnitMode(scope.orgUnitMode())
            .program(scope.program())
            .enrollmentStatus(enrollmentStatus)
            .followUp(scope.followUp())
            .trackedEntityType(scope.trackedEntityType())
            .includeDeleted(false)
            .order(toAPIOrderFormat(scope.order()))
            .page(1)
            .pageSize(BaseQuery.DEFAULT_PAGE_SIZE)
            .paging(true)

        if (scope.programDate() != null) {
            builder.programStartDate(dateFilterPeriodHelper.getStartDate(scope.programDate()!!))
            builder.programEndDate(dateFilterPeriodHelper.getEndDate(scope.programDate()!!))
        }

        return builder
    }

    private fun getEventStatus(eventFilter: TrackedEntityInstanceQueryEventFilter): EventStatus? {
        // EventStatus does not accepts a list of status but a single value in web API.
        // Additionally, it requires that eventStartDate and eventEndDate are defined.
        val eventStatus = eventFilter.eventStatus()
        val hasEventDate = eventFilter.eventDate()?.startDate() != null && eventFilter.eventDate()?.endDate() != null
        return if (!eventStatus.isNullOrEmpty() && hasEventDate) {
            eventStatus[0]
        } else null
    }

    private fun toAPIFilterFormat(items: List<RepositoryScopeFilterItem>): List<String>? {
        val itemMap: MutableMap<String, String> = HashMap()
        for (item in items) {
            val filterClause = ":" + item.operator().apiUpperOperator + ":" + item.value()
            val existingClause = itemMap[item.key()]
            val newClause = (existingClause ?: "") + filterClause
            itemMap[item.key()] = newClause
        }
        val itemList: MutableList<String> = ArrayList()
        for ((key, value) in itemMap) {
            itemList.add(key + value)
        }
        return itemList
    }

    private fun toAPIOrderFormat(orders: List<TrackedEntityInstanceQueryScopeOrderByItem>): String {
        return orders.mapNotNull { it.toAPIString() }.joinToString(",")
    }
}
