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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ValueSubQueryFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.EventStatusFilterConnector
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.AuthenticatedUserObjectRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class EventCollectionRepositoryAdapterShould {

    private val eventRepository: EventCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val ouRepository: OrganisationUnitCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val userRepository: AuthenticatedUserObjectRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val dateFilterPeriodHelper: DateFilterPeriodHelper = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)

    private val orgunit = "ou"
    private val orgunitChildren = listOf("ou1", "ou2")
    private val orgunitDescendants = orgunitChildren + orgunit + listOf("ou3")
    private val captureOrgunits = listOf("ou1")

    private val startDate = DateUtils.DATE_FORMAT.parse("2021-01-10T00:00:00.000")
    private val endDate = DateUtils.DATE_FORMAT.parse("2021-01-20T00:00:00.000")
    private val period = DateFilterPeriod.builder().startDate(startDate).endDate(endDate).build()

    // Every connector returns the same eventRepository, so the whole filter chain can be verified
    private val programConnector = stringConnector()
    private val programStageConnector = stringConnector()
    private val orgUnitConnector = stringConnector()
    private val assignedUserConnector = stringConnector()
    private val uidConnector = stringConnector()
    private val aocConnector = stringConnector()
    private val eventDateConnector = dateConnector()
    private val dueDateConnector = dateConnector()
    private val lastUpdatedConnector = dateConnector()
    private val completeDateConnector = dateConnector()
    private val statusConnector: EventStatusFilterConnector = mock {
        on { `in`(any<Collection<EventStatus>>()) } doReturn eventRepository
    }
    private val deletedConnector: BooleanFilterConnector<EventCollectionRepository> = mock {
        on { isFalse } doReturn eventRepository
    }
    private val syncStateConnector: EnumFilterConnector<EventCollectionRepository, State> = mock {
        on { `in`(any<Collection<State>>()) } doReturn eventRepository
    }
    private val dataValueConnectors = listOf("de", "de1", "de2").associateWith { dataValueConnector() }

    private lateinit var adapter: EventCollectionRepositoryAdapter

    @Before
    fun setUp() = runTest {
        adapter = EventCollectionRepositoryAdapter(
            eventRepository,
            ouRepository,
            userRepository,
            dateFilterPeriodHelper,
        )

        val childrenRepository: OrganisationUnitCollectionRepository = mock {
            onBlocking { suspendGetUids() } doReturn orgunitChildren
        }
        val parentConnector: StringFilterConnector<OrganisationUnitCollectionRepository> = mock {
            on { eq(orgunit) } doReturn childrenRepository
        }
        whenever(ouRepository.suspendGetUids()) doReturn orgunitDescendants
        whenever(ouRepository.byPath().like(orgunit).suspendGetUids()) doReturn orgunitDescendants
        whenever(ouRepository.byParentUid()) doReturn parentConnector
        whenever(
            ouRepository.byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE).suspendGetUids(),
        ) doReturn captureOrgunits

        whenever(dateFilterPeriodHelper.getStartDate(period)) doReturn startDate
        whenever(dateFilterPeriodHelper.getEndDate(period)) doReturn endDate

        givenChainedEventRepository()
    }

    @Test
    fun `Should get null if orgUnit is null and mode is SELECTED`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnitMode(OrganisationUnitMode.SELECTED)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isNull()
    }

    @Test
    fun `Should get selected orgunit`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnits(listOf(orgunit))
            .orgUnitMode(OrganisationUnitMode.SELECTED)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(listOf(orgunit))
    }

    @Test
    fun `Should get all if ALL mode`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnitMode(OrganisationUnitMode.ALL)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(orgunitDescendants)
    }

    @Test
    fun `Should get all if ACCESSIBLE mode`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnitMode(OrganisationUnitMode.ACCESSIBLE)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(orgunitDescendants)
    }

    @Test
    fun `Should get capture scope if CAPTURE mode`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnitMode(OrganisationUnitMode.CAPTURE)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(captureOrgunits)
    }

    @Test
    fun `Should get children plus self if CHILDREN mode`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnits(listOf(orgunit))
            .orgUnitMode(OrganisationUnitMode.CHILDREN)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(orgunitChildren + orgunit)
    }

    @Test
    fun `Should get null if orgUnit is null and mode is CHILDREN or DESCENDANTS`() = runTest {
        listOf(OrganisationUnitMode.CHILDREN, OrganisationUnitMode.DESCENDANTS).forEach { mode ->
            val scope = EventQueryRepositoryScope.builder().orgUnitMode(mode).build()

            assertThat(adapter.getOrganisationUnits(scope)).isNull()
        }
    }

    @Test
    fun `Should get descendants if DESCENDANTS mode`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnits(listOf(orgunit))
            .orgUnitMode(OrganisationUnitMode.DESCENDANTS)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(orgunitDescendants)
    }

    @Test
    fun `Should apply program, programStage, followUp and TEI filters`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .program("program")
            .programStage("programStage")
            .followUp(true)
            .trackedEntityInstance("tei")
            .build()

        val repository = adapter.getCollectionRepository(scope)

        assertThat(repository).isSameInstanceAs(eventRepository)
        verify(programConnector).eq("program")
        verify(programStageConnector).eq("programStage")
        verify(eventRepository).byFollowUp(true)
        verify(eventRepository).byTrackedEntityInstanceUids(listOf("tei"))
    }

    @Test
    fun `Should apply orgunit selection`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnits(listOf(orgunit))
            .orgUnitMode(OrganisationUnitMode.SELECTED)
            .build()

        adapter.getCollectionRepository(scope)

        verify(orgUnitConnector).`in`(listOf(orgunit))
    }

    @Test
    fun `Should not apply orgunit selection if there are no orgunits`() = runTest {
        adapter.getCollectionRepository(EventQueryRepositoryScope.empty())

        verify(eventRepository, never()).byOrganisationUnitUid()
    }

    @Test
    fun `Should apply assigned user mode CURRENT`() = runTest {
        whenever(userRepository.blockingGet()) doReturn AuthenticatedUser.builder().user("user").build()
        val scope = EventQueryRepositoryScope.builder().assignedUserMode(AssignedUserMode.CURRENT).build()

        adapter.getCollectionRepository(scope)

        verify(assignedUserConnector).eq("user")
    }

    @Test
    fun `Should apply assigned user mode ANY`() = runTest {
        val scope = EventQueryRepositoryScope.builder().assignedUserMode(AssignedUserMode.ANY).build()

        adapter.getCollectionRepository(scope)

        verify(assignedUserConnector).isNotNull
    }

    @Test
    fun `Should apply assigned user mode NONE`() = runTest {
        val scope = EventQueryRepositoryScope.builder().assignedUserMode(AssignedUserMode.NONE).build()

        adapter.getCollectionRepository(scope)

        verify(assignedUserConnector).isNull
    }

    @Test
    fun `Should ignore assigned user mode PROVIDED`() = runTest {
        val scope = EventQueryRepositoryScope.builder().assignedUserMode(AssignedUserMode.PROVIDED).build()

        adapter.getCollectionRepository(scope)

        verify(eventRepository, never()).byAssignedUser()
    }

    @Test
    fun `Should apply eq, ge, gt, le, lt, like and in data filters`() = runTest {
        val filter = EventDataFilter.builder()
            .dataItem("de")
            .eq("eq")
            .ge("ge")
            .gt("gt")
            .le("le")
            .lt("lt")
            .like("like")
            .`in`(setOf("in1", "in2"))
            .build()
        val scope = EventQueryRepositoryScope.builder().dataFilters(listOf(filter)).build()

        adapter.getCollectionRepository(scope)

        val connector = dataValueConnectors.getValue("de")
        verify(connector).eq("eq")
        verify(connector).ge("ge")
        verify(connector).gt("gt")
        verify(connector).le("le")
        verify(connector).lt("lt")
        verify(connector).like("like")
        verify(connector).`in`(setOf("in1", "in2"))
    }

    @Test
    fun `Should not apply empty in data filter`() = runTest {
        val filter = EventDataFilter.builder().dataItem("de").`in`(emptySet()).build()
        val scope = EventQueryRepositoryScope.builder().dataFilters(listOf(filter)).build()

        adapter.getCollectionRepository(scope)

        verify(eventRepository, never()).byDataValue(any())
    }

    @Test
    fun `Should apply date data filter with one millisecond margin`() = runTest {
        val filter = EventDataFilter.builder().dataItem("de").dateFilter(period).build()
        val scope = EventQueryRepositoryScope.builder().dataFilters(listOf(filter)).build()

        adapter.getCollectionRepository(scope)

        val connector = dataValueConnectors.getValue("de")
        verify(connector).gt(DateUtils.DATE_FORMAT.format(Date(startDate.time - 1)))
        verify(connector).lt(DateUtils.DATE_FORMAT.format(Date(endDate.time + 1)))
    }

    @Test
    fun `Should apply isEmpty data filter`() = runTest {
        val emptyFilter = EventDataFilter.builder().dataItem("de1").isEmpty(true).build()
        val notEmptyFilter = EventDataFilter.builder().dataItem("de2").isEmpty(false).build()
        val scope = EventQueryRepositoryScope.builder().dataFilters(listOf(emptyFilter, notEmptyFilter)).build()

        adapter.getCollectionRepository(scope)

        verify(dataValueConnectors.getValue("de1")).isNullOrBlank()
        verify(dataValueConnectors.getValue("de2")).isNotNullAndIsNotBlank()
    }

    @Test
    fun `Should apply events uid and status filters`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .events(listOf("event1", "event2"))
            .eventStatus(listOf(EventStatus.ACTIVE, EventStatus.COMPLETED))
            .build()

        adapter.getCollectionRepository(scope)

        verify(uidConnector).`in`(listOf("event1", "event2"))
        verify(statusConnector).`in`(listOf(EventStatus.ACTIVE, EventStatus.COMPLETED))
    }

    @Test
    fun `Should not apply empty events uid filter`() = runTest {
        val scope = EventQueryRepositoryScope.builder().events(emptyList()).build()

        adapter.getCollectionRepository(scope)

        verify(eventRepository, never()).byUid()
    }

    @Test
    fun `Should apply eventDate, dueDate, lastUpdated and completedDate periods`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .eventDate(period)
            .dueDate(period)
            .lastUpdatedDate(period)
            .completedDate(period)
            .build()

        adapter.getCollectionRepository(scope)

        listOf(eventDateConnector, dueDateConnector, lastUpdatedConnector, completeDateConnector).forEach {
            verify(it).afterOrEqual(startDate)
            verify(it).beforeOrEqual(endDate)
        }
    }

    @Test
    fun `Should skip period bounds that cannot be resolved`() = runTest {
        val openPeriod = DateFilterPeriod.builder().build()
        whenever(dateFilterPeriodHelper.getStartDate(openPeriod)) doReturn null
        whenever(dateFilterPeriodHelper.getEndDate(openPeriod)) doReturn null
        val scope = EventQueryRepositoryScope.builder()
            .eventDate(openPeriod)
            .dueDate(openPeriod)
            .lastUpdatedDate(openPeriod)
            .completedDate(openPeriod)
            .build()

        adapter.getCollectionRepository(scope)

        verify(eventRepository, never()).byEventDate()
        verify(eventRepository, never()).byDueDate()
        verify(eventRepository, never()).byLastUpdated()
        verify(eventRepository, never()).byCompleteDate()
    }

    @Test
    fun `Should exclude deleted events unless includeDeleted`() = runTest {
        adapter.getCollectionRepository(EventQueryRepositoryScope.empty())
        verify(deletedConnector).isFalse

        Mockito.clearInvocations(eventRepository)
        adapter.getCollectionRepository(EventQueryRepositoryScope.builder().includeDeleted(true).build())
        verify(eventRepository, never()).byDeleted()
    }

    @Test
    fun `Should apply states and attributeOptionCombos`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .states(listOf(State.TO_POST, State.TO_UPDATE))
            .attributeOptionCombos(listOf("aoc"))
            .build()

        adapter.getCollectionRepository(scope)

        verify(syncStateConnector).`in`(listOf(State.TO_POST, State.TO_UPDATE))
        verify(aocConnector).`in`(listOf("aoc"))
    }

    @Test
    fun `Should apply and concat sort orders`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .order(
                listOf(
                    EventQueryScopeOrderByItem.builder()
                        .column(EventQueryScopeOrderColumn.EVENT_DATE)
                        .direction(RepositoryScope.OrderByDirection.ASC)
                        .build(),
                    EventQueryScopeOrderByItem.builder()
                        .column(EventQueryScopeOrderColumn.COMPLETED_DATE)
                        .direction(RepositoryScope.OrderByDirection.DESC)
                        .build(),
                ),
            ).build()

        val intermediateRepository: EventCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
        whenever(eventRepository.orderByEventDate(any())) doReturn intermediateRepository

        adapter.getCollectionRepository(scope)

        verify(eventRepository).orderByEventDate(RepositoryScope.OrderByDirection.ASC)
        verify(intermediateRepository).orderByCompleteDate(RepositoryScope.OrderByDirection.DESC)
    }

    @Test
    fun `Should map every supported order column`() = runTest {
        val asc = RepositoryScope.OrderByDirection.ASC
        val columns = listOf(
            EventQueryScopeOrderColumn.EVENT_DATE,
            EventQueryScopeOrderColumn.DUE_DATE,
            EventQueryScopeOrderColumn.COMPLETED_DATE,
            EventQueryScopeOrderColumn.CREATED,
            EventQueryScopeOrderColumn.LAST_UPDATED,
            EventQueryScopeOrderColumn.ORGUNIT_NAME,
            EventQueryScopeOrderColumn.TIMELINE,
            EventQueryScopeOrderColumn.dataElement("de"),
        )
        val scope = EventQueryRepositoryScope.builder()
            .order(columns.map { EventQueryScopeOrderByItem.builder().column(it).direction(asc).build() })
            .build()

        adapter.getCollectionRepository(scope)

        verify(eventRepository).orderByEventDate(asc)
        verify(eventRepository).orderByDueDate(asc)
        verify(eventRepository).orderByCompleteDate(asc)
        verify(eventRepository).orderByCreated(asc)
        verify(eventRepository).orderByLastUpdated(asc)
        verify(eventRepository).orderByOrganisationUnitName(asc)
        verify(eventRepository).orderByTimeline(asc)
        verify(eventRepository).orderByDataElement(asc, "de")
    }

    @Test
    fun `Should ignore order columns not supported offline`() = runTest {
        val scope = EventQueryRepositoryScope.builder()
            .order(
                listOf(
                    EventQueryScopeOrderColumn.EVENT,
                    EventQueryScopeOrderColumn.PROGRAM,
                    EventQueryScopeOrderColumn.PROGRAM_STAGE,
                    EventQueryScopeOrderColumn.ENROLLMENT,
                    EventQueryScopeOrderColumn.ENROLLMENT_STATUS,
                    EventQueryScopeOrderColumn.ORGUNIT,
                    EventQueryScopeOrderColumn.TRACKED_ENTITY_INSTANCE,
                    EventQueryScopeOrderColumn.FOLLOW_UP,
                    EventQueryScopeOrderColumn.STATUS,
                    EventQueryScopeOrderColumn.STORED_BY,
                    EventQueryScopeOrderColumn.COMPLETED_BY,
                ).map {
                    EventQueryScopeOrderByItem.builder()
                        .column(it)
                        .direction(RepositoryScope.OrderByDirection.ASC)
                        .build()
                },
            )
            .build()

        val repository = adapter.getCollectionRepository(scope)

        assertThat(repository).isSameInstanceAs(eventRepository)
    }

    private fun givenChainedEventRepository() {
        whenever(eventRepository.byProgramUid()) doReturn programConnector
        whenever(eventRepository.byProgramStageUid()) doReturn programStageConnector
        whenever(eventRepository.byFollowUp(any())) doReturn eventRepository
        whenever(eventRepository.byTrackedEntityInstanceUids(any())) doReturn eventRepository
        whenever(eventRepository.byOrganisationUnitUid()) doReturn orgUnitConnector
        whenever(eventRepository.byAssignedUser()) doReturn assignedUserConnector
        whenever(eventRepository.byUid()) doReturn uidConnector
        whenever(eventRepository.byStatus()) doReturn statusConnector
        whenever(eventRepository.byEventDate()) doReturn eventDateConnector
        whenever(eventRepository.byDueDate()) doReturn dueDateConnector
        whenever(eventRepository.byLastUpdated()) doReturn lastUpdatedConnector
        whenever(eventRepository.byCompleteDate()) doReturn completeDateConnector
        whenever(eventRepository.byDeleted()) doReturn deletedConnector
        whenever(eventRepository.bySyncState()) doReturn syncStateConnector
        whenever(eventRepository.byAttributeOptionComboUid()) doReturn aocConnector
        dataValueConnectors.forEach { (de, connector) ->
            whenever(eventRepository.byDataValue(de)) doReturn connector
        }
        val asc = RepositoryScope.OrderByDirection.ASC
        whenever(eventRepository.orderByEventDate(asc)) doReturn eventRepository
        whenever(eventRepository.orderByDueDate(asc)) doReturn eventRepository
        whenever(eventRepository.orderByCompleteDate(asc)) doReturn eventRepository
        whenever(eventRepository.orderByCreated(asc)) doReturn eventRepository
        whenever(eventRepository.orderByLastUpdated(asc)) doReturn eventRepository
        whenever(eventRepository.orderByOrganisationUnitName(asc)) doReturn eventRepository
        whenever(eventRepository.orderByTimeline(asc)) doReturn eventRepository
        whenever(eventRepository.orderByDataElement(asc, "de")) doReturn eventRepository
    }

    private fun stringConnector(): StringFilterConnector<EventCollectionRepository> = mock {
        on { eq(anyOrNull()) } doReturn eventRepository
        on { `in`(any<Collection<String>>()) } doReturn eventRepository
        on { isNull } doReturn eventRepository
        on { isNotNull } doReturn eventRepository
    }

    private fun dateConnector(): DateFilterConnector<EventCollectionRepository> = mock {
        on { afterOrEqual(any()) } doReturn eventRepository
        on { beforeOrEqual(any()) } doReturn eventRepository
    }

    private fun dataValueConnector(): ValueSubQueryFilterConnector<EventCollectionRepository> = mock {
        on { eq(any()) } doReturn eventRepository
        on { ge(any()) } doReturn eventRepository
        on { gt(any()) } doReturn eventRepository
        on { le(any()) } doReturn eventRepository
        on { lt(any()) } doReturn eventRepository
        on { like(any()) } doReturn eventRepository
        on { `in`(any()) } doReturn eventRepository
        on { isNullOrBlank() } doReturn eventRepository
        on { isNotNullAndIsNotBlank() } doReturn eventRepository
    }
}
