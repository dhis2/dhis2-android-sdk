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
package org.hisp.dhis.android.core.period.generator.internal

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.hisp.dhis.android.core.period.PeriodType
import kotlin.math.abs

internal class BiWeeklyPeriodGenerator(clock: Clock) :
    AbstractPeriodGenerator(clock, PeriodType.BiWeekly) {

    private val weekHelper = WeeklyPeriodGeneratorHelper(DayOfWeek.MONDAY)

    override fun getStartOfPeriodFor(date: LocalDate): LocalDate {
        val startDate = weekHelper.getFirstDayOfWeek(date)
        val weekNumber = weekHelper.getWeekNumber(date)
        val isSecondWeekOfBiWeek = weekNumber % 2 == 0

        return if (isSecondWeekOfBiWeek) {
            startDate.minus(1, DateTimeUnit.WEEK)
        } else {
            startDate
        }
    }

    override fun getStartOfYearFor(date: LocalDate): LocalDate {
        return weekHelper.getFirstDayOfYear(date.year)
    }

    override fun movePeriodForStartDate(startDate: LocalDate, offset: Int): LocalDate {
        var periodId = generateId(startDate)
        var periodStartDate = startDate
        var dateInPeriod = startDate

        var iterations = 0
        while (iterations < abs(offset)) {
            val weekIncrement = if (offset > 0) 1 else -1
            val nextPeriodDate = dateInPeriod.plus(weekIncrement, DateTimeUnit.WEEK)
            val nextPeriodStartDate = getStartOfPeriodFor(nextPeriodDate)
            val nextPeriodId = generateId(nextPeriodStartDate)

            if (nextPeriodId != periodId) {
                iterations++
            }
            periodId = nextPeriodId
            periodStartDate = nextPeriodStartDate
            dateInPeriod = nextPeriodDate
        }

        return periodStartDate
    }

    override fun getEndDateForStartDate(startDate: LocalDate): LocalDate {
        return startDate.plus(2, DateTimeUnit.WEEK).minus(1, DateTimeUnit.DAY)
    }

    override fun generateId(startDate: LocalDate): String {
        val year = weekHelper.getWeekYearForStartDate(startDate)
        val weekNumber = (weekHelper.getWeekNumber(startDate) / 2) + 1

        return "${year}BiW$weekNumber"
    }
}
