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
package org.hisp.dhis.android.testapp.event

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class EventOverdueFilterIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    companion object {

        private var existingEventUids = setOf<String>()

        private val now = ClockProviderFactory.clockProvider.clock.now()
        private val threeDaysAgo = DateUtils.dateWithOffset(now, -3, PeriodType.Daily).toJavaDate()
        private val yesterday = DateUtils.dateWithOffset(now, -1, PeriodType.Daily).toJavaDate()
        private val tomorrow = DateUtils.dateWithOffset(now, 1, PeriodType.Daily).toJavaDate()
        private val threeDaysFromNow = DateUtils.dateWithOffset(now, 3, PeriodType.Daily).toJavaDate()
        private val oneWeekFromNow = DateUtils.dateWithOffset(now, 7, PeriodType.Daily).toJavaDate()

        private val eventCreateProjection = EventCreateProjection.create(
            null,
            "lxAQ7Zs9VYR",
            "dBwrot7S420",
            "DiszpKrYNg8",
            "bRowv6yZOF2",
        )

        private lateinit var overdueScheduleStatusUid: String
        private lateinit var scheduleOverdueStatusUid: String
        private lateinit var overdueOverdueStatusUid: String
        private lateinit var overdueScheduleStatusUid2: String
        private lateinit var scheduleScheduleStatusUid: String
        private lateinit var activeTestUid: String
        private lateinit var completedTestUid: String
        private lateinit var skippedTestUid: String

        @BeforeClass
        @JvmStatic
        fun setUp() {
            existingEventUids = d2.eventModule().events().get().blockingGet().map { it.uid() }.toSet()
            createTestEvents()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            cleanUpTestEvents()
        }

        @JvmStatic
        private fun createTestEvents() {
            // OVERDUE (logical): SCHEDULE status with past due date (3 days ago)
            overdueScheduleStatusUid = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(overdueScheduleStatusUid).setStatus(EventStatus.SCHEDULE)
            d2.eventModule().events().uid(overdueScheduleStatusUid).setDueDate(threeDaysAgo)

            // SCHEDULE (logical): OVERDUE status with future due date (tomorrow)
            scheduleOverdueStatusUid = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(scheduleOverdueStatusUid).setStatus(EventStatus.OVERDUE)
            d2.eventModule().events().uid(scheduleOverdueStatusUid).setDueDate(tomorrow)

            // OVERDUE (logical): OVERDUE status with past due date (3 days ago)
            overdueOverdueStatusUid = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(overdueOverdueStatusUid).setStatus(EventStatus.OVERDUE)
            d2.eventModule().events().uid(overdueOverdueStatusUid).setDueDate(threeDaysAgo)

            // OVERDUE (logical): SCHEDULE status with past due date (yesterday)
            overdueScheduleStatusUid2 = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(overdueScheduleStatusUid2).setStatus(EventStatus.SCHEDULE)
            d2.eventModule().events().uid(overdueScheduleStatusUid2).setDueDate(yesterday)

            // SCHEDULE (logical): SCHEDULE status with future due date (1 week from now)
            scheduleScheduleStatusUid = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(scheduleScheduleStatusUid).setStatus(EventStatus.SCHEDULE)
            d2.eventModule().events().uid(scheduleScheduleStatusUid).setDueDate(oneWeekFromNow)

            // ACTIVE event: Has event_date, with meaningful due date in future
            activeTestUid = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(activeTestUid).setStatus(EventStatus.ACTIVE)
            d2.eventModule().events().uid(activeTestUid).setEventDate(now.toJavaDate())
            d2.eventModule().events().uid(activeTestUid).setDueDate(threeDaysFromNow)

            // COMPLETED event: Has event_date, with meaningful due date that was met
            completedTestUid = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(completedTestUid).setStatus(EventStatus.COMPLETED)
            d2.eventModule().events().uid(completedTestUid).setEventDate(yesterday)
            d2.eventModule().events().uid(completedTestUid).setDueDate(tomorrow)

            // SKIPPED event: Regular status event with past due date (but not overdue because it's skipped)
            skippedTestUid = d2.eventModule().events().add(eventCreateProjection).blockingGet()
            d2.eventModule().events().uid(skippedTestUid).setStatus(EventStatus.SKIPPED)
            d2.eventModule().events().uid(skippedTestUid).setDueDate(yesterday)
        }

        @JvmStatic
        private fun cleanUpTestEvents() {
            d2.eventModule().events().uid(overdueScheduleStatusUid).blockingDelete()
            d2.eventModule().events().uid(scheduleOverdueStatusUid).blockingDelete()
            d2.eventModule().events().uid(overdueOverdueStatusUid).blockingDelete()
            d2.eventModule().events().uid(overdueScheduleStatusUid2).blockingDelete()
            d2.eventModule().events().uid(scheduleScheduleStatusUid).blockingDelete()
            d2.eventModule().events().uid(activeTestUid).blockingDelete()
            d2.eventModule().events().uid(completedTestUid).blockingDelete()
            d2.eventModule().events().uid(skippedTestUid).blockingDelete()
        }

        @JvmStatic
        private fun filterOutExistingEvents(eventUids: List<String>): List<String> {
            return eventUids.filterNot { it in existingEventUids }
        }
    }

    @Test
    fun should_filter_overdue_events_correctly() {
        val overdueEvents = d2.eventModule().events()
            .byStatus().eq(EventStatus.OVERDUE)
            .blockingGet()

        val overdueUids = filterOutExistingEvents(overdueEvents.map { it.uid() })
        assertThat(overdueUids).containsExactly(
            overdueScheduleStatusUid,
            overdueOverdueStatusUid,
            overdueScheduleStatusUid2,
        )
    }

    @Test
    fun should_filter_overdue_events_with_mixed_statuses() {
        val events = d2.eventModule().events()
            .byStatus().`in`(listOf(EventStatus.ACTIVE, EventStatus.OVERDUE))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            activeTestUid,
            overdueScheduleStatusUid,
            overdueOverdueStatusUid,
            overdueScheduleStatusUid2,
        )
    }

    @Test
    fun should_filter_schedule_events_correctly() {
        val scheduleEvents = d2.eventModule().events()
            .byStatus().eq(EventStatus.SCHEDULE)
            .blockingGet()

        val scheduleUids = filterOutExistingEvents(scheduleEvents.map { it.uid() })
        assertThat(scheduleUids).containsExactly(scheduleScheduleStatusUid, scheduleOverdueStatusUid)
    }

    @Test
    fun should_handle_normal_status_filtering() {
        val activeEvents = d2.eventModule().events()
            .byStatus().eq(EventStatus.ACTIVE)
            .blockingGet()

        val activeEventUids = filterOutExistingEvents(activeEvents.map { it.uid() })
        assertThat(activeEventUids).contains(activeTestUid)
    }

    @Test
    fun should_exclude_overdue_events_with_neq() {
        val nonOverdueEvents = d2.eventModule().events()
            .byStatus().neq(EventStatus.OVERDUE)
            .blockingGet()

        val nonOverdueUids = filterOutExistingEvents(nonOverdueEvents.map { it.uid() })
        assertThat(nonOverdueUids).containsExactly(
            scheduleScheduleStatusUid,
            scheduleOverdueStatusUid,
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_exclude_overdue_events_with_notIn() {
        val nonOverdueEvents = d2.eventModule().events()
            .byStatus().notIn(listOf(EventStatus.OVERDUE))
            .blockingGet()

        val nonOverdueUids = filterOutExistingEvents(nonOverdueEvents.map { it.uid() })
        assertThat(nonOverdueUids).containsExactly(
            scheduleScheduleStatusUid,
            scheduleOverdueStatusUid,
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_exclude_mixed_statuses_including_overdue_with_notIn() {
        val filteredEvents = d2.eventModule().events()
            .byStatus().notIn(listOf(EventStatus.OVERDUE, EventStatus.ACTIVE))
            .blockingGet()

        val filteredUids = filterOutExistingEvents(filteredEvents.map { it.uid() })
        assertThat(filteredUids).containsExactly(
            scheduleScheduleStatusUid,
            scheduleOverdueStatusUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_exclude_schedule_events_with_neq() {
        val nonScheduleEvents = d2.eventModule().events()
            .byStatus().neq(EventStatus.SCHEDULE)
            .blockingGet()

        val nonScheduleUids = filterOutExistingEvents(nonScheduleEvents.map { it.uid() })
        assertThat(nonScheduleUids).containsExactly(
            overdueScheduleStatusUid,
            overdueOverdueStatusUid,
            overdueScheduleStatusUid2,
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_exclude_schedule_events_with_notIn() {
        val nonScheduleEvents = d2.eventModule().events()
            .byStatus().notIn(listOf(EventStatus.SCHEDULE))
            .blockingGet()

        val nonScheduleUids = filterOutExistingEvents(nonScheduleEvents.map { it.uid() })
        assertThat(nonScheduleUids).containsExactly(
            overdueScheduleStatusUid,
            overdueOverdueStatusUid,
            overdueScheduleStatusUid2,
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_handle_overdue_and_schedule_in_collection() {
        val events = d2.eventModule().events()
            .byStatus().`in`(listOf(EventStatus.OVERDUE, EventStatus.SCHEDULE))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            overdueScheduleStatusUid,
            scheduleOverdueStatusUid,
            overdueOverdueStatusUid,
            scheduleScheduleStatusUid,
            overdueScheduleStatusUid2,
        )
    }

    @Test
    fun should_handle_complex_mixed_status_with_overdue_schedule_and_regular() {
        val events = d2.eventModule().events()
            .byStatus()
            .`in`(listOf(EventStatus.OVERDUE, EventStatus.SCHEDULE, EventStatus.ACTIVE, EventStatus.COMPLETED))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            overdueScheduleStatusUid,
            scheduleOverdueStatusUid,
            overdueOverdueStatusUid,
            scheduleScheduleStatusUid,
            overdueScheduleStatusUid2,
            activeTestUid,
            completedTestUid,
        )
    }

    @Test
    fun should_exclude_overdue_and_schedule_with_notIn() {
        val events = d2.eventModule().events()
            .byStatus().notIn(listOf(EventStatus.OVERDUE, EventStatus.SCHEDULE))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_handle_mixed_complex_exclusion_with_notIn() {
        val events = d2.eventModule().events()
            .byStatus().notIn(listOf(EventStatus.OVERDUE, EventStatus.SCHEDULE, EventStatus.ACTIVE))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_handle_empty_list_with_in() {
        val events = d2.eventModule().events()
            .byStatus().`in`(emptyList())
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).isEmpty()
    }

    @Test
    fun should_handle_empty_list_with_notIn() {
        val events = d2.eventModule().events()
            .byStatus().notIn(emptyList())
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            overdueScheduleStatusUid,
            scheduleOverdueStatusUid,
            overdueOverdueStatusUid,
            scheduleScheduleStatusUid,
            overdueScheduleStatusUid2,
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_handle_single_overdue_in_list() {
        val events = d2.eventModule().events()
            .byStatus().`in`(listOf(EventStatus.OVERDUE))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            overdueScheduleStatusUid,
            overdueOverdueStatusUid,
            overdueScheduleStatusUid2,
        )
    }

    @Test
    fun should_handle_single_schedule_in_list() {
        val events = d2.eventModule().events()
            .byStatus().`in`(listOf(EventStatus.SCHEDULE))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(scheduleScheduleStatusUid, scheduleOverdueStatusUid)
    }

    @Test
    fun should_handle_varargs_overload_with_mixed_statuses() {
        val events = d2.eventModule().events()
            .byStatus().`in`(EventStatus.OVERDUE, EventStatus.SCHEDULE, EventStatus.ACTIVE)
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            overdueScheduleStatusUid,
            scheduleOverdueStatusUid,
            overdueOverdueStatusUid,
            scheduleScheduleStatusUid,
            overdueScheduleStatusUid2,
            activeTestUid,
        )
    }

    @Test
    fun should_handle_varargs_overload_with_notIn() {
        val events = d2.eventModule().events()
            .byStatus().notIn(EventStatus.OVERDUE, EventStatus.SCHEDULE)
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }

    @Test
    fun should_handle_only_regular_statuses_in_mixed_collection() {
        val events = d2.eventModule().events()
            .byStatus().`in`(listOf(EventStatus.ACTIVE, EventStatus.COMPLETED, EventStatus.SKIPPED))
            .blockingGet()

        val eventUids = filterOutExistingEvents(events.map { it.uid() })
        assertThat(eventUids).containsExactly(
            activeTestUid,
            completedTestUid,
            skippedTestUid,
        )
    }
}
