/*
 *  Copyright (c) 2004-2024, University of Oslo
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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.settings.SystemSettingCollectionRepository
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

/**
 * Integration test to verify that financial year periods are generated correctly
 * based on the analyticsFinancialYearStart system setting.
 */
class FinancialYearPeriodIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val systemSettingRepository: SystemSettingCollectionRepository = d2.settingModule().systemSetting()
    private val relativePeriodHelper: RelativePeriodHelper = koin.get()
    private val parentPeriodGenerator: ParentPeriodGenerator = koin.get()

    @Test
    fun use_financial_year_from_system_setting() = runTest {
        // Verify that the system setting is loaded (from system_settings.json in test resources)
        val setting = systemSettingRepository.analyticsFinancialYearStart().blockingGet()
        assertThat(setting).isNotNull()
        assertThat(setting?.value()).isEqualTo("FINANCIAL_YEAR_JULY")

        // Verify that the helper returns the correct PeriodType
        val periodType = relativePeriodHelper.getFinancialYearPeriodType()
        assertThat(periodType).isEqualTo(PeriodType.FinancialJuly)
    }

    @Test
    fun generate_this_financial_year_with_july_start() = runTest {
        // The mock data has FINANCIAL_YEAR_JULY configured
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(1)

        // Verify the period ID contains "July" suffix
        val periodId = periods[0].periodId()
        assertThat(periodId).contains("July")

        // Verify the period type is FinancialJuly
        assertThat(periods[0].periodType()).isEqualTo(PeriodType.FinancialJuly)
    }

    @Test
    fun generate_last_financial_year_with_july_start() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_FINANCIAL_YEAR)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(1)

        // Verify the period ID contains "July" suffix
        val periodId = periods[0].periodId()
        assertThat(periodId).contains("July")

        // Verify the period type is FinancialJuly
        assertThat(periods[0].periodType()).isEqualTo(PeriodType.FinancialJuly)
    }

    @Test
    fun generate_last_5_financial_years_with_july_start() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_5_FINANCIAL_YEARS)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(5)

        // Verify all periods have "July" suffix
        periods.forEach { period ->
            assertThat(period.periodId()).contains("July")
            assertThat(period.periodType()).isEqualTo(PeriodType.FinancialJuly)
        }
    }

    @Test
    fun generate_last_10_financial_years_with_july_start() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_10_FINANCIAL_YEARS)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(10)

        // Verify all periods have "July" suffix
        periods.forEach { period ->
            assertThat(period.periodId()).contains("July")
            assertThat(period.periodType()).isEqualTo(PeriodType.FinancialJuly)
        }
    }

    @Test
    fun verify_financial_july_period_starts_in_july() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.THIS_FINANCIAL_YEAR)

        assertThat(periods).isNotEmpty()
        val period = periods[0]

        // For FinancialJuly, the period should start in July (month 7)
        val startDate = period.startDate()
        assertThat(startDate).isNotNull()

        val calendar = java.util.Calendar.getInstance()
        calendar.time = startDate
        val startMonth = calendar.get(java.util.Calendar.MONTH) + 1 // Calendar.MONTH is 0-based

        assertThat(startMonth).isEqualTo(7) // July
    }
}
