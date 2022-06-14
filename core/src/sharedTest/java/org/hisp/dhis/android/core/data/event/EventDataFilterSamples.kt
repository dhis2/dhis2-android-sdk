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
package org.hisp.dhis.android.core.data.event

import java.text.ParseException
import java.util.*
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.event.EventDataFilter

internal object EventDataFilterSamples {
    fun get(): EventDataFilter {
        return EventDataFilter.builder()
            .id(1L)
            .eventFilter("eventFilter")
            .dataItem("abcDataElementUid")
            .le("20")
            .ge("10")
            .gt("10")
            .lt("20")
            .eq("abc")
            .`in`(setOf("Norway", "India"))
            .like("abc")
            .dateFilter(
                DateFilterPeriod.builder()
                    .startDate(getSimpleDate("2014-05-01"))
                    .endDate(getSimpleDate("2019-03-20"))
                    .type(DatePeriodType.ABSOLUTE)
                    .build()
            )
            .build()
    }

    fun get1(): EventDataFilter {
        return EventDataFilter.builder()
            .dataItem("abcDataElementUid")
            .le("20")
            .ge("10")
            .gt("10")
            .lt("20")
            .`in`(setOf("India", "Norway"))
            .like("abc")
            .build()
    }

    fun get2(): EventDataFilter {
        return EventDataFilter.builder()
            .dataItem("dateDataElementUid")
            .dateFilter(
                DateFilterPeriod.builder()
                    .startDate(getSimpleDate("2014-05-01"))
                    .endDate(getSimpleDate("2019-03-20"))
                    .type(DatePeriodType.ABSOLUTE)
                    .build()
            )
            .build()
    }

    fun get3(): EventDataFilter {
        return EventDataFilter.builder()
            .dataItem("anotherDateDataElementUid")
            .dateFilter(
                DateFilterPeriod.builder()
                    .startBuffer(-5)
                    .endBuffer(5)
                    .type(DatePeriodType.RELATIVE)
                    .build()
            )
            .build()
    }

    fun get4(): EventDataFilter {
        return EventDataFilter.builder()
            .dataItem("yetAnotherDateDataElementUid")
            .dateFilter(
                DateFilterPeriod.builder()
                    .period(RelativePeriod.LAST_WEEK)
                    .type(DatePeriodType.RELATIVE)
                    .build()
            )
            .build()
    }

    val eventDataFilters: List<EventDataFilter>
        get() = listOf(get1(), get2(), get3(), get4())

    private fun getSimpleDate(dateStr: String): Date? {
        return try {
            DateUtils.SIMPLE_DATE_FORMAT.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
}
