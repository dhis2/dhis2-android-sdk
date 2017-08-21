package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RuleFunction {
    static final String DATE_PATTERN = "yyyy-MM-dd";

    @Nonnull
    public abstract String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap);

    @Nullable
    public static RuleFunction create(@Nonnull String fun) {
        switch (fun) {
            case RuleFunctionDaysBetween.D2_DAYS_BETWEEN:
                return RuleFunctionDaysBetween.create();
            case RuleFunctionWeeksBetween.D2_WEEKS_BETWEEN:
                return RuleFunctionWeeksBetween.create();
            case RuleFunctionHasValue.D2_HAS_VALUE:
                return RuleFunctionHasValue.create();
            case RuleFunctionFloor.D2_FLOOR:
                return RuleFunctionFloor.create();
            case RuleFunctionCeil.D2_CEIL:
                return RuleFunctionCeil.create();
            default:
                return null;
        }
    }
}
