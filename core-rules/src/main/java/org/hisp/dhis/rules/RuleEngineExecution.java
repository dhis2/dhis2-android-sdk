package org.hisp.dhis.rules;

import org.hisp.dhis.rules.functions.RuleFunction;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionAssign;
import org.hisp.dhis.rules.models.RuleActionCreateEvent;
import org.hisp.dhis.rules.models.RuleActionDisplayKeyValuePair;
import org.hisp.dhis.rules.models.RuleActionDisplayText;
import org.hisp.dhis.rules.models.RuleActionErrorOnCompletion;
import org.hisp.dhis.rules.models.RuleActionShowError;
import org.hisp.dhis.rules.models.RuleActionShowWarning;
import org.hisp.dhis.rules.models.RuleActionWarningOnCompletion;
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

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);

            // send expression to evaluator
            if (Boolean.valueOf(process(rule.condition()))) {

                // process each action for this rule
                for (int j = 0; j < rule.actions().size(); j++) {
                    ruleEffects.add(create(rule.actions().get(j)));
                }
            }
        }

        return ruleEffects;
    }

    @Nonnull
    private RuleEffect create(@Nonnull RuleAction ruleAction) {
        // Only certain types of actions might
        // contain code to execute.
        if (ruleAction instanceof RuleActionAssign) {
            return RuleEffect.create(ruleAction,
                    process(((RuleActionAssign) ruleAction).data()));
        } else if (ruleAction instanceof RuleActionCreateEvent) {
            return RuleEffect.create(ruleAction, process(
                    ((RuleActionCreateEvent) ruleAction).data()));
        } else if (ruleAction instanceof RuleActionDisplayKeyValuePair) {
            return RuleEffect.create(ruleAction, process(
                    ((RuleActionDisplayKeyValuePair) ruleAction).data()));
        } else if (ruleAction instanceof RuleActionDisplayText) {
            return RuleEffect.create(ruleAction, process(
                    ((RuleActionDisplayText) ruleAction).data()));
        } else if (ruleAction instanceof RuleActionErrorOnCompletion) {
            return RuleEffect.create(ruleAction, process(
                    ((RuleActionErrorOnCompletion) ruleAction).data()));
        } else if (ruleAction instanceof RuleActionShowError) {
            return RuleEffect.create(ruleAction,
                    process(((RuleActionShowError) ruleAction).data()));
        } else if (ruleAction instanceof RuleActionShowWarning) {
            return RuleEffect.create(ruleAction,
                    process(((RuleActionShowWarning) ruleAction).data()));
        } else if (ruleAction instanceof RuleActionWarningOnCompletion) {
            return RuleEffect.create(ruleAction,
                    process(((RuleActionWarningOnCompletion) ruleAction).data()));
        }

        return RuleEffect.create(ruleAction);
    }

    @Nonnull
    private String process(@Nonnull String expression) {
        // we don't want to run empty expression
        if (!expression.isEmpty()) {
            return expressionEvaluator.evaluate(processRuleExpression(expression));
        }

        return "";
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
            RuleVariableValue variableValue = valueMap.get(
                    RuleExpression.unwrapVariableName(variable));

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
