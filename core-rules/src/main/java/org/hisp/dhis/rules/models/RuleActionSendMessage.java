package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionSendMessage extends RuleAction {

    @Nonnull
    public abstract String notification();

    @Nonnull
    public abstract String data();

    @Nonnull
    public static RuleActionSendMessage create(@Nullable String notification, @Nullable String data) {
        return new AutoValue_RuleActionSendMessage(notification == null ? "": notification, data == null ? "" : data);
    }
}
