package org.hisp.dhis.rules;

import org.hisp.dhis.rules.functions.RuleFunction;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

class RuleEngineExecution implements Callable<List<RuleEffect>> {

    @Nonnull
    private final RuleExpressionEvaluator expressionEvaluator;

    @Nonnull
    private final Map<String, RuleVariableValue> valueMap;

    @Nonnull
    private final List<Rule> rules;

    RuleEngineExecution(@Nonnull RuleExpressionEvaluator expressionEvaluator,
            @Nonnull List<Rule> rules, @Nonnull Map<String, RuleVariableValue> valueMap) {
        this.expressionEvaluator = expressionEvaluator;
        this.valueMap = valueMap;
        this.rules = rules;
    }

    @Override
    public List<RuleEffect> call() throws Exception {
        List<RuleEffect> ruleEffects = new ArrayList<>();

//        for (int i = 0; i < rules.size(); i++) {
//            // send expression to evaluator
//            if (Boolean.valueOf(expressionProcessor
//                    .process(rules.get(i).condition()))) {
//                // populate list of rule effects(from actions)
//                // ruleEffects.add();
//                System.out.println("Creating rule effect here");
//            }
//        }

        return ruleEffects;
    }

    @Nonnull
    private String process(@Nonnull String expression) {
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
