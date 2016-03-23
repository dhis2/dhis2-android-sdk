package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.program.ProgramRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markusbekken on 23.03.2016.
 */
public class RuleEngineExecution {
    public static List<RuleEffect> execute(List<ProgramRule> rules) {
        ArrayList<RuleEffect> effects = new ArrayList<>();

        for (ProgramRule rule:rules) {
            //TODO perform evaluation
        }

        return effects;
    }
}
