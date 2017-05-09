package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class ImportSummary {

    private static final String IMPORT_COUNT = "importCount";
    private static final String IMPORT_TYPE_SUMMARRY = "importTypeSummaries";

    @NonNull
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @NonNull
    @JsonProperty(IMPORT_TYPE_SUMMARRY)
    public abstract List<ImportTypeSummary> importTypeSummaries();

    @JsonCreator
    public static ImportSummary create(
            @JsonProperty(IMPORT_COUNT) ImportCount importCount,
            @JsonProperty(IMPORT_TYPE_SUMMARRY) List<ImportTypeSummary> importTypeSummaries) {
        return new AutoValue_ImportSummary(importCount, safeUnmodifiableList(importTypeSummaries));
    }

}
