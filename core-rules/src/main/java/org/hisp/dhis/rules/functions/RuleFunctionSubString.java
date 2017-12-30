package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionSubString extends RuleFunction {
    static final String D2_SUBSTRING = "d2:substring";

    @Nonnull
    static RuleFunctionSubString create() {
        return new RuleFunctionSubString();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments == null) {
            throw new IllegalArgumentException("Three arguments is expected");
        } else if (arguments.size() != 3) {
            throw new IllegalArgumentException("three argument were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(substring(arguments.get(0), Integer.parseInt(arguments.get(1)),
                Integer.parseInt(arguments.get(2))));
    }

    private static String substring(String inputString, int startIndex, int endIndex) {
        if (inputString == null) {
            return "";
        }
        int safeStartIndex = Math.min(Math.max(0, startIndex), inputString.length());
        int safeEndIndex = Math.min(Math.max(0, endIndex), inputString.length());
        return inputString.substring(safeStartIndex, safeEndIndex);
    }
}
