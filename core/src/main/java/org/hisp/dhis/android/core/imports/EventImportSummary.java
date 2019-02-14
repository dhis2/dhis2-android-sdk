package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_EventImportSummary.Builder.class)
public abstract class EventImportSummary extends BaseImportSummary {

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseImportSummary.Builder<EventImportSummary.Builder> {

        public abstract EventImportSummary build();
    }
}
