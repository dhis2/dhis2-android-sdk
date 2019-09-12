package org.hisp.dhis.android.core.fileresource.internal;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.fileresource.FileResource;

@AutoValue
@JsonDeserialize(builder = AutoValue_FileResourceType.Builder.class)
abstract class FileResourceType {

    @Nullable
    @JsonProperty
    abstract FileResource fileResource();

    public static Builder builder() {
        return new AutoValue_FileResourceType.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder fileResource(FileResource fileResource);

        public abstract FileResourceType build();
    }
}
