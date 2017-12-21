package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import static org.hisp.dhis.rules.functions.RuleFunctionDaysBetween.daysBetween;

/**
 * Produces the number of full weeks between the first and second argument.
 * If the second argument date is before the first argument the return value
 * will be the negative number of weeks between the two dates.
 * The static date format is 'yyyy-MM-dd'.
 */
final class RuleFunctionWeeksBetween extends RuleFunction {
    static final String D2_WEEKS_BETWEEN = "d2:weeksBetween";

    @Nonnull
    static RuleFunctionWeeksBetween create() {
        return new RuleFunctionWeeksBetween();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {

        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(daysBetween(arguments.get(0), arguments.get(1)) / 7);
    }
}
