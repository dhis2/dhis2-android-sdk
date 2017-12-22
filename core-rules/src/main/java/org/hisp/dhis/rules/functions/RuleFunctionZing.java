package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Evaluates the argument of type number to zero if the value is negative,
 * otherwise to the value itself.
 */

final class RuleFunctionZing extends RuleFunction {
    static final String D2_ZING = "d2:zing";

    @Nonnull
    static RuleFunctionZing create() {
        return new RuleFunctionZing();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {

        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " were supplied");
        }

        double result = Math.max(0d, toDouble(arguments.get(0), 0.0));
        return String.valueOf(result);
    }

    private static double toDouble(@Nullable final String str, final double defaultValue) {
        if (str == null) {
            return defaultValue;
        }

        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
