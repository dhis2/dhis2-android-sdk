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
abstract class TrackedEntityInstanceQueryOnline extends BaseQuery {

    @NonNull
    abstract List<String> orgUnits();

    @Nullable
    abstract OrganisationUnitMode orgUnitMode();

    @Nullable
    abstract String program();

    @Nullable
    abstract String programStage();

    @Nullable
    abstract String query();

    @Nullable
    abstract List<String> attribute();

    @Nullable
    abstract List<String> filter();

    @Nullable
    abstract Date programStartDate();

    @Nullable
    abstract Date programEndDate();

    @Nullable
    abstract EnrollmentStatus enrollmentStatus();

    @Nullable
    abstract Date incidentStartDate();

    @Nullable
    abstract Date incidentEndDate();

    @Nullable
    abstract Boolean followUp();

    @Nullable
    abstract EventStatus eventStatus();

    @Nullable
    abstract Date eventStartDate();

    @Nullable
    abstract Date eventEndDate();

    @Nullable
    abstract String trackedEntityType();

    //TODO It is not used in the query because it modifies returned grid structure: if true, it adds an extra column
    @NonNull
    abstract Boolean includeDeleted();

    @Nullable
    abstract AssignedUserMode assignedUserMode();

    @Nullable
    abstract List<String> uids();

    @Nullable
    abstract Date lastUpdatedStartDate();

    @Nullable
    abstract Date lastUpdatedEndDate();

    @Nullable
    abstract String order();

    String formattedProgramStartDate() {
        return formatDate(programStartDate());
    }

    String formattedProgramEndDate() {
        return formatDate(programEndDate());
    }

    String formattedIncidentStartDate() {
        return formatDate(incidentStartDate());
    }

    String formattedIncidentEndDate() {
        return formatDate(incidentEndDate());
    }

    String formattedEventStartDate() {
        return formatDate(eventStartDate());
    }

    String formattedEventEndDate() {
        return formatDate(eventEndDate());
    }

    String formattedLastUpdatedStartDate() {
        return formatDate(lastUpdatedStartDate());
    }

    String formattedLastUpdatedEndDate() {
        return formatDate(lastUpdatedEndDate());
    }

    private String formatDate(Date date) {
        return date == null ? null : DateUtils.SIMPLE_DATE_FORMAT.format(date);
    }

    abstract Builder toBuilder();

    static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQueryOnline.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder extends BaseQuery.Builder<Builder> {
        abstract Builder orgUnits(List<String> orgUnits);

        abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        abstract Builder program(String program);

        abstract Builder programStage(String programStage);

        abstract Builder query(String query);

        abstract Builder attribute(List<String> attribute);

        abstract Builder filter(List<String> filter);

        abstract Builder programStartDate(Date programStartDate);

        abstract Builder programEndDate(Date programEndDate);

        abstract Builder enrollmentStatus(EnrollmentStatus programStatus);

        abstract Builder incidentStartDate(Date incidentStartDate);

        abstract Builder incidentEndDate(Date incidentEndDate);

        abstract Builder followUp(Boolean followUp);

        abstract Builder eventStatus(EventStatus eventStatus);

        abstract Builder eventStartDate(Date eventStartDate);

        abstract Builder eventEndDate(Date eventEndDate);

        abstract Builder trackedEntityType(String trackedEntityType);

        abstract Builder includeDeleted(Boolean includeDeleted);

        abstract Builder assignedUserMode(AssignedUserMode assignedUserMode);

        abstract Builder uids(List<String> uids);

        abstract Builder lastUpdatedStartDate(Date lastUpdatedStartDate);

        abstract Builder lastUpdatedEndDate(Date lastUpdatedEndDate);

        abstract Builder order(String order);

        abstract TrackedEntityInstanceQueryOnline build();
    }
}