package org.hisp.dhis.client.models.enrollment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseDataModel;
import org.hisp.dhis.client.models.trackedentity.TrackedEntityAttributeValue;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_Enrollment.Builder.class)
public abstract class Enrollment extends BaseDataModel {

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

    @Override
    public boolean isValid() {
        if (created() == null || lastUpdated() == null) {
            return false;
        }

        if (trackedEntityAttributeValues() == null || trackedEntityAttributeValues().isEmpty()) {
            return false;
        }

        return true;
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {

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
