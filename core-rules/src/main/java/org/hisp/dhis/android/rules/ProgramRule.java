package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class ProgramRule {

    // @Nullable
    public abstract String programStage();

    // @NonNull
    public abstract String condition();

    // @Nullable
    public abstract Integer priority();

    // @NonNull
    public abstract List<ProgramRuleAction> actions();
}
