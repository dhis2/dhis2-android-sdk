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

import dagger.Reusable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.PeriodHelper

@Reusable
class EventDateUtils @Inject constructor(
    private val periodHelper: PeriodHelper
) {

    /**
     * Check if an event is expired due to completion
     *
     * @param evaluationDate  date or today if null
     * @param completeDate date that event was completed
     * @param completeExpiryDays number of days for which the event is still editable after completion.
     * @return true or false
     */
    fun isExpiredAfterCompletion(evaluationDate: Date?, completeDate: Date?, completeExpiryDays: Int): Boolean {
        val referenceDate = evaluationDate ?: getCalendar().time
        return completeDate != null &&
            completeExpiryDays > 0 &&
            completeDate.time + TimeUnit.DAYS.toMillis(completeExpiryDays.toLong()) < referenceDate.time
    }

    /**
     * Check if an event is expired today.
     *
     * @param eventDate         Date of the event (Can be either eventDate or dueDate, but can not be null).
     * @param completeDate      date that event was completed (can be null).
     * @param status            status of event (ACTIVE,COMPLETED,SCHEDULE,OVERDUE,SKIPPED,VISITED).
     * @param completeExpiryDays       extra days to edit event when completed .
     * @param programPeriodType period in which the event can be edited.
     * @param expiryDays           extra days after period to edit event.
     * @return true or false
     */
    fun isEventExpired(
        event: Event,
        completeExpiryDays: Int,
        programPeriodType: PeriodType?,
        expiryDays: Int
    ): Boolean {
        if (event.status() == EventStatus.COMPLETED && event.completedDate() == null) return false

        val expiredBecauseOfCompletion =
            if (event.status() == EventStatus.COMPLETED) {
                isExpiredAfterCompletion(null, event.completedDate(), completeExpiryDays)
            } else {
                false
            }

        val expiredBecauseOfPeriod = programPeriodType?.let { periodType ->
            var nextPeriod = periodHelper
                .blockingGetPeriodForPeriodTypeAndDate(periodType, event.eventDate()!!, 1).startDate()!!
            val currentDate: Date = getCalendar().time
            if (expiryDays > 0) {
                val calendar: Calendar = getCalendar()
                calendar.time = nextPeriod
                calendar.add(Calendar.DAY_OF_YEAR, expiryDays)
                nextPeriod = calendar.time
            }
            nextPeriod <= currentDate
        } ?: false

        return expiredBecauseOfCompletion || expiredBecauseOfPeriod
    }

    private fun getCalendar(): Calendar {
        val calendar = periodHelper.calendar

        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }
}
