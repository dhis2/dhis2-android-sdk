package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * Split the text by delimiter, and keep the nth element(0 is the first).
 */
final class RuleFunctionSplit extends RuleFunction {
    static final String D2_SPLIT = "d2:split";

    @Nonnull
    static RuleFunctionSplit create() {
        return new RuleFunctionSplit();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments == null) {
            throw new IllegalArgumentException("Three arguments is expected");
        } else if (arguments.size() != 3) {
            throw new IllegalArgumentException("Three arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return split(arguments.get(0), arguments.get(1), Integer.parseInt(arguments.get(2)));
    }

    private static String split(String inputString, String splitString, int fieldIndex) {
        if (inputString == null || splitString == null) {
            return "";
        }
        String[] fields = inputString.split(Pattern.quote(splitString));
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[fieldIndex] : "";
    }

}
