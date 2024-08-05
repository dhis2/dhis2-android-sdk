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
package org.hisp.dhis.android.core.parser.internal.service.dataitem

import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsPeriodHelper
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType

@Suppress("MagicNumber")
internal class ItemYearlyPeriodCount : ItemPeriodBase() {
    override fun evaluate(period: Period): Double {
        return when (period.periodType()!!) {
            PeriodType.Daily -> evaluateDaily(period)
            PeriodType.Weekly,
            PeriodType.WeeklyWednesday,
            PeriodType.WeeklyThursday,
            PeriodType.WeeklySaturday,
            PeriodType.WeeklySunday,
            PeriodType.BiWeekly,
            -> evaluateWeeklyOrBiWeekly(period)
            PeriodType.Monthly -> 12
            PeriodType.BiMonthly -> 6
            PeriodType.Quarterly,
            PeriodType.QuarterlyNov,
            -> 4
            PeriodType.SixMonthly,
            PeriodType.SixMonthlyApril,
            PeriodType.SixMonthlyNov,
            -> 2
            PeriodType.Yearly,
            PeriodType.FinancialApril,
            PeriodType.FinancialJuly,
            PeriodType.FinancialOct,
            PeriodType.FinancialNov,
            -> 1
        }.toDouble()
    }

    private fun evaluateDaily(period: Period): Int {
        val year = getYear(period)
        return if (year % 4 == 0) 366 else 365
    }

    private fun evaluateWeeklyOrBiWeekly(period: Period): Int {
        val year = getYear(period)
        return AnalyticsPeriodHelper.countWeeksOrBiWeeksInYear(period.periodType()!!, year)
    }

    private fun getYear(period: Period): Int {
        return period.periodId()!!.substring(0, 4).toInt()
    }
}
