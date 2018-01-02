package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Produces a string concatenated string from the input parameters.
 * Supports any number of parameters
 */
final class RuleFunctionConcatenate extends RuleFunction {
    static final String D2_CONCATENATE = "d2:concatenate";

    @Nonnull
    static RuleFunctionConcatenate create() {
        return new RuleFunctionConcatenate();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        StringBuilder finalValue = new StringBuilder();

        if (arguments != null) {
            for (String value : arguments) {
                if (value != null) {
                    finalValue.append(value);
                }
            }
        }

        return finalValue.toString();
    }
}
