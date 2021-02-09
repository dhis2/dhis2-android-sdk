package org.hisp.dhis.android.core.settings;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FilterSorting {

    public abstract FiltersSet home();

    public abstract FilterScopesSettings dataSettings();

    public abstract FilterScopesSettings programSettings();
}
