/*
 * Copyright (c) 2017, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.event.Event;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class Enrollment {
    private static final String UID = "enrollment";
    private static final String CREATED = "created";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String CREATED_AT_CLIENT = "createdAtClient";
    private static final String LAST_UPDATED_AT_CLIENT = "lastUpdatedAtClient";
    private static final String ORGANISATION_UNIT = "orgUnit";
    private static final String PROGRAM = "program";
    private static final String DATE_OF_ENROLLMENT = "enrollmentDate";
    private static final String DATE_OF_INCIDENT = "incidentDate";
    private static final String FOLLOW_UP = "followup";
    private static final String ENROLLMENT_STATUS = "status";
    private static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    private static final String COORDINATE = "coordinate";
    private static final String DELETED = "deleted";
    private static final String EVENTS = "events";

    public static final Field<Enrollment, String> uid = Field.create(UID);
    public static final Field<Enrollment, String> created = Field.create(CREATED);
    public static final Field<Enrollment, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<Enrollment, String> createdAtClient = Field.create(CREATED_AT_CLIENT);
    public static final Field<Enrollment, String> lastUpdatedAtClient = Field.create(LAST_UPDATED_AT_CLIENT);
    public static final Field<Enrollment, String> organisationUnit = Field.create(ORGANISATION_UNIT);
    public static final Field<Enrollment, String> program = Field.create(PROGRAM);
    public static final Field<Enrollment, String> dateOfEnrollment = Field.create(DATE_OF_ENROLLMENT);
    public static final Field<Enrollment, String> dateOfIncident = Field.create(DATE_OF_INCIDENT);
    public static final Field<Enrollment, String> followUp = Field.create(FOLLOW_UP);
    public static final Field<Enrollment, String> enrollmentStatus = Field.create(ENROLLMENT_STATUS);
    public static final Field<Enrollment, Boolean> deleted = Field.create(DELETED);
    public static final Field<Enrollment, String> trackedEntityInstance = Field.create(TRACKED_ENTITY_INSTANCE);
    public static final Field<Enrollment, Coordinates> coordinate = Field.create(COORDINATE);

    public static final NestedField<Enrollment, Event> events = NestedField.create(EVENTS);

    @JsonProperty(UID)
    public abstract String uid();

    @Nullable
    @JsonProperty(CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(CREATED_AT_CLIENT)
    public abstract String createdAtClient();

    @Nullable
    @JsonProperty(LAST_UPDATED_AT_CLIENT)
    public abstract String lastUpdatedAtClient();

    @Nullable
    @JsonProperty(ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract String program();

    @Nullable
    @JsonProperty(DATE_OF_ENROLLMENT)
    public abstract Date dateOfEnrollment();

    @Nullable
    @JsonProperty(DATE_OF_INCIDENT)
    public abstract Date dateOfIncident();

    @Nullable
    @JsonProperty(FOLLOW_UP)
    public abstract Boolean followUp();

    @Nullable
    @JsonProperty(ENROLLMENT_STATUS)
    public abstract EnrollmentStatus enrollmentStatus();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_INSTANCE)
    public abstract String trackedEntityInstance();

    @Nullable
    @JsonProperty(COORDINATE)
    public abstract Coordinates coordinate();

    @Nullable
    @JsonProperty(DELETED)
    public abstract Boolean deleted();

    @Nullable
    @JsonProperty(EVENTS)
    public abstract List<Event> events();

    @JsonCreator
    public static Enrollment create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(CREATED_AT_CLIENT) String createdAtClient,
            @JsonProperty(LAST_UPDATED_AT_CLIENT) String lastUpdatedAtClient,
            @JsonProperty(ORGANISATION_UNIT) String organisationUnit,
            @JsonProperty(PROGRAM) String program,
            @JsonProperty(DATE_OF_ENROLLMENT) Date dateOfEnrollment,
            @JsonProperty(DATE_OF_INCIDENT) Date dateOfIncident,
            @JsonProperty(FOLLOW_UP) Boolean followUp,
            @JsonProperty(ENROLLMENT_STATUS) EnrollmentStatus enrollmentStatus,
            @JsonProperty(TRACKED_ENTITY_INSTANCE) String trackedEntityInstance,
            @JsonProperty(COORDINATE) Coordinates coordinate,
            @JsonProperty(DELETED) Boolean deleted,
            @JsonProperty(EVENTS) List<Event> events) {
        return new AutoValue_Enrollment(uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                organisationUnit, program, dateOfEnrollment, dateOfIncident, followUp, enrollmentStatus,
                trackedEntityInstance, coordinate, deleted, safeUnmodifiableList(events));
    }

}
