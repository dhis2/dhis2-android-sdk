package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

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
        if (arguments.size() != 3) {
            throw new IllegalArgumentException("Three arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return (split(arguments.get(0), arguments.get(1), Integer.parseInt(arguments.get(2))));
    }

    /**
     * Split a string given a separator and get the nth item.
     *
     * @param inputString input value.
     * @param splitString separator value.
     * @param fieldIndex item index to get from the split.
     * @return the field after split.
     */
    private static String split(String inputString, String splitString, int fieldIndex) {
        if (inputString == null || splitString == null)
            return "";
        String[] fields = inputString == null ? new String[0] : inputString.split(Pattern.quote(splitString));
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[fieldIndex] : "";
    }

}
