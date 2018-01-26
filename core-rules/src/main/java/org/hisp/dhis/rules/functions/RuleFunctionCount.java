package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Counts the number of values that is entered for the source field in the argument.
 * The source field parameter is the name of one of the defined source fields in the program
 */
final class RuleFunctionCount extends RuleFunction {
    static final String D2_COUNT = "d2:count";

    @Nonnull
    static RuleFunctionCount create() {
        return new RuleFunctionCount();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (valueMap == null) {
            throw new IllegalArgumentException("valueMap is expected");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(count(arguments.get(0), valueMap));
    }

    private Integer count(String variableName, Map<String, RuleVariableValue> valueMap) {
        RuleVariableValue ruleVariableValue = valueMap.get(variableName);
        Integer count = 0;
        if (ruleVariableValue != null && ruleVariableValue.value() != null) {
            if (ruleVariableValue.candidates().size() > 0) {
                count = ruleVariableValue.candidates().size();
            } else {
                //If there is a value found for the variable, the count is 1 even if there is
                // no list of alternate values
                //This happens for variables of "DATAELEMENT_CURRENT_STAGE" and "TEI_ATTRIBUTE"
                count = 1;
            }
        }
        return count;
    }
}
