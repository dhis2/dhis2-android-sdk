package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.common.ValueType;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleActionType;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by markusbekken on 22.03.2016.
 */
public class RulesEngineTestHelpers {
    public static ProgramRule createSimpleProgramRuleShowError(String ruleIdentifier,
                                                               String actionIdentifier,
                                                               String ruleCondition,
                                                               String errorMessage) {
        ProgramRuleAction pra = new ProgramRuleAction();
        pra.setUId(actionIdentifier);
        pra.setProgramRuleActionType(ProgramRuleActionType.SHOWERROR);
        pra.setContent(errorMessage);
        ArrayList<ProgramRuleAction> actions = new ArrayList<>();
        actions.add(pra);

        ProgramRule pr = new ProgramRule();
        pr.setUId(ruleIdentifier);
        pr.setCondition(ruleCondition);
        pr.setProgramRuleActions(actions);

        pra.setProgramRule(pr);

        return pr;
    }

    public static ProgramRuleVariable createProgramRuleVariableCurrentEvent(String variableName, DataElement dataElement) {
        ProgramRuleVariable prv = new ProgramRuleVariable();
        prv.setDataElement(dataElement);
        prv.setDisplayName(variableName);
        prv.setSourceType(ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT);
        return prv;
    }

    public static DataElement createDataElement (String identifier, String name, ValueType valueType ) {
        DataElement dataElement = new DataElement();
        dataElement.setDisplayName(name);
        dataElement.setValueType(valueType);
        dataElement.setUId(identifier);
        return dataElement;
    }

    public static void assertErrorRuleInEffect(List<RuleEffect> allEffects,
                                               String errorMessage,
                                               DataElement dataElement,
                                               TrackedEntityAttribute attribute) {
        assertTrue(genericRuleEffectInEffect(allEffects,
                ProgramRuleActionType.SHOWERROR,
                null,
                errorMessage,
                null,
                dataElement,
                attribute,
                null,
                null));
    }

    public static void assertErrorRuleNotInEffect(List<RuleEffect> allEffects,
                                               String errorMessage,
                                               DataElement dataElement,
                                               TrackedEntityAttribute attribute) {
        assertFalse(genericRuleEffectInEffect(allEffects,
                ProgramRuleActionType.SHOWERROR,
                null,
                errorMessage,
                null,
                dataElement,
                attribute,
                null,
                null));
    }

    private static boolean genericRuleEffectInEffect(
            List<RuleEffect> allEffects,
            ProgramRuleActionType action,
            String location,
            String content,
            String data,
            DataElement dataElement,
            TrackedEntityAttribute attribute,
            ProgramIndicator programIndicator,
            ProgramStage programStage ) {

        for (RuleEffect ruleEffect:allEffects) {
            if(isMatchingOrNotMatched(action, ruleEffect.getProgramRuleActionType()) &&
                    isMatchingOrNotMatched(location, ruleEffect.getLocation()) &&
                    isMatchingOrNotMatched(content, ruleEffect.getContent()) &&
                    isMatchingOrNotMatched(data, ruleEffect.getData()) &&
                    isMatchingOrNotMatched(dataElement, ruleEffect.getDataElement()) &&
                    isMatchingOrNotMatched(attribute, ruleEffect.getTrackedEntityAttribute()) &&
                    isMatchingOrNotMatched(programIndicator, ruleEffect.getProgramIndicator()) &&
                    isMatchingOrNotMatched(programStage, ruleEffect.getProgramStage())) {
                return true;
            }
        }

        //The loop came to an end without finding any matching rule effects
        return  false;
    }

    private static boolean isMatchingOrNotMatched(String original, String toCompare) {
        if(original == null || !original.isEmpty()) {
            return true;
        }
        else {
            return original == toCompare;
        }
    }

    private static boolean isMatchingOrNotMatched(ProgramRuleActionType original, ProgramRuleActionType toCompare) {
        if(original == null) {
            return true;
        }
        else {
            return original == toCompare;
        }
    }

    private static boolean isMatchingOrNotMatched(DataElement original, DataElement toCompare) {
        if(original == null) {
            return true;
        }
        else {
            return original.getUId() == toCompare.getUId();
        }
    }

    private static boolean isMatchingOrNotMatched(TrackedEntityAttribute original, TrackedEntityAttribute toCompare) {
        if(original == null) {
            return true;
        }
        else {
            return original.getUId() == toCompare.getUId();
        }
    }

    private static boolean isMatchingOrNotMatched(ProgramIndicator original, ProgramIndicator toCompare) {
        if(original == null) {
            return true;
        }
        else {
            return original.getUId() == toCompare.getUId();
        }
    }

    private static boolean isMatchingOrNotMatched(ProgramStage original, ProgramStage toCompare) {
        if(original == null) {
            return true;
        }
        else {
            return original.getUId() == toCompare.getUId();
        }
    }
}
