package org.hisp.dhis.rules.functions;


import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public class RuleFunctionValidatePattern extends RuleFunction {
    static final String D2_VALIDATE_PATTERN = "d2:validatePattern";

    @Nonnull
    public static RuleFunctionValidatePattern create() {
        return new RuleFunctionValidatePattern();
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Two arguments were expected, " +
                    arguments.size() + " were supplied");
        }

        return String.valueOf(validatePattern(arguments.get(0), arguments.get(1)));
    }

    /**
     * Function which validate pattern
     *
     * @param patternString pattern escaped
     * @param inputToValidate string to be evaluate
     * @return return true if the pattern match or false if the pattern doesn't match
     */
    private Boolean validatePattern(String patternString, String inputToValidate) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(inputToValidate);
        boolean matchFound = matcher.matches();
        return matchFound;
    }

}
