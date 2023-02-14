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
package org.hisp.dhis.android.core.arch.helpers

import java.util.*
import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory

object DateUtils {

    @JvmField
    val DATE_FORMAT = SafeDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

    @JvmField
    val SPACE_DATE_FORMAT = SafeDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    @JvmField
    val SIMPLE_DATE_FORMAT = SafeDateFormat("yyyy-MM-dd")

    @JvmStatic
    @Suppress("MagicNumber")
    fun dateWithOffset(date: Date, periods: Int, periodType: PeriodType): Date {
        val calendar = CalendarProviderFactory.calendarProvider.calendar.clone() as Calendar

        calendar.time = date

        when (periodType) {
            PeriodType.Daily -> calendar.add(Calendar.DATE, periods)
            PeriodType.Weekly,
            PeriodType.WeeklySaturday,
            PeriodType.WeeklySunday,
            PeriodType.WeeklyThursday,
            PeriodType.WeeklyWednesday -> calendar.add(Calendar.WEEK_OF_YEAR, periods)
            PeriodType.BiWeekly -> calendar.add(Calendar.WEEK_OF_YEAR, 2 * periods)
            PeriodType.Monthly -> calendar.add(Calendar.MONTH, periods)
            PeriodType.BiMonthly -> calendar.add(Calendar.MONTH, 2 * periods)
            PeriodType.Quarterly -> calendar.add(Calendar.MONTH, 3 * periods)
            PeriodType.SixMonthly,
            PeriodType.SixMonthlyApril,
            PeriodType.SixMonthlyNov -> calendar.add(Calendar.MONTH, 6 * periods)
            PeriodType.Yearly,
            PeriodType.FinancialApril,
            PeriodType.FinancialJuly,
            PeriodType.FinancialOct,
            PeriodType.FinancialNov -> calendar.add(Calendar.YEAR, periods)
        }

        return calendar.time
    }

    @JvmStatic
    fun getStartDate(periods: List<Period>): Date? {
        return periods.mapNotNull { it.startDate() }.minByOrNull { it.time }
    }

    @JvmStatic
    fun getEndDate(periods: List<Period>): Date? {
        return periods.mapNotNull { it.endDate() }.maxByOrNull { it.time }
    }
}
