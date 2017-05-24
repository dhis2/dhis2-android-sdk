package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonRootName("conflicts")
public abstract class ImportConflict {
    private static final String OBJECT = "object";
    private static final String VALUE = "value";

    @NonNull
    @JsonProperty(OBJECT)
    public abstract String object();

    @NonNull
    @JsonProperty(VALUE)
    public abstract String value();

    @JsonCreator
    public static ImportConflict create(
            @JsonProperty(OBJECT) String object,
            @JsonProperty(VALUE) String value) {
        return new AutoValue_ImportConflict(object, value);
    }

}
