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
package org.hisp.dhis.android.core.period.internal.generator.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.hisp.dhis.android.core.period.generator.internal.WeeklyPeriodGeneratorFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WeeklyPeriodGeneratorShould {

    @Test
    fun generate_weekly_periods_for_one_day() {
        val clock = Clock.fixed(LocalDate(2018, 3, 15))

        val periods = WeeklyPeriodGeneratorFactory.weekly(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018W10")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 3, 5))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 3, 11))
        }
    }

    @Test
    fun generate_weekly_periods() {
        val clock = Clock.fixed(LocalDate(2018, 3, 22))

        val periods = WeeklyPeriodGeneratorFactory.weekly(clock).generatePeriods(-2, 0)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018W10", "2018W11"))
    }

    @Test
    fun generate_weekly_periods_for_changing_year() {
        val clock = Clock.fixed(LocalDate(2017, 1, 21))

        val periods = WeeklyPeriodGeneratorFactory.weekly(clock).generatePeriods(-3, 0)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2016W52", "2017W1", "2017W2"))
    }

    @Test
    fun generate_the_first_week_including_january_4() {
        val clock = Clock.fixed(LocalDate(2018, 1, 4))

        WeeklyPeriodGeneratorFactory.weekly(clock).generatePeriods(0, 1).let { periods ->
            assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018W1"))
        }

        WeeklyPeriodGeneratorFactory.wednesday(clock).generatePeriods(0, 1).let { periods ->
            assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018WedW1"))
        }

        WeeklyPeriodGeneratorFactory.thursday(clock).generatePeriods(0, 1).let { periods ->
            assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018ThuW1"))
        }

        WeeklyPeriodGeneratorFactory.saturday(clock).generatePeriods(0, 1).let { periods ->
            assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018SatW1"))
        }

        WeeklyPeriodGeneratorFactory.sunday(clock).generatePeriods(0, 1).let { periods ->
            assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018SunW1"))
        }
    }

    @Test
    fun generate_period_id() {
        val saturdayGenerator = WeeklyPeriodGeneratorFactory.saturday(Clock.System)
        assertThat("2019SatW51").isEqualTo(saturdayGenerator.generatePeriod(0, LocalDate(2019, 12, 20)).periodId)
        assertThat("2019SatW52").isEqualTo(saturdayGenerator.generatePeriod(0, LocalDate(2019, 12, 21)).periodId)

        val sundayGenerator = WeeklyPeriodGeneratorFactory.sunday(Clock.System)
        assertThat("2019SunW51").isEqualTo(sundayGenerator.generatePeriod(0, LocalDate(2019, 12, 21)).periodId)
        assertThat("2019SunW52").isEqualTo(sundayGenerator.generatePeriod(0, LocalDate(2019, 12, 22)).periodId)

        val weeklyGenerator = WeeklyPeriodGeneratorFactory.weekly(Clock.System)
        assertThat("2019W51").isEqualTo(weeklyGenerator.generatePeriod(0, LocalDate(2019, 12, 22)).periodId)
        assertThat("2019W52").isEqualTo(weeklyGenerator.generatePeriod(0, LocalDate(2019, 12, 23)).periodId)

        val wednesdayGenerator = WeeklyPeriodGeneratorFactory.wednesday(Clock.System)
        assertThat("2019WedW51").isEqualTo(wednesdayGenerator.generatePeriod(0, LocalDate(2019, 12, 24)).periodId)
        assertThat("2019WedW52").isEqualTo(wednesdayGenerator.generatePeriod(0, LocalDate(2019, 12, 25)).periodId)

        val thursdayGenerator = WeeklyPeriodGeneratorFactory.thursday(Clock.System)
        assertThat("2019ThuW51").isEqualTo(thursdayGenerator.generatePeriod(0, LocalDate(2019, 12, 25)).periodId)
        assertThat("2019ThuW52").isEqualTo(thursdayGenerator.generatePeriod(0, LocalDate(2019, 12, 26)).periodId)
    }

    @Test
    fun generate_period_id_with_offsets() {
        val saturdayGenerator = WeeklyPeriodGeneratorFactory.saturday(Clock.System)
        assertThat("2019SatW52").isEqualTo(saturdayGenerator.generatePeriod(1, LocalDate(2019, 12, 20)).periodId)
        assertThat("2019SatW51").isEqualTo(saturdayGenerator.generatePeriod(-1, LocalDate(2019, 12, 21)).periodId)

        val sundayGenerator = WeeklyPeriodGeneratorFactory.sunday(Clock.System)
        assertThat("2019SunW52").isEqualTo(sundayGenerator.generatePeriod(1, LocalDate(2019, 12, 21)).periodId)
        assertThat("2019SunW51").isEqualTo(sundayGenerator.generatePeriod(-1, LocalDate(2019, 12, 22)).periodId)

        val weeklyGenerator = WeeklyPeriodGeneratorFactory.weekly(Clock.System)
        assertThat("2019W52").isEqualTo(weeklyGenerator.generatePeriod(1, LocalDate(2019, 12, 22)).periodId)
        assertThat("2019W51").isEqualTo(weeklyGenerator.generatePeriod(-1, LocalDate(2019, 12, 23)).periodId)

        val wednesdayGenerator = WeeklyPeriodGeneratorFactory.wednesday(Clock.System)
        assertThat("2019WedW52").isEqualTo(wednesdayGenerator.generatePeriod(1, LocalDate(2019, 12, 24)).periodId)
        assertThat("2019WedW51").isEqualTo(wednesdayGenerator.generatePeriod(-1, LocalDate(2019, 12, 25)).periodId)

        val thursdayGenerator = WeeklyPeriodGeneratorFactory.thursday(Clock.System)
        assertThat("2019ThuW52").isEqualTo(thursdayGenerator.generatePeriod(1, LocalDate(2019, 12, 25)).periodId)
        assertThat("2019ThuW51").isEqualTo(thursdayGenerator.generatePeriod(-1, LocalDate(2019, 12, 26)).periodId)
    }

    @Test
    fun generate_periods_in_this_year() {
        val clock = Clock.fixed(LocalDate(2019, 8, 29))

        WeeklyPeriodGeneratorFactory.weekly(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(52)
            assertThat(periods.first().periodId).isEqualTo("2019W1")
            assertThat(periods.last().periodId).isEqualTo("2019W52")
        }

        WeeklyPeriodGeneratorFactory.saturday(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(53)
            assertThat(periods.first().periodId).isEqualTo("2019SatW1")
            assertThat(periods.last().periodId).isEqualTo("2019SatW53")
        }

        WeeklyPeriodGeneratorFactory.sunday(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(52)
            assertThat(periods.first().periodId).isEqualTo("2019SunW1")
            assertThat(periods.last().periodId).isEqualTo("2019SunW52")
        }

        WeeklyPeriodGeneratorFactory.wednesday(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(52)
            assertThat(periods.first().periodId).isEqualTo("2019WedW1")
            assertThat(periods.last().periodId).isEqualTo("2019WedW52")
        }

        WeeklyPeriodGeneratorFactory.thursday(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(52)
            assertThat(periods.first().periodId).isEqualTo("2019ThuW1")
            assertThat(periods.last().periodId).isEqualTo("2019ThuW52")
        }
    }
}
