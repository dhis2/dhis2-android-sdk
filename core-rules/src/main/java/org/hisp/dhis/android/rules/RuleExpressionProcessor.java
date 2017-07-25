package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.functions.RuleFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

final class RuleExpressionProcessor {

    @Nonnull
    private final RuleExpressionEvaluator expressionEvaluator;

    @Nonnull
    private final Map<String, RuleVariableValue> valueMap;

    RuleExpressionProcessor(@Nonnull RuleExpressionEvaluator expressionEvaluator,
            @Nonnull Map<String, RuleVariableValue> valueMap) {
        this.expressionEvaluator = expressionEvaluator;
        this.valueMap = valueMap;
    }

    @Nonnull
    String process(@Nonnull String expression) {
        return expressionEvaluator.evaluate(processRuleExpression(expression));
    }

    @Nonnull
    private String processRuleExpression(@Nonnull String ruleExpression) {
        String expressionWithVariableValues = bindVariableValues(ruleExpression);
        String expressionWithFunctionValues = bindFunctionValues(expressionWithVariableValues);
        return expressionEvaluator.evaluate(expressionWithFunctionValues);
    }

    @Nonnull
    private String bindVariableValues(@Nonnull String expression) {
        RuleExpression ruleExpression = RuleExpression.from(expression);
        RuleExpressionBinder ruleExpressionBinder = RuleExpressionBinder.from(ruleExpression);

        // substitute variable values
        for (String variable : ruleExpression.variables()) {
            RuleVariableValue variableValue = valueMap.get(variable);

            ruleExpressionBinder.bindVariable(variable, variableValue.value() == null ?
                    variableValue.type().defaultValue() : variableValue.value());
        }

        return ruleExpressionBinder.build();
    }

    @Nonnull
    private String bindFunctionValues(@Nonnull String expression) {
        RuleExpression ruleExpression = RuleExpression.from(expression);
        RuleExpressionBinder ruleExpressionBinder = RuleExpressionBinder.from(ruleExpression);

        for (String function : ruleExpression.functions()) {
            RuleFunctionCall ruleFunctionCall = RuleFunctionCall.from(function);

            List<String> arguments = new ArrayList<>(ruleFunctionCall.arguments());
            for (int j = 0; j < arguments.size(); j++) {
                arguments.set(j, processRuleExpression(arguments.get(j)));
            }

            ruleExpressionBinder.bindFunction(ruleFunctionCall.functionCall(),
                    RuleFunction.create(ruleFunctionCall.functionName()).evaluate(arguments));
        }

        return ruleExpressionBinder.build();
    }
}
