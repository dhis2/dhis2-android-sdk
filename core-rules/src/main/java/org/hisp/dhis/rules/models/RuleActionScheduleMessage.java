package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionScheduleMessage extends RuleAction {

    @Nonnull
    public abstract String notification();

    @Nonnull
    public abstract String data();

    @Nonnull
    public static RuleActionScheduleMessage create(@Nullable String notification, @Nullable String data) {
        return new AutoValue_RuleActionScheduleMessage(notification == null ? "" : notification,
                data == null ? "" : data);
    }
}
