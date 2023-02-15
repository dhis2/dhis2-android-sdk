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

package org.hisp.dhis.android.core.trackedentity.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.call.queries.internal.BaseQuery;
import org.hisp.dhis.android.core.arch.helpers.DateUtils;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.Date;
import java.util.List;

@AutoValue
@SuppressWarnings({"PMD.ExcessivePublicCount"})
public abstract class TrackedEntityInstanceQueryOnline extends BaseQuery {

    @NonNull
    public abstract List<String> orgUnits();

    @Nullable
    public abstract OrganisationUnitMode orgUnitMode();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract String programStage();

    @Nullable
    public abstract String query();

    @Nullable
    public abstract List<String> attribute();

    @Nullable
    public abstract List<String> filter();

    @Nullable
    public abstract Date programStartDate();

    @Nullable
    public abstract Date programEndDate();

    @Nullable
    public abstract EnrollmentStatus enrollmentStatus();

    @Nullable
    public abstract Date incidentStartDate();

    @Nullable
    public abstract Date incidentEndDate();

    @Nullable
    public abstract Boolean followUp();

    @Nullable
    public abstract EventStatus eventStatus();

    @Nullable
    public abstract Date eventStartDate();

    @Nullable
    public abstract Date eventEndDate();

    @Nullable
    public abstract String trackedEntityType();

    //TODO It is not used in the query because it modifies returned grid structure: if true, it adds an extra column
    @NonNull
    public abstract Boolean includeDeleted();

    @Nullable
    public abstract AssignedUserMode assignedUserMode();

    @Nullable
    public abstract List<String> uids();

    @Nullable
    public abstract Date lastUpdatedStartDate();

    @Nullable
    public abstract Date lastUpdatedEndDate();

    @Nullable
    public abstract String order();

    public String formattedProgramStartDate() {
        return formatDate(programStartDate());
    }

    public String formattedProgramEndDate() {
        return formatDate(programEndDate());
    }

    public String formattedIncidentStartDate() {
        return formatDate(incidentStartDate());
    }

    public String formattedIncidentEndDate() {
        return formatDate(incidentEndDate());
    }

    public String formattedEventStartDate() {
        return formatDate(eventStartDate());
    }

    public String formattedEventEndDate() {
        return formatDate(eventEndDate());
    }

    public String formattedLastUpdatedStartDate() {
        return formatDate(lastUpdatedStartDate());
    }

    public String formattedLastUpdatedEndDate() {
        return formatDate(lastUpdatedEndDate());
    }

    private String formatDate(Date date) {
        return date == null ? null : DateUtils.SIMPLE_DATE_FORMAT.format(date);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQueryOnline.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseQuery.Builder<Builder> {
        public abstract Builder orgUnits(List<String> orgUnits);

        public abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        public abstract Builder program(String program);

        public abstract Builder programStage(String programStage);

        public abstract Builder query(String query);

        public abstract Builder attribute(List<String> attribute);

        public abstract Builder filter(List<String> filter);

        public abstract Builder programStartDate(Date programStartDate);

        public abstract Builder programEndDate(Date programEndDate);

        public abstract Builder enrollmentStatus(EnrollmentStatus programStatus);

        public abstract Builder incidentStartDate(Date incidentStartDate);

        public abstract Builder incidentEndDate(Date incidentEndDate);

        public abstract Builder followUp(Boolean followUp);

        public abstract Builder eventStatus(EventStatus eventStatus);

        public abstract Builder eventStartDate(Date eventStartDate);

        public abstract Builder eventEndDate(Date eventEndDate);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder includeDeleted(Boolean includeDeleted);

        public abstract Builder assignedUserMode(AssignedUserMode assignedUserMode);

        public abstract Builder uids(List<String> uids);

        public abstract Builder lastUpdatedStartDate(Date lastUpdatedStartDate);

        public abstract Builder lastUpdatedEndDate(Date lastUpdatedEndDate);

        public abstract Builder order(String order);

        public abstract TrackedEntityInstanceQueryOnline build();
    }
}