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
class YearlyFinancialOctoberPeriodGeneratorShould {

    @Test
    fun generate_periods_for_one_year() {
        val clock = Clock.fixed(LocalDate(2019, 2, 21))

        val periods = YearlyPeriodGeneratorFactory.financialOct(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2017Oct")
            assertThat(period.startDate).isEqualTo(LocalDate(2017, 10, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 9, 30))
        }
    }

    @Test
    fun generate_starting_period_on_oct_1() {
        val clock = Clock.fixed(LocalDate(2018, 10, 1))

        val periods = YearlyPeriodGeneratorFactory.financialOct(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2017Oct")
            assertThat(period.startDate).isEqualTo(LocalDate(2017, 10, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 9, 30))
        }
    }

    @Test
    fun generate_ending_period_on_sep_30() {
        val clock = Clock.fixed(LocalDate(2018, 9, 30))

        val periods = YearlyPeriodGeneratorFactory.financialOct(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2016Oct")
            assertThat(period.startDate).isEqualTo(LocalDate(2016, 10, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2017, 9, 30))
        }
    }

    @Test
    fun generate_periods_for_two_year() {
        val clock = Clock.fixed(LocalDate(2019, 2, 21))

        val periods = YearlyPeriodGeneratorFactory.financialOct(clock).generatePeriods(-2, 0)

        assertThat(periods.size).isEqualTo(2)

        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2016Oct", "2017Oct"))
    }

    @Test
    fun generate_period_id() {
        val generator = YearlyPeriodGeneratorFactory.financialOct(Clock.System)
        assertThat("2018Oct").isEqualTo(generator.generatePeriod(LocalDate(2019, 9, 30), 0).periodId)
        assertThat("2019Oct").isEqualTo(generator.generatePeriod(LocalDate(2019, 10, 2), 0).periodId)
    }

    @Test
    fun generate_period_id_with_offset() {
        val generator = YearlyPeriodGeneratorFactory.financialOct(Clock.System)
        assertThat("2019Oct").isEqualTo(generator.generatePeriod(LocalDate(2019, 9, 30), 1).periodId)
        assertThat("2018Oct").isEqualTo(generator.generatePeriod(LocalDate(2019, 10, 2), -1).periodId)
    }

    @Test
    fun generate_periods_in_this_year() {
        val clock = Clock.fixed(LocalDate(2020, 9, 29))

        val generator = YearlyPeriodGeneratorFactory.financialOct(clock)
        val periods = generator.generatePeriodsInYear(0)
        assertThat(periods.size).isEqualTo(1)
        assertThat(periods.first().periodId).isEqualTo("2019Oct")
    }
}
