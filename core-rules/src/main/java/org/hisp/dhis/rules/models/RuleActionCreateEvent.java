package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionCreateEvent extends RuleAction {

    @Nonnull
    public abstract String content();

    @Nonnull
    public abstract String data();

    @Nonnull
    public abstract String programStage();

    @Nonnull
    public static RuleActionCreateEvent create(@Nullable String content,
            @Nullable String data, @Nonnull String programStage) {
        return new AutoValue_RuleActionCreateEvent(content == null ? "" : content,
                data == null ? "" : data, programStage);
    }
}
