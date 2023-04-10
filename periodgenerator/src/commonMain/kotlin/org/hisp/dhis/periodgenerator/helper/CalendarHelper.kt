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

package org.hisp.dhis.periodgenerator.helper

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class CalendarHelper {
    companion object {
        fun weekOfYearAndYear(date: LocalDate, weekStartDay: DayOfWeek = DayOfWeek.MONDAY): Pair<Int, Int> {
            val week: Int = (10 + date.dayOfYear + weekStartDay.ordinal - date.dayOfWeek.ordinal) / 7
            return if (week in 1..52) {
                Pair(week, date.year)
            } else if (week < 1) {
                val week = week
                Pair(week, date.year - 1)
            } else if (week > 52) {
                val year = date.year
                val week = week
                Pair(week, year)
            } else {
                throw IllegalArgumentException("Error calculating week of year for $date and $weekStartDay")
            }
        }

        fun yearIsLeap(year: Int): Boolean {
            return LocalDate(year, 12, 31).dayOfYear == 366
        }

        fun weeksInAYear(year: Int, weekStartDay: DayOfWeek = DayOfWeek.MONDAY): Int {
            val firstDayOfYearDayOfWeek = LocalDate(year, 1, 1).dayOfWeek
            val lastDayOfYearDayOfWeek = LocalDate(year, 12, 31).dayOfWeek
            return if (
                firstDayOfYearDayOfWeek.ordinal == (weekStartDay.ordinal + DayOfWeek.THURSDAY.ordinal) % 7 ||
                lastDayOfYearDayOfWeek.ordinal == (weekStartDay.ordinal + DayOfWeek.THURSDAY.ordinal) % 7
            ) 53 else 52
        }
    }
}