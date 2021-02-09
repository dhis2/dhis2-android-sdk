package org.hisp.dhis.android.core.settings;

import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
public abstract class FiltersSet<T> {

    public abstract Map<T, FilterConfig> filters();
}
