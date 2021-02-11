package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_AppearanceSettings.Builder.class)
public abstract class AppearanceSettings {

    @JsonProperty
    public abstract FilterSorting filterSorting();

    public abstract AppearanceSettings.Builder toBuilder();

    public static AppearanceSettings.Builder builder() {
        return new AutoValue_AppearanceSettings.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder filterSorting(FilterSorting filterSorting);

        public abstract AppearanceSettings build();
    }
}
