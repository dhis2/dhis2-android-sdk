package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.dataelement.ValueType;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.assertErrorRuleInEffect;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createDataElement;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createProgramRuleVariableCurrentEvent;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createProgramRuleVariable;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createSimpleProgramRuleShowError;

/**
 * Created by markusbekken on 22.05.2016.
 */
public class RulesEngineFallbackTests {
    @Test
    public void ruleEngineExecuteBooleanFallback() {
        //Metadata
        String errorMessage = "this error will occur if simpleBoolean is false";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "!#{simpleBoolean}",
                errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Boolean DataElement", ValueType.BOOLEAN);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariableCurrentEvent("simpleBoolean", d1));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Empty payload - testing fallback
        Event simpleEvent = new Event();

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineTextFallback() {
        //Metadata
        String errorMessage = "this error will occur if simpleText is empty string";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "#{simpleText} == ''",
                errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Test DataElement", ValueType.TEXT);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariable("simpleText", d1, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Empty payload - testing fallback
        Event simpleEvent = new Event();

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteNumberFallback() {
        //Metadata
        String errorMessage = "this error will occur if simpleNumber is 0";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "#{simpleNumber} == 0",
                errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Number DataElement", ValueType.NUMBER);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariableCurrentEvent("simpleNumber", d1));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Empty payload - testing fallback
        Event simpleEvent = new Event();

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }
}
