/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.search;

import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper;
import org.hisp.dhis.android.core.common.DatePeriodType;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.period.internal.CalendarProvider;
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory;
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceLocalQueryHelperShould {

    private TrackedEntityInstanceQueryRepositoryScope.Builder queryBuilder;

    private TrackedEntityInstanceLocalQueryHelper localQueryHelper;

    private final String programUid = "IpHINAT79UW";

    @Before
    public void setUp() {
        queryBuilder = TrackedEntityInstanceQueryRepositoryScope.builder();

        CalendarProvider calendarProvider = CalendarProviderFactory.getCalendarProvider();
        DateFilterPeriodHelper periodHelper =
                new DateFilterPeriodHelper(calendarProvider, ParentPeriodGeneratorImpl.create(calendarProvider));

        localQueryHelper = new TrackedEntityInstanceLocalQueryHelper(periodHelper);
    }

    @Test
    public void build_sql_query_with_programs() {
        TrackedEntityInstanceQueryRepositoryScope scope = queryBuilder
                .program(programUid)
                .orgUnits(Collections.singletonList("DiszpKrYNg8"))
                .orgUnitMode(OrganisationUnitMode.DESCENDANTS)
                .query(RepositoryScopeFilterItem.builder().key("").operator(FilterItemOperator.LIKE).value("female").build())
                .build();

        String sqlQuery = localQueryHelper.getSqlQuery(scope, Collections.emptySet(), 50);
        assertThat(sqlQuery).contains("program");
    }

    @Test
    public void build_sql_query_with_enrollment_date() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        TrackedEntityInstanceQueryRepositoryScope scope  = queryBuilder
                .program(programUid)
                .programDate(DateFilterPeriod.builder()
                        .type(DatePeriodType.ABSOLUTE)
                        .startDate(format.parse("2019-04-15"))
                        .endDate(format.parse("2019-05-19"))
                        .build())
                .query(RepositoryScopeFilterItem.builder().key("").operator(FilterItemOperator.LIKE).value("female").build())
                .build();

        String sqlQuery = localQueryHelper.getSqlQuery(scope, Collections.emptySet(), 50);
        assertThat(sqlQuery).contains("date(en.enrollmentDate) >= '2019-04-15'");
        assertThat(sqlQuery).contains("date(en.enrollmentDate) <= '2019-05-19'");
    }

    @Test
    public void build_sql_query_with_states() {
        TrackedEntityInstanceQueryRepositoryScope scope  = queryBuilder
                .states(Arrays.asList(State.SYNCED, State.TO_POST, State.TO_UPDATE))
                .program(programUid)
                .query(RepositoryScopeFilterItem.builder().key("").operator(FilterItemOperator.LIKE).value("female").build())
                .build();

        String sqlQuery = localQueryHelper.getSqlQuery(scope, Collections.emptySet(), 50);
        assertThat(sqlQuery).contains("aggregatedSyncState IN ('SYNCED', 'TO_POST', 'TO_UPDATE')");
    }

    @Test
    public void build_sql_without_relationships_by_default() {
        TrackedEntityInstanceQueryRepositoryScope scope  = queryBuilder
                .program(programUid)
                .build();

        String sqlQuery = localQueryHelper.getSqlQuery(scope, Collections.emptySet(), 50);
        assertThat(sqlQuery).contains("aggregatedSyncState != 'RELATIONSHIP'");
    }

    @Test
    public void build_sql_query_with_include_deleted() {
        TrackedEntityInstanceQueryRepositoryScope scopeDeleted = queryBuilder
                .includeDeleted(true)
                .build();

        String sqlQuery = localQueryHelper.getSqlQuery(scopeDeleted, Collections.emptySet(), 50);
        assertThat(sqlQuery).doesNotContain("deleted");

        TrackedEntityInstanceQueryRepositoryScope scope = queryBuilder
                .program(programUid)
                .includeDeleted(false)
                .build();

        String sqlQuery2 = localQueryHelper.getSqlQuery(scope, Collections.emptySet(), 50);
        assertThat(sqlQuery2).contains("deleted != 1");
    }

    @Test
    public void build_sql_query_with_follow_up() {
        TrackedEntityInstanceQueryRepositoryScope scope = queryBuilder
                .program(programUid)
                .followUp(true)
                .build();

        String sqlQuery = localQueryHelper.getSqlQuery(scope, Collections.emptySet(), 50);
        assertThat(sqlQuery).contains("followup = 1");
    }

    @Test
    public void build_sql_query_with_assigned_user_mode() {
        TrackedEntityInstanceQueryEventFilter eventFilter =
                TrackedEntityInstanceQueryEventFilter.builder().assignedUserMode(AssignedUserMode.ANY).build();
        TrackedEntityInstanceQueryRepositoryScope scope = queryBuilder
                .program(programUid)
                .eventFilters(Collections.singletonList(eventFilter))
                .build();

        String sqlQuery = localQueryHelper.getSqlQuery(scope, Collections.emptySet(), 50);
        assertThat(sqlQuery).contains("assignedUser IS NOT NULL");
    }

    @Test
    public void build_sql_query_with_event_status_only_if_event_dates_defined() {
        TrackedEntityInstanceQueryEventFilter eventFilterWithoutDates = TrackedEntityInstanceQueryEventFilter.builder()
                .eventStatus(Collections.singletonList(EventStatus.ACTIVE)).build();
        TrackedEntityInstanceQueryRepositoryScope scopeWithoutDates = queryBuilder
                .program(programUid)
                .eventFilters(Collections.singletonList(eventFilterWithoutDates))
                .build();

        String query1 = localQueryHelper.getSqlQuery(scopeWithoutDates, Collections.emptySet(), 50);
        assertThat(query1).doesNotContain("ACTIVE");

        TrackedEntityInstanceQueryEventFilter eventFilterWithDates = TrackedEntityInstanceQueryEventFilter.builder()
                .eventDate(DateFilterPeriod.builder()
                        .type(DatePeriodType.ABSOLUTE)
                        .startDate(new Date())
                        .endDate(new Date())
                        .build())
                .eventStatus(Collections.singletonList(EventStatus.ACTIVE)).build();
        TrackedEntityInstanceQueryRepositoryScope scopeWithDates = queryBuilder
                .program(programUid)
                .eventFilters(Collections.singletonList(eventFilterWithDates))
                .build();

        String query2 = localQueryHelper.getSqlQuery(scopeWithDates, Collections.emptySet(), 50);
        assertThat(query2).contains("ACTIVE");
        assertThat(query2).contains("eventDate");
    }

    @Test
    public void build_sql_query_with_due_date_in_overdue() {
        TrackedEntityInstanceQueryEventFilter eventFilter = TrackedEntityInstanceQueryEventFilter.builder()
                .eventDate(DateFilterPeriod.builder().startDate(new Date()).endDate(new Date()).build())
                .eventStatus(Collections.singletonList(EventStatus.OVERDUE)).build();
        TrackedEntityInstanceQueryRepositoryScope overdueQuery = queryBuilder
                .program(programUid)
                .eventFilters(Collections.singletonList(eventFilter))
                .build();

        String query = localQueryHelper.getSqlQuery(overdueQuery, Collections.emptySet(), 50);
        assertThat(query).contains("dueDate");
        assertThat(query).contains("eventDate");
    }

}
