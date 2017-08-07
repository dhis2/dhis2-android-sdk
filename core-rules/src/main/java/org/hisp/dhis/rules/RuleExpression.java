package org.hisp.dhis.rules;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

@AutoValue
abstract class RuleExpression {
    static final String VARIABLE_PATTERN = "[A#CV]\\{(\\w+.?\\w*)\\}";
    static final String FUNCTION_PATTERN = "d2:(\\w+.?\\w*)\\( *(([\\d/\\*\\+\\-%\\. ]+)|" +
            "( *'[^']*'))*( *, *(([\\d/\\*\\+\\-%\\. ]+)|'[^']*'))* *\\)";

    static final Pattern VARIABLE_PATTERN_COMPILED = Pattern.compile(VARIABLE_PATTERN);
    static final Pattern FUNCTION_PATTERN_COMPILED = Pattern.compile(FUNCTION_PATTERN);

    @Nonnull
    public abstract String expression();

    @Nonnull
    public abstract Set<String> variables();

    @Nonnull
    public abstract Set<String> functions();

    @Nonnull
    static String unwrapVariableName(@Nonnull String variable) {
        Matcher variableNameMatcher = VARIABLE_PATTERN_COMPILED.matcher(variable);

        // extract variable name
        if (variableNameMatcher.find()) {
            return variableNameMatcher.group(1);
        }

        throw new IllegalArgumentException("Malformed variable: " + variable);
    }

    @Nonnull
    static RuleExpression from(@Nonnull String expression) {
        if (expression == null) {
            throw new NullPointerException("expression == null");
        }

        Set<String> variables = new HashSet<>();
        Set<String> functions = new HashSet<>();

        Matcher variableMatcher = VARIABLE_PATTERN_COMPILED.matcher(expression);
        Matcher functionMatcher = FUNCTION_PATTERN_COMPILED.matcher(expression);

        // iterate over matched values and aggregate them
        while (variableMatcher.find()) {
            variables.add(variableMatcher.group());
        }

        while (functionMatcher.find()) {
            functions.add(functionMatcher.group());
        }

        return new AutoValue_RuleExpression(expression, Collections.unmodifiableSet(variables),
                Collections.unmodifiableSet(functions));
    }
}