package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public abstract class BaseImportSummaries {

    private static final String RESPONSE_TYPE = "responseType";
    private static final String IMPORT_STATUS = "status";

    private static final String IMPORTED = "imported";
    private static final String UPDATED = "updated";
    private static final String DELETED = "deleted";
    private static final String IGNORED = "ignored";

    @NonNull
    @JsonProperty(IMPORT_STATUS)
    public abstract ImportStatus status();

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

    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder<T extends Builder> {

        public abstract T status(ImportStatus status);

        public abstract T responseType(String responseType);

        public abstract T imported(Integer imported);

        public abstract T updated(Integer updated);

        public abstract T deleted(Integer deleted);

        public abstract T ignored(Integer ignored);
    }
}
