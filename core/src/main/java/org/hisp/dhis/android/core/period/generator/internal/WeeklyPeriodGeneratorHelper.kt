/*
 *  Copyright (c) 2004-2024, University of Oslo
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

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

internal class WeeklyPeriodGeneratorHelper(private val weekStartDay: DayOfWeek,) {

    fun getFirstDayOfYear(year: Int): LocalDate {
        val firstJanuary = LocalDate(year, 1, 1)
        val startOfWeek = getFirstDayOfWeek(firstJanuary)
        val daysInPreviousYear = daysFromWeekStart(firstJanuary)

        return if (daysInPreviousYear < DAYS_IN_YEAR_THRESHOLD) {
            startOfWeek
        } else {
            startOfWeek.plus(1, DateTimeUnit.WEEK)
        }
    }

    fun getFirstDayOfWeek(date: LocalDate): LocalDate {
        val daysToGoBack = daysFromWeekStart(date)
        return date.minus(daysToGoBack, DateTimeUnit.DAY)
    }

    fun getWeekNumber(date: LocalDate): Int {
        val startDate = getFirstDayOfWeek(date)
        val year = getWeekYearForStartDate(startDate)
        val startOfTargetYear = getFirstDayOfYear(year)
        val daysFromStartOfYear = startDate.toEpochDays() - startOfTargetYear.toEpochDays()
        return Math.floorDiv(daysFromStartOfYear, WEEK_DAYS) + 1
    }

    fun getWeekYearForStartDate(startDate: LocalDate): Int {
        val endDate = startDate.plus(WEEK_DAYS - 1, DateTimeUnit.DAY)
        return if (endDate.dayOfYear >= DAYS_IN_YEAR_THRESHOLD) {
            endDate.year
        } else {
            startDate.year
        }
    }

    private fun daysFromWeekStart(date: LocalDate): Int {
        val diff = date.dayOfWeek.value - weekStartDay.value
        return if (diff >= 0) diff else diff + WEEK_DAYS
    }

    companion object {
        private const val DAYS_IN_YEAR_THRESHOLD = 4
        private const val WEEK_DAYS = 7
    }
}