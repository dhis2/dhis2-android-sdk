package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Property;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class NestedField<Parent, Child> implements Property<Parent, Child> {
    public abstract List<Property<Child, ?>> children();

    public static <T, K> NestedField<T, K> create(@NonNull String name) {
        return new AutoValue_NestedField<>(name, Collections.<Property<K, ?>>emptyList());
    }

    @SafeVarargs
    public final NestedField<Parent, ?> with(Property<Child, ?>... properties) {
        if (properties != null) {
            return new AutoValue_NestedField<>(name(), Arrays.asList(properties));
        }

        return create(name());
    }
}
