package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class RuleFunctionSubString extends RuleFunction {
    static final String D2_SUBSTRING = "d2:substring";

    @Nonnull
    public static RuleFunctionSubString create() {
        return new RuleFunctionSubString();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 3) {
            throw new IllegalArgumentException("three argument were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(substring(arguments.get(0), Integer.parseInt(arguments.get(1)), Integer.parseInt(arguments.get(2))));
    }

    /**
     * Return a substring from a start index up to an end index (not included).
     *
     * @param inputString input value.
     * @param startIndex start index.
     * @param endIndex end index (not included)
     * @return the substring.
     */
    public static String substring(String inputString, int startIndex, int endIndex) {
        if (inputString == null)
            return "";
        int safeStartIndex = Math.min(Math.max(0, startIndex), inputString.length());
        int safeEndIndex = Math.min(Math.max(0, endIndex), inputString.length());
        return inputString.substring(safeStartIndex, safeEndIndex);
    }
}
