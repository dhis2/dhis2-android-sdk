package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionAssign {

    @Nonnull
    public abstract String content();

    @Nonnull
    public abstract String data();

    @Nonnull
    public abstract String field();

    @Nonnull
    public static RuleActionAssign create(@Nullable String content,
            @Nullable String data, @Nullable String field) {
        if (content == null && data == null && field == null) {
            throw new IllegalArgumentException("Either data or field " +
                    "parameters must be not null.");
        }

        return new AutoValue_RuleActionAssign(content == null ? "" : content,
                data == null ? "" : data, field == null ? "" : field);
    }
}
