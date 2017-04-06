package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class TrackedEntityDataValue {

    @Nonnull
    public abstract Event event();

    @Nonnull
    public abstract String dataElement();

    @Nonnull
    public abstract String value();

    public static TrackedEntityDataValue create(@Nonnull Event event,
            @Nonnull String dataelement, @Nonnull String value) {
        return new AutoValue_TrackedEntityDataValue(event, dataelement, value);
    }
}
