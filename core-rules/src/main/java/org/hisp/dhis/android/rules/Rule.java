package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class Rule {

    @Nullable
    public abstract String programStage();

    @Nullable
    public abstract Integer priority();

    @Nonnull
    public abstract String condition();

    @Nonnull
    public abstract List<RuleAction> actions();

    @Nonnull
    public static Rule create(@Nullable String programStage, @Nullable Integer priority,
            @Nonnull String condition, @Nonnull List<RuleAction> actions) {
        return new AutoValue_Rule(programStage, priority, condition, actions);
    }

    @Nonnull
    public static Rule create(@Nullable Integer priority, @Nonnull String condition,
            @Nonnull List<RuleAction> actions) {
        return new AutoValue_Rule(null, priority, condition, actions);
    }

    @Nonnull
    public static Rule create(@Nonnull String condition, @Nonnull List<RuleAction> actions) {
        return new AutoValue_Rule(null, null, condition, actions);
    }
}
