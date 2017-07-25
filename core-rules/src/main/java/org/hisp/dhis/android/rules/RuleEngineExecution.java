package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Rule;
import org.hisp.dhis.android.rules.models.RuleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

class RuleEngineExecution implements Callable<List<RuleEffect>> {

    @Nonnull
    private final RuleExpressionProcessor expressionProcessor;

    @Nonnull
    private final List<Rule> rules;

    RuleEngineExecution(@Nonnull RuleExpressionProcessor expressionProcessor,
            @Nonnull List<Rule> rules) {
        this.expressionProcessor = expressionProcessor;
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
}
