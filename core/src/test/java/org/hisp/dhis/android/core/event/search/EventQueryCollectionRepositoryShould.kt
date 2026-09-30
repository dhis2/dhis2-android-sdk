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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyOneObjectRepositoryFinalImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.event.EventFilterCollectionRepository
import org.hisp.dhis.android.core.event.EventObjectRepository
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.EventEndpointCallFactory
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class EventQueryCollectionRepositoryShould {

    private val offlineAdapter: EventCollectionRepositoryAdapter = mock()
    private val onlineAdapter: EventQueryOnlineAdapter = mock()
    private val filterRepository: EventFilterCollectionRepository = mock()
    private val trackerCallFactory: TrackerParentCallFactory = mock()

    private val offlineRepository: EventCollectionRepository = mock()
    private val eventCall: EventEndpointCallFactory = mock()
    private val onlineQuery = TrackedEntityInstanceQueryOnline(
        page = 1,
        pageSize = 50,
        paging = false,
        orgUnits = emptyList(),
        includeDeleted = false,
    )
    private val events: List<Event> = listOf(mock(), mock())

    private lateinit var queryRepository: EventQueryCollectionRepository

    @Before
    fun setUp() = runTest {
        val emptyScope = EventQueryRepositoryScope.empty()
        queryRepository = EventQueryCollectionRepository(
            offlineAdapter,
            onlineAdapter,
            filterRepository,
            trackerCallFactory,
            emptyScope,
        )

        whenever(offlineAdapter.getCollectionRepository(any())) doReturn offlineRepository
        whenever(onlineAdapter.scopeToOnlineQuery(any())) doReturn onlineQuery
        whenever(trackerCallFactory.getEventCall()) doReturn eventCall
    }

    @Test
    fun `Should create scope with event date`() {
        val startDate = DateUtils.DATE_FORMAT.parse("2021-01-20T00:00:00.000")
        val endDate = DateUtils.DATE_FORMAT.parse("2021-01-29T00:00:00.000")

        val scope = queryRepository
            .byEventDate().afterOrEqual(startDate)
            .byEventDate().beforeOrEqual(endDate)
            .scope

        assertThat(scope.eventDate()?.startDate()).isEqualTo(startDate)
        assertThat(scope.eventDate()?.endDate()).isEqualTo(endDate)
    }

    @Test
    fun `Should create scope with relative event date`() {
        val scope = queryRepository
            .byEventDate().inPeriod(RelativePeriod.LAST_3_DAYS)
            .scope

        assertThat(scope.eventDate()?.period()).isEqualTo(RelativePeriod.LAST_3_DAYS)
        assertThat(scope.eventDate()?.type()).isEqualTo(DatePeriodType.RELATIVE)
    }

    @Test
    fun `Should merge lastUpdated, completeDate and dueDate periods`() {
        val startDate = DateUtils.DATE_FORMAT.parse("2021-01-20T00:00:00.000")
        val endDate = DateUtils.DATE_FORMAT.parse("2021-01-29T00:00:00.000")

        val scope = queryRepository
            .byLastUpdated().afterOrEqual(startDate)
            .byLastUpdated().beforeOrEqual(endDate)
            .byCompleteDate().afterOrEqual(startDate)
            .byCompleteDate().beforeOrEqual(endDate)
            .byDueDate().afterOrEqual(startDate)
            .byDueDate().beforeOrEqual(endDate)
            .scope

        listOf(scope.lastUpdatedDate(), scope.completedDate(), scope.dueDate()).forEach { period ->
            assertThat(period?.startDate()).isEqualTo(startDate)
            assertThat(period?.endDate()).isEqualTo(endDate)
        }
    }

    @Test
    fun `Should set online only mode`() {
        val scope = queryRepository.onlineOnly().scope

        assertThat(scope.mode()).isEqualTo(RepositoryMode.ONLINE_ONLY)
    }

    @Test
    fun `Should set filter connectors`() {
        val scope = queryRepository
            .byUid().`in`("event1", "event2")
            .byStatus().`in`(EventStatus.ACTIVE, EventStatus.SCHEDULE)
            .byProgram().eq("program")
            .byProgramStage().eq("programStage")
            .byOrgUnits().`in`("ou1", "ou2")
            .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
            .byIncludeDeleted().eq(true)
            .byTrackedEntityInstance().eq("tei")
            .byAssignedUser().eq(AssignedUserMode.CURRENT)
            .byAttributeOptionCombo().`in`("aoc")
            .scope

        assertThat(scope.events()).containsExactly("event1", "event2").inOrder()
        assertThat(scope.eventStatus()).containsExactly(EventStatus.ACTIVE, EventStatus.SCHEDULE).inOrder()
        assertThat(scope.program()).isEqualTo("program")
        assertThat(scope.programStage()).isEqualTo("programStage")
        assertThat(scope.orgUnits()).containsExactly("ou1", "ou2").inOrder()
        assertThat(scope.orgUnitMode()).isEqualTo(OrganisationUnitMode.DESCENDANTS)
        assertThat(scope.includeDeleted()).isTrue()
        assertThat(scope.trackedEntityInstance()).isEqualTo("tei")
        assertThat(scope.assignedUserMode()).isEqualTo(AssignedUserMode.CURRENT)
        assertThat(scope.attributeOptionCombos()).containsExactly("aoc")
    }

    @Test
    fun `Should force offline only mode when filtering by states`() {
        val scope = queryRepository
            .onlineOnly()
            .byStates().`in`(State.TO_POST, State.TO_UPDATE)
            .scope

        assertThat(scope.states()).containsExactly(State.TO_POST, State.TO_UPDATE).inOrder()
        assertThat(scope.mode()).isEqualTo(RepositoryMode.OFFLINE_ONLY)
    }

    @Test
    fun `Should keep scope when orgUnitMode or includeDeleted are null`() {
        val scope = queryRepository
            .byOrgUnitMode().eq(null)
            .byIncludeDeleted().eq(null)
            .scope

        assertThat(scope).isEqualTo(EventQueryRepositoryScope.empty())
    }

    @Test
    fun `Should append data value filters`() {
        val scope = queryRepository
            .byDataValue("de1").eq("5")
            .byDataValue("de1").like("abc")
            .byDataValue("de2").ge("1")
            .scope

        assertThat(scope.dataFilters()).containsExactly(
            EventDataFilter.builder().dataItem("de1").eq("5").build(),
            EventDataFilter.builder().dataItem("de1").like("abc").build(),
            EventDataFilter.builder().dataItem("de2").ge("1").build(),
        ).inOrder()
    }

    @Test
    fun `Should apply event filter by uid`() {
        val eventFilter = EventFilter.builder().uid("filter").program("program").programStage("stage").build()
        val filterRepositoryWithDataFilters: EventFilterCollectionRepository = mock()
        val filterObjectRepository: ReadOnlyOneObjectRepositoryFinalImpl<EventFilter> = mock {
            on { blockingGet() } doReturn eventFilter
        }
        whenever(filterRepository.withEventDataFilters()) doReturn filterRepositoryWithDataFilters
        whenever(filterRepositoryWithDataFilters.uid("filter")) doReturn filterObjectRepository

        val scope = queryRepository.byEventFilter().eq("filter").scope

        assertThat(scope.program()).isEqualTo("program")
        assertThat(scope.programStage()).isEqualTo("stage")
    }

    @Test
    fun `Should apply event filter object`() {
        val eventFilter = EventFilter.builder().uid("filter").program("program").programStage("stage").build()

        val scope = queryRepository.byEventFilterObject().eq(eventFilter).scope

        assertThat(scope.program()).isEqualTo("program")
        assertThat(scope.programStage()).isEqualTo("stage")
    }

    @Test
    fun `Should concat sort orders`() {
        val scope = queryRepository
            .orderByEventDate().eq(RepositoryScope.OrderByDirection.ASC)
            .orderByLastUpdated().eq(RepositoryScope.OrderByDirection.DESC)
            .scope

        assertThat(scope.order().size).isEqualTo(2)
        assertThat(scope.order().first().column()).isEqualTo(EventQueryScopeOrderColumn.EVENT_DATE)
        assertThat(scope.order().first().direction()).isEqualTo(RepositoryScope.OrderByDirection.ASC)
        assertThat(scope.order().last().column()).isEqualTo(EventQueryScopeOrderColumn.LAST_UPDATED)
        assertThat(scope.order().last().direction()).isEqualTo(RepositoryScope.OrderByDirection.DESC)
    }

    @Test
    fun `Should add every order column`() {
        val asc = RepositoryScope.OrderByDirection.ASC
        val scope = queryRepository
            .orderByEventDate().eq(asc)
            .orderByDueDate().eq(asc)
            .orderByCompleteDate().eq(asc)
            .orderByCreated().eq(asc)
            .orderByLastUpdated().eq(asc)
            .orderByOrganisationUnitName().eq(asc)
            .orderByTimeline().eq(asc)
            .orderByDataElement("de").eq(asc)
            .scope

        assertThat(scope.order().map { it.column().type() }).containsExactly(
            EventQueryScopeOrderColumn.Type.EVENT_DATE,
            EventQueryScopeOrderColumn.Type.DUE_DATE,
            EventQueryScopeOrderColumn.Type.COMPLETED_DATE,
            EventQueryScopeOrderColumn.Type.CREATED,
            EventQueryScopeOrderColumn.Type.LAST_UPDATED,
            EventQueryScopeOrderColumn.Type.ORGUNIT_NAME,
            EventQueryScopeOrderColumn.Type.TIMELINE,
            EventQueryScopeOrderColumn.Type.DATA_ELEMENT,
        ).inOrder()
        assertThat(scope.order().last().column().value()).isEqualTo("de")
        assertThat(scope.order().map { it.direction() }.distinct()).containsExactly(asc)
    }

    @Test
    fun `Should ignore null order direction`() {
        val scope = queryRepository.orderByEventDate().eq(null).scope

        assertThat(scope.order()).isEmpty()
    }

    @Test
    fun `Should delegate queries to offline repository in offline mode`() = runTest {
        val eventRepository: EventObjectRepository = mock()
        val oneRepository: ReadOnlyOneObjectRepositoryFinalImpl<Event> = mock()
        whenever(offlineRepository.suspendGet()) doReturn events
        whenever(offlineRepository.suspendGetUids()) doReturn listOf("event1", "event2")
        whenever(offlineRepository.suspendCount()) doReturn 2
        whenever(offlineRepository.suspendIsEmpty()) doReturn false
        whenever(offlineRepository.uid("event1")) doReturn eventRepository
        whenever(offlineRepository.one()) doReturn oneRepository

        assertThat(queryRepository.suspendGet()).isEqualTo(events)
        assertThat(queryRepository.suspendGetUids()).containsExactly("event1", "event2").inOrder()
        assertThat(queryRepository.suspendCount()).isEqualTo(2)
        assertThat(queryRepository.suspendIsEmpty()).isFalse()
        assertThat(queryRepository.uid("event1")).isSameInstanceAs(eventRepository)
        assertThat(queryRepository.one()).isSameInstanceAs(oneRepository)

        verify(trackerCallFactory, never()).getEventCall()
    }

    @Test
    fun `Should delegate queries to tracker call in online mode`() = runTest {
        val payload: Payload<Event> = mock {
            on { items } doReturn events
        }
        whenever(eventCall.getQueryCall(onlineQuery)) doReturn payload
        whenever(eventCall.getQueryUids(onlineQuery)) doReturn listOf("event1", "event2")
        val onlineRepository = queryRepository.onlineOnly()

        assertThat(onlineRepository.suspendGet()).isEqualTo(events)
        assertThat(onlineRepository.suspendGetUids()).containsExactly("event1", "event2").inOrder()
        assertThat(onlineRepository.suspendCount()).isEqualTo(2)

        verify(offlineRepository, never()).suspendGet()
        verify(offlineRepository, never()).suspendGetUids()
    }
}
