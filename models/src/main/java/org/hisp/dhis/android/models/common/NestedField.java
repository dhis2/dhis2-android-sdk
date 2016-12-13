package org.hisp.dhis.android.models.common;

import com.google.auto.value.AutoValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

@AutoValue
public abstract class NestedField<Parent, Child> implements Property<Parent, Child> {
    public abstract List<Property<Parent, ?>> children();

    public static <T, K> NestedField<T, K> create(@Nonnull String name) {
        return new AutoValue_NestedField<>(name, Collections.<Property<T, ?>>emptyList());
    }

    @SafeVarargs
    public final NestedField<Parent, ?> with(Property<Parent, ?>... properties) {
        if (properties != null) {
            return new AutoValue_NestedField<>(name(), Arrays.asList(properties));
        }

        return create(name());
    }
}
