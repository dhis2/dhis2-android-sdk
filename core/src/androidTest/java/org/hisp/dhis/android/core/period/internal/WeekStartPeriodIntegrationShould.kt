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
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toKtxInstant
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.settings.SystemSettingCollectionRepository
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

/**
 * Integration test to verify that weekly periods are generated correctly
 * based on the analyticsWeekStart system setting.
 */
class WeekStartPeriodIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val systemSettingRepository: SystemSettingCollectionRepository = d2.settingModule().systemSetting()
    private val relativePeriodHelper: RelativePeriodHelper = koin.get()
    private val parentPeriodGenerator: ParentPeriodGenerator = koin.get()

    @Test
    fun use_week_start_from_system_setting() = runTest {
        // Verify that the system setting is loaded (from system_settings.json in test resources)
        val setting = systemSettingRepository.analyticsWeeklyStart().blockingGet()
        assertThat(setting).isNotNull()
        assertThat(setting?.value()).isEqualTo("WEEKLY_FRIDAY")

        // Verify that the helper returns the correct PeriodType
        val periodType = relativePeriodHelper.getWeeklyPeriodType()
        assertThat(periodType).isEqualTo(PeriodType.WeeklyFriday)
    }

    @Test
    fun generate_this_week_with_friday_start() = runTest {
        // The mock data has WEEKLY_FRIDAY configured
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.THIS_WEEK)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(1)

        // Verify the period ID contains "FriW" suffix
        val periodId = periods[0].periodId()
        assertThat(periodId).contains("FriW")

        // Verify the period type is WeeklyFriday
        assertThat(periods[0].periodType()).isEqualTo(PeriodType.WeeklyFriday)
    }

    @Test
    fun generate_last_week_with_friday_start() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_WEEK)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(1)

        // Verify the period ID contains "FriW" suffix
        val periodId = periods[0].periodId()
        assertThat(periodId).contains("FriW")

        // Verify the period type is WeeklyFriday
        assertThat(periods[0].periodType()).isEqualTo(PeriodType.WeeklyFriday)
    }

    @Test
    fun generate_last_4_weeks_with_friday_start() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_4_WEEKS)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(4)

        // Verify all periods have "FriW" suffix
        periods.forEach { period ->
            assertThat(period.periodId()).contains("FriW")
            assertThat(period.periodType()).isEqualTo(PeriodType.WeeklyFriday)
        }
    }

    @Test
    fun generate_last_12_weeks_with_friday_start() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_12_WEEKS)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(12)

        // Verify all periods have "FriW" suffix
        periods.forEach { period ->
            assertThat(period.periodId()).contains("FriW")
            assertThat(period.periodType()).isEqualTo(PeriodType.WeeklyFriday)
        }
    }

    @Test
    fun generate_last_52_weeks_with_friday_start() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_52_WEEKS)

        assertThat(periods).isNotEmpty()
        assertThat(periods.size).isEqualTo(52)

        // Verify all periods have "FriW" suffix
        periods.forEach { period ->
            assertThat(period.periodId()).contains("FriW")
            assertThat(period.periodType()).isEqualTo(PeriodType.WeeklyFriday)
        }
    }

    @Test
    fun verify_weekly_friday_period_starts_on_friday() = runTest {
        val periods = parentPeriodGenerator.generateRelativePeriods(RelativePeriod.THIS_WEEK)

        assertThat(periods).isNotEmpty()
        val period = periods[0]

        // For WeeklyFriday, the period should start on Friday
        val startDate = period.startDate()
        assertThat(startDate).isNotNull()

        val localDate = startDate!!.toKtxInstant().toLocalDateTime(TimeZone.currentSystemDefault())

        assertThat(localDate.dayOfWeek).isEqualTo(DayOfWeek.FRIDAY)
    }
}
