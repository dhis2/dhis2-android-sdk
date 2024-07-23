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
class SixMonthlyPeriodGeneratorShould {

    @Test
    fun generate_last_period() {
        val clock = Clock.fixed(LocalDate(2018, 8, 21))

        val periods = NMonthlyPeriodGeneratorFactory.sixMonthly(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018S1")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 1, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 6, 30))
        }
    }

    @Test
    fun generate_starting_period_on_first_day_for_january() {
        val clock = Clock.fixed(LocalDate(2018, 7, 1))

        val periods = NMonthlyPeriodGeneratorFactory.sixMonthly(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2018S1")
            assertThat(period.startDate).isEqualTo(LocalDate(2018, 1, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2018, 6, 30))
        }
    }

    @Test
    fun generate_ending_period_on_last_day_for_january() {
        val clock = Clock.fixed(LocalDate(2018, 6, 30))

        val periods = NMonthlyPeriodGeneratorFactory.sixMonthly(clock).generatePeriods(-1, 0)

        assertThat(periods.size).isEqualTo(1)
        periods.first().let { period ->
            assertThat(period.periodId).isEqualTo("2017S2")
            assertThat(period.startDate).isEqualTo(LocalDate(2017, 7, 1))
            assertThat(period.endDate).isEqualTo(LocalDate(2017, 12, 31))
        }
    }

    @Test
    fun generate_last_two_periods() {
        val clock = Clock.fixed(LocalDate(2018, 8, 21))

        val periods = NMonthlyPeriodGeneratorFactory.sixMonthly(clock).generatePeriods(-2, 0)

        assertThat(periods.size).isEqualTo(2)
        assertThat(periods.map { it.periodId }).isEqualTo(listOf("2017S2", "2018S1"))
    }

    @Test
    fun generate_period_id() {
        val generator = NMonthlyPeriodGeneratorFactory.sixMonthly(Clock.System)
        assertThat("2019S1").isEqualTo(generator.generatePeriod(LocalDate(2019, 6, 30), 0).periodId)
        assertThat("2019S2").isEqualTo(generator.generatePeriod(LocalDate(2019, 7, 1), 0).periodId)
    }

    @Test
    fun generate_period_with_right_start_and_end_for_april() {
        val generator = NMonthlyPeriodGeneratorFactory.sixMonthlyApril(Clock.System)
        val p1 = generator.generatePeriod(LocalDate(2019, 9, 30), 0)
        val p2 = generator.generatePeriod(LocalDate(2019, 10, 1), 0)

        assertThat(p1.startDate).isEqualTo(LocalDate(2019, 4, 1))
        assertThat(p1.endDate).isEqualTo(LocalDate(2019, 9, 30))
        assertThat(p2.startDate).isEqualTo(LocalDate(2019, 10, 1))
        assertThat(p2.endDate).isEqualTo(LocalDate(2020, 3, 31))
    }

    @Test
    fun generate_period_with_right_start_and_end_for_november() {
        val generator = NMonthlyPeriodGeneratorFactory.sixMonthlyNov(Clock.System)
        val p1 = generator.generatePeriod(LocalDate(2019, 10, 31), 0)
        val p2 = generator.generatePeriod(LocalDate(2019, 11, 1), 0)

        assertThat(p1.startDate).isEqualTo(LocalDate(2019, 5, 1))
        assertThat(p1.endDate).isEqualTo(LocalDate(2019, 10, 31))
        assertThat(p2.startDate).isEqualTo(LocalDate(2019, 11, 1))
        assertThat(p2.endDate).isEqualTo(LocalDate(2020, 4, 30))
    }

    @Test
    fun generate_period_ids_for_april() {
        val generator = NMonthlyPeriodGeneratorFactory.sixMonthlyApril(Clock.System)

        assertThat("2018AprilS2").isEqualTo(generator.generatePeriod(LocalDate(2019, 3, 30), 0).periodId)
        assertThat("2018AprilS2").isEqualTo(generator.generatePeriod(LocalDate(2018, 12, 31), 0).periodId)
        assertThat("2018AprilS2").isEqualTo(generator.generatePeriod(LocalDate(2019, 1, 1), 0).periodId)
        assertThat("2019AprilS1").isEqualTo(generator.generatePeriod(LocalDate(2019, 4, 1), 0).periodId)
        assertThat("2019AprilS1").isEqualTo(generator.generatePeriod(LocalDate(2019, 9, 30), 0).periodId)
        assertThat("2019AprilS2").isEqualTo(generator.generatePeriod(LocalDate(2019, 10, 1), 0).periodId)
    }

    @Test
    fun generate_period_id_for_november() {
        val generator = NMonthlyPeriodGeneratorFactory.sixMonthlyNov(Clock.System)

        assertThat("2019NovS1").isEqualTo(generator.generatePeriod(LocalDate(2019, 4, 30), 0).periodId)
        assertThat("2019NovS2").isEqualTo(generator.generatePeriod(LocalDate(2019, 5, 1), 0).periodId)
        assertThat("2019NovS2").isEqualTo(generator.generatePeriod(LocalDate(2019, 10, 31), 0).periodId)
        assertThat("2020NovS1").isEqualTo(generator.generatePeriod(LocalDate(2019, 11, 1), 0).periodId)
        assertThat("2020NovS1").isEqualTo(generator.generatePeriod(LocalDate(2019, 12, 31), 0).periodId)
        assertThat("2020NovS1").isEqualTo(generator.generatePeriod(LocalDate(2020, 1, 1), 0).periodId)
    }

    @Test
    fun generate_period_ids_with_offset() {
        val aprilGenerator = NMonthlyPeriodGeneratorFactory.sixMonthlyApril(Clock.System)
        assertThat("2019AprilS1").isEqualTo(aprilGenerator.generatePeriod(LocalDate(2019, 3, 30), 1).periodId)
        assertThat("2018AprilS1").isEqualTo(aprilGenerator.generatePeriod(LocalDate(2018, 12, 31), -1).periodId)

        val novGenerator = NMonthlyPeriodGeneratorFactory.sixMonthlyNov(Clock.System)
        assertThat("2019NovS2").isEqualTo(novGenerator.generatePeriod(LocalDate(2019, 4, 30), 1).periodId)
        assertThat("2019NovS1").isEqualTo(novGenerator.generatePeriod(LocalDate(2019, 5, 1), -1).periodId)
    }

    @Test
    fun generate_periods_in_this_year() {
        val clock = Clock.fixed(LocalDate(2019, 8, 29))

        NMonthlyPeriodGeneratorFactory.sixMonthly(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(2)
            assertThat(periods[0].periodId).isEqualTo("2019S1")
            assertThat(periods[1].periodId).isEqualTo("2019S2")
        }

        NMonthlyPeriodGeneratorFactory.sixMonthlyApril(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(2)
            assertThat(periods[0].periodId).isEqualTo("2019AprilS1")
            assertThat(periods[1].periodId).isEqualTo("2019AprilS2")
        }

        NMonthlyPeriodGeneratorFactory.sixMonthlyNov(clock).generatePeriodsInYear(0).let { periods ->
            assertThat(periods.size).isEqualTo(2)
            assertThat(periods[0].periodId).isEqualTo("2019NovS1")
            assertThat(periods[1].periodId).isEqualTo("2019NovS2")
        }
    }

    @Test
    fun generate_periods_in_last_year() {
        val clock = Clock.fixed(LocalDate(2019, 8, 29))

        NMonthlyPeriodGeneratorFactory.sixMonthly(clock).generatePeriodsInYear(-1).let { periods ->
            assertThat(periods.size).isEqualTo(2)
            assertThat(periods[0].periodId).isEqualTo("2018S1")
            assertThat(periods[1].periodId).isEqualTo("2018S2")
        }

        NMonthlyPeriodGeneratorFactory.sixMonthlyApril(clock).generatePeriodsInYear(-1).let { periods ->
            assertThat(periods.size).isEqualTo(2)
            assertThat(periods[0].periodId).isEqualTo("2018AprilS1")
            assertThat(periods[1].periodId).isEqualTo("2018AprilS2")
        }

        NMonthlyPeriodGeneratorFactory.sixMonthlyNov(clock).generatePeriodsInYear(-1).let { periods ->
            assertThat(periods.size).isEqualTo(2)
            assertThat(periods[0].periodId).isEqualTo("2018NovS1")
            assertThat(periods[1].periodId).isEqualTo("2018NovS2")
        }
    }
}
