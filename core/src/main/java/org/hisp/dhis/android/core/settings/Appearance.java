package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Appearance {

    @JsonProperty
    public abstract FilterSorting filterSorting();
}
