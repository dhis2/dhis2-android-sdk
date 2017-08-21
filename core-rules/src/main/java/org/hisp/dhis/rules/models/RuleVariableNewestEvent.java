package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableNewestEvent extends RuleVariableDataElement {

    @Nonnull
    public static RuleVariableNewestEvent create(@Nonnull String name,
            @Nonnull String dataElement, @Nonnull RuleValueType dataElementValueType) {
        return new AutoValue_RuleVariableNewestEvent(name, dataElement, dataElementValueType);
    }
}
