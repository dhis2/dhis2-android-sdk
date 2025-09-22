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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Before
import org.junit.Test

class EventOverdueFilterIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val now = ClockProviderFactory.clockProvider.clock.now()
    private val yesterday = DateUtils.dateWithOffset(now, -1, PeriodType.Daily).toJavaDate()
    private val tomorrow = DateUtils.dateWithOffset(now, 1, PeriodType.Daily).toJavaDate()

    private lateinit var overdueTestUid1: String
    private lateinit var overdueTestUid2: String
    private lateinit var scheduleTestUid: String
    private lateinit var activeTestUid: String

    @Before
    fun setUp() = runTest {
        createTestEvents()
    }

    private fun createTestEvents() {
        overdueTestUid1 = d2.eventModule().events().blockingAdd(
            EventCreateProjection.create(
                null,
                "lxAQ7Zs9VYR",
                "dBwrot7S420",
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            ),
        )
        d2.eventModule().events().uid(overdueTestUid1).setStatus(EventStatus.SCHEDULE)
        d2.eventModule().events().uid(overdueTestUid1).setDueDate(yesterday)

        scheduleTestUid = d2.eventModule().events().blockingAdd(
            EventCreateProjection.create(
                null,
                "lxAQ7Zs9VYR",
                "dBwrot7S420",
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            ),
        )
        d2.eventModule().events().uid(scheduleTestUid).setStatus(EventStatus.SCHEDULE)
        d2.eventModule().events().uid(scheduleTestUid).setDueDate(tomorrow)

        overdueTestUid2 = d2.eventModule().events().blockingAdd(
            EventCreateProjection.create(
                null,
                "lxAQ7Zs9VYR",
                "dBwrot7S420",
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            ),
        )
        d2.eventModule().events().uid(overdueTestUid2).setStatus(EventStatus.OVERDUE)
        d2.eventModule().events().uid(overdueTestUid2).setDueDate(yesterday)

        activeTestUid = d2.eventModule().events().blockingAdd(
            EventCreateProjection.create(
                null,
                "lxAQ7Zs9VYR",
                "dBwrot7S420",
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            ),
        )
        d2.eventModule().events().uid(activeTestUid).setStatus(EventStatus.ACTIVE)
        d2.eventModule().events().uid(activeTestUid).setEventDate(now.toJavaDate())
    }

    @Test
    fun should_filter_overdue_events_correctly() = runTest {
        val overdueEvents = d2.eventModule().events()
            .byStatus().eq(EventStatus.OVERDUE)
            .blockingGet()

        assertThat(overdueEvents.size).isEqualTo(2)

        val overdueUids = overdueEvents.map { it.uid() }
        assertThat(overdueUids).containsExactly(overdueTestUid1, overdueTestUid2)
    }

    @Test
    fun should_filter_overdue_events_with_mixed_statuses() = runTest {
        val events = d2.eventModule().events()
            .byStatus().`in`(listOf(EventStatus.ACTIVE, EventStatus.OVERDUE))
            .blockingGet()

        assertThat(events.size).isEqualTo(3)

        val eventUids = events.map { it.uid() }
        assertThat(eventUids).containsExactly(activeTestUid, overdueTestUid1, overdueTestUid2)
    }

    @Test
    fun should_not_include_schedule_events_with_future_due_date() = runTest {
        val scheduleEvents = d2.eventModule().events()
            .byStatus().eq(EventStatus.SCHEDULE)
            .blockingGet()

        assertThat(scheduleEvents.size).isEqualTo(1)
        assertThat(scheduleEvents[0].uid()).isEqualTo(scheduleTestUid)
    }

    @Test
    fun should_handle_normal_status_filtering() = runTest {
        val activeEvents = d2.eventModule().events()
            .byStatus().eq(EventStatus.ACTIVE)
            .blockingGet()

        val activeEventUids = activeEvents.map { it.uid() }
        assertThat(activeEventUids).contains(activeTestUid)
    }

    @Test
    fun should_exclude_overdue_events_with_neq() = runTest {
    val nonOverdueEvents = d2.eventModule().events()
            .byStatus().neq(EventStatus.OVERDUE)
            .blockingGet()

        val nonOverdueUids = nonOverdueEvents.map { it.uid() }
        assertThat(nonOverdueUids).containsExactly(scheduleTestUid, activeTestUid)
        assertThat(nonOverdueUids).doesNotContain(overdueTestUid1)
        assertThat(nonOverdueUids).doesNotContain(overdueTestUid2)
    }

    @Test
    fun should_exclude_overdue_events_with_notIn() = runTest {
        val nonOverdueEvents = d2.eventModule().events()
            .byStatus().notIn(listOf(EventStatus.OVERDUE))
            .blockingGet()

        val nonOverdueUids = nonOverdueEvents.map { it.uid() }
        assertThat(nonOverdueUids).containsExactly(scheduleTestUid, activeTestUid)
        assertThat(nonOverdueUids).doesNotContain(overdueTestUid1)
        assertThat(nonOverdueUids).doesNotContain(overdueTestUid2)
    }

    @Test
    fun should_exclude_mixed_statuses_including_overdue_with_notIn() = runTest {
        val filteredEvents = d2.eventModule().events()
            .byStatus().notIn(listOf(EventStatus.OVERDUE, EventStatus.ACTIVE))
            .blockingGet()

        val filteredUids = filteredEvents.map { it.uid() }
        assertThat(filteredUids).containsExactly(scheduleTestUid)
        assertThat(filteredUids).doesNotContain(overdueTestUid1)
        assertThat(filteredUids).doesNotContain(overdueTestUid2)
        assertThat(filteredUids).doesNotContain(activeTestUid)
    }
}
