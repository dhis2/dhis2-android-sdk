package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RuleFunctionModulus extends RuleFunction {
    static final String D2_MODULUS = "d2:modulus";

    @Nonnull
    static RuleFunctionModulus create() {
        return new RuleFunctionModulus();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Al most dividend and divisor arguments was expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(Integer.parseInt(arguments.get(0)) % Integer.parseInt(arguments.get(1)));
    }
}
