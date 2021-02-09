package org.hisp.dhis.android.core.settings;

import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
public abstract class FilterScopesSettings {

    public abstract Map<Filter, FilterConfig> globalSettings();

    public abstract Map<String, FiltersSet> specificSettings();
}
