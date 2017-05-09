package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class ImportSummaries {

    private static final String IMPORT_STATUS = "status";
    private static final String IMPORT_COUNT = "importCount";
    private static final String STATUS_CODE = "httpStatusCode";
    private static final String MESSAGE = "message";
    private static final String IMPORT_SUMMARIES = "importSummaries";

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus importStatus();

    @NonNull
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @NonNull
    @JsonProperty(STATUS_CODE)
    public abstract Integer statusCode();

    @NonNull
    @JsonProperty(MESSAGE)
    public abstract String message();

    @NonNull
    @JsonProperty(IMPORT_SUMMARIES)
    public abstract List<ImportSummary> importSummaries();


    @JsonCreator
    public static ImportSummaries create(
            @JsonProperty(IMPORT_STATUS) ImportStatus importStatus,
            @JsonProperty(IMPORT_COUNT) ImportCount importCount,
            @JsonProperty(STATUS_CODE) Integer statusCode,
            @JsonProperty(MESSAGE) String message,
            @JsonProperty(IMPORT_SUMMARIES) List<ImportSummary> importSummaries) {
        return new AutoValue_ImportSummaries(
                importStatus, importCount, statusCode, message, safeUnmodifiableList(importSummaries)
        );
    }
}
