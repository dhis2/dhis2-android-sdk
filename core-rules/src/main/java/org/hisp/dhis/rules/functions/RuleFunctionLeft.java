package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionLeft extends RuleFunction {
    static final String D2_LEFT = "d2:left";

    @Nonnull
    static RuleFunctionLeft create() {
        return new RuleFunctionLeft();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return left(arguments.get(0),Integer.parseInt(arguments.get(1)));
    }

    /**
     * Return a substring from the beginning of a string up to a given length.
     *
     * @param inputString input value.
     * @param length of the substring.
     * @return the left substring.
     */
    private static String left(String inputString, int length) {
        if (inputString == null)
            return "";
        int safeLength = Math.min(Math.max(0, length), inputString.length());
        return inputString.substring(0, safeLength);
    }
}
