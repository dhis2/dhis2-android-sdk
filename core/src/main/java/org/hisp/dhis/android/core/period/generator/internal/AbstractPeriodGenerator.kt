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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.period.PeriodType

internal abstract class AbstractPeriodGenerator(
    private val clock: Clock,
    protected val periodType: PeriodType,
) : PeriodGenerator {

    @Synchronized
    @Throws(RuntimeException::class)
    override fun generatePeriods(start: Int, end: Int): List<PeriodKt> {
        if (start >= end) {
            return emptyList()
        }

        val currentDate = getToday()
        val firstPeriodStartDate = moveToStartOfThePeriodOfADayWithOffset(currentDate, start)

        val periods = (0..<(end - start)).map { offset -> generatePeriod(firstPeriodStartDate, offset) }

        return periods
    }

    @Synchronized
    override fun generatePeriod(date: LocalDate, periodOffset: Int): PeriodKt {
        val startDate = moveToStartOfThePeriodOfADayWithOffset(date, periodOffset)
        val endDate = this.getEndDateForStartDate(startDate)
        val periodId = generateId(startDate)

        return PeriodKt(
            periodId,
            periodType,
            startDate,
            endDate,
        )
    }

    @Synchronized
    override fun generatePeriodsInYear(yearOffset: Int): List<PeriodKt> {
        val currentDate = getToday()
        val targetDate = currentDate.plus(yearOffset, DateTimeUnit.YEAR)

        val currentPeriod = generatePeriod(targetDate, 0)
        val targetYear = currentPeriod.periodId.substring(0, YEAR_DIGITS)
        val startOfTargetYear = getStartOfYearFor(targetDate)

        val periods = generateSequence(0) { it + 1 }
            .map { offset -> generatePeriod(startOfTargetYear, offset) }
            .takeWhile { period -> period.periodId.startsWith(targetYear) }

        return periods.toList()
    }

    private fun moveToStartOfThePeriodOfADayWithOffset(date: LocalDate, periodOffset: Int): LocalDate {
        val startOfCurrentPeriod = getStartOfPeriodFor(date)
        return this.movePeriodForStartDate(startOfCurrentPeriod, periodOffset)
    }

    private fun getToday(): LocalDate {
        return clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    protected open fun getEndDateForStartDate(startDate: LocalDate): LocalDate {
        return this.movePeriodForStartDate(startDate, 1).minus(1, DateTimeUnit.DAY)
    }

    protected abstract fun getStartOfPeriodFor(date: LocalDate): LocalDate

    protected abstract fun getStartOfYearFor(date: LocalDate): LocalDate

    protected abstract fun movePeriodForStartDate(startDate: LocalDate, offset: Int): LocalDate

    protected abstract fun generateId(startDate: LocalDate): String

    companion object {
        private const val YEAR_DIGITS = 4
    }
}
