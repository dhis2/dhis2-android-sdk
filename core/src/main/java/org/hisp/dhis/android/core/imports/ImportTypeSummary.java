package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class ImportTypeSummary {
    private static final String TYPE = "type";
    private static final String IMPORT_COUNT = "importCount";
    private static final String IMPORT_CONFLICTS = "conflicts";

    @NonNull
    @JsonProperty(TYPE)
    public abstract String type();

    @NonNull
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @Nullable
    @JsonProperty(IMPORT_CONFLICTS)
    public abstract List<ImportConflict> importConflicts();

    @JsonCreator
    public static ImportTypeSummary create(
            @JsonProperty(TYPE) String type,
            @JsonProperty(IMPORT_COUNT) ImportCount importCount,
            @JsonProperty(IMPORT_CONFLICTS) List<ImportConflict> importConflicts) {
        return new AutoValue_ImportTypeSummary(type, importCount, safeUnmodifiableList(importConflicts));
    }
}
