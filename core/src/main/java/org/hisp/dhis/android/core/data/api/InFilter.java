package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

@AutoValue
public abstract class InFilter<T, K> implements Filter<T, K> {

    public static <T, K> Filter<T, K> create(@NonNull Field<T, K> field,
                                             @Nullable Collection<String> values) {
        //If the filter is incomplete, returning null, tells Retrofit that this filter should not be included.
        if (values == null || values.isEmpty()) {
            return null;
        }
        return new AutoValue_InFilter<>(field, "in", Collections.unmodifiableCollection(values));
    }

    @Override
    public String generateString() {
        StringBuilder builder = new StringBuilder();
        builder.append(field().name())
                .append(':')
                .append(operator())
                .append(":[");
        //a list of values:
        Iterator<String> valuesIterator = values().iterator();
        while (valuesIterator.hasNext()) {
            builder.append(valuesIterator.next());
            if (valuesIterator.hasNext()) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }
}
