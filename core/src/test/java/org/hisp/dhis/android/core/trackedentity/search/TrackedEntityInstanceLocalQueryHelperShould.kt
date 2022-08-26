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

import com.google.common.truth.Truth.assertThat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory.calendarProvider
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Companion.create
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackedEntityInstanceLocalQueryHelperShould {
    private lateinit var queryBuilder: TrackedEntityInstanceQueryRepositoryScope.Builder
    private lateinit var localQueryHelper: TrackedEntityInstanceLocalQueryHelper
    private val programUid = "IpHINAT79UW"

    @Before
    fun setUp() {
        queryBuilder = TrackedEntityInstanceQueryRepositoryScope.builder()
        val calendarProvider = calendarProvider
        val periodHelper = DateFilterPeriodHelper(calendarProvider, create(calendarProvider))
        localQueryHelper = TrackedEntityInstanceLocalQueryHelper(periodHelper)
    }

    @Test
    fun build_sql_query_with_programs() {
        val scope = queryBuilder
            .program(programUid)
            .orgUnits(listOf("DiszpKrYNg8"))
            .orgUnitMode(OrganisationUnitMode.DESCENDANTS)
            .query(
                RepositoryScopeFilterItem.builder().key("").operator(FilterItemOperator.LIKE).value("female").build()
            )
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery).contains("program")
        assertThat(sqlQuery).contains("accessLevel = 'PROTECTED'")
    }

    @Test
    @Throws(ParseException::class)
    fun build_sql_query_with_enrollment_date() {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val scope = queryBuilder
            .program(programUid)
            .programDate(
                DateFilterPeriod.builder()
                    .type(DatePeriodType.ABSOLUTE)
                    .startDate(format.parse("2019-04-15"))
                    .endDate(format.parse("2019-05-19"))
                    .build()
            )
            .query(
                RepositoryScopeFilterItem.builder().key("").operator(FilterItemOperator.LIKE).value("female").build()
            )
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery).contains("date(en.enrollmentDate) >= '2019-04-15'")
        assertThat(sqlQuery).contains("date(en.enrollmentDate) <= '2019-05-19'")
    }

    @Test
    fun build_sql_query_with_states() {
        val scope = queryBuilder
            .states(listOf(State.SYNCED, State.TO_POST, State.TO_UPDATE))
            .program(programUid)
            .query(
                RepositoryScopeFilterItem.builder().key("").operator(FilterItemOperator.LIKE).value("female").build()
            )
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery).contains("aggregatedSyncState IN ('SYNCED', 'TO_POST', 'TO_UPDATE')")
    }

    @Test
    fun build_sql_without_relationships_by_default() {
        val scope = queryBuilder
            .program(programUid)
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery).contains("aggregatedSyncState != 'RELATIONSHIP'")
    }

    @Test
    fun build_sql_query_with_include_deleted() {
        val scopeDeleted = queryBuilder
            .includeDeleted(true)
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scopeDeleted, emptySet(), 50)

        assertThat(sqlQuery).doesNotContain("deleted")

        val scope = queryBuilder
            .program(programUid)
            .includeDeleted(false)
            .build()

        val sqlQuery2 = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery2).contains("deleted != 1")
    }

    @Test
    fun build_sql_query_with_follow_up() {
        val scope = queryBuilder
            .program(programUid)
            .followUp(true)
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery).contains("followup = 1")
    }

    @Test
    fun build_sql_query_with_assigned_user_mode() {
        val eventFilter = TrackedEntityInstanceQueryEventFilter.builder().assignedUserMode(AssignedUserMode.ANY).build()
        val scope = queryBuilder
            .program(programUid)
            .eventFilters(listOf(eventFilter))
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery).contains("assignedUser IS NOT NULL")
    }

    @Test
    fun build_sql_query_with_event_status_only_if_event_dates_defined() {
        val eventFilterWithoutDates = TrackedEntityInstanceQueryEventFilter.builder()
            .eventStatus(listOf(EventStatus.ACTIVE)).build()

        val scopeWithoutDates = queryBuilder
            .program(programUid)
            .eventFilters(listOf(eventFilterWithoutDates))
            .build()

        val query1 = localQueryHelper.getSqlQuery(scopeWithoutDates, emptySet(), 50)

        assertThat(query1).doesNotContain("ACTIVE")

        val eventFilterWithDates = TrackedEntityInstanceQueryEventFilter.builder()
            .eventDate(
                DateFilterPeriod.builder()
                    .type(DatePeriodType.ABSOLUTE)
                    .startDate(Date())
                    .endDate(Date())
                    .build()
            )
            .eventStatus(listOf(EventStatus.ACTIVE)).build()

        val scopeWithDates = queryBuilder
            .program(programUid)
            .eventFilters(listOf(eventFilterWithDates))
            .build()

        val query2 = localQueryHelper.getSqlQuery(scopeWithDates, emptySet(), 50)

        assertThat(query2).contains("ACTIVE")
        assertThat(query2).contains("eventDate")
    }

    @Test
    fun build_sql_query_with_due_date_in_overdue() {
        val eventFilter = TrackedEntityInstanceQueryEventFilter.builder()
            .eventDate(DateFilterPeriod.builder().startDate(Date()).endDate(Date()).build())
            .eventStatus(listOf(EventStatus.OVERDUE)).build()

        val overdueQuery = queryBuilder
            .program(programUid)
            .eventFilters(listOf(eventFilter))
            .build()

        val query = localQueryHelper.getSqlQuery(overdueQuery, emptySet(), 50)

        assertThat(query).contains("dueDate")
        assertThat(query).contains("eventDate")
    }

    @Test
    fun build_sql_query_with_in_filer() {
        val scope = queryBuilder
            .program(programUid)
            .filter(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("key")
                        .operator(FilterItemOperator.IN)
                        .value(FilterOperatorsHelper.listToStr(listOf("element1", "element2")))
                        .build()
                )
            )
            .build()

        val sqlQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 50)

        assertThat(sqlQuery).contains("value IN ('element1','element2')")
    }
}
