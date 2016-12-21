/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.enrollment;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Enrollment.Builder.class)
public abstract class Enrollment {
    private static final String JSON_PROPERTY_UID = "enrollment";
    private static final String JSON_PROPERTY_CREATED = "created";
    private static final String JSON_PROPERTY_LAST_UPDATED = "lastUpdated";
    private static final String JSON_PROPERTY_ORGANISATION_UNIT = "orgUnit";
    private static final String JSON_PROPERTY_PROGRAM = "program";
    private static final String JSON_PROPERTY_DATE_OF_ENROLLMENT = "enrollmentDate";
    private static final String JSON_PROPERTY_DATE_OF_INCIDENT = "incidentDate";
    private static final String JSON_PROPERTY_FOLLOW_UP = "followup";
    private static final String JSON_PROPERTY_ENROLLMENT_STATUS = "status";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE_VALUES = "attributes";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";

    @JsonProperty(JSON_PROPERTY_UID)
    public abstract String uid();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM)
    public abstract String program();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATE_OF_ENROLLMENT)
    public abstract Date dateOfEnrollment();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATE_OF_INCIDENT)
    public abstract Date dateOfIncident();

    @Nullable
    @JsonProperty(JSON_PROPERTY_FOLLOW_UP)
    public abstract Boolean followUp();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ENROLLMENT_STATUS)
    public abstract EnrollmentStatus enrollmentStatus();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE)
    public abstract String trackedEntityInstance();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE_VALUES)
    public abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

    public static Builder builder() {
        return new AutoValue_Enrollment.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonProperty(JSON_PROPERTY_UID)
        public abstract Builder uid(String uid);

        @JsonProperty(JSON_PROPERTY_CREATED)
        public abstract Builder created(@Nullable Date created);

        @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
        public abstract Builder organisationUnit(@Nullable String orgUnit);

        @JsonProperty(JSON_PROPERTY_PROGRAM)
        public abstract Builder program(@Nullable String program);

        @JsonProperty(JSON_PROPERTY_DATE_OF_ENROLLMENT)
        public abstract Builder dateOfEnrollment(@Nullable Date dateOfEnrollment);

        @JsonProperty(JSON_PROPERTY_DATE_OF_INCIDENT)
        public abstract Builder dateOfIncident(@Nullable Date dateOfIncident);

        @JsonProperty(JSON_PROPERTY_FOLLOW_UP)
        public abstract Builder followUp(@Nullable Boolean followUp);

        @JsonProperty(JSON_PROPERTY_ENROLLMENT_STATUS)
        public abstract Builder enrollmentStatus(@Nullable EnrollmentStatus enrollmentStatus);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE)
        public abstract Builder trackedEntityInstance(@Nullable String trackedEntityInstance);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE_VALUES)
        public abstract Builder trackedEntityAttributeValues(@Nullable List<TrackedEntityAttributeValue> trackedEntityAttributeValues);

        abstract Enrollment autoBuild();

        abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

        public Enrollment build() {
            if (trackedEntityAttributeValues() != null) {
                trackedEntityAttributeValues(Collections.unmodifiableList(trackedEntityAttributeValues()));
            }
            return autoBuild();
        }
    }
}
