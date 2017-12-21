package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        String finalValue = "";

        if (arguments != null) {
            for (String value : arguments) {
                if (value != null) {
                    finalValue += value;
                }
            }
        }

        return finalValue;
    }
}
