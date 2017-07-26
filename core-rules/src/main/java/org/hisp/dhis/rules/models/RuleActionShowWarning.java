package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionShowWarning extends RuleActionMessage {

    @Nonnull
    public static RuleActionShowWarning create(@Nullable String content,
            @Nullable String data, @Nonnull String field) {
        if (content == null && data == null) {
            throw new IllegalArgumentException("Both content and data must not be null");
        }

        return new AutoValue_RuleActionShowWarning(content == null ? "" :
                content, data == null ? "" : data, field);
    }
}
