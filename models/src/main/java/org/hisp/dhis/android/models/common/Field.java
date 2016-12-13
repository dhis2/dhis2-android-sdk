package org.hisp.dhis.android.models.common;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class Field<Parent, Child> implements Property<Parent, Child> {
    public static <T, K> Field<T, K> create(@Nonnull String name) {
        return new AutoValue_Field<>(name);
    }
}
