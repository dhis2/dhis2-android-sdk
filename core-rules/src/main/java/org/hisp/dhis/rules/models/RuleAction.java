package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// ToDo: split this model into two pieces: tracked entity attribute and data element
@AutoValue
public abstract class RuleAction {

    @Nonnull
    public abstract RuleActionType programRuleActionType();

    @Nullable
    public abstract String programStage();

    @Nullable
    public abstract String programStageSection();

    @Nullable
    public abstract String programStageIndicator();

    @Nullable
    public abstract String trackedEntityAttribute();

    @Nullable
    public abstract String dataElement();

    @Nullable
    public abstract String content();

    @Nullable
    public abstract String location();

    @Nullable
    public abstract String data();

    @Nonnull
    public static RuleAction create(
            @Nonnull RuleActionType actionType,
            @Nullable String programStage,
            @Nullable String programStageSection,
            @Nullable String programStageIndicator,
            @Nullable String trackedEntityAttribute,
            @Nullable String dataElement,
            @Nullable String content,
            @Nullable String location,
            @Nullable String data) {
        return new AutoValue_RuleAction(actionType, programStage, programStageSection,
                programStageIndicator, trackedEntityAttribute, dataElement,
                content, location, data);
    }
}
