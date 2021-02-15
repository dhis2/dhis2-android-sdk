package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_FilterScopesSettings.Builder.class)
public abstract class FilterScopesSettings<T> {

    public abstract FiltersSet<T> globalSettings();

    public abstract Map<String, FiltersSet<T>> specificSettings();

    public abstract Builder<T> toBuilder();

    public static <T> Builder<T> builder() {
        return new AutoValue_FilterScopesSettings.Builder<>();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder<T> {

        public abstract Builder<T> globalSettings(FiltersSet<T>  globalSettings);

        public abstract Builder<T> specificSettings(Map<String, FiltersSet<T>> specificSettings);

        public abstract FilterScopesSettings<T> build();
    }
}
