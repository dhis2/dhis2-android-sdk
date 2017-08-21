package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionHasValue extends RuleFunction {
    static final String D2_HAS_VALUE = "d2:hasValue";

    @Nonnull
    static RuleFunctionHasValue create() {
        return new RuleFunctionHasValue();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " were supplied");
        }

        // ToDo: make sure that argument names are actually argument names and not values.
        String variableName = arguments.get(0).replace("'", "");
        return String.valueOf(valueMap.get(variableName).value() != null);
    }
}
