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
package org.hisp.dhis.android.core.period.internal

import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.clock.internal.ClockProvider
import org.hisp.dhis.android.core.period.generator.internal.BiWeeklyPeriodGenerator
import org.hisp.dhis.android.core.period.generator.internal.DailyPeriodGenerator
import org.hisp.dhis.android.core.period.generator.internal.MonthlyPeriodGenerator
import org.hisp.dhis.android.core.period.generator.internal.NMonthlyPeriodGenerators
import org.hisp.dhis.android.core.period.generator.internal.PeriodGenerator
import org.hisp.dhis.android.core.period.generator.internal.WeeklyPeriodGenerators
import org.hisp.dhis.android.core.period.generator.internal.YearlyPeriodGenerators
import java.util.Date

internal class ParentPeriodGeneratorImpl(
    private val daily: PeriodGenerator,
    private val weekly: WeeklyPeriodGenerators,
    private val biWeekly: PeriodGenerator,
    private val monthly: PeriodGenerator,
    private val nMonthly: NMonthlyPeriodGenerators,
    private val yearly: YearlyPeriodGenerators,
) : ParentPeriodGenerator {

    override fun generatePeriods(): List<Period> {
        return PeriodType.entries.flatMap { periodType ->
            generatePeriods(periodType, periodType.defaultEndPeriods)
        }
    }

    override fun generatePeriods(periodType: PeriodType, endPeriods: Int): List<Period> {
        return generatePeriods(periodType, periodType.defaultStartPeriods, endPeriods)
    }

    override fun generatePeriods(periodType: PeriodType, startPeriods: Int, endPeriods: Int): List<Period> {
        return getPeriodGenerator(periodType).generatePeriods(startPeriods, endPeriods).toPeriods()
    }

    override fun generatePeriod(periodType: PeriodType, date: Date, offset: Int): Period {
        val periodGenerator = getPeriodGenerator(periodType)
        return periodGenerator.generatePeriod(date.toLocalDate(), offset).toPeriods()
    }

    override fun generateRelativePeriods(relativePeriod: RelativePeriod): List<Period> {
        val periodGenerator = getPeriodGenerator(relativePeriod.periodType)

        return when {
            relativePeriod.start != null && relativePeriod.end != null ->
                periodGenerator.generatePeriods(relativePeriod.start, relativePeriod.end).toPeriods()
            relativePeriod.periodsThisYear ->
                periodGenerator.generatePeriodsInYear(0).toPeriods()
            relativePeriod.periodsLastYear ->
                periodGenerator.generatePeriodsInYear(-1).toPeriods()
            else ->
                emptyList()
        }
    }

    @Suppress("ComplexMethod")
    private fun getPeriodGenerator(periodType: PeriodType): PeriodGenerator {
        return when (periodType) {
            PeriodType.Daily -> daily
            PeriodType.Weekly -> weekly.weekly
            PeriodType.WeeklyWednesday -> weekly.weeklyWednesday
            PeriodType.WeeklyThursday -> weekly.weeklyThursday
            PeriodType.WeeklySaturday -> weekly.weeklySaturday
            PeriodType.WeeklySunday -> weekly.weeklySunday
            PeriodType.BiWeekly -> biWeekly
            PeriodType.Monthly -> monthly
            PeriodType.BiMonthly -> nMonthly.biMonthly
            PeriodType.Quarterly -> nMonthly.quarterly
            PeriodType.QuarterlyNov -> nMonthly.quarterlyNov
            PeriodType.SixMonthly -> nMonthly.sixMonthly
            PeriodType.SixMonthlyApril -> nMonthly.sixMonthlyApril
            PeriodType.SixMonthlyNov -> nMonthly.sixMonthlyNov
            PeriodType.Yearly -> yearly.yearly
            PeriodType.FinancialApril -> yearly.financialApril
            PeriodType.FinancialJuly -> yearly.financialJuly
            PeriodType.FinancialOct -> yearly.financialOct
            PeriodType.FinancialNov -> yearly.financialNov
        }
    }

    companion object {
        fun create(clockProvider: ClockProvider): ParentPeriodGeneratorImpl {
            val clock = clockProvider.clock
            return ParentPeriodGeneratorImpl(
                DailyPeriodGenerator(clock),
                WeeklyPeriodGenerators(clock),
                BiWeeklyPeriodGenerator(clock),
                MonthlyPeriodGenerator(clock),
                NMonthlyPeriodGenerators(clock),
                YearlyPeriodGenerators(clock),
            )
        }
    }
}
