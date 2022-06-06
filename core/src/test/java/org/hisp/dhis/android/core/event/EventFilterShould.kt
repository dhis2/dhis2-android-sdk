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
package org.hisp.dhis.android.core.event

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.junit.Test

class EventFilterShould : BaseObjectShould("event/event_filter.json"), ObjectShould {

    @Test
    @Suppress("LongMethod")
    override fun map_from_json_string() {
        val eventFilter: EventFilter = objectMapper.readValue(jsonStream, EventFilter::class.java)

        assertThat(eventFilter.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2019-09-27T00:19:06.590"))
        assertThat(eventFilter.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2019-09-27T00:19:06.590"))
        assertThat(eventFilter.uid()).isEqualTo("event_filter_uid")
        assertThat(eventFilter.code()).isEqualTo("tb_events")
        assertThat(eventFilter.programStage()).isEqualTo("program_stage_uid")
        assertThat(eventFilter.description()).isEqualTo("Simple Filter for TB events")
        assertThat(eventFilter.name()).isEqualTo("TB events")
        assertThat(eventFilter.displayName()).isEqualTo("TB events")

        assertThat(eventFilter.eventQueryCriteria()!!.assignedUserMode()).isEqualTo(AssignedUserMode.CURRENT)
        assertThat(eventFilter.eventQueryCriteria()!!.organisationUnit()).isEqualTo("orgUnitUid")
        assertThat(eventFilter.eventQueryCriteria()!!.eventStatus()).isEqualTo(EventStatus.ACTIVE)
        assertThat(eventFilter.eventQueryCriteria()!!.order()).isEqualTo("dueDate:asc,createdDate:desc")
        assertThat(eventFilter.eventQueryCriteria()!!.followUp()).isFalse()
        assertThat(eventFilter.eventQueryCriteria()!!.ouMode()).isEqualTo(OrganisationUnitMode.ACCESSIBLE)

        assertThat(
            eventFilter.eventQueryCriteria()!!.eventDate()!!.startDate()
        ).isEqualTo(DateUtils.SIMPLE_DATE_FORMAT.parse("2014-05-01"))
        assertThat(
            eventFilter.eventQueryCriteria()!!.eventDate()!!.endDate()
        ).isEqualTo(DateUtils.SIMPLE_DATE_FORMAT.parse("2014-05-01"))
        assertThat(eventFilter.eventQueryCriteria()!!.eventDate()!!.type()).isEqualTo(DatePeriodType.ABSOLUTE)

        assertThat(eventFilter.eventQueryCriteria()!!.dueDate()!!.period()).isEqualTo(RelativePeriod.LAST_2_SIXMONTHS)
        assertThat(eventFilter.eventQueryCriteria()!!.dueDate()!!.type()).isEqualTo(DatePeriodType.RELATIVE)

        assertThat(eventFilter.eventQueryCriteria()!!.lastUpdatedDate()!!.startBuffer()).isEqualTo(-5)
        assertThat(eventFilter.eventQueryCriteria()!!.lastUpdatedDate()!!.endBuffer()).isEqualTo(5)
        assertThat(eventFilter.eventQueryCriteria()!!.lastUpdatedDate()!!.type()).isEqualTo(DatePeriodType.RELATIVE)

        assertThat(eventFilter.eventQueryCriteria()!!.completedDate()!!.period()).isEqualTo(RelativePeriod.TODAY)
        assertThat(eventFilter.eventQueryCriteria()!!.completedDate()!!.type()).isEqualTo(DatePeriodType.RELATIVE)

        assertThat(eventFilter.eventQueryCriteria()!!.displayColumnOrder()).isEqualTo(
            listOf(
                "eventDate",
                "status",
                "assignedUser",
                "qrur9Dvnyt5",
                "oZg33kd9taw"
            )
        )
        assertThat(eventFilter.eventQueryCriteria()!!.events()).isEqualTo(listOf("event1Uid", "event2Uid"))

        val dateFilter1 = eventFilter.eventQueryCriteria()!!.dataFilters()!![0]!!
        assertThat(dateFilter1.dataItem()).isEqualTo("abcDataElementUid")
        assertThat(dateFilter1.le()).isEqualTo("20")
        assertThat(dateFilter1.ge()).isEqualTo("10")
        assertThat(dateFilter1.lt()).isEqualTo("20")
        assertThat(dateFilter1.gt()).isEqualTo("10")
        assertThat(dateFilter1.`in`()).isEqualTo(setOf("India", "Norway"))
        assertThat(dateFilter1.like()).isEqualTo("abc")

        val dateFilter2 = eventFilter.eventQueryCriteria()!!.dataFilters()!![1]!!
        assertThat(dateFilter2.dataItem()).isEqualTo("dateDataElementUid")
        assertThat(dateFilter2.dateFilter()!!.startDate()).isEqualTo(DateUtils.SIMPLE_DATE_FORMAT.parse("2014-05-01"))
        assertThat(dateFilter2.dateFilter()!!.endDate()).isEqualTo(DateUtils.SIMPLE_DATE_FORMAT.parse("2019-03-20"))
        assertThat(dateFilter2.dateFilter()!!.type()).isEqualTo(DatePeriodType.ABSOLUTE)

        val dateFilter3 = eventFilter.eventQueryCriteria()!!.dataFilters()!![2]!!
        assertThat(dateFilter3.dataItem()).isEqualTo("anotherDateDataElementUid")
        assertThat(dateFilter3.dateFilter()!!.startBuffer()).isEqualTo(-5)
        assertThat(dateFilter3.dateFilter()!!.endBuffer()).isEqualTo(5)
        assertThat(dateFilter3.dateFilter()!!.type()).isEqualTo(DatePeriodType.RELATIVE)

        val dateFilter4 = eventFilter.eventQueryCriteria()!!.dataFilters()!![3]!!
        assertThat(dateFilter4.dataItem()).isEqualTo("yetAnotherDateDataElementUid")
        assertThat(dateFilter4.dateFilter()!!.period()).isEqualTo(RelativePeriod.LAST_WEEK)
        assertThat(dateFilter4.dateFilter()!!.type()).isEqualTo(DatePeriodType.RELATIVE)
    }
}
