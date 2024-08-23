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
package org.hisp.dhis.android.core.arch.helpers

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import java.util.Date

object DateUtils {

    @JvmField
    val DATE_FORMAT = SafeDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

    @JvmField
    val SPACE_DATE_FORMAT = SafeDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    @JvmField
    val SIMPLE_DATE_FORMAT = SafeDateFormat("yyyy-MM-dd")

    @Suppress("MagicNumber")
    fun dateWithOffset(instant: Instant, periods: Int, periodType: PeriodType): Instant {
        val instantWithOffset = when (periodType) {
            PeriodType.Daily -> instant.plus(periods, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            PeriodType.Weekly,
            PeriodType.WeeklySaturday,
            PeriodType.WeeklySunday,
            PeriodType.WeeklyThursday,
            PeriodType.WeeklyWednesday,
            -> instant.plus(periods, DateTimeUnit.WEEK, TimeZone.currentSystemDefault())

            PeriodType.BiWeekly -> instant.plus(periods * 2, DateTimeUnit.WEEK, TimeZone.currentSystemDefault())
            PeriodType.Monthly -> instant.plus(periods, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
            PeriodType.BiMonthly -> instant.plus(periods * 2, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
            PeriodType.Quarterly,
            PeriodType.QuarterlyNov,
            -> instant.plus(periods * 3, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())

            PeriodType.SixMonthly,
            PeriodType.SixMonthlyApril,
            PeriodType.SixMonthlyNov,
            -> instant.plus(periods * 6, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())

            PeriodType.Yearly,
            PeriodType.FinancialApril,
            PeriodType.FinancialJuly,
            PeriodType.FinancialOct,
            PeriodType.FinancialNov,
            -> instant.plus(periods, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
        }

        return instantWithOffset
    }

    fun getStartDate(periods: List<Period>): Date? {
        return periods.mapNotNull { it.startDate() }.minByOrNull { it.time }
    }

    fun getEndDate(periods: List<Period>): Date? {
        return periods.mapNotNull { it.endDate() }.maxByOrNull { it.time }
    }

    fun addMonths(instant: Instant, amount: Int): Instant {
        return instant.plus(amount, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
    }

    internal fun Int.zeroPrefixed(length: Int = 2): String = this.toString().padStart(length, '0')

    internal fun getCurrentTimeAndDate(): String {
        val dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val year = dateTime.year
        val month = dateTime.monthNumber.zeroPrefixed()
        val day = dateTime.dayOfMonth.zeroPrefixed()
        val hour = dateTime.hour.zeroPrefixed()
        val minute = dateTime.minute.zeroPrefixed()
        val seconds = dateTime.second.zeroPrefixed()

        return "$year$month$day-$hour$minute$seconds"
    }

    internal fun LocalDate.atStartOfDayInSystem(): Instant {
        return this.atStartOfDayIn(TimeZone.currentSystemDefault())
    }

    internal fun Date.toKtxInstant(): Instant {
        return Instant.fromEpochMilliseconds(this.time)
    }

    internal fun Instant.toJavaDate(): Date {
        return Date(this.toEpochMilliseconds())
    }
}
