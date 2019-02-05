package org.hisp.dhis.android.core.imports;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_EnrollmentImportSummary.Builder.class)
public abstract class EnrollmentImportSummary extends BaseImportSummary {

    private static final String IMPORT_EVENT = "events";

    @Nullable
    @JsonProperty(IMPORT_EVENT)
    public abstract EventImportSummaries events();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseImportSummary.Builder<EnrollmentImportSummary.Builder> {

        public abstract Builder events(EventImportSummaries events);

        public abstract EnrollmentImportSummary build();
    }
}
