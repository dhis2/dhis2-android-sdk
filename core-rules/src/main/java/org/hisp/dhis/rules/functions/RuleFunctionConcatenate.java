package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RuleFunctionConcatenate extends RuleFunction {
    static final String D2_CONCATENATE = "d2:concatenate";

    @Nonnull
    static RuleFunctionConcatenate create() {
        return new RuleFunctionConcatenate();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("Al most two arguments was expected, " +
                    arguments.size() + " were supplied");
        }

        String finalValue = "";

        for (String value : arguments) {
            finalValue += value;
        }

        return finalValue;
    }
}
