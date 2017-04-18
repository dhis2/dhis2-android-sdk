package org.hisp.dhis.android.rules;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleExpressionBinder {

    @Nonnull
    private final String ruleExpressionTemplate;

    @Nonnull
    private final RuleExpression ruleExpression;

    @Nonnull
    private final Map<String, String> ruleVariableValues;

    RuleExpressionBinder(@Nonnull String ruleExpressionTemplate,
            @Nonnull RuleExpression ruleExpression,
            @Nonnull Map<String, String> ruleVariableValues) {
        this.ruleExpressionTemplate = ruleExpressionTemplate;
        this.ruleExpression = ruleExpression;
        this.ruleVariableValues = ruleVariableValues;
    }

    RuleExpressionBinder bind(@Nonnull String variable, @Nonnull String value) {
        if (!ruleVariableValues.containsKey(variable)) {
            throw new IllegalArgumentException("Non-existing variable: " + variable);
        }

        ruleVariableValues.put(variable, value);
        return this;
    }

    @Nonnull
    String build() {
        String[] values = new String[ruleVariableValues.size()];

        int index = 0;
        for (Map.Entry<String, String> variableValue : ruleVariableValues.entrySet()) {
            if (variableValue.getValue() == null) {
                throw new IllegalStateException("Value has not been substituted for " +
                        "variable: " + variableValue.getKey());
            }

            values[index++] = variableValue.getValue();
        }

        // yes, this ugly cast is needed to make compiler happy
        return String.format(Locale.US, ruleExpressionTemplate, (Object[]) values);
    }

    @Nonnull
    static RuleExpressionBinder from(@Nonnull RuleExpression ruleExpression) {
        // Using linked hash map to preserve order of variables. This is very important
        // since we will rely on order when replacing variables with values.
        Map<String, String> ruleVariablePlaceholders =
                new LinkedHashMap<>(ruleExpression.variables().size());

        // populate list with placeholders which later will be used as
        // source values in expression
        for (String variable : ruleExpression.variables()) {
            ruleVariablePlaceholders.put(variable, null);
        }

        // create expression template which later will be used as a target
        // for substituting values.
        String expressionTemplate = ruleExpression.expression()
                .replaceAll(RuleExpression.VARIABLE_PATTERN, "%s");

        return new RuleExpressionBinder(expressionTemplate,
                ruleExpression, ruleVariablePlaceholders);
    }
}
