package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Returns the number of numeric zero and positive values among the given object arguments.
 * Can be provided with any number of arguments.
 */
final class RuleFunctionZpvc extends RuleFunction {
    static final String D2_ZPVC = "d2:zpvc";

    @Nonnull
    static RuleFunctionZpvc create() {
        return new RuleFunctionZpvc();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {

        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Al most one argument was expected, " +
                    arguments.size() + " were supplied");
        }

        int count = 0;

        for (String value : arguments) {
            if (value != null && toDouble(value, -1) >= 0d) {
                count++;
            }
        }

        return String.valueOf(count);
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
