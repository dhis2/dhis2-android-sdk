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
import org.hisp.dhis.android.core.period.generator.internal.NMonthlyPeriodGeneratorFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class QuarterPeriodGeneratorShould {

    @Test
    fun generate_last_period_forQ1() {
        val clock = Clock.fixed(LocalDate(2018, 4, 21))

        val periods = NMonthlyPeriodGeneratorFactory.quarter(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018Q1")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 1, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 3, 31))
        }
    }

    @Test
    fun generate_last_period_forQ2() {
        val clock = Clock.fixed(LocalDate(2018, 9, 11))

        val periods = NMonthlyPeriodGeneratorFactory.quarter(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018Q2")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 4, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 6, 30))
        }
    }

    @Test
    fun generate_last_period_forQ3() {
        val clock = Clock.fixed(LocalDate(2018, 11, 3))

        val periods = NMonthlyPeriodGeneratorFactory.quarter(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018Q3")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 7, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 9, 30))
        }
    }

    @Test
    fun generate_last_period_forQ4() {
        val clock = Clock.fixed(LocalDate(2019, 2, 3))

        val periods = NMonthlyPeriodGeneratorFactory.quarter(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018Q4")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 10, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 12, 31))
        }
    }

    @Test
    fun generate_starting_period_on_first_day() {
        val clock = Clock.fixed(LocalDate(2018, 4, 1))

        val periods = NMonthlyPeriodGeneratorFactory.quarter(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018Q1")
        }
    }

    @Test
    fun generate_ending_period_on_last_day() {
        val clock = Clock.fixed(LocalDate(2018, 6, 30))

        val periods = NMonthlyPeriodGeneratorFactory.quarter(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018Q1")
        }
    }

    @Test
    fun generate_last_two_periods() {
        val clock = Clock.fixed(LocalDate(2018, 5, 21))

        val periods = NMonthlyPeriodGeneratorFactory.quarter(clock).generatePeriods(-2, 0)

        assertThat(periods.size).isEqualTo(2)
        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2017Q4", "2018Q1"))
    }

    @Test
    fun generate_period_id() {
        val generator = NMonthlyPeriodGeneratorFactory.quarter(Clock.System)
        assertThat("2019Q2").isEqualTo(generator.generatePeriod(LocalDate(2019, 6, 30), 0).periodId)
        assertThat("2019Q3").isEqualTo(generator.generatePeriod(LocalDate(2019, 7, 1), 0).periodId)
    }

    @Test
    fun generate_period_id_with_offset() {
        val generator = NMonthlyPeriodGeneratorFactory.quarter(Clock.System)
        assertThat("2019Q3").isEqualTo(generator.generatePeriod(LocalDate(2019, 6, 30), 1).periodId)
        assertThat("2019Q2").isEqualTo(generator.generatePeriod(LocalDate(2019, 7, 1), -1).periodId)
    }

    @Test
    fun generate_periods_in_this_year() {
        val clock = Clock.fixed(LocalDate(2019, 8, 29))
        val generator = NMonthlyPeriodGeneratorFactory.quarter(clock)

        val periods = generator.generatePeriodsInYear(0)

        assertThat(periods.size).isEqualTo(4)
        assertThat(periods.first().periodId).isEqualTo("2019Q1")
        assertThat(periods.last().periodId).isEqualTo("2019Q4")
    }

    @Test
    fun generate_periods_in_last_year() {
        val clock = Clock.fixed(LocalDate(2019, 8, 29))
        val generator = NMonthlyPeriodGeneratorFactory.quarter(clock)

        val periods = generator.generatePeriodsInYear(-1)

        assertThat(periods.size).isEqualTo(4)
        assertThat(periods.first().periodId).isEqualTo("2018Q1")
        assertThat(periods.last().periodId).isEqualTo("2018Q4")
    }
}
