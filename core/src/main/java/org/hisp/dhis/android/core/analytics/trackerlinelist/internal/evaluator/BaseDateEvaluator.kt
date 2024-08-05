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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator

import org.hisp.dhis.android.core.analytics.trackerlinelist.DateFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.DateItem
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl
import org.hisp.dhis.android.core.period.internal.PeriodParser
import java.util.Date

internal abstract class BaseDateEvaluator(
    private val item: DateItem,
) : TrackerLineListEvaluator() {

    private val parentPeriodGenerator = ParentPeriodGeneratorImpl.create(ClockProviderFactory.clockProvider)
    private val periodParser = PeriodParser(CalendarProviderFactory.calendarProvider)

    fun getDateWhereClause(): String {
        return if (item.filters.isEmpty()) {
            "1"
        } else {
            return item.filters.joinToString(" OR ") { "(${getFilterWhereClause(it)})" }
        }
    }

    private fun getFilterWhereClause(filter: DateFilter): String {
        val filterHelper = FilterHelper(item.id)
        return when (filter) {
            is DateFilter.Absolute -> {
                val periodType = PeriodType.periodTypeFromPeriodId(filter.uid)
                val date = periodParser.parse(filter.uid)
                val period = parentPeriodGenerator.generatePeriod(periodType, date, 0)

                betweenDates(period.startDate()!!, period.endDate()!!)
            }

            is DateFilter.Relative -> {
                val periods = parentPeriodGenerator.generateRelativePeriods(filter.relative)

                betweenDates(periods.first().startDate()!!, periods.last().endDate()!!)
            }

            is DateFilter.Range -> {
                betweenDates(filter.startDate, filter.endDate)
            }

            is DateFilter.EqualTo -> filterHelper.equalTo(filter.timestamp, filter.ignoreCase)
            is DateFilter.NotEqualTo -> filterHelper.notEqualTo(filter.timestamp, filter.ignoreCase)
            is DateFilter.Like -> filterHelper.like(filter.timestamp, filter.ignoreCase)
            is DateFilter.NotLike -> filterHelper.notLike(filter.timestamp, filter.ignoreCase)
        }
    }

    private fun betweenDates(startDate: Date, endDate: Date): String {
        return betweenDates(
            startDate = DateUtils.DATE_FORMAT.format(startDate),
            endDate = DateUtils.DATE_FORMAT.format(endDate),
        )
    }

    private fun betweenDates(startDate: String, endDate: String): String {
        return "julianday(${item.id}) >= julianday('$startDate') AND julianday(${item.id}) <= julianday('$endDate')"
    }
}
