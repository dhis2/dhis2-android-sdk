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
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import java.util.Calendar
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.junit.Before
import org.junit.Test

class EventDateUtilsShould {

    private val event: Event = mock()
    private val periodHelper: PeriodHelper = mock()

    private val eventDateUtils = EventDateUtils(periodHelper)

    private val firstJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
    private val thirdJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-03T00:00:00.000")
    private val firstFebruary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-02-0T00:00:00.000")

    private val february: Period = mock()

    @Before
    fun setUp() {
        whenever(periodHelper.calendar) doReturn getCalendar()
        whenever(
            periodHelper.blockingGetPeriodForPeriodTypeAndDate(
                PeriodType.Monthly, thirdJanuary,
                1
            )
        ) doReturn february
        whenever(february.startDate()) doReturn firstFebruary
    }

    @Test
    fun `Should evaluate if is expired after completion`() {
        assertThat(eventDateUtils.isExpiredAfterCompletion(thirdJanuary, firstJanuary, 5)).isFalse()
        assertThat(eventDateUtils.isExpiredAfterCompletion(thirdJanuary, firstJanuary, 1)).isTrue()
    }

    @Test
    fun `Should return is not expired if within expiry days`() {
        whenever(event.status()) doReturn EventStatus.ACTIVE
        whenever(event.eventDate()) doReturn thirdJanuary

        assertThat(eventDateUtils.isEventExpired(event, 0, PeriodType.Monthly, 10)).isFalse()
    }

    @Test
    fun `Should return is expired if out of expiry days`() {
        whenever(event.status()) doReturn EventStatus.ACTIVE
        whenever(event.eventDate()) doReturn thirdJanuary

        assertThat(eventDateUtils.isEventExpired(event, 0, PeriodType.Monthly, 2)).isTrue()
    }

    @Test
    fun `Should return is not expired if no periodType provided`() {
        whenever(event.status()) doReturn EventStatus.ACTIVE

        assertThat(eventDateUtils.isEventExpired(event, 0, null, 2)).isFalse()
    }

    private fun getCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = 2020
        calendar[Calendar.MONTH] = 1 // February
        calendar[Calendar.DATE] = 5
        return calendar
    }
}
