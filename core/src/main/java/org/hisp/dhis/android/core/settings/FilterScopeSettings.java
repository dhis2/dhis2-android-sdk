package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_FilterScopeSettings.Builder.class)
public abstract class FilterScopeSettings<T> {

    public abstract Map<T, FilterSetting> globalSettings();

    public abstract Map<String, Map<T, FilterSetting>> specificSettings();

    public abstract Builder<T> toBuilder();

    public static <T> Builder<T> builder() {
        return new AutoValue_FilterScopeSettings.Builder<>();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder<T> {

        public abstract Builder<T> globalSettings(Map<T, FilterSetting> globalSettings);

        public abstract Builder<T> specificSettings(Map<String, Map<T, FilterSetting>> specificSettings);

        public abstract FilterScopeSettings<T> build();
    }
}
