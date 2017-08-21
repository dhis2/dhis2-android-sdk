package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableCurrentEvent extends RuleVariableDataElement {

    @Nonnull
    public static RuleVariableCurrentEvent create(@Nonnull String name,
            @Nonnull String dataElement, @Nonnull RuleValueType dataElementValueType) {
        return new AutoValue_RuleVariableCurrentEvent(name, dataElement, dataElementValueType);
    }
}
