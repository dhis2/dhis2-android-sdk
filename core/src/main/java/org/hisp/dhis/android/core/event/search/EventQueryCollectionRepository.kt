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
package org.hisp.dhis.android.core.event.search

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingData
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EventDataFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.PeriodFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.event.EventFilterCollectionRepository
import org.hisp.dhis.android.core.event.EventObjectRepository
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class EventQueryCollectionRepository internal constructor(
    private val eventCollectionRepositoryAdapter: EventCollectionRepositoryAdapter,
    private val eventFilterRepository: EventFilterCollectionRepository,
    @JvmField val scope: EventQueryRepositoryScope,
) : ReadOnlyWithUidCollectionRepository<Event> {

    private val connectorFactory: ScopedFilterConnectorFactory<
        EventQueryCollectionRepository,
        EventQueryRepositoryScope,
        > = ScopedFilterConnectorFactory { s: EventQueryRepositoryScope ->
        EventQueryCollectionRepository(
            eventCollectionRepositoryAdapter,
            eventFilterRepository,
            s,
        )
    }

    fun byUid(): ListFilterConnector<EventQueryCollectionRepository, String> {
        return connectorFactory.listConnector { uidList: List<String> ->
            scope.toBuilder().events(uidList).build()
        }
    }

    fun byLastUpdated(): PeriodFilterConnector<EventQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter ->
            val merged: DateFilterPeriod =
                DateFilterPeriodHelper.mergeDateFilterPeriods(scope.lastUpdatedDate(), filter)!!
            scope.toBuilder().lastUpdatedDate(merged).build()
        }
    }

    /**
     * Filter by event status.
     * <br></br>**IMPORTANT:** this filter accepts a list of event status, but only the first one will be used for
     * the online query because the web API does not support querying by multiple status.
     *
     * @return Repository connector
     */
    fun byStatus(): ListFilterConnector<EventQueryCollectionRepository, EventStatus> {
        return connectorFactory.listConnector { status: List<EventStatus> ->
            scope.toBuilder().eventStatus(status).build()
        }
    }

    fun byProgram(): EqFilterConnector<EventQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { program ->
            scope.toBuilder().program(program).build()
        }
    }

    fun byProgramStage(): EqFilterConnector<EventQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { programStage ->
            scope.toBuilder().programStage(programStage).build()
        }
    }

    /**
     * Filter by Event organisation units.
     * <br></br>**IMPORTANT:** this filter accepts a list of organisation units, but only the first one will be used for
     * the online query because the web API does not support querying by multiple organisation units.
     *
     * @return Repository connector
     */
    fun byOrgUnits(): ListFilterConnector<EventQueryCollectionRepository, String> {
        return connectorFactory.listConnector { orgUnits: List<String> ->
            scope.toBuilder().orgUnits(orgUnits).build()
        }
    }

    fun byOrgUnitMode(): EqFilterConnector<EventQueryCollectionRepository, OrganisationUnitMode> {
        return connectorFactory.eqConnector { mode ->
            scope.toBuilder().orgUnitMode(mode).build()
        }
    }

    fun byEventDate(): PeriodFilterConnector<EventQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter: DateFilterPeriod ->
            val merged: DateFilterPeriod = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.eventDate(), filter)!!
            scope.toBuilder().eventDate(merged).build()
        }
    }

    fun byCompleteDate(): PeriodFilterConnector<EventQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter: DateFilterPeriod ->
            val merged: DateFilterPeriod =
                DateFilterPeriodHelper.mergeDateFilterPeriods(scope.completedDate(), filter)!!
            scope.toBuilder().completedDate(merged).build()
        }
    }

    fun byDueDate(): PeriodFilterConnector<EventQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter: DateFilterPeriod ->
            val merged: DateFilterPeriod = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.dueDate(), filter)!!
            scope.toBuilder().dueDate(merged).build()
        }
    }

    fun byIncludeDeleted(): EqFilterConnector<EventQueryCollectionRepository, Boolean> {
        return connectorFactory.eqConnector { includeDeleted ->
            scope.toBuilder().includeDeleted(includeDeleted).build()
        }
    }

    fun byTrackedEntityInstance(): EqFilterConnector<EventQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { tei ->
            scope.toBuilder().trackedEntityInstance(tei).build()
        }
    }

    fun byAssignedUser(): EqFilterConnector<EventQueryCollectionRepository, AssignedUserMode> {
        return connectorFactory.eqConnector { userMode ->
            scope.toBuilder().assignedUserMode(userMode).build()
        }
    }

    fun byEventFilter(): EqFilterConnector<EventQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { id ->
            val filter: EventFilter = eventFilterRepository.withEventDataFilters().uid(id).blockingGet()!!
            EventQueryRepositoryScopeHelper.addEventFilter(scope, filter)
        }
    }

    /**
     * Filter by sync status.
     * <br></br>**IMPORTANT:** using this filter forces **offlineOnly** mode.
     *
     * @return Repository connector
     */
    fun byStates(): ListFilterConnector<EventQueryCollectionRepository, State> {
        return connectorFactory.listConnector { states: List<State> ->
            scope.toBuilder().states(states).build()
        }
    }

    fun byAttributeOptionCombo(): ListFilterConnector<EventQueryCollectionRepository, String> {
        return connectorFactory.listConnector { aoc: List<String> ->
            scope.toBuilder().attributeOptionCombos(aoc).build()
        }
    }

    fun byDataValue(dataElementId: String): EventDataFilterConnector<EventQueryCollectionRepository> {
        return connectorFactory.eventDataFilterConnector(
            dataElementId,
        ) { filter: EventDataFilter ->
            val filters: List<EventDataFilter> =
                DateFilterPeriodHelper.mergeEventDataFilters(scope.dataFilters(), filter)
            scope.toBuilder().dataFilters(filters).build()
        }
    }

    fun orderByEventDate(): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.EVENT_DATE)
    }

    fun orderByDueDate(): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.DUE_DATE)
    }

    fun orderByCompleteDate(): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.COMPLETED_DATE)
    }

    fun orderByCreated(): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.CREATED)
    }

    fun orderByLastUpdated(): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.LAST_UPDATED)
    }

    fun orderByOrganisationUnitName(): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.ORGUNIT_NAME)
    }

    fun orderByTimeline(): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.TIMELINE)
    }

    fun orderByDataElement(dataElement: String?): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return orderConnector(EventQueryScopeOrderColumn.dataElement(dataElement))
    }

    private fun orderConnector(
        col: EventQueryScopeOrderColumn,
    ): EqFilterConnector<EventQueryCollectionRepository, OrderByDirection> {
        return connectorFactory.eqConnector { direction ->
            val order: MutableList<EventQueryScopeOrderByItem> = ArrayList(scope.order())
            order.add(EventQueryScopeOrderByItem.builder().column(col).direction(direction).build())
            scope.toBuilder().order(order).build()
        }
    }

    override fun uid(uid: String?): EventObjectRepository {
        return eventCollectionRepository.uid(uid)
    }

    override fun getUids(): Single<List<String>> {
        return eventCollectionRepository.getUids()
    }

    override fun blockingGetUids(): List<String> {
        return eventCollectionRepository.blockingGetUids()
    }

    override fun get(): Single<List<Event>> {
        return eventCollectionRepository.get()
    }

    override fun blockingGet(): List<Event> {
        return eventCollectionRepository.blockingGet()
    }

    override fun getPaged(pageSize: Int): LiveData<PagedList<Event>> {
        return eventCollectionRepository.getPaged(pageSize)
    }

    override fun getPagingData(pageSize: Int): Flow<PagingData<Event>> {
        return eventCollectionRepository.getPagingData(pageSize)
    }

    fun getPager(pageSize: Int): Pager<Event, Event> {
        return eventCollectionRepository.getPager(pageSize)
    }

    val dataSource: DataSource<Event, Event>
        get() = eventCollectionRepository.dataSource

    override fun count(): Single<Int> {
        return eventCollectionRepository.count()
    }

    override fun blockingCount(): Int {
        return eventCollectionRepository.blockingCount()
    }

    override fun isEmpty(): Single<Boolean> {
        return eventCollectionRepository.isEmpty()
    }
    override fun blockingIsEmpty(): Boolean {
        return eventCollectionRepository.blockingIsEmpty()
    }

    override fun one(): ReadOnlyObjectRepository<Event> {
        return eventCollectionRepository.one()
    }

    private val eventCollectionRepository: EventCollectionRepository
        get() = eventCollectionRepositoryAdapter
            .getCollectionRepository(scope)
            .withTrackedEntityDataValues()
}
