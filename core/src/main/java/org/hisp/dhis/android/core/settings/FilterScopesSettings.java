package org.hisp.dhis.android.core.settings;

import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
public abstract class FilterScopesSettings<T> {

    public abstract Map<ProgramFilter, FilterConfig> globalSettings();

    public abstract Map<String, FiltersSet<T>> specificSettings();
}
