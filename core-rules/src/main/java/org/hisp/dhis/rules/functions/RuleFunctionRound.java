package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Rounds the input argument to the nearest whole number.
 */
final class RuleFunctionRound extends RuleFunction {
    static final String D2_ROUND = "d2:round";

    @Nonnull
    static RuleFunctionRound create() {
        return new RuleFunctionRound();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {

        if (arguments == null) {
            throw new IllegalArgumentException("One argument was expected");
        } else if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " was supplied");
        }

        return round(Double.parseDouble(arguments.get(0)));
    }

    private static String round(Double value) {
        return String.valueOf(Math.round(value));
    }
}
