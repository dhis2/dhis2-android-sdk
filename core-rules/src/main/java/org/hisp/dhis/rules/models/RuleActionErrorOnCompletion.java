package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionErrorOnCompletion extends RuleActionMessage {

    @Nonnull
    public static RuleActionErrorOnCompletion create(@Nullable String content,
            @Nullable String data, @Nullable String field) {
        if (content == null && data == null && field == null) {
            throw new IllegalArgumentException("Content, data and field" +
                    " must not be null at the same time");
        }

        return new AutoValue_RuleActionErrorOnCompletion(content, data, field);
    }
}
