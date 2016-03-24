package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.commons.util.ExpressionUtils;

/**
 * Created by markusbekken on 23.03.2016.
 */
public class RuleEngineExecution {
    public static List<RuleEffect> execute(List<ProgramRule> rules) {
        ArrayList<RuleEffect> effects = new ArrayList<>();

        for (ProgramRule rule:rules) {
            if( ExpressionUtils.isTrue(rule.getCondition(), null) ) {
                for(ProgramRuleAction action: rule.getProgramRuleActions()) {
                    effects.add(createEffect(action));
                }
            }
        }

        return effects;
    }

    private static RuleEffect createEffect( ProgramRuleAction action ) {
        RuleEffect effect = new RuleEffect();
        effect.setProgramRule(action.getProgramRule());
        effect.setProgramRuleActionType(action.getProgramRuleActionType());
        effect.setContent(action.getContent());
        effect.setData(action.getData());
        effect.setDataElement(action.getDataElement());
        effect.setProgramIndicator(action.getProgramIndicator());
        effect.setLocation(action.getLocation());
        effect.setProgramStage(action.getProgramStage());
        effect.setProgramStageSection(action.getProgramStageSection());
        effect.setTrackedEntityAttribute(action.getTrackedEntityAttribute());

        return effect;
    }
}
