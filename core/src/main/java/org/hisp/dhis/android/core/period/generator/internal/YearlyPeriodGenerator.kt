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
package org.hisp.dhis.android.core.period.generator.internal

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.datetime.plus
import org.hisp.dhis.android.core.period.PeriodType

internal open class YearlyPeriodGenerator(
    clock: Clock,
    periodType: PeriodType,
    private val startMonth: Month,
    private val suffix: String,
) :
    AbstractPeriodGenerator(clock, periodType) {

    override fun getStartOfPeriodFor(date: LocalDate): LocalDate {
        return getStartOfYearFor(date)
    }

    override fun getStartOfYearFor(date: LocalDate): LocalDate {
        val startYear =
            if (date.month.number < startMonth.number) {
                date.year - 1
            } else {
                date.year
            }

        return LocalDate(startYear, startMonth, 1)
    }

    override fun movePeriodForStartDate(startDate: LocalDate, offset: Int): LocalDate {
        return startDate.plus(offset, DateTimeUnit.YEAR)
    }

    override fun generateId(startDate: LocalDate): String {
        val year =
            if (periodType == PeriodType.FinancialNov) {
                startDate.year + 1
            } else {
                startDate.year
            }

        return "$year$suffix"
    }
}