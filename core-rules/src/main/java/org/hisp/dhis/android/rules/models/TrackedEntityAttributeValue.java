package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class TrackedEntityAttributeValue {

    @Nonnull
    public abstract String trackedEntityAttribute();

    @Nonnull
    public abstract String value();

    @Nonnull
    public static TrackedEntityAttributeValue create(
            @Nonnull String trackedEntityAttribute, @Nonnull String value) {
        return new AutoValue_TrackedEntityAttributeValue(trackedEntityAttribute, value);
    }
}
