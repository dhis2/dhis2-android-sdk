package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleVariableNewestStageEvent extends RuleVariableDataElement {

    @Nonnull
    public abstract String programStage();

    @Nonnull
    public static RuleVariableNewestStageEvent create(@Nonnull String name, @Nonnull String dataElement,
            @Nonnull String programStage, @Nonnull RuleValueType dataElementType) {
        return new AutoValue_RuleVariableNewestStageEvent(name, dataElement,
                dataElementType, programStage);
    }
}
