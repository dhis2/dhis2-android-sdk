package org.hisp.dhis.android.rules.functions;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RuleFunction {
    static final String DATE_PATTERN = "yyyy-MM-dd";

    @Nonnull
    public abstract String evaluate(@Nonnull List<String> arguments);

    @Nullable
    public static RuleFunction create(@Nonnull String fun) {
        switch (fun) {
            case RuleFunctionDaysBetween.D2_DAYS_BETWEEN:
                return RuleFunctionDaysBetween.create();
            default:
                return null;
        }
    }
}
