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
package org.hisp.dhis.android.core.event.internal

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.hisp.dhis.android.core.arch.helpers.DateUtils.atStartOfDayInSystem
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.clock.internal.ClockProvider
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.koin.core.annotation.Singleton

@Singleton
internal class EventDateUtils(
    private val periodHelper: PeriodHelper,
    private val clockProvider: ClockProvider,
) {

    /**
     * Check if an event is expired due to completion
     *
     * @param evaluationDate  date or today if null
     * @param completeDate date that event was completed
     * @param completeExpiryDays number of days for which the event is still editable after completion.
     * @return true or false
     */
    fun isExpiredAfterCompletion(evaluationDate: Instant?, completeDate: Instant?, completeExpiryDays: Int): Boolean {
        val referenceDate = evaluationDate ?: currentDateInstant()
        return completeDate != null &&
            completeExpiryDays > 0 &&
            completeDate.plus(completeExpiryDays, DateTimeUnit.DAY, TimeZone.currentSystemDefault()) < referenceDate
    }

    /**
     * Check if an event is expired today.
     *
     * @param event                 the event to check.
     * @param completeExpiryDays    extra days to edit event when completed .
     * @param programPeriodType     period in which the event can be edited.
     * @param expiryDays            extra days after period to edit event.
     * @return true or false
     */
    fun isEventExpired(
        event: Event,
        completeExpiryDays: Int,
        programPeriodType: PeriodType?,
        expiryDays: Int,
    ): Boolean {
        return when {
            event.status() == EventStatus.COMPLETED && event.completedDate() == null -> false
            isExpiredBecauseOfCompletion(event, completeExpiryDays) -> true
            isExpiredBecauseOfPeriod(event, programPeriodType, expiryDays) -> true
            else -> false
        }
    }

    private fun isExpiredBecauseOfCompletion(
        event: Event,
        completeExpiryDays: Int,
    ) = event.takeIf { it.status() == EventStatus.COMPLETED }
        ?.completedDate()?.time
        ?.let { isExpiredAfterCompletion(null, Instant.fromEpochMilliseconds(it), completeExpiryDays) }
        ?: false

    private fun isExpiredBecauseOfPeriod(
        event: Event,
        programPeriodType: PeriodType?,
        expiryDays: Int,
    ) = (event.eventDate() ?: event.dueDate())?.let { eventDateOrDueDate ->
        programPeriodType?.let { periodType ->
            val nextPeriod = periodHelper.blockingGetPeriodForPeriodTypeAndDate(periodType, eventDateOrDueDate, 1)
                .startDate()?.let { Instant.fromEpochMilliseconds(it.time).plusDays(expiryDays) }

            nextPeriod != null && expiryDays > 0 && nextPeriod <= currentDateInstant()
        }
    } ?: false

    private fun currentDateInstant(): Instant {
        return clockProvider.clock.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
            .atStartOfDayInSystem()
    }

    private fun Instant.plusDays(days: Int): Instant {
        return this.plus(days, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
    }
}
