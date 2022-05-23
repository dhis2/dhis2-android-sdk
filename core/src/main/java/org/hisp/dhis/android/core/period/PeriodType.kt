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
package org.hisp.dhis.android.core.period

import java.lang.IllegalArgumentException
import java.util.*
import kotlin.Throws

@Suppress("MagicNumber")
enum class PeriodType(
    val defaultStartPeriods: Int,
    val defaultEndPeriods: Int,
    val pattern: String,
    val sortOrder: Int
) {
    Daily(-59, 1, "\\b(\\d{4})(\\d{2})(\\d{2})\\b", 1),
    Weekly(-12, 1, "\\b(\\d{4})W(\\d[\\d]?)\\b", 2),
    WeeklySaturday(-12, 1, "\\b(\\d{4})SatW(\\d[\\d]?)\\b", 3),
    WeeklySunday(-12, 1, "\\b(\\d{4})SunW(\\d[\\d]?)\\b", 4),
    WeeklyThursday(-12, 1, "\\b(\\d{4})ThuW(\\d[\\d]?)\\b", 5),
    WeeklyWednesday(-12, 1, "\\b(\\d{4})WedW(\\d[\\d]?)\\b", 6),
    BiWeekly(-12, 1, "\\b(\\d{4})BiW(\\d[\\d]?)\\b", 7),
    Monthly(-11, 1, "\\b(\\d{4})[-]?(\\d{2})\\b", 8),
    BiMonthly(-5, 1, "\\b(\\d{4})(\\d{2})B\\b", 9),
    Quarterly(-4, 1, "\\b(\\d{4})Q(\\d)\\b", 10),
    SixMonthly(-4, 1, "\\b(\\d{4})S(\\d)\\b", 11),
    SixMonthlyApril(-4, 1, "\\b(\\d{4})AprilS(\\d)\\b", 12),
    SixMonthlyNov(-4, 1, "\\b(\\d{4})NovS(\\d)\\b", 13),
    Yearly(-4, 1, "\\b(\\d{4})\\b", 14),
    FinancialApril(-4, 1, "\\b(\\d{4})April\\b", 15),
    FinancialJuly(-4, 1, "\\b(\\d{4})July\\b", 16),
    FinancialOct(-4, 1, "\\b(\\d{4})Oct\\b", 17),
    FinancialNov(-4, 1, "\\b(\\d{4})Nov\\b", 18);

    companion object {
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun periodTypeFromPeriodId(periodId: String): PeriodType {
            return values().find {
                periodId.matches(it.pattern.toRegex())
            } ?: throw IllegalArgumentException("The period id does not match any period type")
        }

        @JvmStatic
        fun firstDayOfTheWeek(periodType: PeriodType?): Int {
            return when (periodType) {
                WeeklySunday -> Calendar.SUNDAY
                WeeklyWednesday -> Calendar.WEDNESDAY
                WeeklyThursday -> Calendar.THURSDAY
                WeeklySaturday -> Calendar.SATURDAY
                else -> Calendar.MONDAY
            }
        }
    }
}
