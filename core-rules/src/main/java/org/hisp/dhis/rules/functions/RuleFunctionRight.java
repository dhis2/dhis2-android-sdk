package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionRight extends RuleFunction {
    static final String D2_RIGHT = "d2:right";

    @Nonnull
    static RuleFunctionRight create() {
        return new RuleFunctionRight();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments == null) {
            throw new IllegalArgumentException("Two argument is expected");
        } else if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return right(arguments.get(0), Integer.parseInt(arguments.get(1)));
    }

    /**
     * Return a substring of the end of a string up to a given length.
     *
     * @param inputString input value.
     * @param length      of the substring.
     * @return the right substring.
     */
    private static String right(String inputString, int length) {
        if (inputString == null) {
            return "";
        }
        int safeLength = Math.min(Math.max(0, length), inputString.length());
        return inputString.substring(inputString.length() - safeLength);
    }
}
