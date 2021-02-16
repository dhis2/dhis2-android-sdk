package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_DataSetFilters.Builder.class)
public abstract class DataSetFilters {

    public abstract Map<DataSetFilter, FilterSetting> globalSettings();

    public abstract Map<String, Map<DataSetFilter, FilterSetting>> specificSettings();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_DataSetFilters.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder globalSettings(Map<DataSetFilter, FilterSetting> globalSettings);

        public abstract Builder specificSettings(Map<String, Map<DataSetFilter, FilterSetting>> specificSettings);

        public abstract DataSetFilters build();
    }
}
