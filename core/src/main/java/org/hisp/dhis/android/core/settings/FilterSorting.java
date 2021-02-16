package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_FilterSorting.Builder.class)
public abstract class FilterSorting {

    public abstract Map<HomeFilter, FilterSetting> home();

    public abstract DataSetFilters dataSetSettings();

    public abstract ProgramFilters programSettings();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_FilterSorting.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder home(Map<HomeFilter, FilterSetting> home);

        public abstract Builder dataSetSettings(DataSetFilters dataSetSettings);

        public abstract Builder programSettings(ProgramFilters programSettings);

        public abstract FilterSorting build();
    }
}
