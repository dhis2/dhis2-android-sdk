package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Rule;
import org.hisp.dhis.android.rules.models.RuleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

class RuleEngineExecution implements Callable<List<RuleEffect>> {

    @Nonnull
    private final RuleExpressionEvaluator expressionEvaluator;

    @Nonnull
    private final List<Rule> rules;

    @Nonnull
    private final Map<String, RuleVariableValue> valueMap;

    RuleEngineExecution(@Nonnull RuleExpressionEvaluator expressionEvaluator,
            @Nonnull List<Rule> rules, @Nonnull Map<String, RuleVariableValue> valueMap) {
        this.expressionEvaluator = expressionEvaluator;
        this.rules = rules;
        this.valueMap = valueMap;
    }

    @Override
    public List<RuleEffect> call() throws Exception {
        List<RuleEffect> ruleEffects = new ArrayList<>();

        for (int i = 0; i < rules.size(); i++) {
            expressionEvaluator.evaluate(processRule(rules.get(i)));

            // send expression to evaluator
            // if (Boolean.valueOf(expressionEvaluator.evaluate(preparedExpression))) {
            //      populate list of rule effects (from actions)
            //      ruleEffects.add();
            // }
        }

        return ruleEffects;
    }

    @Nonnull
    private String processRule(@Nonnull Rule rule) {
        // wrap rule expression into object representation
        RuleExpression ruleExpression = RuleExpression.from(rule.condition());

        // substitute variable values
        RuleExpressionBinder ruleExpressionBinder = RuleExpressionBinder.from(ruleExpression);

        for (int index = 0; index < ruleExpression.variables().size(); index++) {
            String variable = ruleExpression.variables().get(index);
            RuleVariableValue variableValue = valueMap.get(variable);

            ruleExpressionBinder.bind(variable, variableValue.value() == null ?
                    variableValue.type().defaultValue() : variableValue.value());
        }

        return ruleExpressionBinder.build();
    }
}
