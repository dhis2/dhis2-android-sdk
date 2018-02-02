package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.utils.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

@AutoValue
public abstract class EqualFilter<T, K> implements Filter<T, K> {

    public static <T, K> Filter<T, K> create(@NonNull Field<T, K> field, @Nullable Collection<String> values) {
        //If the filter is incomplete, returning null, tells Retrofit that this filter should not be included.
        if (values == null || values.isEmpty()) {
            return null;
        }
        return new AutoValue_EqualFilter<>(field, "=", Collections.unmodifiableCollection(values));
    }

    @Override
    public String generateString() {
        StringBuilder builder = new StringBuilder();

        Iterator<String> valuesIterator = values().iterator();
        while (valuesIterator.hasNext()) {
            builder.append(valuesIterator.next());
            if (valuesIterator.hasNext()) {
                builder.append(',');
            }
        }
        return builder.toString();
    }
}

