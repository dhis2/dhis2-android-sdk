package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RuleAction {

    // @NonNull
    // enum ProgramRuleActionType

    // @Nullable
    public abstract String programStage();

    // @Nullable
    public abstract String programStageSection();

    // @Nullable
    public abstract String programStageIndicator();

    // @Nullable
    public abstract String trackedEntityAttribute();

    // @Nullable
    public abstract String dataElement();

    // hardcoded message: takes precedence over data
    // @Nullable
    public abstract String content();

    // @Nullable
    public abstract String location();

    // @Nullable
    public abstract String data();
}
