package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Counts the number of matching values that is entered for the source field in the first argument.
 * Only occurrences that matches the second argument is counted.
 * The source field parameter is the name of one of the defined source fields in the program
 */
final class RuleFunctionCountIfValue extends RuleFunction {
    static final String D2_COUNT_IF_VALUE = "d2:countIfValue";

    @Nonnull
    static RuleFunctionCountIfValue create() {
        return new RuleFunctionCountIfValue();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (valueMap == null) {
            throw new IllegalArgumentException("valueMap is expected");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("Two arguments is expected");
        } else if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments was expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(countIfValue(arguments.get(0), arguments.get(1), valueMap));
    }

    private static Integer countIfValue(String variableName, String valueToCompare,
            Map<String, RuleVariableValue> valueMap) {

        RuleVariableValue ruleVariableValue = valueMap.get(variableName);
        Integer count = 0;

        if (ruleVariableValue != null && valueToCompare != null && !valueToCompare.isEmpty()
                && ruleVariableValue.value() != null) {
            if (ruleVariableValue.candidates().size() > 0) {
                count = countCandidates(ruleVariableValue, valueToCompare);
            } else {
                count = countValue(ruleVariableValue, valueToCompare);
            }
        }
        return count;
    }

    private static int countCandidates(RuleVariableValue ruleVariableValue, String valueToCompare) {
        int count = 0;
        for (String candidateValue : ruleVariableValue.candidates()) {
            if (candidateValue != null && candidateValue.equals(valueToCompare)) {
                count++;
            }
        }
        return count;
    }

    private static int countValue(RuleVariableValue ruleVariableValue,
            String valueToCompare) {
        String value = ruleVariableValue.value().replace("'", "");

        if (valueToCompare.equals(value)) {
            return 1;
        }
        return 0;
    }

}
