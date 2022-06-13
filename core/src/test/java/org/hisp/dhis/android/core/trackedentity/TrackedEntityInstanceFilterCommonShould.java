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

package org.hisp.dhis.android.core.trackedentity;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;

public abstract class TrackedEntityInstanceFilterCommonShould extends BaseObjectShould implements ObjectShould {

    public TrackedEntityInstanceFilterCommonShould(String jsonPath) {
        super(jsonPath);
    }

    protected void teiFilterCommonAsserts(TrackedEntityInstanceFilter trackedEntityInstanceFilter) {

        assertThat(trackedEntityInstanceFilter.lastUpdated()).isEqualTo(getDate("2019-09-27T00:19:06.590"));
        assertThat(trackedEntityInstanceFilter.created()).isEqualTo(getDate("2019-09-27T00:19:06.590"));
        assertThat(trackedEntityInstanceFilter.uid()).isEqualTo("klhzVgls081");
        assertThat(trackedEntityInstanceFilter.code()).isEqualTo("assigned_none");
        assertThat(trackedEntityInstanceFilter.name()).isEqualTo("Ongoing foci responses");
        assertThat(trackedEntityInstanceFilter.displayName()).isEqualTo("Ongoing foci responses");
        assertThat(trackedEntityInstanceFilter.description())
                .isEqualTo("Foci response assigned to someone, and the enrollment is still active");
        assertThat(trackedEntityInstanceFilter.followUp()).isFalse();
        assertThat(trackedEntityInstanceFilter.enrollmentStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(trackedEntityInstanceFilter.sortOrder()).isEqualTo(2);
        assertThat(trackedEntityInstanceFilter.program().uid()).isEqualTo("M3xtLkYBlKI");
        assertThat(trackedEntityInstanceFilter.enrollmentCreatedPeriod().periodFrom()).isEqualTo(-5);
        assertThat(trackedEntityInstanceFilter.enrollmentCreatedPeriod().periodTo()).isEqualTo(5);

        TrackedEntityInstanceEventFilter eventFilter = trackedEntityInstanceFilter.eventFilters().get(0);
        assertThat(eventFilter.programStage()).isEqualTo("uvMKOn1oWvd");
        assertThat(eventFilter.assignedUserMode()).isEqualTo(AssignedUserMode.ANY);
        assertThat(eventFilter.eventStatus()).isEqualTo(EventStatus.OVERDUE);

        assertThat(eventFilter.eventCreatedPeriod().periodFrom()).isEqualTo(-11);
        assertThat(eventFilter.eventCreatedPeriod().periodTo()).isEqualTo(11);
    }
}