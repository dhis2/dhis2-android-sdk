package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * valuates to true if the input text is an exact match with the supplied
 * regular expression pattern. The regular expression needs to be escaped.
 */
final class RuleFunctionValidatePattern extends RuleFunction {
    static final String D2_VALIDATE_PATTERN = "d2:validatePattern";

    @Nonnull
    static RuleFunctionValidatePattern create() {
        return new RuleFunctionValidatePattern();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments == null) {
            throw new IllegalArgumentException("One argument is expected");
        } else if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(validatePattern(arguments.get(1), arguments.get(0)));
    }

    /**
     * Function which validate pattern
     *
     * @param patternString pattern escaped
     * @param inputToValidate string to be evaluate
     * @return return true if the pattern match or false if the pattern doesn't match
     */
    private static Boolean validatePattern(String patternString, String inputToValidate) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(inputToValidate);
        return matcher.matches();
    }

}
