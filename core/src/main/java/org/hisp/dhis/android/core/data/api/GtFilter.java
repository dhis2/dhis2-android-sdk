package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.Iterator;

@AutoValue
public abstract class GtFilter<T, K> implements Filter<T, K> {
    public static <T, K> Filter<T, K> create(@NonNull Field<T, K> field, @Nullable String value) {
        //If the filter is incomplete, returning null, tells Retrofit that this filter should not be included.
        if (value == null || value.equals("")) {
            return null;
        }
        return new AutoValue_GtFilter<>(field, "gt",
                Collections.unmodifiableCollection(Collections.singletonList(value)));
    }

    @Override
    public String generateString() {
        StringBuilder builder = new StringBuilder();
        builder.append(field().name())
                .append(':')
                .append(operator())
                .append(':');

        Iterator<String> valuesIterator = values().iterator();
        builder.append(valuesIterator.next());
        return builder.toString();
    }
}

