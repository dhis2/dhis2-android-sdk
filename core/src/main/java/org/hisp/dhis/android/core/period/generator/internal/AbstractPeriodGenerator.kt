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
    protected val clock: Clock,
    protected val periodType: PeriodType
) : PeriodGenerator {

    @Synchronized
    @Throws(RuntimeException::class)
    override fun generatePeriods(start: Int, end: Int): List<PeriodK> {
        if (start >= end) {
            return emptyList()
        }

        val currentDate = getToday()
        val firstPeriodStartDate = moveToStartOfThePeriodOfADayWithOffset(currentDate, start)

        val periods = (0 ..< (end - start)).map { offset -> generatePeriod(offset, firstPeriodStartDate) }

        return periods
    }

    @Synchronized
    override fun generatePeriod(periodOffset: Int, refDate: LocalDate): PeriodK {
        val startDate = moveToStartOfThePeriodOfADayWithOffset(refDate, periodOffset)

        val periodId = generateId(startDate)
        val nextPeriodStartDate = this.movePeriods(startDate, 1)
        val endDate = nextPeriodStartDate.minus(1, DateTimeUnit.DAY)

        return PeriodK(
            periodId,
            periodType,
            startDate,
            endDate
        )
    }


    @Synchronized
    override fun generatePeriodsInYear(yearOffset: Int): List<PeriodK> {
        val currentDate = getToday()

        val startOfCurrentYear = getStartOfCurrentYear(currentDate)
        val startOfTargetYear = startOfCurrentYear.plus(yearOffset, DateTimeUnit.YEAR)

        val targetYear = startOfTargetYear.year

        val periods = generateSequence(0) { it + 1 }
            .map { offset -> generatePeriod(offset, startOfTargetYear)}
            .takeWhile { period -> period.periodId.startsWith(targetYear.toString()) }

        return periods.toList()
    }

    private fun moveToStartOfThePeriodOfADayWithOffset(date: LocalDate, periodOffset: Int): LocalDate {
        val startOfCurrentPeriod = getStartOfCurrentPeriod(date)
        return this.movePeriods(startOfCurrentPeriod, periodOffset)
    }

    private fun getToday(): LocalDate {
        return clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    protected abstract fun getStartOfCurrentPeriod(date: LocalDate): LocalDate

    protected abstract fun getStartOfCurrentYear(date: LocalDate): LocalDate

    protected abstract fun movePeriods(date: LocalDate, offset: Int): LocalDate

    protected abstract fun generateId(date: LocalDate): String
}