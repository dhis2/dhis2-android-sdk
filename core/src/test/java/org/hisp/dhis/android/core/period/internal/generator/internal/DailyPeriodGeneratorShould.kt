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
import org.hisp.dhis.android.core.period.generator.internal.DailyPeriodGenerator
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DailyPeriodGeneratorShould {

    @Test
    fun generate_daily_periods_for_one_day() {
        val clock = Clock.fixed(LocalDate(2024, 2, 2))

        val periods = DailyPeriodGenerator(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("20240201")
            assertThat(period.startDate).isEqualTo(LocalDate(2024, 2, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2024, 2, 1))
        }
    }

    @Test
    fun generate_daily_periods_for_changing_year() {
        val clock = Clock.fixed(LocalDate(2024, 1, 3))

        val periods = DailyPeriodGenerator(clock).generatePeriods(-3, 0)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("20231231", "20240101", "20240102"))
    }

    @Test
    fun generate_period_id() {
        val generator = DailyPeriodGenerator(Clock.System)

        assertThat("20191230").isEqualTo(generator.generatePeriod(0, LocalDate(2019, 12, 30)).periodId)
        assertThat("20200102").isEqualTo(generator.generatePeriod(0, LocalDate(2020, 1, 2)).periodId)
    }

    @Test
    fun generate_period_id_with_offset() {
        val generator = DailyPeriodGenerator(Clock.System)

        assertThat("20200101").isEqualTo(generator.generatePeriod(2, LocalDate(2019, 12, 30)).periodId)
        assertThat("20191229").isEqualTo(generator.generatePeriod(-4, LocalDate(2020, 1, 2)).periodId)
    }

    @Test
    fun generate_periods_in_this_year() {
        val clock = Clock.fixed(LocalDate(2024, 8, 29))
        val generator = DailyPeriodGenerator(clock)

        val periods = generator.generatePeriodsInYear(0)

        assertThat(periods.size).isEqualTo(366)
        assertThat(periods[0].periodId).isEqualTo("20240101")
        assertThat(periods[365].periodId).isEqualTo("20241231")
    }

    @Test
    fun generate_periods_in_last_year() {
        val clock = Clock.fixed(LocalDate(2024, 8, 29))
        val generator = DailyPeriodGenerator(clock)

        val periods = generator.generatePeriodsInYear(-1)

        assertThat(periods.size).isEqualTo(365)
        assertThat(periods[0].periodId).isEqualTo("20230101")
        assertThat(periods[364].periodId).isEqualTo("20231231")
    }
}