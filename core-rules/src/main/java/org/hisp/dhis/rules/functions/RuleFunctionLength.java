package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleFunctionLength extends RuleFunction {
    static final String D2_LENGTH = "d2:length";

    @Nonnull
    static RuleFunctionLength create() {
        return new RuleFunctionLength();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {

        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.size() != 1) {
            throw new IllegalArgumentException("One argument was expected, " +
                    arguments.size() + " were supplied");
        }

        int length = 0;

        if (arguments.get(0) != null) {
            length = arguments.get(0).length();
        }

        return String.valueOf(length);
    }
}
