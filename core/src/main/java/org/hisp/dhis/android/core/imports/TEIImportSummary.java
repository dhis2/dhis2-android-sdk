package org.hisp.dhis.android.core.imports;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_TEIImportSummary.Builder.class)
public abstract class TEIImportSummary extends BaseImportSummary {

    private static final String IMPORT_ENROLLMENT = "enrollments";

    @Nullable
    @JsonProperty(IMPORT_ENROLLMENT)
    public abstract EnrollmentImportSummaries enrollments();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseImportSummary.Builder<TEIImportSummary.Builder> {

        public abstract Builder enrollments(EnrollmentImportSummaries enrollments);

        public abstract TEIImportSummary build();
    }
}
