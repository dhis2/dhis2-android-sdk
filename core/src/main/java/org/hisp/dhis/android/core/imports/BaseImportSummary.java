package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

public abstract class BaseImportSummary implements ImportSummary {

    private static final String IMPORT_COUNT = "importCount";
    private static final String IMPORT_STATUS = "status";
    private static final String RESPONSE_TYPE = "responseType";
    private static final String REFERENCE = "reference";
    private static final String IMPORT_CONFLICT = "conflicts";
    private static final String DESCRIPTION = "description";

    @NonNull
    @JsonProperty(IMPORT_COUNT)
    public abstract ImportCount importCount();

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus status();

    @NonNull
    @JsonProperty(RESPONSE_TYPE)
    public abstract String responseType();

    //TODO: Reference SHOULD be annotated with NotNull. This is just a bug in ImportSummary response from server.
    @Nullable
    @JsonProperty(REFERENCE)
    public abstract String reference();

    @Nullable
    @JsonProperty(IMPORT_CONFLICT)
    public abstract List<ImportConflict> conflicts();

    @Nullable
    @JsonProperty(DESCRIPTION)
    public abstract String description();

    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder<T extends Builder> {

        public abstract T importCount(ImportCount importCount);

        public abstract T status(ImportStatus status);

        public abstract T responseType(String responseType);

        public abstract T reference(String reference);

        public abstract T conflicts(List<ImportConflict> conflicts);

        public abstract T description(String description);
    }
}
