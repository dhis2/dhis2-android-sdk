package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import static org.hisp.dhis.rules.functions.RuleFunctionDaysBetween.daysBetween;

final class RuleFunctionWeeksBetween extends RuleFunction {
    static final String D2_WEEKS_BETWEEN = "d2:weeksBetween";

    @Nonnull
    public static RuleFunctionWeeksBetween create() {
        return new RuleFunctionWeeksBetween();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(daysBetween(arguments.get(0), arguments.get(1)) / 7);
    }
}
