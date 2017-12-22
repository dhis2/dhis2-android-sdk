package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Counts the number of values that is zero or positive entered for the source field in the argument
 * . The source field parameter is the name of one of the defined source fields in the program
 */
final class RuleFunctionCountIfZeroPos extends RuleFunction {
    static final String D2_COUNT_IF_ZERO_POS = "d2:countIfZeroPos";

    @Nonnull
    static RuleFunctionCountIfZeroPos create() {
        return new RuleFunctionCountIfZeroPos();
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

        return String.valueOf(countIfZeroPos(arguments.get(0), valueMap));
    }

    private Integer countIfZeroPos(String variableName,
            Map<String, RuleVariableValue> valueMap) {
        RuleVariableValue ruleVariableValue = valueMap.get(variableName);
        Integer count = 0;
        if (ruleVariableValue != null) {
            if (ruleVariableValue.value() != null) {
                if (ruleVariableValue.candidates() != null
                        && ruleVariableValue.candidates().size() > 0) {
                    for (String candidateValue : ruleVariableValue.candidates()) {
                        if (ifZeroPos(candidateValue)) {
                            count++;
                        }
                    }
                } else if (ruleVariableValue.value() != null) {
                    String value = ruleVariableValue.value().replace("'", "");

                    if (ifZeroPos(value)) {
                        return 1;
                    }
                }
            }
        }
        return count;
    }

    private boolean ifZeroPos(String candidateValue) {
        boolean ifZeroPos = false;
        try {
            if (candidateValue != null && Integer.parseInt(candidateValue) >= 0) {
                ifZeroPos = true;
            }
        } catch (NumberFormatException e) {
            //It's not important, this value simply don't count as zero or positive
        }

        return ifZeroPos;
    }
}
