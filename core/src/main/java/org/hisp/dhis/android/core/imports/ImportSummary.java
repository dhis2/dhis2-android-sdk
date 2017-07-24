package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class ImportSummary {

    private static final String IMPORT_COUNT = "importCount";
    private static final String IMPORT_STATUS = "status";
    private static final String RESPONSE_TYPE = "responseType";
    private static final String REFERENCE = "reference";
    private static final String IMPORT_ENROLLMENT = "enrollments";
    private static final String IMPORT_EVENT = "events";
    private static final String IMPORT_CONFLICT = "conflicts";

    @NonNull
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus importStatus();

    @NonNull
    @JsonProperty(RESPONSE_TYPE)
    public abstract String responseType();

    //TODO: Reference SHOULD be annotated with NotNull. This is just a bug in ImportSummary response from server.
    @Nullable
    @JsonProperty(REFERENCE)
    public abstract String reference();

    @Nullable
    @JsonProperty(IMPORT_ENROLLMENT)
    public abstract ImportEnrollment importEnrollment();

    @Nullable
    @JsonProperty(IMPORT_EVENT)
    public abstract ImportEvent importEvent();

    @Nullable
    @JsonProperty(IMPORT_CONFLICT)
    public abstract List<ImportConflict> importConflicts();

    @JsonCreator
    public static ImportSummary create(
            @JsonProperty(IMPORT_COUNT) ImportCount importCount,
            @JsonProperty(IMPORT_STATUS) ImportStatus importStatus,
            @JsonProperty(RESPONSE_TYPE) String responseType,
            @JsonProperty(REFERENCE) String reference,
            @JsonProperty(IMPORT_ENROLLMENT) ImportEnrollment importEnrollment,
            @JsonProperty(IMPORT_EVENT) ImportEvent importEvent,
            @JsonProperty(IMPORT_CONFLICT) List<ImportConflict> importConflicts) {
        return new AutoValue_ImportSummary(importCount, importStatus,
                responseType, reference, importEnrollment, importEvent,
                safeUnmodifiableList(importConflicts));
    }

}
