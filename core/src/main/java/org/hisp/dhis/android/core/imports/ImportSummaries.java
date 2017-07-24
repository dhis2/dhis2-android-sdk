package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class ImportSummaries {

    private static final String RESPONSE_TYPE = "responseType";
    private static final String IMPORT_STATUS = "status";
    private static final String IMPORT_COUNT = "importCount";
    private static final String IMPORT_SUMMARIES = "importSummaries";

    private static final String IMPORTED = "imported";
    private static final String UPDATED = "updated";
    private static final String DELETED = "deleted";
    private static final String IGNORED = "ignored";

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus importStatus();

    @Nullable
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @NonNull
    @JsonProperty(RESPONSE_TYPE)
    public abstract String responseType();

    @NonNull
    @JsonProperty(IMPORTED)
    public abstract Integer imported();

    @NonNull
    @JsonProperty(UPDATED)
    public abstract Integer updated();

    @NonNull
    @JsonProperty(DELETED)
    public abstract Integer deleted();

    @NonNull
    @JsonProperty(IGNORED)
    public abstract Integer ignored();


    @NonNull
    @JsonProperty(IMPORT_SUMMARIES)
    public abstract List<ImportSummary> importSummaries();


    @JsonCreator
    public static ImportSummaries create(
            @JsonProperty(IMPORT_STATUS) ImportStatus importStatus,
            @JsonProperty(IMPORT_COUNT) ImportCount importCount,
            @JsonProperty(RESPONSE_TYPE) String responseType,
            @JsonProperty(IMPORTED) Integer imported,
            @JsonProperty(UPDATED) Integer updated,
            @JsonProperty(DELETED) Integer deleted,
            @JsonProperty(IGNORED) Integer ignored,
            @JsonProperty(IMPORT_SUMMARIES) List<ImportSummary> importSummaries) {
        return new AutoValue_ImportSummaries(
                importStatus, importCount, responseType, imported,
                updated, deleted, ignored, safeUnmodifiableList(importSummaries)
        );
    }
}
