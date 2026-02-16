/*
 *  Copyright (c) 2004-2026, University of Oslo
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
import kotlinx.datetime.LocalDateTime
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.clock.internal.FixedClockProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FinancialYearPeriodHelperShould {

    private val fixedDate = LocalDateTime(2019, 12, 10, 0, 0)
    private val clockProvider = FixedClockProvider(fixedDate)

    @Test
    fun `Should use FinancialApril when setting is FINANCIAL_YEAR_APRIL`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialApril)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods.size).isEqualTo(1)
        assertThat(periods[0].periodId()).isEqualTo("2019April")
    }

    @Test
    fun `Should use FinancialJuly when setting is FINANCIAL_YEAR_JULY`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialJuly)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods.size).isEqualTo(1)
        assertThat(periods[0].periodId()).isEqualTo("2019July")
    }

    @Test
    fun `Should use FinancialOct when setting is FINANCIAL_YEAR_OCTOBER`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialOct)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods.size).isEqualTo(1)
        assertThat(periods[0].periodId()).isEqualTo("2019Oct")
    }

    @Test
    fun `Should use FinancialNov when setting is FINANCIAL_YEAR_NOVEMBER`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialNov)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods.size).isEqualTo(1)
        assertThat(periods[0].periodId()).isEqualTo("2020Nov")
    }

    @Test
    fun `Should use FinancialFeb when setting is FINANCIAL_YEAR_FEBRUARY`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialFeb)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods.size).isEqualTo(1)
        assertThat(periods[0].periodId()).isEqualTo("2019Feb")
    }

    @Test
    fun `Should use FinancialAug when setting is FINANCIAL_YEAR_AUGUST`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialAug)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods.size).isEqualTo(1)
        assertThat(periods[0].periodId()).isEqualTo("2019Aug")
    }

    @Test
    fun `Should use FinancialSep when setting is FINANCIAL_YEAR_SEPTEMBER`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialSep)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods.size).isEqualTo(1)
        assertThat(periods[0].periodId()).isEqualTo("2019Sep")
    }

    @Test
    fun `Should generate correct LAST_FINANCIAL_YEAR with different settings`() {
        val financialYearHelperOct = FinancialYearPeriodHelperMock(PeriodType.FinancialOct)
        val periodGeneratorOct = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelperOct)

        val periodsOct = periodGeneratorOct.generateRelativePeriods(RelativePeriod.LAST_FINANCIAL_YEAR)

        assertThat(periodsOct.size).isEqualTo(1)
        assertThat(periodsOct[0].periodId()).isEqualTo("2018Oct")
    }

    @Test
    fun `Should generate correct LAST_5_FINANCIAL_YEARS with different settings`() {
        val financialYearHelper = FinancialYearPeriodHelperMock(PeriodType.FinancialJuly)
        val periodGenerator = ParentPeriodGeneratorImpl.create(clockProvider, financialYearHelper)

        val periods = periodGenerator.generateRelativePeriods(RelativePeriod.LAST_5_FINANCIAL_YEARS)

        assertThat(periods.size).isEqualTo(5)
        assertThat(periods[0].periodId()).isEqualTo("2014July")
        assertThat(periods[4].periodId()).isEqualTo("2018July")
    }
}
