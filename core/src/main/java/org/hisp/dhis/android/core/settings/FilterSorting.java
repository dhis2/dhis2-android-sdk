package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_FilterSorting.Builder.class)
public abstract class FilterSorting {

    public abstract FiltersSet<HomeFilter> home();

    public abstract FilterScopesSettings<DataSetFilter> dataSettings();

    public abstract FilterScopesSettings<ProgramFilter> programSettings();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_FilterSorting.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder home(FiltersSet<HomeFilter> home);

        public abstract Builder dataSettings(FilterScopesSettings<DataSetFilter> dataSettings);

        public abstract Builder programSettings(FilterScopesSettings<ProgramFilter> programSettings);

        public abstract FilterSorting build();
    }
}
