package org.hisp.dhis.android.core.imports;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_TEIImportSummaries.Builder.class)
public abstract class TEIImportSummaries extends BaseImportSummaries implements ImportSummaries {

    private static final String IMPORT_SUMMARIES = "importSummaries";

    /**
    static final TEIImportSummaries EMPTY = TEIImportSummaries.create(
            ImportStatus.SUCCESS,
            ImportCount.EMPTY,
            "ImportSummaries",
            0, 0, 0, 0,
            Collections.<ImportSummary>emptyList()
    );
     */

    @Override
    @Nullable
    @JsonProperty(IMPORT_SUMMARIES)
    public abstract List<TEIImportSummary> importSummaries();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseImportSummaries.Builder<TEIImportSummaries.Builder> {

        public abstract Builder importSummaries(List<TEIImportSummary> importSummaries);

        public abstract TEIImportSummaries build();
    }
}
