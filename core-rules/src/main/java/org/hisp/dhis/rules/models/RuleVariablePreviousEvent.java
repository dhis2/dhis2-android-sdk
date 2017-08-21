package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariablePreviousEvent extends RuleVariableDataElement {

    @Nonnull
    public static RuleVariablePreviousEvent create(@Nonnull String name,
            @Nonnull String dataElement, @Nonnull RuleValueType valueType) {
        return new AutoValue_RuleVariablePreviousEvent(name, dataElement, valueType);
    }
}
