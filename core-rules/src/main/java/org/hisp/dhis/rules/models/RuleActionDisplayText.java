package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class RuleActionDisplayText extends RuleActionText {

    @Nonnull
    public static RuleActionDisplayText createForFeedback(
            @Nullable String content, @Nullable String data) {
        if (content == null && data == null) {
            throw new IllegalArgumentException("Both content and data must not be null");
        }

        return new AutoValue_RuleActionDisplayText(content == null ? "" : content,
                data == null ? "" : data, LOCATION_FEEDBACK_WIDGET);
    }

    @Nonnull
    public static RuleActionDisplayText createForIndicators(
            @Nullable String content, @Nullable String data) {
        if (content == null && data == null) {
            throw new IllegalArgumentException("Both content and data must not be null");
        }

        return new AutoValue_RuleActionDisplayText(content == null ? "" : content,
                data == null ? "" : data, LOCATION_INDICATOR_WIDGET);
    }
}
