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
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.generator.internal.BiWeeklyPeriodGenerator
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BiWeeklyPeriodGeneratorShould {

    @Test
    fun generate_bi_weekly_periods_for_one_bi_week() {
        val clock = Clock.fixed(LocalDate(2018, 3, 22))

        val periods = BiWeeklyPeriodGenerator(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018BiW5")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 2, 26))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 3, 11))
        }
    }

    @Test
    fun generate_bi_weekly_periods() {
        val clock = Clock.fixed(LocalDate(2018, 3, 29))

        val periods = BiWeeklyPeriodGenerator(clock).generatePeriods(-2, 0)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018BiW5", "2018BiW6"))
    }

    @Test
    fun generate_all_bi_weekly_periods() {
        val clock = Clock.fixed(LocalDate(2021, 3, 25))

        val periods = BiWeeklyPeriodGenerator(clock).generatePeriods(
            PeriodType.BiWeekly.defaultStartPeriods,
            PeriodType.BiWeekly.defaultEndPeriods,
        )

        val periodIds = periods.map { it.periodId }

        assertThat(periodIds.contains("2020BiW27")).isTrue()
        assertThat(periods.size).isEqualTo(13)
    }

    @Test
    fun generate_bi_weekly_periods_for_changing_year() {
        val clock = Clock.fixed(LocalDate(2017, 2, 1))

        val periods = BiWeeklyPeriodGenerator(clock).generatePeriods(-3, 0)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2016BiW26", "2017BiW1", "2017BiW2"))
    }

    @Test
    fun generate_the_first_bi_week_including_january_4() {
        val clock = Clock.fixed(LocalDate(2018, 1, 22))

        val periods = BiWeeklyPeriodGenerator(clock).generatePeriods(-1, 0)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2018BiW1"))
    }

    @Test
    fun generate_last_bi_week_in_a_53_weeks_year() {
        val clock = Clock.fixed(LocalDate(2020, 12, 29))

        val periods = BiWeeklyPeriodGenerator(clock).generatePeriods(0, 1)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2020BiW27"))
    }

    @Test
    fun generate_period_id() {
        val generator = BiWeeklyPeriodGenerator(Clock.System)

        assertThat("2019BiW26").isEqualTo(generator.generatePeriod(LocalDate(2019, 12, 23), 0).periodId)
        assertThat("2020BiW1").isEqualTo(generator.generatePeriod(LocalDate(2020, 1, 2), 0).periodId)
    }

    @Test
    fun generate_period_id_with_offset() {
        val generator = BiWeeklyPeriodGenerator(Clock.System)

        assertThat("2020BiW2").isEqualTo(generator.generatePeriod(LocalDate(2019, 12, 23), 2).periodId)
        assertThat("2019BiW25").isEqualTo(generator.generatePeriod(LocalDate(2020, 1, 2), -2).periodId)
    }

    @Test
    fun generate_period_id_with_very_large_offset() {
        val generator = BiWeeklyPeriodGenerator(Clock.System)

        val periodPast = generator.generatePeriod(LocalDate(2024, 7, 29), -180)

        assertThat(periodPast.periodId).isEqualTo("2017BiW19")
        assertThat(periodPast.startDate).isEqualTo(LocalDate(2017, 9, 11))
        assertThat(periodPast.endDate).isEqualTo(LocalDate(2017, 9, 24))

        val periodFuture = generator.generatePeriod(LocalDate(2024, 7, 29), 64)

        assertThat(periodFuture.periodId).isEqualTo("2027BiW1")
        assertThat(periodFuture.startDate).isEqualTo(LocalDate(2027, 1, 4))
        assertThat(periodFuture.endDate).isEqualTo(LocalDate(2027, 1, 17))
    }

    @Test
    fun generate_last_periods_in_53_weeks_year() {
        val clock = Clock.fixed(LocalDate(2021, 1, 18))

        val generator = BiWeeklyPeriodGenerator(clock)
        val periods = generator.generatePeriods(-3, 0)

        val first2021 = periods[periods.size - 1]
        val last2020 = periods[periods.size - 2]

        assertThat(first2021.periodId).isEqualTo("2021BiW1")
        assertThat(first2021.startDate).isEqualTo(LocalDate(2021, 1, 4))
        assertThat(first2021.endDate).isEqualTo(LocalDate(2021, 1, 17))

        assertThat(last2020.periodId).isEqualTo("2020BiW27")
        assertThat(last2020.startDate).isEqualTo(LocalDate(2020, 12, 28))
        assertThat(last2020.endDate).isEqualTo(LocalDate(2021, 1, 10))
    }

    @Test
    fun generate_last_periods_in_53_weeks_year_starting_past_year() {
        val clock = Clock.fixed(LocalDate(2020, 12, 2))

        val generator = BiWeeklyPeriodGenerator(clock)
        val periods = generator.generatePeriods(1, 5)

        val first2021 = periods[periods.size - 2]
        val last2020 = periods[periods.size - 3]

        assertThat(first2021.periodId).isEqualTo("2021BiW1")
        assertThat(first2021.startDate).isEqualTo(LocalDate(2021, 1, 4))
        assertThat(first2021.endDate).isEqualTo(LocalDate(2021, 1, 17))

        assertThat(last2020.periodId).isEqualTo("2020BiW27")
        assertThat(last2020.startDate).isEqualTo(LocalDate(2020, 12, 28))
        assertThat(last2020.endDate).isEqualTo(LocalDate(2021, 1, 10))
    }

    @Test
    fun generate_periods_in_this_year() {
        val clock = Clock.fixed(LocalDate(2020, 8, 29))
        val generator = BiWeeklyPeriodGenerator(clock)
        val periods = generator.generatePeriodsInYear(0)

        assertThat(periods.size).isEqualTo(27)
        assertThat(periods[0].periodId).isEqualTo("2020BiW1")
        assertThat(periods[26].periodId).isEqualTo("2020BiW27")
        assertThat(periods[26].startDate).isEqualTo(LocalDate(2020, 12, 28))

        val clock2 = Clock.fixed(LocalDate(2021, 5, 15))
        val generator2 = BiWeeklyPeriodGenerator(clock2)
        val periods2 = generator2.generatePeriodsInYear(0)

        assertThat(periods2.size).isEqualTo(26)
        assertThat(periods2[0].periodId).isEqualTo("2021BiW1")
        assertThat(periods2[0].startDate).isEqualTo(LocalDate(2021, 1, 4))
    }

    @Test
    fun generate_periods_in_last_year() {
        val clock = Clock.fixed(LocalDate(2020, 8, 29))
        val generator = BiWeeklyPeriodGenerator(clock)
        val periods = generator.generatePeriodsInYear(-1)

        assertThat(periods.size).isEqualTo(26)
        assertThat(periods[0].periodId).isEqualTo("2019BiW1")
        assertThat(periods[25].periodId).isEqualTo("2019BiW26")
    }
}
