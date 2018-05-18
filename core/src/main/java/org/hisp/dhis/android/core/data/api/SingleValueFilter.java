package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.Iterator;

@AutoValue
public abstract class SingleValueFilter<T, K> implements Filter<T, K> {

    private static <T, K> Filter<T, K> create(@NonNull Field<T, K> field,
                                              @NonNull String operator,
                                              @Nullable String value) {
        //If the filter is incomplete, returning null, tells Retrofit that this filter should not be included.
        if (value == null || value.equals("")) {
            return null;
        }
        return new AutoValue_SingleValueFilter<>(field, operator,
                Collections.unmodifiableCollection(Collections.singletonList(value)));
    }

    public static <T, K> Filter<T, K> gt(@NonNull Field<T, K> field, @Nullable String value) {
        return create(field, "gt", value);
    }

    public static <T, K> Filter<T, K> eq(@NonNull Field<T, K> field, @Nullable String value) {
        return create(field, "eq", value);
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

