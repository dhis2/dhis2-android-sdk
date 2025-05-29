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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.hisp.dhis.android.core.arch.helpers.DateUtils.atStartOfDayInSystem
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.clock.internal.ClockProvider
import org.hisp.dhis.android.core.period.clock.internal.FixedClockProvider
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Date

class EventDateUtilsShould {

    private val event: Event = mock()
    private val periodHelper: PeriodHelper = mock()
    private val fixedDate = LocalDateTime(2020, 2, 5, 0, 0)
    private val clockProvider: ClockProvider = FixedClockProvider(fixedDate)

    private val eventDateUtils = EventDateUtils(periodHelper, clockProvider)

    private val firstJanuary = LocalDate(2020, 1, 1).atStartOfDayInSystem()
    private val thirdJanuary = LocalDate(2020, 1, 3).atStartOfDayInSystem()
    private val firstFebruary = LocalDate(2020, 2, 1).atStartOfDayInSystem()

    private val february: Period = mock()

    @Before
    fun setUp() {
        whenever(
            periodHelper.blockingGetPeriodForPeriodTypeAndDate(
                PeriodType.Monthly,
                Date(thirdJanuary.toEpochMilliseconds()),
                1,
            ),
        ) doReturn february
        whenever(february.startDate()) doReturn Date(firstFebruary.toEpochMilliseconds())
    }

    @Test
    fun should_evaluate_if_is_expired_after_completion() {
        assertThat(eventDateUtils.isExpiredAfterCompletion(thirdJanuary, firstJanuary, 5)).isFalse()
        assertThat(eventDateUtils.isExpiredAfterCompletion(thirdJanuary, firstJanuary, 1)).isTrue()
    }

    @Test
    fun should_return_correct_expiration_provided_expiry_days() {
        whenever(event.status()) doReturn EventStatus.ACTIVE
        whenever(event.eventDate()) doReturn Date(thirdJanuary.toEpochMilliseconds())

        assertThat(eventDateUtils.isEventExpired(event, 0, PeriodType.Monthly, 10)).isFalse()
        assertThat(eventDateUtils.isEventExpired(event, 0, PeriodType.Monthly, 2)).isTrue()
        // According to dhis2 core logic, default expiry days is 0 and represent no expiration.
        assertThat(eventDateUtils.isEventExpired(event, 0, PeriodType.Monthly, 0)).isFalse()
    }

    @Test
    fun should_return_is_not_expired_if_no_period_type_provided() {
        whenever(event.status()) doReturn EventStatus.ACTIVE

        assertThat(eventDateUtils.isEventExpired(event, 0, null, 2)).isFalse()
    }

    @Test
    fun should_return_is_not_expired_if_no_event_or_due_date_provided() {
        whenever(event.status()) doReturn EventStatus.ACTIVE
        whenever(event.eventDate()) doReturn null
        whenever(event.dueDate()) doReturn null

        assertThat(eventDateUtils.isEventExpired(event, 0, null, 2)).isFalse()
    }
}
