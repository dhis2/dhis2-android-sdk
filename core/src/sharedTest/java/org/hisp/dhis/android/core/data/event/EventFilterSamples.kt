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
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.event.EventQueryCriteria
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

internal object EventFilterSamples {
    @JvmStatic
    fun get(): EventFilter {
        return EventFilter.builder()
            .id(1L)
            .uid("event_filter_uid")
            .code("tb_events")
            .name("TB events")
            .displayName("TB events")
            .created(getDate("2019-09-27T00:19:06.590"))
            .lastUpdated(getDate("2019-09-27T00:19:06.590"))
            .program("program_uid")
            .programStage("program_stage_uid")
            .description("Simple Filter for TB events")
            .eventQueryCriteria(
                EventQueryCriteria.builder()
                    .followUp(false)
                    .organisationUnit("orgUnitUid")
                    .ouMode(OrganisationUnitMode.ACCESSIBLE)
                    .assignedUserMode(AssignedUserMode.CURRENT)
                    .order("dueDate:asc,createdDate:desc")
                    .displayColumnOrder(listOf("eventDate", "status", "assignedUser", "qrur9Dvnyt5", "oZg33kd9taw"))
                    .dataFilters(EventDataFilterSamples.eventDataFilters)
                    .events(listOf("event1Uid", "event2Uid"))
                    .eventStatus(EventStatus.ACTIVE)
                    .eventDate(
                        DateFilterPeriod.builder()
                            .startDate(getSimpleDate("2014-05-01"))
                            .endDate(getSimpleDate("2014-05-01"))
                            .type(DatePeriodType.ABSOLUTE)
                            .build()
                    )
                    .dueDate(
                        DateFilterPeriod.builder()
                            .period(RelativePeriod.LAST_2_SIXMONTHS)
                            .type(DatePeriodType.RELATIVE)
                            .build()
                    )
                    .lastUpdatedDate(
                        DateFilterPeriod.builder()
                            .startBuffer(-5)
                            .endBuffer(5)
                            .type(DatePeriodType.RELATIVE)
                            .build()
                    )
                    .completedDate(
                        DateFilterPeriod.builder()
                            .period(RelativePeriod.TODAY)
                            .type(DatePeriodType.RELATIVE)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun getDate(dateStr: String): Date? {
        return try {
            DateUtils.DATE_FORMAT.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun getSimpleDate(dateStr: String): Date? {
        return try {
            DateUtils.SIMPLE_DATE_FORMAT.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
}
