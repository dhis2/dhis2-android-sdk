package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramFilters.Builder.class)
public abstract class ProgramFilters {

    public abstract Map<ProgramFilter, FilterSetting> globalSettings();

    public abstract Map<String, Map<ProgramFilter, FilterSetting>> specificSettings();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_ProgramFilters.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder globalSettings(Map<ProgramFilter, FilterSetting> globalSettings);

        public abstract Builder specificSettings(Map<String, Map<ProgramFilter, FilterSetting>> specificSettings);

        public abstract ProgramFilters build();
    }
}
