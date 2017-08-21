package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableAttribute extends RuleVariable {

    @Nonnull
    public abstract String trackedEntityAttribute();

    @Nonnull
    public abstract RuleValueType trackedEntityAttributeType();

    @Nonnull
    public static RuleVariableAttribute create(@Nonnull String name,
            @Nonnull String attribute, @Nonnull RuleValueType attributeType) {
        return new AutoValue_RuleVariableAttribute(name, attribute, attributeType);
    }
}
