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
package org.hisp.dhis.android.core.data.trackedentity;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.DatePeriodType;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.RelativePeriod;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter;

import java.text.ParseException;
import java.util.Date;

public class TrackedEntityInstanceFilterSamples {

    public static TrackedEntityInstanceFilter get() {
        return TrackedEntityInstanceFilter.builder()
                .id(1L)
                .uid("klhzVgls081")
                .code("assigned_none")
                .name("Ongoing foci responses")
                .displayName("Ongoing foci responses")
                .created(getDate("2019-09-27T00:19:06.590"))
                .lastUpdated(getDate("2019-09-27T00:19:06.590"))
                .description("Foci response assigned to someone, and the enrollment is still active")
                .sortOrder(2)
                .program(ObjectWithUid.create("M3xtLkYBlKI"))
                .entityQueryCriteria(EntityQueryCriteria.builder()
                        .followUp(Boolean.FALSE)
                        .enrollmentStatus(EnrollmentStatus.ACTIVE)
                        .eventStatus(EventStatus.COMPLETED)
                        .programStage("uvMKOn1oWvd")
                        .enrollmentCreatedDate(DateFilterPeriod.builder()
                                .period(RelativePeriod.TODAY)
                                .startDate(getDate("2014-05-01T00:00:00.000"))
                                .startBuffer(-5)
                                .endBuffer(5)
                                .type(DatePeriodType.RELATIVE)
                                .endDate(getDate("2019-03-20T00:00:00.000"))
                                .build())
                        .enrollmentIncidentDate(DateFilterPeriod.builder()
                                .period(RelativePeriod.TODAY)
                                .startDate(getDate("2014-05-01T00:00:00.000"))
                                .startBuffer(-5)
                                .endBuffer(5)
                                .type(DatePeriodType.RELATIVE)
                                .endDate(getDate("2019-03-20T00:00:00.000"))
                                .build())
                        .ouMode(OrganisationUnitMode.SELECTED)
                        .trackedEntityType("trackedEntityTypeUid")
                        .assignedUserMode(AssignedUserMode.PROVIDED)
                        .trackedEntityInstances(Lists.newArrayList("a3kGcGDCuk7", "a3kGcGDCuk8"))
                        .displayColumnOrder(Lists.newArrayList("eventDate", "status"))
                        .order("dueDate:asc,createdDate:desc")
                        .organisationUnit("orgUnitUid")
                        .eventDate(DateFilterPeriod.builder()
                                .startBuffer(-5)
                                .endBuffer(5)
                                .type(DatePeriodType.RELATIVE)
                                .build())
                        .lastUpdatedDate(DateFilterPeriod.builder()
                                .startBuffer(-5)
                                .endBuffer(5)
                                .type(DatePeriodType.RELATIVE)
                                .build())
                        .build())
                .build();
    }

    private static Date getDate(String dateStr) {
        try {
            return BaseIdentifiableObject.DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}