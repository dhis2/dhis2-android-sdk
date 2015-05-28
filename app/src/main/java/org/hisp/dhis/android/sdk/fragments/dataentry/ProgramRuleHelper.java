package org.hisp.dhis.android.sdk.fragments.dataentry;

import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.utils.services.ProgramRuleService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 29.04.15.
 */
public class ProgramRuleHelper {

    List<String> programRulesDataElements;
    List<ProgramRule> programRules;

    public ProgramRuleHelper(Program program) {
        programRules = program.getProgramRules();
        if(programRules==null) return;
        programRulesDataElements = new ArrayList<>();
        for(ProgramRule programRule: programRules) {
            List<String> dataElementsInRule = ProgramRuleService.getDataElementsInRule(programRule);
            programRulesDataElements.addAll(dataElementsInRule);
        }
    }

    boolean dataElementInRule(String dataElement) {
        return programRulesDataElements.contains(dataElement);
    }

}
