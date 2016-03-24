package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.commons.util.ExpressionUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jexl2.*;

/**
 * Created by markusbekken on 23.03.2016.
 */
public class RuleEngineExecution {
    public static List<RuleEffect> execute(List<ProgramRule> rules) {
        ArrayList<RuleEffect> effects = new ArrayList<>();

        for (ProgramRule rule:rules) {
            if(conditionIsTrue(rule.getCondition())) {
                for(ProgramRuleAction action: rule.getProgramRuleActions()) {
                    effects.add(createEffect(action));
                }
            }
        }

        return effects;
    }

    /**
     * Evaluates a passed expression from a {@link ProgramRule} to true or false.
     * @param condition
     * @return
     */
    private static boolean conditionIsTrue(final String condition) {
        boolean isTrue = false;
        try {
            isTrue = ExpressionUtils.isTrue(condition, null);
        } catch(JexlException jxlException) {
            jxlException.printStackTrace();
        }
        return isTrue;
    }

    /**
     * Mapping method for creating a {@link RuleEffect} from a {@link ProgramRuleAction} object.
     * @param action
     * @return
     */
    private static RuleEffect createEffect(ProgramRuleAction action) {
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
