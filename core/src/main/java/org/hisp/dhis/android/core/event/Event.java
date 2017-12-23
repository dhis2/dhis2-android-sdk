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

package org.hisp.dhis.android.core.event;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class Event {
    private static final String UID = "event";
    private static final String ENROLLMENT_UID = "enrollment";
    private static final String CREATED = "created";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String CREATED_AT_CLIENT = "createdAtClient";
    private static final String LAST_UPDATED_AT_CLIENT = "lastUpdatedAtClient";
    private static final String STATUS = "status";
    private static final String COORDINATE = "coordinate";
    private static final String PROGRAM = "program";
    private static final String PROGRAM_STAGE = "programStage";
    private static final String ORGANISATION_UNIT = "orgUnit";
    private static final String EVENT_DATE = "eventDate";
    private static final String COMPLETE_DATE = "completedDate";
    private static final String DUE_DATE = "dueDate";
    private static final String DELETED = "deleted";
    private static final String TRACKED_ENTITY_DATA_VALUES = "dataValues";
    private static final String ATTRIBUTE_CATEGORY_OPTIONS = "attributeCategoryOptions";
    private static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
    private static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";

    public static final Field<Event, String> uid = Field.create(UID);
    public static final Field<Event, String> enrollment = Field.create(ENROLLMENT_UID);
    public static final Field<Event, String> created = Field.create(CREATED);
    public static final Field<Event, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<Event, String> createdAtClient = Field.create(CREATED_AT_CLIENT);
    public static final Field<Event, String> lastUpdatedAtClient = Field.create(LAST_UPDATED_AT_CLIENT);
    public static final Field<Event, EventStatus> eventStatus = Field.create(STATUS);
    public static final Field<Event, Coordinates> coordinates = Field.create(COORDINATE);
    public static final Field<Event, String> program = Field.create(PROGRAM);
    public static final Field<Event, String> programStage = Field.create(PROGRAM_STAGE);
    public static final Field<Event, String> organisationUnit = Field.create(ORGANISATION_UNIT);
    public static final Field<Event, String> eventDate = Field.create(EVENT_DATE);
    public static final Field<Event, String> completeDate = Field.create(COMPLETE_DATE);
    public static final Field<Event, Boolean> deleted = Field.create(DELETED);
    public static final Field<Event, String> dueDate = Field.create(DUE_DATE);
    public static final Field<Event, String> attributeCategoryOptions = Field.create(ATTRIBUTE_CATEGORY_OPTIONS);
    public static final Field<Event, String> attributeOptionCombo = Field.create(ATTRIBUTE_OPTION_COMBO);
    public static final Field<Event, String> trackedEntityInstance = Field.create(TRACKED_ENTITY_INSTANCE);

    public static final NestedField<Event, TrackedEntityDataValue> trackedEntityDataValues
            = NestedField.create(TRACKED_ENTITY_DATA_VALUES);

    @JsonProperty(UID)
    public abstract String uid();

    @Nullable
    @JsonProperty(ENROLLMENT_UID)
    public abstract String enrollmentUid();

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
    @JsonProperty(PROGRAM)
    public abstract String program();

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract String programStage();

    @Nullable
    @JsonProperty(ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty(EVENT_DATE)
    public abstract Date eventDate();

    @Nullable
    @JsonProperty(STATUS)
    public abstract EventStatus status();

    @Nullable
    @JsonProperty(COORDINATE)
    public abstract Coordinates coordinates();

    @Nullable
    @JsonProperty(COMPLETE_DATE)
    public abstract Date completedDate();

    @Nullable
    @JsonProperty(DUE_DATE)
    public abstract Date dueDate();

    @Nullable
    @JsonProperty(DELETED)
    public abstract Boolean deleted();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_DATA_VALUES)
    public abstract List<TrackedEntityDataValue> trackedEntityDataValues();

    @Nullable
    @JsonProperty(ATTRIBUTE_CATEGORY_OPTIONS)
    public abstract String attributeCategoryOptions();

    @Nullable
    @JsonProperty(ATTRIBUTE_OPTION_COMBO)
    public abstract String attributeOptionCombo();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_INSTANCE)
    public abstract String trackedEntityInstance();


    @JsonCreator
    public static Event create(
            @JsonProperty(UID) String uid,
            @JsonProperty(ENROLLMENT_UID) String enrollmentUid,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(CREATED_AT_CLIENT) String createdAtClient,
            @JsonProperty(LAST_UPDATED_AT_CLIENT) String lastUpdatedAtClient,
            @JsonProperty(PROGRAM) String program,
            @JsonProperty(PROGRAM_STAGE) String programStage,
            @JsonProperty(ORGANISATION_UNIT) String organisationUnit,
            @JsonProperty(EVENT_DATE) Date eventDate,
            @JsonProperty(STATUS) EventStatus eventStatus,
            @JsonProperty(COORDINATE) Coordinates coordinates,
            @JsonProperty(COMPLETE_DATE) Date completedDate,
            @JsonProperty(DUE_DATE) Date dueDate,
            @JsonProperty(DELETED) Boolean deleted,
            @JsonProperty(TRACKED_ENTITY_DATA_VALUES) List<TrackedEntityDataValue> dataValues,
            @JsonProperty(ATTRIBUTE_CATEGORY_OPTIONS) String attributeCategoryOptions,
            @JsonProperty(ATTRIBUTE_OPTION_COMBO) String attributeOptionCombo,
            @JsonProperty(TRACKED_ENTITY_INSTANCE) String trackedEntityInstance) {
        return new AutoValue_Event(uid, enrollmentUid, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                program, programStage, organisationUnit, eventDate, eventStatus, coordinates,
                completedDate, dueDate, deleted, safeUnmodifiableList(dataValues), attributeCategoryOptions,
                attributeOptionCombo, trackedEntityInstance);
    }

}
