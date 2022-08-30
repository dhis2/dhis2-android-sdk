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
package org.hisp.dhis.android.core.period.internal

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.period.DatePeriod
import org.hisp.dhis.android.core.period.internal.InPeriodQueryHelper.buildInPeriodsQuery
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class InPeriodQueryHelperShould {

    @Test
    @Throws(Exception::class)
    fun generate_one_requested_period() {
        val formatter = DateUtils.DATE_FORMAT

        val date1 = formatter.parse("2001-12-24T12:24:25.203")
        val date2 = formatter.parse("2002-12-24T12:24:25.203")
        val date3 = formatter.parse("2003-12-24T12:24:25.203")
        val date4 = formatter.parse("2004-12-24T12:24:25.203")
        val date5 = formatter.parse("2005-12-24T12:24:25.203")
        val date6 = formatter.parse("2006-12-24T12:24:25.203")

        val datePeriods: List<DatePeriod> = listOf(
            DatePeriod.create(date1, date2),
            DatePeriod.create(date3, date4),
            DatePeriod.create(date5, date6)
        )

        val inPeriodQuery = buildInPeriodsQuery("COL1", datePeriods, formatter)
        Truth.assertThat(inPeriodQuery).isEqualTo(
            "(COL1 >= '2001-12-24T12:24:25.203' AND COL1 <= '2002-12-24T12:24:25.203') OR " +
                "(COL1 >= '2003-12-24T12:24:25.203' AND COL1 <= '2004-12-24T12:24:25.203') OR " +
                "(COL1 >= '2005-12-24T12:24:25.203' AND COL1 <= '2006-12-24T12:24:25.203')"
        )
    }

    @Test
    @Throws(Exception::class)
    fun generate_one_requested_period_simple_date() {
        val formatter = DateUtils.SIMPLE_DATE_FORMAT

        val date1 = formatter.parse("2001-12-24T12:24:25.203")
        val date2 = formatter.parse("2002-12-24T12:24:25.203")
        val date3 = formatter.parse("2003-12-24T12:24:25.203")
        val date4 = formatter.parse("2004-12-24T12:24:25.203")

        val datePeriods: List<DatePeriod> = listOf(
            DatePeriod.create(date1, date2),
            DatePeriod.create(date3, date4)
        )

        val inPeriodQuery = buildInPeriodsQuery("date(COL1)", datePeriods, formatter)
        Truth.assertThat(inPeriodQuery).isEqualTo(
            "(date(COL1) >= '2001-12-24' AND date(COL1) <= '2002-12-24') OR " +
                "(date(COL1) >= '2003-12-24' AND date(COL1) <= '2004-12-24')"
        )
    }
}
