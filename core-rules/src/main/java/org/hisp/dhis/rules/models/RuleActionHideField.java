package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionHideField extends RuleAction {

    /**
     * @return a message to show to user
     * when a target field is hidden.
     */
    @Nonnull
    public abstract String content();

    /**
     * @return uid of the target field to hide.
     * It can be both dataElement and trackedEntityAttribute.
     */
    @Nonnull
    public abstract String field();

    @Nonnull
    public static RuleActionHideField create(
            @Nullable String content, @Nonnull String field) {
        return new AutoValue_RuleActionHideField(content == null ? "" : content, field);
    }
}
