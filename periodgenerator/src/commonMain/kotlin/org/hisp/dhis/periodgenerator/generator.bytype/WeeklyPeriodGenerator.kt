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
package org.hisp.dhis.periodgenerator.generator.bytype

import kotlinx.datetime.*
import org.hisp.dhis.periodgenerator.helper.CalendarHelper
import org.hisp.dhis.periodgenerator.period.PeriodType

internal class WeeklyPeriodGenerator(
    periodType: PeriodType,
    private val weekStartDay: DayOfWeek,
    private val suffix: String
) : AbstractPeriodGenerator(periodType) {
    override fun startOfCurrentPeriod(date: LocalDate, periodOffset: Int): LocalDate {
        val daysUntilStartOfTheWeek = date.dayOfWeek.ordinal - weekStartDay.ordinal
        return date.minus(daysUntilStartOfTheWeek, DateTimeUnit.DAY).plus(periodOffset, DateTimeUnit.WEEK)
    }

    override fun moveToStartOfCurrentPeriod() {
    }

    override fun moveToStartOfCurrentYear() {
    }

    override fun movePeriods(number: Int) {
    }

    override fun generateId(date: LocalDate): String {
        // day --> weekOfYear
        // 7 days --> week
        // 1st week of year --> diff % 7 --> weekOfYear
        // 1st week of year? --> 1st Thursday
        // 1st Thursday?

        // 1, 2, 3




        // TODO Is wrong, fix it
        val week: Int = (10 + date.dayOfYear - date.dayOfWeek.ordinal) / 7
        when {
            week in 1..52 -> {
                return date.year.toString() + suffix + week.toString()
            }
            week < 1 -> {
                val w = CalendarHelper.weeksInAYear(date.year - 1, weekStartDay)
                return (date.year - 1).toString() + suffix + w.toString()
            }
            week > 52 -> {
                date.year + 1
            }
            else -> 0
        }



    }
}

