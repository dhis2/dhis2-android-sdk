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

import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.FilterOperatorsHelper
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
internal class TrackedEntityInstanceQueryOnlineHelper(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper,
) {

    @Suppress("ComplexMethod")
    fun fromScope(scope: TrackedEntityInstanceQueryRepositoryScope): List<TrackedEntityInstanceQueryOnline> {
        val queries = if (scope.eventFilters().isEmpty()) {
            listOf(getBaseQuery(scope))
        } else {
            scope.eventFilters().map { eventFilter ->
                getBaseQuery(scope)
                    .run {
                        eventFilter.eventStatus()
                            ?.let { copy(eventStatus = getEventStatus(it)) } ?: this
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
                                eventEndDate = dateFilterPeriodHelper.getEndDate(it),
                            )
                        } ?: this
                    }
            }
        }

        return queries.map { query ->
            if (query.eventStatus == EventStatus.SCHEDULE && query.dueStartDate == null) {
                query.copy(dueStartDate = Date())
            } else {
                query
            }
        }
    }

    @Suppress("ComplexMethod")
    private fun getBaseQuery(
        scope: TrackedEntityInstanceQueryRepositoryScope,
    ): TrackedEntityInstanceQueryOnline {
        // EnrollmentStatus does not accepts a list of status but a single value in web API.
        val enrollmentStatus = scope.enrollmentStatus()?.getOrNull(0)

        return TrackedEntityInstanceQueryOnline(
            page = 1,
            pageSize = BaseQuery.DEFAULT_PAGE_SIZE,
            paging = true,
            orgUnits = scope.orgUnits(),
            orgUnitMode = scope.orgUnitMode(),
            program = scope.program(),
            attributeFilter = scope.filter(),
            dataValueFilter = scope.dataValue(),
            lastUpdatedStartDate = scope.lastUpdatedDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
            lastUpdatedEndDate = scope.lastUpdatedDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
            enrollmentStatus = enrollmentStatus,
            followUp = scope.followUp(),
            includeDeleted = scope.includeDeleted(),
            trackedEntityType = scope.trackedEntityType(),
            order = scope.order(),
        ).run {
            scope.program()?.let {
                copy(
                    programStartDate = scope.programDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    programEndDate = scope.programDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
                    incidentStartDate = scope.incidentDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    incidentEndDate = scope.incidentDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
                    eventStatus = scope.eventStatus()?.let { getEventStatus(it) },
                    assignedUserMode = assignedUserMode,
                    programStage = scope.programStage(),
                    eventStartDate = scope.eventDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    eventEndDate = scope.eventDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
                    dueStartDate = scope.dueDate()?.let { dateFilterPeriodHelper.getStartDate(it) },
                    dueEndDate = scope.dueDate()?.let { dateFilterPeriodHelper.getEndDate(it) },
                )
            } ?: this
        }
    }

    private fun getEventStatus(eventStatus: List<EventStatus>): EventStatus? {
        // EventStatus does not accepts a list of status but a single value in web API.
        return eventStatus.firstOrNull()
    }

    companion object {

        fun toAPIOrderFormat(
            orders: List<TrackedEntityInstanceQueryScopeOrderByItem>,
            version: TrackerExporterVersion,
        ): String? {
            val apiOrders = orders.mapNotNull { it.toAPIString(version) }
            return if (apiOrders.isNotEmpty()) {
                apiOrders.joinToString(",")
            } else {
                null
            }
        }

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
