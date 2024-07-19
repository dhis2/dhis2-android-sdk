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

internal class WeeklyPeriodGenerator(
    clock: Clock,
    periodType: PeriodType,
    private val weekStartDay: DayOfWeek,
    private val suffix: String,
) : AbstractPeriodGenerator(clock, periodType) {
    override fun getStartOfPeriodFor(date: LocalDate): LocalDate {
        val daysToGoBack = daysFromWeekStart(date)
        return date.minus(daysToGoBack, DateTimeUnit.DAY)
    }

    override fun getStartOfYearFor(date: LocalDate): LocalDate {
        val firstJanuary = LocalDate(date.year, 1, 1)
        val startOfWeek = getStartOfPeriodFor(firstJanuary)
        val daysInPreviousYear = daysFromWeekStart(firstJanuary)

        return if (daysInPreviousYear < DAYS_IN_YEAR_THRESHOLD) {
            startOfWeek
        } else {
            startOfWeek.plus(1, DateTimeUnit.WEEK)
        }
    }

    override fun movePeriods(date: LocalDate, offset: Int): LocalDate {
        return date.plus(offset, DateTimeUnit.WEEK)
    }

    override fun generateId(startDate: LocalDate, endDate: LocalDate): String {
        val daysInEndYear = endDate.dayOfYear
        val year =
            if (daysInEndYear >= DAYS_IN_YEAR_THRESHOLD) {
                endDate.year
            } else {
                startDate.year
            }

        val startOfTargetYear = getStartOfYearFor(LocalDate(year, 1, 1))
        val daysFromStartOfYear = startDate.toEpochDays() - startOfTargetYear.toEpochDays()
        val weekNumber = Math.floorDiv(daysFromStartOfYear, DAYS_IN_WEEK) + 1

        return "$year$suffix$weekNumber"
    }

    private fun daysFromWeekStart(date: LocalDate): Int {
        val diff = date.dayOfWeek.value - weekStartDay.value
        return if (diff >= 0) diff else diff + DAYS_IN_WEEK
    }

    companion object {
        const val DAYS_IN_YEAR_THRESHOLD = 4
        const val DAYS_IN_WEEK = 7
    }
}
