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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl
import org.junit.Before
import org.junit.Test

class DateFilterPeriodHelperShould {

    private lateinit var dateFilterPeriodHelper: DateFilterPeriodHelper

    @Before
    fun setUp() {
        val calendarProvider = CalendarProviderFactory.createFixed()

        dateFilterPeriodHelper =
            DateFilterPeriodHelper(calendarProvider, ParentPeriodGeneratorImpl.create(calendarProvider))
    }

    @Test
    fun should_return_absolute_start_date() {
        val filter = DateFilterPeriod.builder()
            .type(DatePeriodType.ABSOLUTE)
            .startDate(DateUtils.DATE_FORMAT.parse("2020-01-20T00:00:00.000"))
            .build()

        val startDate = dateFilterPeriodHelper.getStartDate(filter)

        assertThat(startDate).isEqualTo(filter.startDate())
    }

    @Test
    fun should_return_buffered_start_date() {
        val filter = DateFilterPeriod.builder()
            .type(DatePeriodType.RELATIVE)
            .startBuffer(-5)
            .build()

        val startDate = dateFilterPeriodHelper.getStartDate(filter)

        assertThat(startDate).isEqualTo(DateUtils.DATE_FORMAT.parse("2019-12-5T10:30:00.000"))
    }

    @Test
    fun should_return_absolute_end_date() {
        val filter = DateFilterPeriod.builder()
            .type(DatePeriodType.ABSOLUTE)
            .endDate(DateUtils.DATE_FORMAT.parse("2020-01-20T00:00:00.000"))
            .build()

        val endDate = dateFilterPeriodHelper.getEndDate(filter)

        assertThat(endDate).isEqualTo(filter.endDate())
    }

    @Test
    fun should_return_buffered_end_date() {
        val filter = DateFilterPeriod.builder()
            .type(DatePeriodType.RELATIVE)
            .endBuffer(-2)
            .build()

        val endDate = dateFilterPeriodHelper.getEndDate(filter)

        assertThat(endDate).isEqualTo(DateUtils.DATE_FORMAT.parse("2019-12-8T10:30:00.000"))
    }

    @Test
    fun should_return_relative_date() {
        val filter = DateFilterPeriod.builder()
            .type(DatePeriodType.RELATIVE)
            .period(RelativePeriod.LAST_3_DAYS)
            .build()

        val startDate = dateFilterPeriodHelper.getStartDate(filter)
        val endDate = dateFilterPeriodHelper.getEndDate(filter)

        assertThat(startDate).isEqualTo(DateUtils.DATE_FORMAT.parse("2019-12-7T00:00:00.000"))
        assertThat(endDate).isEqualTo(DateUtils.DATE_FORMAT.parse("2019-12-9T23:59:59.999"))
    }
}
