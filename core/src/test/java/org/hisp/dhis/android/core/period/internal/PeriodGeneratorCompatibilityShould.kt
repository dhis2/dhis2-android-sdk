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

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.generator.internal.PeriodKt
import org.hisp.dhis.android.core.period.generator.internal.YearlyPeriodGenerator
import org.hisp.dhis.android.core.period.internal.generator.internal.fixed
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Calendar
import java.util.Date

internal typealias OldPeriodGenerator = PeriodGenerator
internal typealias NewPeriodGenerator = org.hisp.dhis.android.core.period.generator.internal.PeriodGenerator

internal typealias NewDailyPeriodGenerator =
    org.hisp.dhis.android.core.period.generator.internal.DailyPeriodGenerator
internal typealias NewWeeklyPeriodGeneratorFactory =
    org.hisp.dhis.android.core.period.generator.internal.WeeklyPeriodGeneratorFactory
internal typealias NewBiWeeklyPeriodGenerator =
    org.hisp.dhis.android.core.period.generator.internal.BiWeeklyPeriodGenerator
internal typealias NewMonthlyPeriodGenerator =
    org.hisp.dhis.android.core.period.generator.internal.MonthlyPeriodGenerator
internal typealias NewNMonthlyPeriodGeneratorFactory =
    org.hisp.dhis.android.core.period.generator.internal.NMonthlyPeriodGeneratorFactory
internal typealias NewYearlyPeriodGeneratorFactory =
    org.hisp.dhis.android.core.period.generator.internal.YearlyPeriodGeneratorFactory

@RunWith(JUnit4::class)
class PeriodGeneratorCompatibilityShould {

    private val clock: Clock = Clock.fixed(LocalDate(2024, 7, 29))
    private val calendar: Calendar = createFixed()

    private val years = 15

    private val generators = listOf(
        Generator(
            DailyPeriodGenerator(calendar),
            NewDailyPeriodGenerator(clock),
            365 * years,
        ),
        Generator(
            WeeklyPeriodGeneratorFactory.weekly(calendar),
            NewWeeklyPeriodGeneratorFactory.weekly(clock),
            52 * years,
        ),
        Generator(
            WeeklyPeriodGeneratorFactory.wednesday(calendar),
            NewWeeklyPeriodGeneratorFactory.wednesday(clock),
            52 * years,
        ),
        Generator(
            WeeklyPeriodGeneratorFactory.thursday(calendar),
            NewWeeklyPeriodGeneratorFactory.thursday(clock),
            52 * years,
        ),
        Generator(
            WeeklyPeriodGeneratorFactory.saturday(calendar),
            NewWeeklyPeriodGeneratorFactory.saturday(clock),
            52 * years,
        ),
        Generator(
            WeeklyPeriodGeneratorFactory.sunday(calendar),
            NewWeeklyPeriodGeneratorFactory.sunday(clock),
            52 * years,
        ),
        Generator(
            BiWeeklyPeriodGenerator(calendar),
            NewBiWeeklyPeriodGenerator(clock),
            12 * years,
        ),
        Generator(
            MonthlyPeriodGenerator(calendar),
            NewMonthlyPeriodGenerator(clock),
            12 * years,
        ),
        Generator(
            NMonthlyPeriodGeneratorFactory.biMonthly(calendar),
            NewNMonthlyPeriodGeneratorFactory.biMonthly(clock),
            6 * years,
        ),
        Generator(
            NMonthlyPeriodGeneratorFactory.quarter(calendar),
            NewNMonthlyPeriodGeneratorFactory.quarter(clock),
            4 * years,
        ),
        Generator(
            NMonthlyPeriodGeneratorFactory.sixMonthly(calendar),
            NewNMonthlyPeriodGeneratorFactory.sixMonthly(clock),
            2 * years,
        ),
        Generator(
            NMonthlyPeriodGeneratorFactory.sixMonthlyApril(calendar),
            NewNMonthlyPeriodGeneratorFactory.sixMonthlyApril(clock),
            2 * years,
        ),
        Generator(
            NMonthlyPeriodGeneratorFactory.sixMonthlyNov(calendar),
            NewNMonthlyPeriodGeneratorFactory.sixMonthlyNov(clock),
            2 * years,
        ),
        Generator(
            YearlyPeriodGeneratorFactory.yearly(calendar),
            NewYearlyPeriodGeneratorFactory.yearly(clock),
            years,
        ),
        Generator(
            YearlyPeriodGeneratorFactory.financialApril(calendar),
            NewYearlyPeriodGeneratorFactory.financialApril(clock),
            years,
        ),
        Generator(
            YearlyPeriodGeneratorFactory.financialJuly(calendar),
            NewYearlyPeriodGeneratorFactory.financialJuly(clock),
            years,
        ),
        Generator(
            YearlyPeriodGeneratorFactory.financialOct(calendar),
            NewYearlyPeriodGeneratorFactory.financialOct(clock),
            years,
        ),
        Generator(
            YearlyPeriodGeneratorFactory.financialNov(calendar),
            NewYearlyPeriodGeneratorFactory.financialNov(clock),
            years,
        ),
    )

    @Test
    fun generate_periods() {
        generators.forEach { generator ->
            val oldPeriods = generator.oldGenerator.generatePeriods(-generator.periods, generator.periods)
            val newPeriods = generator.newGenerator.generatePeriods(-generator.periods, generator.periods)

            areEqual(oldPeriods, newPeriods)
        }
    }

    @Test
    fun generate_periods_in_year() {
        (-5..5).forEach { offset ->
            generators
                .filterNot { it.newGenerator is YearlyPeriodGenerator }
                .forEach { generator ->
                    val oldPeriods = generator.oldGenerator.generatePeriodsInYear(offset)
                    val newPeriods = generator.newGenerator.generatePeriodsInYear(offset)

                    areEqual(oldPeriods, newPeriods)
                }
        }
    }

    @Test
    fun generate_periods_with_offset() {
        val date = calendar.time
        val localDate = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        generators.forEach { generator ->
            (-generator.periods..generator.periods).forEach { offset ->
                if (generator.newGenerator is NewBiWeeklyPeriodGenerator && (offset < -90 || offset > 63)) {
                    // Skip because of a bug in old BiWeeklyPeriodGenerator
                    return@forEach
                }

                val oldPeriods = generator.oldGenerator.generatePeriod(date, offset)
                val newPeriods = generator.newGenerator.generatePeriod(localDate, offset)

                areEqual(listOf(oldPeriods), listOf(newPeriods))
            }
        }
    }

    private fun toStartDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toJavaInstant())
    }

    private fun toEndDate(localDate: LocalDate): Date {
        val nextDay = toStartDate(localDate.plus(1, DateTimeUnit.DAY))
        return Date(nextDay.time - 1)
    }

    private fun areEqual(oldPeriods: List<Period>, newPeriods: List<PeriodKt>) {
        assertThat(oldPeriods.size).isEqualTo(newPeriods.size)

        oldPeriods.zip(newPeriods).forEach { (oldPeriod, newPeriod) ->
            assertThat(oldPeriod.periodId()).isEqualTo(newPeriod.periodId)
            assertThat(oldPeriod.startDate()).isEqualTo(toStartDate(newPeriod.startDate))
            assertThat(oldPeriod.endDate()).isEqualTo(toEndDate(newPeriod.endDate))
        }
    }

    private fun createFixed(): Calendar {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = 2024
        calendar[Calendar.MONTH] = 6
        calendar[Calendar.DATE] = 29

        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }
}

internal data class Generator(
    val oldGenerator: OldPeriodGenerator,
    val newGenerator: NewPeriodGenerator,
    val periods: Int,
)
