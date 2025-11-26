/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.programstageworkinglist;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.List;

@AutoValue
public abstract class ProgramStageQueryCriteria implements CoreObject {

    @Nullable
    public abstract EventStatus eventStatus();

    @Nullable
    public abstract DateFilterPeriod eventCreatedAt();

    @Nullable
    public abstract DateFilterPeriod eventOccurredAt();

    @Nullable
    public abstract DateFilterPeriod eventScheduledAt();

    @Nullable
    public abstract EnrollmentStatus enrollmentStatus();

    @Nullable
    public abstract DateFilterPeriod enrolledAt();

    @Nullable
    public abstract DateFilterPeriod enrollmentOccurredAt();

    @Nullable
    public abstract String order();

    @Nullable
    public abstract List<String> displayColumnOrder();

    @Nullable
    public abstract String orgUnit();

    @Nullable
    public abstract OrganisationUnitMode ouMode();

    @Nullable
    public abstract AssignedUserMode assignedUserMode();

    @Nullable
    public abstract List<ProgramStageWorkingListEventDataFilter> dataFilters();

    @Nullable
    public abstract List<ProgramStageWorkingListAttributeValueFilter> attributeValueFilters();


    public static Builder builder() {
        return new AutoValue_ProgramStageQueryCriteria.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder eventStatus(EventStatus eventStatus);

        public abstract Builder eventCreatedAt(DateFilterPeriod eventCreatedAt);

        public abstract Builder eventOccurredAt(DateFilterPeriod eventOccurredAt);

        public abstract Builder eventScheduledAt(DateFilterPeriod eventScheduledAt);

        public abstract Builder enrollmentStatus(EnrollmentStatus enrollmentStatus);

        public abstract Builder enrolledAt(DateFilterPeriod enrolledAt);

        public abstract Builder enrollmentOccurredAt(DateFilterPeriod enrollmentOccurredAt);

        public abstract Builder order(String order);

        public abstract Builder displayColumnOrder(List<String> displayColumnOrder);

        public abstract Builder orgUnit(String orgUnit);

        public abstract Builder ouMode(OrganisationUnitMode ouMode);

        public abstract Builder assignedUserMode(AssignedUserMode assignedUserMode);

        public abstract Builder dataFilters(List<ProgramStageWorkingListEventDataFilter> dataFilters);

        public abstract Builder attributeValueFilters(
                List<ProgramStageWorkingListAttributeValueFilter> attributeValueFilters);

        public abstract ProgramStageQueryCriteria build();
    }
}
