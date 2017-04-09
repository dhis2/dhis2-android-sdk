package org.hisp.dhis.android.rules.models.variables;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class DataValue {

    @Nonnull
    public abstract String field();

    @Nonnull
    public abstract String value();

    @Nonnull
    public static DataValue create(@Nonnull String field, @Nonnull String value) {
        return new AutoValue_DataValue(field, value);
    }
}
