package org.hisp.dhis.rules;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleExpressionBinder {

    @Nonnull
    private final String ruleExpression;

    @Nonnull
    private final Map<String, String> ruleVariableValues;

    @Nonnull
    private final Map<String, String> ruleFunctionCalls;

    RuleExpressionBinder(@Nonnull String ruleExpression,
            @Nonnull Map<String, String> ruleVariableValues,
            @Nonnull Map<String, String> ruleFunctionCalls) {
        this.ruleExpression = ruleExpression;
        this.ruleVariableValues = ruleVariableValues;
        this.ruleFunctionCalls = ruleFunctionCalls;
    }

    RuleExpressionBinder bindVariable(@Nonnull String variable, @Nonnull String value) {
        if (!ruleVariableValues.containsKey(variable)) {
            throw new IllegalArgumentException("Non-existing variable: " + variable);
        }

        ruleVariableValues.put(variable, value);
        return this;
    }

    RuleExpressionBinder bindFunction(@Nonnull String functionCall, @Nonnull String value) {
        if (!ruleFunctionCalls.containsKey(functionCall)) {
            throw new IllegalArgumentException("Non-existing function call: " + functionCall);
        }

        ruleFunctionCalls.put(functionCall, value);
        return this;
    }

    @Nonnull
    String build() {
        String expression = ruleExpression;

        // iterate over variables and replace them with values
        for (Map.Entry<String, String> variableValue : ruleVariableValues.entrySet()) {
            if (variableValue.getValue() != null) {
                while (expression.contains(variableValue.getKey())) {
                    expression = expression.replace(variableValue.getKey(),
                            variableValue.getValue());
                }
            }
        }

        // iterate over function calls and replace them with values
        for (Map.Entry<String, String> functionCall : ruleFunctionCalls.entrySet()) {
            if (functionCall.getValue() != null) {
                while (expression.contains(functionCall.getKey())) {
                    expression = expression.replace(functionCall.getKey(),
                            functionCall.getValue());
                }
            }
        }

        return expression;
    }

    @Nonnull
    static RuleExpressionBinder from(@Nonnull RuleExpression ruleExpression) {
        Map<String, String> ruleVariablePlaceholders =
                new HashMap<>(ruleExpression.variables().size());
        Map<String, String> ruleFunctionPlaceholders =
                new HashMap<>(ruleExpression.functions().size());

        // populate list with placeholders which later will be used as
        // source values in expression
        for (String variable : ruleExpression.variables()) {
            ruleVariablePlaceholders.put(variable, null);
        }

        for (String function : ruleExpression.functions()) {
            ruleFunctionPlaceholders.put(function, null);
        }

        return new RuleExpressionBinder(ruleExpression.expression(),
                ruleVariablePlaceholders, ruleFunctionPlaceholders);
    }
}
