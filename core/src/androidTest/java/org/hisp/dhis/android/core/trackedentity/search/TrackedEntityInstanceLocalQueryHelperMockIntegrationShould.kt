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
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Companion.create
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TrackedEntityInstanceLocalQueryHelperMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter)

    private val calendarProvider = CalendarProviderFactory.calendarProvider
    private val periodHelper = DateFilterPeriodHelper(calendarProvider, create(calendarProvider))
    private val localQueryHelper = TrackedEntityInstanceLocalQueryHelper(periodHelper)

    @Test
    fun should_generate_valid_sql_queries() {
        val scope = TrackedEntityInstanceQueryRepositoryScope.builder()
            .program("programUid")
            .programStage("programStageUid")
            .programDate(
                DateFilterPeriod.builder()
                    .period(RelativePeriod.LAST_10_YEARS)
                    .build()
            )
            .incidentDate(
                DateFilterPeriod.builder()
                    .period(RelativePeriod.LAST_3_MONTHS)
                    .build()
            )
            .enrollmentStatus(listOf(EnrollmentStatus.ACTIVE))
            .eventDate(
                DateFilterPeriod.builder()
                    .period(RelativePeriod.LAST_10_YEARS)
                    .build()
            )
            .eventStatus(listOf(EventStatus.ACTIVE))
            .attribute(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("attributeUid1")
                        .operator(FilterItemOperator.EQ)
                        .value("value1")
                        .build(),
                    RepositoryScopeFilterItem.builder()
                        .key("attributeUid2")
                        .operator(FilterItemOperator.EQ)
                        .value("value2")
                        .build()
                )
            )
            .filter(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("attributeUid1")
                        .operator(FilterItemOperator.EQ)
                        .value("value1")
                        .build(),
                    RepositoryScopeFilterItem.builder()
                        .key("attributeUid2")
                        .operator(FilterItemOperator.EQ)
                        .value("value2")
                        .build()
                )
            )
            .query(
                RepositoryScopeFilterItem.builder()
                    .key("")
                    .operator(FilterItemOperator.EQ)
                    .value("value1")
                    .build()
            )
            .trackedEntityType("trackedEntityTypeUid")
            .orgUnitMode(OrganisationUnitMode.CAPTURE)
            .orgUnits(listOf("orgunit1", "orgunit2"))
            .followUp(true)
            .assignedUserMode(AssignedUserMode.CURRENT)
            .eventFilters(
                listOf(
                    TrackedEntityInstanceQueryEventFilter.builder()
                        .assignedUserMode(AssignedUserMode.CURRENT)
                        .programStage("programStageUid")
                        .eventStatus(listOf(EventStatus.ACTIVE, EventStatus.COMPLETED))
                        .eventDate(
                            DateFilterPeriod.builder()
                                .period(RelativePeriod.LAST_10_YEARS)
                                .build()
                        )
                        .build()
                )
            )
            .states(listOf(State.SYNCED, State.TO_UPDATE))
            .lastUpdatedDate(
                DateFilterPeriod.builder()
                    .period(RelativePeriod.LAST_10_YEARS)
                    .build()
            )
            .order(
                listOf(
                    TrackedEntityInstanceQueryScopeOrderByItem.builder()
                        .column(TrackedEntityInstanceQueryScopeOrderColumn.ENROLLMENT_DATE)
                        .direction(RepositoryScope.OrderByDirection.DESC)
                        .build(),
                    TrackedEntityInstanceQueryScopeOrderByItem.builder()
                        .column(TrackedEntityInstanceQueryScopeOrderColumn.LAST_UPDATED)
                        .direction(RepositoryScope.OrderByDirection.ASC)
                        .build()
                )
            )
            .build()

        val rawQuery = localQueryHelper.getSqlQuery(scope, emptySet(), 10)

        val instances = trackedEntityInstanceStore.selectRawQuery(rawQuery)

        assertThat(instances).isNotNull()
    }
}
