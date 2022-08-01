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

package org.hisp.dhis.android.core.data.enrollment;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;

import java.text.ParseException;
import java.util.Date;

public class EnrollmentSamples {


    public static Enrollment get() {
        return get("enrollment_uid", "organisation_unit", "program",
                "tracked_entity_instance", getDate("2014-08-20T12:28:56.409"));
    }

    public static Enrollment get(String uid, String organisationUnit, String program, String tei, Date date) {
        return Enrollment.builder()
                .id(1L)
                .uid(uid)
                .created(getDate("2014-08-20T12:28:56.409"))
                .lastUpdated(getDate("2015-10-14T13:36:53.063"))
                .createdAtClient(getDate("2014-10-14T13:36:53.063"))
                .lastUpdatedAtClient(getDate("2014-11-11T10:10:50.123"))
                .organisationUnit(organisationUnit)
                .program(program)
                .enrollmentDate(date)
                .incidentDate(date)
                .completedDate(date)
                .followUp(Boolean.FALSE)
                .status(EnrollmentStatus.ACTIVE)
                .trackedEntityInstance(tei)
                .geometry(Geometry.builder()
                        .type(FeatureType.POINT)
                        .coordinates("[21.21, 23.23]").build())
                .syncState(State.TO_POST)
                .aggregatedSyncState(State.TO_POST)
                .deleted(false)
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