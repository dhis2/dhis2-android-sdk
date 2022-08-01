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
package org.hisp.dhis.android.core.event.search

import dagger.Reusable
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.search.EventQueryScopeOrderColumn.Type as OrderColumnType
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.user.AuthenticatedUserObjectRepository

@Reusable
internal class EventCollectionRepositoryAdapter @Inject constructor(
    private val eventCollectionRepository: EventCollectionRepository,
    private val organisationUnitCollectionRepository: OrganisationUnitCollectionRepository,
    private val userRepository: AuthenticatedUserObjectRepository,
    private val datePeriodHelper: DateFilterPeriodHelper
) {

    @Suppress("ComplexMethod")
    fun getCollectionRepository(scope: EventQueryRepositoryScope): EventCollectionRepository {
        var repository = eventCollectionRepository

        scope.program()?.let { repository = repository.byProgramUid().eq(it) }
        scope.programStage()?.let { repository = repository.byProgramStageUid().eq(it) }
        scope.followUp()?.let { repository = repository.byFollowUp(it) }
        scope.trackedEntityInstance()?.let { repository = repository.byTrackedEntityInstanceUids(listOf(it)) }
        scope.orgUnitMode().let { repository = applyOrgunitSelection(repository, scope) }
        scope.assignedUserMode()?.let { repository = applyUserAssignedMode(repository, it) }
        scope.dataFilters().forEach { filter -> repository = applyDataFilter(repository, filter) }
        if (!scope.events().isNullOrEmpty()) {
            repository = repository.byUid().`in`(scope.events())
        }
        scope.eventStatus()?.let { repository = repository.byStatus().`in`(it) }
        scope.eventDate()?.let { period ->
            datePeriodHelper.getStartDate(period)?.let { repository = repository.byEventDate().afterOrEqual(it) }
            datePeriodHelper.getEndDate(period)?.let { repository = repository.byEventDate().beforeOrEqual(it) }
        }
        scope.dueDate()?.let { period ->
            datePeriodHelper.getStartDate(period)?.let { repository = repository.byDueDate().afterOrEqual(it) }
            datePeriodHelper.getEndDate(period)?.let { repository = repository.byDueDate().beforeOrEqual(it) }
        }
        scope.lastUpdatedDate()?.let { period ->
            datePeriodHelper.getStartDate(period)?.let { repository = repository.byLastUpdated().afterOrEqual(it) }
            datePeriodHelper.getEndDate(period)?.let { repository = repository.byLastUpdated().beforeOrEqual(it) }
        }
        scope.completedDate()?.let { period ->
            datePeriodHelper.getStartDate(period)?.let { repository = repository.byCompleteDate().afterOrEqual(it) }
            datePeriodHelper.getEndDate(period)?.let { repository = repository.byCompleteDate().beforeOrEqual(it) }
        }
        scope.order().forEach { repository = applyOrderColumn(repository, it) }

        if (!scope.includeDeleted()) {
            repository = repository.byDeleted().isFalse
        }

        scope.states()?.let { repository = repository.bySyncState().`in`(it) }
        scope.attributeOptionCombos()?.let { repository = repository.byAttributeOptionComboUid().`in`(it) }

        return repository
    }

    private fun applyOrgunitSelection(
        repository: EventCollectionRepository,
        scope: EventQueryRepositoryScope
    ): EventCollectionRepository {
        return getOrganisationUnits(scope)?.let { repository.byOrganisationUnitUid().`in`(it) } ?: repository
    }

    private fun applyDataFilter(
        repository: EventCollectionRepository,
        filter: EventDataFilter
    ): EventCollectionRepository {
        var filterRepo = repository
        filter.dataItem()?.let { deId ->
            filter.eq()?.let { filterRepo = filterRepo.byDataValue(deId).eq(it) }
            filter.ge()?.let { filterRepo = filterRepo.byDataValue(deId).ge(it) }
            filter.gt()?.let { filterRepo = filterRepo.byDataValue(deId).gt(it) }
            filter.le()?.let { filterRepo = filterRepo.byDataValue(deId).le(it) }
            filter.lt()?.let { filterRepo = filterRepo.byDataValue(deId).lt(it) }
            filter.like()?.let { filterRepo = filterRepo.byDataValue(deId).like(it) }
            if (!filter.`in`().isNullOrEmpty()) {
                filterRepo = filterRepo.byDataValue(deId).`in`(filter.`in`())
            }
            filter.dateFilter()?.let { period ->
                datePeriodHelper.getStartDate(period)?.let {
                    // This is to ensure that comparison with date without time works as expected
                    val date = addMillis(it, -1)
                    filterRepo = filterRepo.byDataValue(deId).gt(DateUtils.DATE_FORMAT.format(date))
                }
                datePeriodHelper.getEndDate(period)?.let {
                    // This is to ensure that comparison with date without time works as expected
                    val date = addMillis(it, 1)
                    filterRepo = filterRepo.byDataValue(deId).lt(DateUtils.DATE_FORMAT.format(date))
                }
            }
        }

        return filterRepo
    }

    fun getOrganisationUnits(scope: EventQueryRepositoryScope): List<String>? {
        return when (scope.orgUnitMode()) {
            OrganisationUnitMode.ALL, OrganisationUnitMode.ACCESSIBLE ->
                organisationUnitCollectionRepository.blockingGetUids()
            OrganisationUnitMode.CAPTURE ->
                organisationUnitCollectionRepository
                    .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE).blockingGetUids()
            OrganisationUnitMode.CHILDREN ->
                scope.orgUnits()?.map { orgUnit ->
                    organisationUnitCollectionRepository.byParentUid().eq(orgUnit).blockingGetUids() + orgUnit
                }?.flatten()
            OrganisationUnitMode.DESCENDANTS ->
                scope.orgUnits()?.map { orgUnit ->
                    organisationUnitCollectionRepository.byPath().like(orgUnit).blockingGetUids()
                }?.flatten()
            OrganisationUnitMode.SELECTED ->
                scope.orgUnits()
        }
    }

    private fun applyOrderColumn(
        repository: EventCollectionRepository,
        order: EventQueryScopeOrderByItem
    ): EventCollectionRepository {
        return when (order.column().type()) {
            OrderColumnType.EVENT_DATE -> repository.orderByEventDate(order.direction())
            OrderColumnType.DUE_DATE -> repository.orderByDueDate(order.direction())
            OrderColumnType.COMPLETED_DATE -> repository.orderByCompleteDate(order.direction())
            OrderColumnType.CREATED -> repository.orderByCreated(order.direction())
            OrderColumnType.LAST_UPDATED -> repository.orderByLastUpdated(order.direction())
            OrderColumnType.ORGUNIT_NAME -> repository.orderByOrganisationUnitName(order.direction())
            OrderColumnType.TIMELINE -> repository.orderByTimeline(order.direction())
            OrderColumnType.DATA_ELEMENT -> repository.orderByDataElement(order.direction(), order.column().value())
            OrderColumnType.EVENT,
            OrderColumnType.PROGRAM,
            OrderColumnType.PROGRAM_STAGE,
            OrderColumnType.ENROLLMENT,
            OrderColumnType.ENROLLMENT_STATUS,
            OrderColumnType.ORGUNIT,
            OrderColumnType.TRACKED_ENTITY_INSTANCE,
            OrderColumnType.FOLLOW_UP,
            OrderColumnType.STATUS,
            OrderColumnType.STORED_BY,
            OrderColumnType.COMPLETED_BY -> repository
        }
    }

    private fun applyUserAssignedMode(
        repository: EventCollectionRepository,
        mode: AssignedUserMode
    ): EventCollectionRepository {
        return when (mode) {
            AssignedUserMode.CURRENT -> repository.byAssignedUser().eq(userRepository.blockingGet().user())
            AssignedUserMode.ANY -> repository.byAssignedUser().isNotNull
            AssignedUserMode.NONE -> repository.byAssignedUser().isNull
            // TODO Not implemented yet
            AssignedUserMode.PROVIDED -> repository
        }
    }

    private fun addMillis(date: Date, millis: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MILLISECOND, millis)
        return calendar.time
    }
}
