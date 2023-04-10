/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.periodgenerator.generator.bytype

import kotlinx.datetime.*
import org.hisp.dhis.periodgenerator.period.Period
import org.hisp.dhis.periodgenerator.period.PeriodType

internal abstract class AbstractPeriodGenerator(
    private val periodType: PeriodType
) : TypeBasedPeriodGenerator {

    override fun generatePeriod(periodId: String): Period {
        TODO("Not yet implemented")
    }

    override fun generatePeriod(date: LocalDate, periodOffset: Int): Period {
        val startDate = startOfCurrentPeriod(date, periodOffset)
        return Period("periodId", periodType, startDate, startDate.plus(periodType.datePeriod()))
    }

    override fun generatePeriods(start: Int, end: Int): List<Period> {
        TODO("Not yet implemented")
    }

    override fun generatePeriodsInYear(yearOffset: Int): List<Period> {
        TODO("Not yet implemented")
    }

    protected abstract fun startOfCurrentPeriod(date: LocalDate, periodOffset: Int): LocalDate

    protected abstract fun moveToStartOfCurrentPeriod()

    protected abstract fun moveToStartOfCurrentYear()

    protected abstract fun movePeriods(number: Int)

    protected abstract fun generateId(date: LocalDate): String

/*    private val initialCalendar: Calendar
    protected var calendar: Calendar
    val idFormatter: SimpleDateFormat
    protected val periodType: PeriodType

    init {
        initialCalendar = calendar.clone() as Calendar
        this.calendar = calendar.clone() as Calendar
        idFormatter = SimpleDateFormat(dateFormatStr, Locale.US)
        this.periodType = periodType
    }

    override fun generatePeriods(start: Int, end: Int): List<Period> {
        calendar = initialCalendar.clone() as Calendar
        if (start >= end) {
            return emptyList()
        }
        val periods: MutableList<Period> = ArrayList()
        setCalendarToStartTimeOfADay(calendar)
        moveToStartOfCurrentPeriod()
        moveToTheFirstPeriod(start)
        for (i in 0 until end - start) {
            val period = generatePeriod(calendar.time, 0)
            periods.add(period)
            calendar.time = period.startDate()
            movePeriods(1)
        }
        return periods
    }

    override fun generatePeriod(date: Date, periodOffset: Int): Period {
        calendar = initialCalendar.clone() as Calendar
        moveToStartOfThePeriodOfADayWithOffset(date, periodOffset)
        val startDate = calendar.time
        val periodId = generateId()
        movePeriods(1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endDate = calendar.time
        moveToStartOfThePeriodOfADayWithOffset(date, periodOffset)
        return Period(periodId, periodType, startDate, endDate)
    }

    override fun generatePeriodsInYear(yearOffset: Int): List<Period> {
        calendar = initialCalendar.clone() as Calendar
        val targetYear = calendar[Calendar.YEAR] + yearOffset
        calendar[Calendar.YEAR] = targetYear
        setCalendarToStartTimeOfADay(calendar)
        moveToStartOfCurrentYear()
        val periods: MutableList<Period> = ArrayList()
        var period: Period
        while (true) {
            period = generatePeriod(calendar.time, 0)
            if (period.periodId() != null &&
                period.periodId()!!.startsWith(Integer.toString(targetYear))
            ) {
                periods.add(period)
                movePeriods(1)
            } else {
                break
            }
        }
        return periods
    }

    private fun moveToTheFirstPeriod(start: Int) {
        var periods = 0
        while (periods < Math.abs(start)) {
            val period = generatePeriod(calendar.time, 0)
            if (start > 0) {
                calendar.time = period.startDate()
                movePeriods(1)
            } else {
                calendar.time = period.startDate()
                calendar.add(Calendar.MILLISECOND, -1)
            }
            periods++
        }
    }

    private fun moveToStartOfThePeriodOfADayWithOffset(date: Date, periodOffset: Int) {
        calendar.time = date
        setCalendarToStartTimeOfADay(calendar)
        moveToStartOfCurrentPeriod()
        movePeriods(periodOffset)
    }

    protected abstract fun moveToStartOfCurrentPeriod()
    protected abstract fun movePeriods(number: Int)
    protected fun generateId(): String {
        return idFormatter.format(calendar.time)
    }

    protected abstract fun moveToStartOfCurrentYear()

    companion object {
        fun setCalendarToStartTimeOfADay(calendar: Calendar) {
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }*/
}