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
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteFields;
import org.hisp.dhis.android.core.event.Event;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class Enrollment implements ObjectWithDeleteInterface, ObjectWithUidInterface {
    private static final String UID = "enrollment";
    private static final String CREATED = "created";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String CREATED_AT_CLIENT = "createdAtClient";
    private static final String LAST_UPDATED_AT_CLIENT = "lastUpdatedAtClient";
    private static final String ORGANISATION_UNIT = "orgUnit";
    private static final String PROGRAM = "program";
    private static final String ENROLLMENT_DATE = "enrollmentDate";
    private static final String INCIDENT_DATE = "incidentDate";
    private static final String FOLLOW_UP = "followup";
    private static final String ENROLLMENT_STATUS = "status";
    private static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    private static final String COORDINATE = "coordinate";
    private static final String DELETED = "deleted";
    private static final String EVENTS = "events";
    private static final String NOTES = "notes";

    private static final Field<Enrollment, String> uid = Field.create(UID);
    private static final Field<Enrollment, String> created = Field.create(CREATED);
    private static final Field<Enrollment, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<Enrollment, String> organisationUnit = Field.create(ORGANISATION_UNIT);
    private static final Field<Enrollment, String> program = Field.create(PROGRAM);
    private static final Field<Enrollment, String> enrollmentDate = Field.create(ENROLLMENT_DATE);
    private static final Field<Enrollment, String> incidentDate = Field.create(INCIDENT_DATE);
    private static final Field<Enrollment, String> followUp = Field.create(FOLLOW_UP);
    private static final Field<Enrollment, String> enrollmentStatus = Field.create(ENROLLMENT_STATUS);
    private static final Field<Enrollment, Boolean> deleted = Field.create(DELETED);
    private static final Field<Enrollment, String> trackedEntityInstance = Field.create(TRACKED_ENTITY_INSTANCE);
    private static final Field<Enrollment, Coordinates> coordinate = Field.create(COORDINATE);

    private static final NestedField<Enrollment, Event> events = NestedField.create(EVENTS);
    private static final NestedField<Enrollment, Note> notes = NestedField.create(NOTES);

    public static final Fields<Enrollment> allFields = Fields.<Enrollment>builder().fields(
            uid, created, lastUpdated, coordinate, enrollmentDate, incidentDate, enrollmentStatus,
            followUp, program, organisationUnit, trackedEntityInstance, deleted, events.with(Event.allFields),
            notes.with(NoteFields.all)
    ).build();

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
    @JsonProperty(ENROLLMENT_DATE)
    public abstract Date enrollmentDate();

    @Nullable
    @JsonProperty(INCIDENT_DATE)
    public abstract Date incidentDate();

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

    @Nullable
    @JsonProperty(NOTES)
    public abstract List<Note> notes();

    @JsonCreator
    public static Enrollment create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(CREATED_AT_CLIENT) String createdAtClient,
            @JsonProperty(LAST_UPDATED_AT_CLIENT) String lastUpdatedAtClient,
            @JsonProperty(ORGANISATION_UNIT) String organisationUnit,
            @JsonProperty(PROGRAM) String program,
            @JsonProperty(ENROLLMENT_DATE) Date enrollmentDate,
            @JsonProperty(INCIDENT_DATE) Date incidentDate,
            @JsonProperty(FOLLOW_UP) Boolean followUp,
            @JsonProperty(ENROLLMENT_STATUS) EnrollmentStatus enrollmentStatus,
            @JsonProperty(TRACKED_ENTITY_INSTANCE) String trackedEntityInstance,
            @JsonProperty(COORDINATE) Coordinates coordinate,
            @JsonProperty(DELETED) Boolean deleted,
            @JsonProperty(EVENTS) List<Event> events,
            @JsonProperty(NOTES) List<Note> notes) {
        return new AutoValue_Enrollment(uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                organisationUnit, program, enrollmentDate, incidentDate, followUp, enrollmentStatus,
                trackedEntityInstance, coordinate, deleted, safeUnmodifiableList(events), notes);
    }

}