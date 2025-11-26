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

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListEventDataFilter
import org.hisp.dhis.android.core.programstageworkinglist.internal.AttributeValueFilterHelper
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityInstanceQueryRepositoryScopeHelper(
    private val filterOperatorHelper: FilterOperatorHelper,
) {

    @Suppress("ComplexMethod")
    fun addTrackedEntityInstanceFilter(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        filter: TrackedEntityInstanceFilter,
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
        eventFilters: List<TrackedEntityInstanceEventFilter>,
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
        filter: AttributeValueFilter?,
    ) {
        filter?.let {
            val existingFilters = builder.build().filter()
            val filterBuilder = RepositoryScopeFilterItem.builder().key(filter.attribute())
            val newFilters = filterOperatorHelper.getFilterItems(filter.attribute(), filter) +
                getAttributeValueFilterOperators(filterBuilder, filter)

            builder.filter(existingFilters + newFilters)
        }
    }

    @Suppress("ComplexMethod")
    fun addProgramStageWorkingList(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        workingList: ProgramStageWorkingList,
    ): TrackedEntityInstanceQueryRepositoryScope {
        val builder = scope.toBuilder()

        workingList.program()?.uid()?.let { builder.program(it) }
        workingList.programStage()?.uid()?.let { builder.programStage(it) }

        workingList.programStageQueryCriteria()?.let { criteria ->
            criteria.eventStatus()?.let { builder.eventStatus(listOf(it)) }
            criteria.eventCreatedAt()?.let { builder.eventCreatedDate(it) }
            criteria.eventOccurredAt()?.let { builder.eventDate(it) }
            criteria.eventScheduledAt()?.let { builder.dueDate(it) }
            criteria.enrollmentStatus()?.let { builder.enrollmentStatus(listOf(it)) }
            criteria.enrolledAt()?.let { builder.programDate(it) }
            criteria.enrollmentOccurredAt()?.let { builder.incidentDate(it) }
            criteria.order()?.let { applyOrder(builder, it) }
            criteria.orgUnit()?.let { builder.orgUnits(listOf(it)) }
            criteria.ouMode()?.let { builder.orgUnitMode(it) }
            criteria.assignedUserMode()?.let { builder.assignedUserMode(it) }
            criteria.dataFilters()?.forEach { applyDataValueFilter(builder, it) }
            criteria.attributeValueFilters()
                ?.map { AttributeValueFilterHelper.from(it) }
                ?.forEach { applyAttributeValueFilter(builder, it) }
        }

        return builder.build()
    }

    private fun applyDataValueFilter(
        builder: TrackedEntityInstanceQueryRepositoryScope.Builder,
        filter: ProgramStageWorkingListEventDataFilter?,
    ) {
        filter?.let {
            val existingFilters = builder.build().dataValue()
            val newFilters = filterOperatorHelper.getFilterItems(filter.dataItem(), filter)

            builder.dataValue(existingFilters + newFilters)
        }
    }

    private fun getAttributeValueFilterOperators(
        filterBuilder: RepositoryScopeFilterItem.Builder,
        filter: AttributeValueFilter,
    ): List<RepositoryScopeFilterItem> {
        val filterItems: MutableList<RepositoryScopeFilterItem> = mutableListOf()

        filter.sw()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.SW).value(it).build())
        }
        filter.ew()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.EW).value(it).build())
        }

        return filterItems
    }

    private fun applyOrder(
        builder: TrackedEntityInstanceQueryRepositoryScope.Builder,
        order: String,
    ) {
        val items = order.split(",").mapNotNull { orderItem ->
            val orderTokens = orderItem.split(":")
            val columnStr = orderTokens.getOrNull(0)
            val directionStr = orderTokens.getOrNull(1) ?: "desc"

            val column = when (columnStr) {
                "created" -> TrackedEntityInstanceQueryScopeOrderColumn.CREATED
                "lastupdated" -> TrackedEntityInstanceQueryScopeOrderColumn.LAST_UPDATED
                "ouname" -> TrackedEntityInstanceQueryScopeOrderColumn.ORGUNIT_NAME
                else -> null
            }

            if (column != null) {
                val direction =
                    if (directionStr == "desc") {
                        RepositoryScope.OrderByDirection.DESC
                    } else {
                        RepositoryScope.OrderByDirection.ASC
                    }

                TrackedEntityInstanceQueryScopeOrderByItem.builder()
                    .column(column)
                    .direction(direction)
                    .build()
            } else {
                null
            }
        }

        if (items.isNotEmpty()) {
            val existingOrder = builder.build().order()
            builder.order(existingOrder + items)
        }
    }

    fun addFilter(
        scope: TrackedEntityInstanceQueryRepositoryScope,
        filter: RepositoryScopeFilterItem,
    ): TrackedEntityInstanceQueryRepositoryScope {
        val otherFilters = scope.filter().filterNot { it.key() == filter.key() && it.operator() == filter.operator() }
        return scope.toBuilder().filter(otherFilters + filter).build()
    }
}
