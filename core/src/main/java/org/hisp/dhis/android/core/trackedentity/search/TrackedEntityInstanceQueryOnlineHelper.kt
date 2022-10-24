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

import java.lang.Exception
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.FilterOperatorsHelper
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

internal class TrackedEntityInstanceQueryOnlineHelper @Inject constructor(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper
) {

    fun fromScope(scope: TrackedEntityInstanceQueryRepositoryScope): List<TrackedEntityInstanceQueryOnline> {
        return if (scope.eventFilters().isEmpty()) {
            listOf(getBaseBuilder(scope).build())
        } else {
            scope.eventFilters().map { eventFilter ->
                val baseBuilder = getBaseBuilder(scope)

                eventFilter.eventStatus()?.let { baseBuilder.eventStatus(getEventStatus(it, eventFilter.eventDate())) }
                eventFilter.assignedUserMode()?.let { baseBuilder.assignedUserMode(it) }
                eventFilter.programStage()?.let { baseBuilder.programStage(it) }
                eventFilter.eventDate()?.let {
                    baseBuilder.eventStartDate(dateFilterPeriodHelper.getStartDate(it))
                    baseBuilder.eventEndDate(dateFilterPeriodHelper.getEndDate(it))
                }

                baseBuilder.build()
            }
        }
    }

    @Throws(D2Error::class, Exception::class)
    fun queryOnlineBlocking(
        onlineCallFactory: TrackedEntityInstanceQueryCallFactory,
        scope: TrackedEntityInstanceQueryRepositoryScope
    ): List<TrackedEntityInstance> {
        return fromScope(scope).foldRight(emptyList()) { queryOnline, acc ->
            val noPagingQuery = queryOnline.toBuilder().paging(false).build()
            val pageInstances = onlineCallFactory.getCall(noPagingQuery).call()

            val validInstances = pageInstances.filter { tei ->
                val isExcluded = scope.excludedUids()?.contains(tei.uid()) ?: false
                val isAlreadyReturned = acc.any { it.uid() == tei.uid() }

                !isExcluded && !isAlreadyReturned
            }

            acc + validInstances
        }
    }

    private fun getBaseBuilder(
        scope: TrackedEntityInstanceQueryRepositoryScope
    ): TrackedEntityInstanceQueryOnline.Builder {

        val query = scope.query()?.let { query -> query.operator().apiUpperOperator + ":" + query.value() }

        // EnrollmentStatus does not accepts a list of status but a single value in web API.
        val enrollmentStatus = scope.enrollmentStatus()?.getOrNull(0)

        val builder = TrackedEntityInstanceQueryOnline.builder()
            .uids(scope.uids())
            .query(query)
            .attribute(toAPIFilterFormat(scope.attribute()))
            .filter(toAPIFilterFormat(scope.filter()))
            .orgUnits(scope.orgUnits())
            .orgUnitMode(scope.orgUnitMode())
            .program(scope.program())
            .programStage(scope.programStage())
            .enrollmentStatus(enrollmentStatus)
            .followUp(scope.followUp())
            .trackedEntityType(scope.trackedEntityType())
            .includeDeleted(false)
            .order(toAPIOrderFormat(scope.order()))
            .page(1)
            .pageSize(BaseQuery.DEFAULT_PAGE_SIZE)
            .paging(true)

        scope.lastUpdatedDate()?.let {
            builder.lastUpdatedStartDate(dateFilterPeriodHelper.getStartDate(it))
            builder.lastUpdatedEndDate(dateFilterPeriodHelper.getEndDate(it))
        }

        if (scope.program() != null) {
            scope.programDate()?.let {
                builder.programStartDate(dateFilterPeriodHelper.getStartDate(it))
                builder.programEndDate(dateFilterPeriodHelper.getEndDate(it))
            }
            scope.incidentDate()?.let {
                builder.incidentStartDate(dateFilterPeriodHelper.getStartDate(it))
                builder.incidentEndDate(dateFilterPeriodHelper.getEndDate(it))
            }
            scope.eventStatus()?.let { builder.eventStatus(getEventStatus(it, scope.eventDate())) }
            scope.assignedUserMode()?.let { builder.assignedUserMode(it) }
            scope.programStage()?.let { builder.programStage(it) }
            scope.eventDate()?.let {
                builder.eventStartDate(dateFilterPeriodHelper.getStartDate(it))
                builder.eventEndDate(dateFilterPeriodHelper.getEndDate(it))
            }
        }

        return builder
    }

    private fun getEventStatus(eventStatus: List<EventStatus>, eventDate: DateFilterPeriod?): EventStatus? {
        // EventStatus does not accepts a list of status but a single value in web API.
        // Additionally, it requires that eventStartDate and eventEndDate are defined.
        val hasEventDate = eventDate?.startDate() != null && eventDate.endDate() != null
        return if (eventStatus.isNotEmpty() && hasEventDate) {
            eventStatus[0]
        } else null
    }

    private fun toAPIFilterFormat(items: List<RepositoryScopeFilterItem>): List<String>? {
        return items
            .groupBy { it.key() }
            .map { (key, items) ->
                val clause = items.map { item -> ":" + item.operator().apiUpperOperator + ":" + getAPIValue(item) }

                key + clause.joinToString(separator = "")
            }
    }

    private fun getAPIValue(item: RepositoryScopeFilterItem): String {
        return if (item.operator() == FilterItemOperator.IN) {
            val list = FilterOperatorsHelper.strToList(item.value())
            list.joinToString(";")
        } else {
            item.value()
        }
    }

    private fun toAPIOrderFormat(orders: List<TrackedEntityInstanceQueryScopeOrderByItem>): String? {
        return if (orders.isNotEmpty()) {
            orders.mapNotNull { it.toAPIString() }.joinToString(",")
        } else {
            null
        }
    }
}
