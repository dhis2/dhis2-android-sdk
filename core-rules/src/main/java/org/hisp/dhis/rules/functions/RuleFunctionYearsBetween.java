package org.hisp.dhis.rules.functions;

import static org.hisp.dhis.rules.functions.RuleFunctionMonthsBetween.monthsBetween;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Produces the number of years between the first and second argument.
 * If the second argument date is before the first argument the return value
 * will be the negative number of years between the two dates.
 * The static date format is 'yyyy-MM-dd'.
 */
final class RuleFunctionYearsBetween extends RuleFunction {
    static final String D2_YEARS_BETWEEN = "d2:yearsBetween";

    @Nonnull
    static RuleFunctionYearsBetween create() {
        return new RuleFunctionYearsBetween();
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

        return String.valueOf(monthsBetween(arguments.get(0), arguments.get(1)) / 12);
    }
}
