package org.hisp.dhis.rules;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

@AutoValue
abstract class RuleFunctionCall {
    private static final Pattern JUST_PARAMS_PATTERN = Pattern.compile("(^[^\\(]+\\()|\\)");
    private static final Pattern SPLIT_PARAMS_PATTERN = Pattern.compile("(('[^']+')|([^,]+))");

    @Nonnull
    public abstract String functionCall();

    @Nonnull
    public abstract String functionName();

    @Nonnull
    public abstract List<String> arguments();

    @Nonnull
    static RuleFunctionCall from(@Nonnull String functionCall) {
        if (functionCall == null) {
            throw new NullPointerException("functionCall == null");
        }

        Matcher functionNameMatcher = RuleExpression
                .FUNCTION_PATTERN_COMPILED.matcher(functionCall);

        if (!functionNameMatcher.find()) {
            throw new IllegalArgumentException("Malformed function call");
        }

        // Function name which later can be used for looking up functions
        String functionName = functionNameMatcher.group(1);

        // strip all special characters and leave just parameters
        String justParameters = JUST_PARAMS_PATTERN.matcher(functionCall).replaceAll("");

        // match each parameter
        Matcher splitParametersMatcher = SPLIT_PARAMS_PATTERN.matcher(justParameters);

        // aggregate matched parameters into list
        List<String> params = new ArrayList<>();
        while (splitParametersMatcher.find()) {
            params.add(splitParametersMatcher.group().trim());
        }

        return new AutoValue_RuleFunctionCall(functionCall, String.format(Locale.US,
                "d2:%s", functionName), Collections.unmodifiableList(params));
    }
}