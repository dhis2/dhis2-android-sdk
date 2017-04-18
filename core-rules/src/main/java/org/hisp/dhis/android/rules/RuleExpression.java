package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

@AutoValue
abstract class RuleExpression {
    static final String VARIABLE_PATTERN = "[A#CV]\\{(\\w+.?\\w*)\\}";
    static final Pattern VARIABLE_PATTERN_COMPILED = Pattern.compile(VARIABLE_PATTERN);

    @Nonnull
    public abstract String expression();

    @Nonnull
    public abstract List<String> variables();

    @Nonnull
    static RuleExpression from(@Nonnull String expression) {
        if (expression == null) {
            throw new NullPointerException("expression == null");
        }

        List<String> variables = new ArrayList<>();

        // iterate over matched values and aggregate them
        Matcher matcher = VARIABLE_PATTERN_COMPILED.matcher(expression);
        while (matcher.find()) {
            variables.add(matcher.group());
        }

        return new AutoValue_RuleExpression(expression, Collections.unmodifiableList(variables));
    }
}