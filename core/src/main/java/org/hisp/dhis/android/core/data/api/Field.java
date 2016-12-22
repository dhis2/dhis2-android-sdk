package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Property;

@AutoValue
public abstract class Field<Parent, Child> implements Property<Parent, Child> {
    public static <T, K> Field<T, K> create(@NonNull String name) {
        return new AutoValue_Field<>(name);
    }
}
