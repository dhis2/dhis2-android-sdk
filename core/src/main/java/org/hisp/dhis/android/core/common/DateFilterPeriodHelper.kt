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
package org.hisp.dhis.android.core.common

import dagger.Reusable
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.CalendarProvider
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator

@Reusable
internal class DateFilterPeriodHelper @Inject constructor(
    private val calendarProvider: CalendarProvider,
    private val parentPeriodGenerator: ParentPeriodGenerator
) {

    companion object {

        @JvmStatic
        fun mergeDateFilterPeriods(baseFilter: DateFilterPeriod?, newFilter: DateFilterPeriod?): DateFilterPeriod? {
            return when {
                newFilter == null -> baseFilter
                baseFilter == null -> newFilter
                newFilter.period() != null -> newFilter
                newFilter.startBuffer() != null || newFilter.endBuffer() != null -> {
                    val builder = baseFilter.toBuilder()

                    builder.period(null)
                    builder.startDate(null)
                    builder.endDate(null)
                    builder.type(DatePeriodType.RELATIVE)

                    newFilter.startBuffer()?.let { builder.startBuffer(it) }
                    newFilter.endBuffer()?.let { builder.endBuffer(it) }

                    builder.build()
                }
                newFilter.startDate() != null || newFilter.endDate() != null -> {
                    val builder = baseFilter.toBuilder()

                    builder.period(null)
                    builder.startBuffer(null)
                    builder.endBuffer(null)
                    builder.type(DatePeriodType.ABSOLUTE)

                    newFilter.startDate()?.let { builder.startDate(it) }
                    newFilter.endDate()?.let { builder.endDate(it) }

                    builder.build()
                }
                else -> null
            }
        }

        @JvmStatic
        fun mergeEventDataFilters(list: List<EventDataFilter>, item: EventDataFilter): List<EventDataFilter> {
            return list + item
        }
    }

    fun getStartDate(filter: DateFilterPeriod): Date? {
        return when (filter.type()) {
            DatePeriodType.RELATIVE ->
                when {
                    filter.period() != null -> getPeriod(filter.period()!!)?.startDate()
                    filter.startBuffer() != null -> addDaysToCurrentDate(filter.startBuffer()!!)
                    else -> null
                }
            DatePeriodType.ABSOLUTE -> filter.startDate()
            else -> null
        }
    }

    fun getEndDate(filter: DateFilterPeriod): Date? {
        return when (filter.type()) {
            DatePeriodType.RELATIVE ->
                when {
                    filter.period() != null -> getPeriod(filter.period()!!)?.endDate()
                    filter.endBuffer() != null -> addDaysToCurrentDate(filter.endBuffer()!!)
                    else -> null
                }
            DatePeriodType.ABSOLUTE -> filter.endDate()
            else -> null
        }
    }

    private fun getPeriod(period: RelativePeriod): Period? {
        val periods = parentPeriodGenerator.generateRelativePeriods(period)

        return if (periods.isNotEmpty()) {
            Period.builder()
                .startDate(periods.first().startDate())
                .endDate(periods.last().endDate())
                .build()
        } else {
            null
        }
    }

    private fun addDaysToCurrentDate(days: Int): Date {
        val calendar = calendarProvider.calendar.clone() as Calendar
        calendar.add(Calendar.DATE, days)
        return calendar.time
    }
}
