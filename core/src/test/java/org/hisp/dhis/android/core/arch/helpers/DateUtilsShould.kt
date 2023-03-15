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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.period.PeriodType
import org.junit.Test

class DateUtilsShould {

    @Test
    fun dateWithOffset_should_add_offset() {
        val refDate = DateUtils.DATE_FORMAT.parse("2022-03-08T12:34:00.000")

        listOf(
            OffsetCase(30, PeriodType.Daily, "2022-04-07T12:34:00.000"),
            OffsetCase(-5, PeriodType.Daily, "2022-03-03T12:34:00.000"),
            OffsetCase(2, PeriodType.Weekly, "2022-03-22T12:34:00.000"),
            OffsetCase(-1, PeriodType.Weekly, "2022-03-01T12:34:00.000"),
            OffsetCase(2, PeriodType.BiWeekly, "2022-04-05T12:34:00.000"),
            OffsetCase(-1, PeriodType.BiWeekly, "2022-02-22T12:34:00.000"),
            OffsetCase(2, PeriodType.Monthly, "2022-05-08T12:34:00.000"),
            OffsetCase(-4, PeriodType.Monthly, "2021-11-08T12:34:00.000"),
            OffsetCase(1, PeriodType.BiMonthly, "2022-05-08T12:34:00.000"),
            OffsetCase(-2, PeriodType.BiMonthly, "2021-11-08T12:34:00.000"),
            OffsetCase(1, PeriodType.Quarterly, "2022-06-08T12:34:00.000"),
            OffsetCase(-2, PeriodType.Quarterly, "2021-09-08T12:34:00.000"),
            OffsetCase(1, PeriodType.SixMonthly, "2022-09-08T12:34:00.000"),
            OffsetCase(-1, PeriodType.SixMonthly, "2021-09-08T12:34:00.000"),
            OffsetCase(1, PeriodType.Yearly, "2023-03-08T12:34:00.000"),
            OffsetCase(-1, PeriodType.Yearly, "2021-03-08T12:34:00.000")
        ).forEach {
            val result = DateUtils.dateWithOffset(refDate, it.periods, it.type)
            assertThat(DateUtils.DATE_FORMAT.format(result)).isEqualTo(it.expected)
        }
    }

    data class OffsetCase(val periods: Int, val type: PeriodType, val expected: String)
}
