package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Produces the modulus when dividing the first with the second argument.
 */
final class RuleFunctionModulus extends RuleFunction {
    static final String D2_MODULUS = "d2:modulus";

    @Nonnull
    static RuleFunctionModulus create() {
        return new RuleFunctionModulus();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments == null) {
            throw new IllegalArgumentException(
                    "Al most dividend and divisor arguments was expected");
        } else if (arguments.size() != 2) {
            throw new IllegalArgumentException(
                    "Al most dividend and divisor arguments was expected, " +
                            arguments.size() + " were supplied");
        }

        return String.valueOf(toInt(arguments.get(0)) % toInt(arguments.get(1)));
    }

    private static int toInt(@Nullable final String str) {
        try {
            return (int) Double.parseDouble(str);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }
}
