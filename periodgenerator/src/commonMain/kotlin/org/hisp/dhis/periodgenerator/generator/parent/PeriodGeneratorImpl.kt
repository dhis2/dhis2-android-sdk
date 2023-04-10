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
package org.hisp.dhis.periodgenerator.generator.parent

import kotlinx.datetime.LocalDate
import org.hisp.dhis.periodgenerator.generator.bytype.TypeBasedPeriodGenerator
import org.hisp.dhis.periodgenerator.period.Period
import org.hisp.dhis.periodgenerator.period.PeriodType
import org.hisp.dhis.periodgenerator.period.RelativePeriod

internal class PeriodGeneratorImpl(
    private val daily: TypeBasedPeriodGenerator,
    // private val weekly: WeeklyPeriodGenerators,
    private val biWeekly: TypeBasedPeriodGenerator,
    private val monthly: TypeBasedPeriodGenerator
    // private val nMonthly: NMonthlyPeriodGenerators,
    // private val yearly: YearlyPeriodGenerators
) : PeriodGenerator {

    override fun generatePeriods(): List<Period> {
        return PeriodType.values().flatMap { generatePeriods(it, it.defaultEndPeriods) }
    }

    override fun generatePeriods(periodType: PeriodType, endPeriods: Int): List<Period> {
        return generatePeriods(periodType, periodType.defaultStartPeriods, endPeriods)
    }

    override fun generatePeriods(periodType: PeriodType, startPeriods: Int, endPeriods: Int): List<Period> {
        return getTypeBasedPeriodGenerator(periodType).generatePeriods(startPeriods, endPeriods)
    }

    override fun generatePeriod(periodType: PeriodType, date: LocalDate, offset: Int): Period {
        return getTypeBasedPeriodGenerator(periodType).generatePeriod(date, offset)
    }

    override fun generateRelativePeriods(relativePeriod: RelativePeriod): List<Period> {
        val periodGenerator = getTypeBasedPeriodGenerator(relativePeriod.periodType)

        return when {
            relativePeriod.start != null && relativePeriod.end != null ->
                periodGenerator.generatePeriods(relativePeriod.start, relativePeriod.end)
            relativePeriod.periodsThisYear ->
                periodGenerator.generatePeriodsInYear(0)
            relativePeriod.periodsLastYear ->
                periodGenerator.generatePeriodsInYear(-1)
            else ->
                emptyList()
        }
    }

    @Suppress("ComplexMethod")
    private fun getTypeBasedPeriodGenerator(periodType: PeriodType): TypeBasedPeriodGenerator {
        return when (periodType) {
            PeriodType.Daily -> daily
            // PeriodType.Weekly -> weekly.weekly
            // PeriodType.WeeklyWednesday -> weekly.weeklyWednesday
            // PeriodType.WeeklyThursday -> weekly.weeklyThursday
            // PeriodType.WeeklySaturday -> weekly.weeklySaturday
            // PeriodType.WeeklySunday -> weekly.weeklySunday
            PeriodType.BiWeekly -> biWeekly
            PeriodType.Monthly -> monthly
            // PeriodType.BiMonthly -> nMonthly.biMonthly
            // PeriodType.Quarterly -> nMonthly.quarter
            // PeriodType.SixMonthly -> nMonthly.sixMonthly
            // PeriodType.SixMonthlyApril -> nMonthly.sixMonthlyApril
            // PeriodType.SixMonthlyNov -> nMonthly.sixMonthlyNov
            // PeriodType.Yearly -> yearly.yearly
            // PeriodType.FinancialApril -> yearly.financialApril
            // PeriodType.FinancialJuly -> yearly.financialJuly
            // PeriodType.FinancialOct -> yearly.financialOct
            // PeriodType.FinancialNov -> yearly.financialNov
            else -> daily
        }
    }

    companion object {
        /*fun create(): PeriodGeneratorImpl {
            return PeriodGeneratorImpl(
                DailyPeriodGenerator(),
                WeeklyPeriodGenerators(),
                BiWeeklyPeriodGenerator(),
                MonthlyPeriodGenerator(),
                NMonthlyPeriodGenerators(),
                YearlyPeriodGenerators()
            )
        }*/
    }
}
