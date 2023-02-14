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
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.FilterOperatorsHelper
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter

@Reusable
internal class TrackedEntityInstanceQueryRepositoryScopeHelper @Inject constructor(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper
) {

    @Suppress("ComplexMethod")
    fun addTrackedEntityInstanceFilter(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        filter: TrackedEntityInstanceFilter
    ): TrackedEntityInstanceQueryRepositoryScope {
        val builder = scope.toBuilder()

        filter.program()?.let { builder.program(it.uid()) }
        filter.entityQueryCriteria().programStage()?.let { builder.programStage(it) }
        filter.entityQueryCriteria().trackedEntityInstances()?.let { builder.uids(it) }
        filter.entityQueryCriteria().trackedEntityType()?.let { builder.trackedEntityType(it) }
        filter.entityQueryCriteria().enrollmentStatus()?.let { builder.enrollmentStatus(listOf(it)) }
        filter.entityQueryCriteria().enrollmentCreatedDate()?.let { builder.programDate(it) }
        filter.entityQueryCriteria().enrollmentIncidentDate()?.let { builder.incidentDate(it) }
        filter.entityQueryCriteria().eventStatus()?.let { builder.eventStatus(listOf(it)) }
        filter.entityQueryCriteria().eventDate()?.let { builder.eventDate(it) }
        filter.entityQueryCriteria().assignedUserMode()?.let { builder.assignedUserMode(it) }
        filter.entityQueryCriteria().followUp()?.let { builder.followUp(it) }
        filter.entityQueryCriteria().organisationUnit()?.let { builder.orgUnits(listOf(it)) }
        filter.entityQueryCriteria().ouMode()?.let { builder.orgUnitMode(it) }
        filter.entityQueryCriteria().attributeValueFilters()?.forEach { applyAttributeValueFilter(builder, it) }
        filter.entityQueryCriteria().lastUpdatedDate()?.let { builder.lastUpdatedDate(it) }
        filter.eventFilters()?.let { applyEventFilters(builder, it) }

        return builder.build()
    }

    private fun applyEventFilters(
        builder: TrackedEntityInstanceQueryRepositoryScope.Builder,
        eventFilters: List<TrackedEntityInstanceEventFilter>
    ) {
        val filters = eventFilters.map { eventFilter ->
            val eventBuilder = TrackedEntityInstanceQueryEventFilter.builder()

            eventFilter.programStage()?.let { eventBuilder.programStage(it) }
            eventFilter.eventStatus()?.let { eventBuilder.eventStatus(listOf(it)) }
            eventFilter.eventCreatedPeriod()?.let { createPeriod ->
                createPeriod.periodFrom()?.let { periodFrom ->
                    val fromFilter = DateFilterPeriod.builder().startBuffer(periodFrom).build()
                    val newFilter =
                        DateFilterPeriodHelper.mergeDateFilterPeriods(eventBuilder.build().eventDate(), fromFilter)
                    eventBuilder.eventDate(newFilter)
                }
                createPeriod.periodTo()?.let { periodTo ->
                    val toFilter = DateFilterPeriod.builder().endBuffer(periodTo).build()
                    val newFilter =
                        DateFilterPeriodHelper.mergeDateFilterPeriods(eventBuilder.build().eventDate(), toFilter)
                    eventBuilder.eventDate(newFilter)
                }
            }
            eventFilter.assignedUserMode()?.let { eventBuilder.assignedUserMode(it) }

            eventBuilder.build()
        }
        if (filters.isNotEmpty()) {
            builder.eventFilters(filters)
        }
    }

    @Suppress("ComplexMethod")
    private fun applyAttributeValueFilter(
        builder: TrackedEntityInstanceQueryRepositoryScope.Builder,
        filter: AttributeValueFilter?
    ) {
        if (filter != null) {
            val existingFilters = builder.build().filter()

            val filterBuilder = RepositoryScopeFilterItem.builder().key(filter.attribute())

            val newFilters = when {
                filter.eq() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.EQ).value(filter.eq()).build())
                filter.like() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.LIKE).value(filter.like()).build())
                filter.le() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.LE).value(filter.le()).build())
                filter.lt() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.LT).value(filter.lt()).build())
                filter.ge() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.GE).value(filter.ge()).build())
                filter.gt() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.GT).value(filter.gt()).build())
                filter.sw() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.SW).value(filter.sw()).build())
                filter.ew() != null ->
                    listOf(filterBuilder.operator(FilterItemOperator.EW).value(filter.ew()).build())
                filter.`in`() != null ->
                    listOf(
                        filterBuilder.operator(FilterItemOperator.IN)
                            .value(FilterOperatorsHelper.listToStr(filter.`in`()!!)).build()
                    )
                filter.dateFilter() != null -> {
                    val start = dateFilterPeriodHelper.getStartDate(filter.dateFilter()!!)?.let {
                        filterBuilder.operator(FilterItemOperator.GE).value(DateUtils.DATE_FORMAT.format(it)).build()
                    }
                    val end = dateFilterPeriodHelper.getEndDate(filter.dateFilter()!!)?.let {
                        filterBuilder.operator(FilterItemOperator.LE).value(DateUtils.DATE_FORMAT.format(it)).build()
                    }
                    listOfNotNull(start, end)
                }

                else -> emptyList()
            }

            builder.filter(existingFilters + newFilters)
        }
    }
}
