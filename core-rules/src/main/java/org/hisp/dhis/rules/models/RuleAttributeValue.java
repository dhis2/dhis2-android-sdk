package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleAttributeValue {

    @Nonnull
    public abstract String trackedEntityAttribute();

    @Nonnull
    public abstract String value();

    @Nonnull
    public static RuleAttributeValue create(@Nonnull String attribute, @Nonnull String value) {
        return new AutoValue_RuleAttributeValue(attribute, value);
    }
}
