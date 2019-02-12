package org.hisp.dhis.android.core.imports;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_EventImportSummaries.Builder.class)
public abstract class EventImportSummaries extends BaseImportSummaries implements ImportSummaries {

    private static final String IMPORT_SUMMARIES = "importSummaries";

    @Override
    @Nullable
    @JsonProperty(IMPORT_SUMMARIES)
    public abstract List<EventImportSummary> importSummaries();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseImportSummaries.Builder<EventImportSummaries.Builder> {

        public abstract Builder importSummaries(List<EventImportSummary> importSummaries);

        public abstract EventImportSummaries build();
    }
}
