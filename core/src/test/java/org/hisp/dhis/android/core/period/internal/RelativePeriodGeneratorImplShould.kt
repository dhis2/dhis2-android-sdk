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
import org.hisp.dhis.android.core.common.RelativePeriod
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RelativePeriodGeneratorImplShould {

    private lateinit var periodGenerator: ParentPeriodGeneratorImpl

    @Before
    fun setUp() {
        periodGenerator = ParentPeriodGeneratorImpl.create(CalendarProviderFactory.createFixed())
    }

    @Test
    @Suppress("ComplexMethod")
    fun `Should create relative periods for all period types`() {
        RelativePeriod.values().forEach { relativePeriod ->
            val periods = periodGenerator.generateRelativePeriods(relativePeriod)

            when (relativePeriod) {
                RelativePeriod.TODAY,
                RelativePeriod.YESTERDAY,
                RelativePeriod.THIS_WEEK,
                RelativePeriod.THIS_BIWEEK,
                RelativePeriod.THIS_MONTH,
                RelativePeriod.THIS_BIMONTH,
                RelativePeriod.THIS_SIX_MONTH,
                RelativePeriod.THIS_QUARTER,
                RelativePeriod.THIS_YEAR,
                RelativePeriod.THIS_FINANCIAL_YEAR,
                RelativePeriod.LAST_MONTH,
                RelativePeriod.LAST_WEEK,
                RelativePeriod.LAST_BIWEEK,
                RelativePeriod.LAST_BIMONTH,
                RelativePeriod.LAST_SIX_MONTH,
                RelativePeriod.LAST_QUARTER,
                RelativePeriod.LAST_YEAR,
                RelativePeriod.LAST_FINANCIAL_YEAR -> assertThat(periods.size).isEqualTo(1)

                RelativePeriod.LAST_2_SIXMONTHS -> assertThat(periods.size).isEqualTo(2)

                RelativePeriod.LAST_3_DAYS,
                RelativePeriod.LAST_3_MONTHS -> assertThat(periods.size).isEqualTo(3)

                RelativePeriod.LAST_4_WEEKS,
                RelativePeriod.LAST_4_BIWEEKS,
                RelativePeriod.LAST_4_QUARTERS -> assertThat(periods.size).isEqualTo(4)

                RelativePeriod.LAST_5_YEARS,
                RelativePeriod.LAST_5_FINANCIAL_YEARS -> assertThat(periods.size).isEqualTo(5)

                RelativePeriod.LAST_10_YEARS,
                RelativePeriod.LAST_10_FINANCIAL_YEARS -> assertThat(periods.size).isEqualTo(10)

                RelativePeriod.LAST_6_MONTHS,
                RelativePeriod.LAST_6_BIMONTHS -> assertThat(periods.size).isEqualTo(6)

                RelativePeriod.LAST_7_DAYS -> assertThat(periods.size).isEqualTo(7)
                RelativePeriod.LAST_14_DAYS -> assertThat(periods.size).isEqualTo(14)
                RelativePeriod.LAST_30_DAYS -> assertThat(periods.size).isEqualTo(30)
                RelativePeriod.LAST_60_DAYS -> assertThat(periods.size).isEqualTo(60)
                RelativePeriod.LAST_90_DAYS -> assertThat(periods.size).isEqualTo(90)
                RelativePeriod.LAST_180_DAYS -> assertThat(periods.size).isEqualTo(180)

                RelativePeriod.LAST_12_WEEKS,
                RelativePeriod.LAST_12_MONTHS -> assertThat(periods.size).isEqualTo(12)

                RelativePeriod.LAST_52_WEEKS,
                RelativePeriod.WEEKS_THIS_YEAR -> assertThat(periods.size).isEqualTo(52)

                RelativePeriod.MONTHS_THIS_YEAR,
                RelativePeriod.MONTHS_LAST_YEAR -> assertThat(periods.size).isEqualTo(12)

                RelativePeriod.BIMONTHS_THIS_YEAR -> assertThat(periods.size).isEqualTo(6)

                RelativePeriod.QUARTERS_THIS_YEAR,
                RelativePeriod.QUARTERS_LAST_YEAR -> assertThat(periods.size).isEqualTo(4)
            }
        }
    }
}
