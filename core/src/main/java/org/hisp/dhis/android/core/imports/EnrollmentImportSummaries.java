package org.hisp.dhis.android.core.imports;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_EnrollmentImportSummaries.Builder.class)
public abstract class EnrollmentImportSummaries extends BaseImportSummaries implements ImportSummaries {

    private static final String IMPORT_SUMMARIES = "importSummaries";

    @Override
    @Nullable
    @JsonProperty(IMPORT_SUMMARIES)
    public abstract List<EnrollmentImportSummary> importSummaries();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseImportSummaries.Builder<EnrollmentImportSummaries.Builder> {

        public abstract Builder importSummaries(List<EnrollmentImportSummary> importSummaries);

        public abstract EnrollmentImportSummaries build();
    }
}
