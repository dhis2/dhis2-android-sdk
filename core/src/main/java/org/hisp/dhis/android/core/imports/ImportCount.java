package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ImportCount {
    private static final String IMPORTED = "imported";
    private static final String UPDATED = "updated";
    private static final String DELETED = "deleted";
    private static final String IGNORED = "ignored";

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

    @JsonCreator
    public static ImportCount create(
            @JsonProperty(IMPORTED) Integer imported,
            @JsonProperty(UPDATED) Integer updated,
            @JsonProperty(DELETED) Integer deleted,
            @JsonProperty(IGNORED) Integer ignored) {
        return new AutoValue_ImportCount(imported, updated, deleted, ignored);
    }
}
