/*
 *  Copyright (c) 2004-2026, University of Oslo
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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.search.FilterOperatorHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class EventQueryOnlineAdapterShould {

    private val dateFilterPeriodHelper: DateFilterPeriodHelper = mock()

    private val eventPeriod = period("2021-01-01T00:00:00.000", "2021-01-31T00:00:00.000")
    private val duePeriod = period("2021-02-01T00:00:00.000", "2021-02-28T00:00:00.000")
    private val lastUpdatedPeriod = period("2021-03-01T00:00:00.000", "2021-03-31T00:00:00.000")

    private lateinit var adapter: EventQueryOnlineAdapter

    @Before
    fun setUp() {
        adapter = EventQueryOnlineAdapter(dateFilterPeriodHelper, FilterOperatorHelper(dateFilterPeriodHelper))

        listOf(eventPeriod, duePeriod, lastUpdatedPeriod).forEach { period ->
            whenever(dateFilterPeriodHelper.getStartDate(period)) doReturn period.startDate()
            whenever(dateFilterPeriodHelper.getEndDate(period)) doReturn period.endDate()
        }
    }

    @Test
    fun `Should map empty scope with defaults`() {
        val query = adapter.scopeToOnlineQuery(EventQueryRepositoryScope.empty())

        assertThat(query.page).isEqualTo(1)
        assertThat(query.pageSize).isEqualTo(50)
        assertThat(query.paging).isFalse()
        assertThat(query.orgUnits).isEmpty()
        assertThat(query.orgUnitMode).isEqualTo(OrganisationUnitMode.SELECTED)
        assertThat(query.program).isNull()
        assertThat(query.programStage).isNull()
        assertThat(query.attributeFilter).isEmpty()
        assertThat(query.dataValueFilter).isEmpty()
        assertThat(query.programStartDate).isNull()
        assertThat(query.programEndDate).isNull()
        assertThat(query.enrollmentStatus).isNull()
        assertThat(query.incidentStartDate).isNull()
        assertThat(query.incidentEndDate).isNull()
        assertThat(query.followUp).isNull()
        assertThat(query.eventStatus).isNull()
        assertThat(query.eventStartDate).isNull()
        assertThat(query.eventEndDate).isNull()
        assertThat(query.dueStartDate).isNull()
        assertThat(query.dueEndDate).isNull()
        assertThat(query.trackedEntityType).isNull()
        assertThat(query.includeDeleted).isFalse()
        assertThat(query.assignedUserMode).isNull()
        assertThat(query.uids).isNull()
        assertThat(query.lastUpdatedStartDate).isNull()
        assertThat(query.lastUpdatedEndDate).isNull()
        assertThat(query.order).isEmpty()
    }

    @Test
    fun `Should map all scope properties`() {
        val order = listOf(
            EventQueryScopeOrderByItem.builder()
                .column(EventQueryScopeOrderColumn.EVENT_DATE)
                .direction(RepositoryScope.OrderByDirection.DESC)
                .build(),
        )
        val scope = EventQueryRepositoryScope.builder()
            .orgUnits(listOf("ou1", "ou2"))
            .orgUnitMode(OrganisationUnitMode.DESCENDANTS)
            .program("program")
            .programStage("programStage")
            .followUp(true)
            .includeDeleted(true)
            .assignedUserMode(AssignedUserMode.CURRENT)
            .events(listOf("event1", "event2"))
            .order(order)
            .build()

        val query = adapter.scopeToOnlineQuery(scope)

        assertThat(query.orgUnits).containsExactly("ou1", "ou2").inOrder()
        assertThat(query.orgUnitMode).isEqualTo(OrganisationUnitMode.DESCENDANTS)
        assertThat(query.program).isEqualTo("program")
        assertThat(query.programStage).isEqualTo("programStage")
        assertThat(query.followUp).isTrue()
        assertThat(query.includeDeleted).isTrue()
        assertThat(query.assignedUserMode).isEqualTo(AssignedUserMode.CURRENT)
        assertThat(query.uids).containsExactly("event1", "event2").inOrder()
        assertThat(query.order).isEqualTo(order)
    }

    @Test
    fun `Should take only first event status`() {
        val scope = EventQueryRepositoryScope.builder()
            .eventStatus(listOf(EventStatus.COMPLETED, EventStatus.ACTIVE))
            .build()

        val query = adapter.scopeToOnlineQuery(scope)

        assertThat(query.eventStatus).isEqualTo(EventStatus.COMPLETED)
    }

    @Test
    fun `Should map event, due and lastUpdated periods to start and end dates`() {
        val scope = EventQueryRepositoryScope.builder()
            .eventDate(eventPeriod)
            .dueDate(duePeriod)
            .lastUpdatedDate(lastUpdatedPeriod)
            .build()

        val query = adapter.scopeToOnlineQuery(scope)

        assertThat(query.eventStartDate).isEqualTo(eventPeriod.startDate())
        assertThat(query.eventEndDate).isEqualTo(eventPeriod.endDate())
        assertThat(query.dueStartDate).isEqualTo(duePeriod.startDate())
        assertThat(query.dueEndDate).isEqualTo(duePeriod.endDate())
        assertThat(query.lastUpdatedStartDate).isEqualTo(lastUpdatedPeriod.startDate())
        assertThat(query.lastUpdatedEndDate).isEqualTo(lastUpdatedPeriod.endDate())
    }

    @Test
    fun `Should convert data filters into dataValueFilter items`() {
        val scope = EventQueryRepositoryScope.builder()
            .dataFilters(
                listOf(
                    EventDataFilter.builder().dataItem("de1").eq("5").build(),
                    EventDataFilter.builder().dataItem("de2").ge("1").lt("10").build(),
                ),
            )
            .build()

        val query = adapter.scopeToOnlineQuery(scope)

        assertThat(query.dataValueFilter).containsExactly(
            filterItem("de1", FilterItemOperator.EQ, "5"),
            filterItem("de2", FilterItemOperator.LT, "10"),
            filterItem("de2", FilterItemOperator.GE, "1"),
        )
    }

    private fun period(start: String, end: String): DateFilterPeriod =
        DateFilterPeriod.builder()
            .startDate(DateUtils.DATE_FORMAT.parse(start))
            .endDate(DateUtils.DATE_FORMAT.parse(end))
            .build()

    private fun filterItem(key: String, operator: FilterItemOperator, value: String): RepositoryScopeFilterItem =
        RepositoryScopeFilterItem.builder().key(key).operator(operator).value(value).build()
}
