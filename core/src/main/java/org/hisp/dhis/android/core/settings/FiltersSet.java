package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_FiltersSet.Builder.class)
public abstract class FiltersSet<T> {

    @JsonProperty
    public abstract Map<T, FilterConfig> filters();

    public abstract Builder<T> toBuilder();

    public static <T> Builder<T> builder() {
        return new AutoValue_FiltersSet.Builder<>();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder<T> {

        public abstract Builder<T> filters(Map<T, FilterConfig> filters);

        public abstract FiltersSet<T> build();
    }
}
