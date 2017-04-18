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
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("[A#CV]\\{(\\w+.?\\w*)\\}");

    @Nonnull
    public abstract List<String> variables();

    @Nonnull
    static RuleExpression from(@Nonnull String expression) {
        if (expression == null) {
            throw new NullPointerException("expression == null");
        }

        List<String> variables = new ArrayList<>();

        // iterate over matched values and aggregate them
        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        while (matcher.find()) {
            variables.add(matcher.group());
        }

        return new AutoValue_RuleExpression(Collections.unmodifiableList(variables));
    }
}