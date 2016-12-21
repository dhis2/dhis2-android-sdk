package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class Filter<T> {

    @NonNull
    public abstract List<Property<T, ?>> fields();

    @NonNull
    public static <K> Filter.Builder<K> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private final List<Property<T, ?>> fields;

        Builder() {
            this.fields = new ArrayList<>();
        }

        @SafeVarargs
        public final Builder<T> fields(@NonNull Property<T, ?>... properties) {
            if (properties == null) {
                throw new NullPointerException();
            }

            fields.addAll(Arrays.asList(properties));
            return this;
        }

        public final Filter<T> build() {
            return new AutoValue_Filter<>(Collections.unmodifiableList(fields));
        }
    }
}
