package org.hisp.dhis.client.models.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseDataModel;
import org.hisp.dhis.client.models.common.Coordinates;
import org.hisp.dhis.client.models.trackedentity.TrackedEntityDataValue;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_Event.Builder.class)
public abstract class Event extends BaseDataModel {
    private static final String JSON_PROPERTY_EVENT_UID = "event";
    private static final String JSON_PROPERTY_ENROLLMENT_UID = "enrollment";
    private static final String JSON_PROPERTY_CREATED = "created";
    private static final String JSON_PROPERTY_LAST_UPDATED = "lastUpdated";
    private static final String JSON_PROPERTY_STATUS = "status";
    private static final String JSON_PROPERTY_COORDINATE = "coordinate";
    private static final String JSON_PROPERTY_PROGRAM = "program";
    private static final String JSON_PROPERTY_PROGRAM_STAGE = "programStage";
    private static final String JSON_PROPERTY_ORGANISATION_UNIT = "organisationUnit";
    private static final String JSON_PROPERTY_EVENT_DATE = "eventDate";
    private static final String JSON_PROPERTY_COMPLETE_DATE = "completedDate";
    private static final String JSON_PROPERTY_DUE_DATE = "dueDate";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES = "trackedEntityDataValues";

    // Mandatory, non-null properties

    @JsonProperty(JSON_PROPERTY_EVENT_UID)
    public abstract String uid();

    @JsonProperty(JSON_PROPERTY_STATUS)
    public abstract EventStatus status();

    @JsonProperty(JSON_PROPERTY_PROGRAM)
    public abstract String program();

    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
    public abstract String programStage();

    @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @JsonProperty(JSON_PROPERTY_EVENT_DATE)
    public abstract Date eventDate();

    // Nullable properties
    @Nullable
    @JsonProperty(JSON_PROPERTY_ENROLLMENT_UID)
    public abstract String enrollmentUid();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(JSON_PROPERTY_COORDINATE)
    public abstract Coordinates coordinates();

    @Nullable
    @JsonProperty(JSON_PROPERTY_COMPLETE_DATE)
    public abstract Date completedDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DUE_DATE)
    public abstract Date dueDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES)
    public abstract List<TrackedEntityDataValue> trackedEntityDataValues();

    @Override
    public boolean isValid() {
        if (created() == null || lastUpdated() == null) {
            return false;
        }

        if (trackedEntityDataValues() == null || trackedEntityDataValues().isEmpty()) {
            return false;
        }

        return true;
    }

    public static Builder builder() {
        return new AutoValue_Event.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {
        @JsonProperty(JSON_PROPERTY_EVENT_UID)
        public abstract Builder uid(@Nullable String uid);

        @JsonProperty(JSON_PROPERTY_CREATED)
        public abstract Builder created(@Nullable Date created);

        @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        @JsonProperty(JSON_PROPERTY_STATUS)
        public abstract Builder status(EventStatus eventStatus);

        @JsonProperty(JSON_PROPERTY_COORDINATE)
        public abstract Builder coordinates(@Nullable Coordinates coordinates);

        @JsonProperty(JSON_PROPERTY_ENROLLMENT_UID)
        public abstract Builder enrollmentUid(@Nullable String enrollmentUid);

        @JsonProperty(JSON_PROPERTY_PROGRAM)
        public abstract Builder program(String program);

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
        public abstract Builder programStage(String programStage);

        @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
        public abstract Builder organisationUnit(String organisationUnit);

        @JsonProperty(JSON_PROPERTY_EVENT_DATE)
        public abstract Builder eventDate(Date eventDate);

        @JsonProperty(JSON_PROPERTY_COMPLETE_DATE)
        public abstract Builder completedDate(@Nullable Date completedDate);

        @JsonProperty(JSON_PROPERTY_DUE_DATE)
        public abstract Builder dueDate(@Nullable Date dueDate);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES)
        public abstract Builder trackedEntityDataValues(@Nullable List<TrackedEntityDataValue> trackedEntityDataValues);

        abstract List<TrackedEntityDataValue> trackedEntityDataValues();

        abstract Event autoBuild();

        public Event build() {
            if (trackedEntityDataValues() != null) {
                trackedEntityDataValues(Collections.unmodifiableList(trackedEntityDataValues()));
            }

            return autoBuild();
        }
    }
}
