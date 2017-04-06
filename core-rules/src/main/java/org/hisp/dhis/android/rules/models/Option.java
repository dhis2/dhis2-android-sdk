package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Option {

    // @NonNull
    public abstract String name();

    // @NonNull
    public abstract String code();
}
