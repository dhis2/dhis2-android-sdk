/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.data.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.hisp.dhis.android.core.arch.helpers.DateUtils;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.DatePeriodType;
import org.hisp.dhis.android.core.common.RelativePeriod;
import org.hisp.dhis.android.core.event.EventDataFilter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class EventDataFilterSamples {

    public static EventDataFilter get() {
        return EventDataFilter.builder()
                .id(1L)
                .eventFilter("eventFilter")
                .dataItem("abcDataElementUid")
                .le("20")
                .ge("10")
                .gt("10")
                .lt("20")
                .eq("abc")
                .in(Sets.newHashSet("India", "Norway"))
                .like("abc")
                .dateFilter(DateFilterPeriod.builder()
                        .startDate(getSimpleDate("2014-05-01"))
                        .endDate(getSimpleDate("2019-03-20"))
                        .type(DatePeriodType.ABSOLUTE)
                        .build())
                .build();
    }

    public static EventDataFilter get1() {
        return EventDataFilter.builder()
                .dataItem("abcDataElementUid")
                .le("20")
                .ge("10")
                .gt("10")
                .lt("20")
                .in(Sets.newHashSet("India", "Norway"))
                .like("abc")
                .build();
    }

    public static EventDataFilter get2() {
        return EventDataFilter.builder()
                .dataItem("dateDataElementUid")
                .dateFilter(DateFilterPeriod.builder()
                        .startDate(getSimpleDate("2014-05-01"))
                        .endDate(getSimpleDate("2019-03-20"))
                        .type(DatePeriodType.ABSOLUTE)
                        .build())
                .build();
    }

    public static EventDataFilter get3() {
        return EventDataFilter.builder()
                .dataItem("anotherDateDataElementUid")
                .dateFilter(DateFilterPeriod.builder()
                        .startBuffer(-5)
                        .endBuffer(5)
                        .type(DatePeriodType.RELATIVE)
                        .build())
                .build();
    }

    public static EventDataFilter get4() {
        return EventDataFilter.builder()
                .dataItem("yetAnotherDateDataElementUid")
                .dateFilter(DateFilterPeriod.builder()
                        .period(RelativePeriod.LAST_WEEK)
                        .type(DatePeriodType.RELATIVE)
                        .build())
                .build();
    }

    public static List<EventDataFilter> getEventDataFilters() {
        return Lists.newArrayList(get1(), get2(), get3(), get4());
    }

    private static Date getSimpleDate(String dateStr) {
        try {
            return DateUtils.SIMPLE_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}