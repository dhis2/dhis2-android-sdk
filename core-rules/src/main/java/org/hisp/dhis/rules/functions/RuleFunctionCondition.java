package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RuleFunctionCondition extends RuleFunction {
    static final String D2_CONDITION = "d2:condition";

    @Nonnull
    static RuleFunctionCondition create() {
        return new RuleFunctionCondition();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two argument was expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(arguments.get(0).equals(arguments.get(1))?"true":"false");
    }
}
