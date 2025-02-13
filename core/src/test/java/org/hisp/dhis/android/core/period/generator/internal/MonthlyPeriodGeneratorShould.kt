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
package org.hisp.dhis.android.core.period.generator.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.hisp.dhis.android.core.period.clock.internal.fixed
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MonthlyPeriodGeneratorShould {

    @Test
    fun generate_daily_periods_for_one_month() {
        val clock = Clock.fixed(LocalDate(2018, 4, 11))

        val periods = MonthlyPeriodGenerator(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("201803")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 3, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 3, 31))
        }
    }

    @Test
    fun generate_starting_period_on_feb_29() {
        val clock = Clock.fixed(LocalDate(2020, 3, 5))

        val periods = MonthlyPeriodGenerator(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("202002")
            assertThat(period.startDate).isEqualTo(LocalDate(2020, 2, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2020, 2, 29))
        }
    }

    @Test
    fun generate_periods_for_three_months() {
        val clock = Clock.fixed(LocalDate(2018, 12, 11))

        val periods = MonthlyPeriodGenerator(clock).generatePeriods(-3, 0)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("201809", "201810", "201811"))
    }

    @Test
    fun generate_period_id() {
        val generator = MonthlyPeriodGenerator(Clock.System)
        assertThat("201906").isEqualTo(generator.generatePeriod(LocalDate(2019, 6, 30), 0).periodId)
        assertThat("201907").isEqualTo(generator.generatePeriod(LocalDate(2019, 7, 1), 0).periodId)
    }

    @Test
    fun generate_period_id_with_offset() {
        val generator = MonthlyPeriodGenerator(Clock.System)
        assertThat("201908").isEqualTo(generator.generatePeriod(LocalDate(2019, 6, 30), 2).periodId)
        assertThat("201905").isEqualTo(generator.generatePeriod(LocalDate(2019, 7, 1), -2).periodId)
    }

    @Test
    fun generate_periods_in_this_year() {
        val clock = Clock.fixed(LocalDate(2019, 8, 29))
        val generator = MonthlyPeriodGenerator(clock)

        val periods = generator.generatePeriodsInYear(0)

        assertThat(periods.size).isEqualTo(12)
        assertThat(periods.first().periodId).isEqualTo("201901")
        assertThat(periods.last().periodId).isEqualTo("201912")
    }

    @Test
    fun generate_periods_in_last_year() {
        val clock = Clock.fixed(LocalDate(2019, 8, 29))
        val generator = MonthlyPeriodGenerator(clock)

        val periods = generator.generatePeriodsInYear(-1)

        assertThat(periods.size).isEqualTo(12)
        assertThat(periods.first().periodId).isEqualTo("201801")
        assertThat(periods.last().periodId).isEqualTo("201812")
    }
}
