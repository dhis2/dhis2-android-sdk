package org.hisp.dhis.android.core.fileresource.internal;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_FileResourceResponse.Builder.class)
abstract class FileResourceResponse {

    @Nullable
    @JsonProperty
    public abstract FileResourceType response();

    public static Builder builder() {
        return new AutoValue_FileResourceResponse.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder response(FileResourceType response);

        public abstract FileResourceResponse build();
    }
}