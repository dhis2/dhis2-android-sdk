package org.hisp.dhis.android.core.settings;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FilterSorting {

    public abstract FiltersSet<HomeFilter> home();

    public abstract FilterScopesSettings<DataSetFilter> dataSettings();

    public abstract FilterScopesSettings<ProgramFilter> programSettings();
}
