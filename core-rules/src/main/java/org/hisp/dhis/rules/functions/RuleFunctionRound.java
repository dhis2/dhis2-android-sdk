package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

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
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("Three arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return round(Double.parseDouble(arguments.get(0)));
    }

    /**
     * Round a number.
     *
     * @param value item
     * @return the number as string.
     */
    private static String round(Double value) {
        return String.valueOf(Math.round(value));
    }
}
