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
package org.hisp.dhis.android.core.period.internal

import com.google.common.truth.Truth.assertThat
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PeriodGeneratorShould {

    private val calendar = Calendar.getInstance()

    private val dailyGenerator: PeriodGenerator = DailyPeriodGenerator(calendar)
    private val weeklyGenerator: PeriodGenerator = WeeklyPeriodGeneratorFactory.weekly(calendar)
    private val monthlyGenerator: PeriodGenerator = MonthlyPeriodGenerator(calendar)

    @Test
    fun generate_single_period_simultaneously() {
        val refDate = DateUtils.SIMPLE_DATE_FORMAT.parse("2019-12-30")

        val periods = (-500..500).map {
            Single.fromCallable {
                dailyGenerator.generatePeriod(refDate, it)
                weeklyGenerator.generatePeriod(refDate, it)
                monthlyGenerator.generatePeriod(refDate, it)
            }.subscribeOn(Schedulers.io())
        }

        Single.merge(periods).blockingSubscribe()

        assertThat(periods.size).isEqualTo(1001)
    }

    @Test
    fun generate_multiple_periods_simultaneously() {
        val periods = (0..100).map {
            Single.fromCallable {
                dailyGenerator.generatePeriods(it - 100, it)
                weeklyGenerator.generatePeriods(it - 100, it)
                monthlyGenerator.generatePeriods(it - 100, it)
            }.subscribeOn(Schedulers.io())
        }

        Single.merge(periods).blockingSubscribe()

        assertThat(periods.size).isEqualTo(101)
    }

    @Test
    fun generate_periods_in_year_simultaneously() {
        val periods = (-5..5).map {
            Single.fromCallable {
                dailyGenerator.generatePeriodsInYear(it)
                weeklyGenerator.generatePeriodsInYear(it)
                monthlyGenerator.generatePeriodsInYear(it)
            }.subscribeOn(Schedulers.io())
        }

        Single.merge(periods).blockingSubscribe()

        assertThat(periods.size).isEqualTo(11)
    }
}
