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
package org.hisp.dhis.android.core.trackedentity.search

import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.FilterOperatorsHelper
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory

internal class TrackedEntityInstanceQueryOnlineHelper @Inject constructor(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper
) {

    fun fromScope(scope: TrackedEntityInstanceQueryRepositoryScope): List<TrackedEntityInstanceQueryOnline> {
        return if (scope.eventFilters().isEmpty()) {
            listOf(getBaseQuery(scope))
        } else {
            scope.eventFilters().map { eventFilter ->
                getBaseQuery(scope)
                    .run {
                        eventFilter.eventStatus()
                            ?.let { copy(eventStatus = getEventStatus(it, eventFilter.eventDate())) } ?: this
                    }
                    .run {
                        eventFilter.assignedUserMode()?.let { copy(assignedUserMode = it) } ?: this
                    }
                    .run {
                        eventFilter.programStage()?.let { copy(programStage = it) } ?: this
                    }
                    .run {
                        eventFilter.eventDate()?.let {
                            copy(
                                eventStartDate = dateFilterPeriodHelper.getStartDate(it),
                                eventEndDate = dateFilterPeriodHelper.getEndDate(it)
                            )
                        } ?: this
                    }
            }
        }
    }

    @Throws(D2Error::class, Exception::class)
    fun queryOnlineBlocking(
        trackerParentCallFactory: TrackerParentCallFactory,
        scope: TrackedEntityInstanceQueryRepositoryScope
    ): List<TrackedEntityInstance> {
        return fromScope(scope).foldRight(emptyList()) { queryOnline, acc ->
            val noPagingQuery = queryOnline.copy(paging = false)
            val pageInstances = runBlocking {
                trackerParentCallFactory.getTrackedEntityCall()
                    .getQueryCall(noPagingQuery)
            }

            val validInstances = pageInstances.trackedEntities.filter { tei ->
                val isExcluded = scope.excludedUids()?.contains(tei.uid()) ?: false
                val isAlreadyReturned = acc.any { it.uid() == tei.uid() }

                !isExcluded && !isAlreadyReturned
            }

            acc + validInstances
        }
    }

    @Suppress("ComplexMethod")
    private fun getBaseQuery(
        scope: TrackedEntityInstanceQueryRepositoryScope
    ): TrackedEntityInstanceQueryOnline {
        val query = scope.query()?.let { query -> query.operator().apiUpperOperator + ":" + query.value() }

        // EnrollmentStatus does not accepts a list of status but a single value in web API.
        val enrollmentStatus = scope.enrollmentStatus()?.getOrNull(0)

        return TrackedEntityInstanceQueryOnline(
            page = 1,
            pageSize = BaseQuery.DEFAULT_PAGE_SIZE,
            paging = true,
            orgUnits = scope.orgUnits(),
            orgUnitMode = scope.orgUnitMode(),
            program = scope.program(),
            query = query,
            attributeFilter = scope.filter(),
            dataValueFilter = scope.dataValue(),
            lastUpdatedStartDate = scope.lastUpdatedDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
            lastUpdatedEndDate = scope.lastUpdatedDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
            enrollmentStatus = enrollmentStatus,
            followUp = scope.followUp(),
            includeDeleted = scope.includeDeleted(),
            trackedEntityType = scope.trackedEntityType(),
            order = toAPIOrderFormat(scope.order())
        ).run {
            scope.program()?.let {
                copy(
                    programStartDate = scope.programDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    programEndDate = scope.programDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
                    incidentStartDate = scope.incidentDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    incidentEndDate = scope.incidentDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
                    eventStatus = scope.eventStatus()?.let { getEventStatus(it, scope.eventDate()) },
                    assignedUserMode = assignedUserMode,
                    programStage = scope.programStage(),
                    eventStartDate = scope.eventDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    eventEndDate = scope.eventDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
                    dueStartDate = scope.dueDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    dueEndDate = scope.dueDate()?.let { dateFilterPeriodHelper.getEndDate(it) }
                )
            } ?: this
        }
    }

    private fun getEventStatus(eventStatus: List<EventStatus>, eventDate: DateFilterPeriod?): EventStatus? {
        // EventStatus does not accepts a list of status but a single value in web API.
        // Additionally, it requires that eventStartDate and eventEndDate are defined.
        val hasEventDate = eventDate?.startDate() != null && eventDate.endDate() != null
        return if (eventStatus.isNotEmpty() && hasEventDate) {
            eventStatus[0]
        } else {
            null
        }
    }

    private fun toAPIOrderFormat(orders: List<TrackedEntityInstanceQueryScopeOrderByItem>): String? {
        return if (orders.isNotEmpty()) {
            orders.mapNotNull { it.toAPIString() }.joinToString(",")
        } else {
            null
        }
    }

    companion object {

        fun toAPIFilterFormat(items: List<RepositoryScopeFilterItem>, upper: Boolean): List<String> {
            // Compatibility for the new Tracker (old Tracker will ignore this format)
            // Following characters need to be escaped escaped with a "/" for backend functionality

            return items
                .groupBy { it.key() }
                .map { (key, items) ->
                    val clause = items.map { item ->
                        val operator = if (upper) item.operator().apiUpperOperator else item.operator().apiOperator

                        ":" + operator + ":" + getAPIValue(item)
                    }

                    key + clause.joinToString(separator = "")
                }
        }

        private fun getAPIValue(item: RepositoryScopeFilterItem): String {
            return if (item.operator() == FilterItemOperator.IN) {
                val list = FilterOperatorsHelper.strToList(item.value()).map { escapeChars(it) }
                list.joinToString(";")
            } else {
                escapeChars(item.value())
            }
        }

        private fun escapeChars(targetString: String): String {
            val charsToEscape = "[;,:]".toRegex()
            val escapingChar = "/\$0"
            return targetString.replace(charsToEscape, escapingChar)
        }
    }
}
