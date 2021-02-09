package org.hisp.dhis.android.core.settings;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FilterConfig {

    public abstract Boolean sort();

    public abstract Boolean filter();
}
