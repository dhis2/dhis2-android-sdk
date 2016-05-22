package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.dataelement.ValueType;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleActionType;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.addDataValueToEvent;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.assertErrorRuleInEffect;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.assertErrorRuleNotInEffect;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createDataElement;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createProgramRuleVariable;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createSimpleProgramRuleShowError;

/**
 * Created by markusbekken on 22.05.2016.
 */
public class RulesEngineAdvancedScenarioTests {
    @Test
    public void ruleEngineExecuteFloorFunction() {
        //Metadata
        String errorMessage = "Error message shown if the predecessor calculation assigns the value 2";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "#{assignedVariable} == 2",
                errorMessage));

        ProgramRule calculationRuleWithPri1 = createSimpleProgramRuleShowError("r2",
                "a2",
                "true",
                "placeholder");
        calculationRuleWithPri1.setPriority(1);
        ProgramRuleAction a2 = calculationRuleWithPri1.getProgramRuleActions().get(0);
        a2.setProgramRuleActionType(ProgramRuleActionType.ASSIGN);
        //variable being assigned:
        a2.setContent("#{assignedVariable}");
        //data to assign to the variable:
        a2.setData("d2:floor(#{simpleInt} / 10)");
        rules.add(calculationRuleWithPri1);

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Integer DataElement", ValueType.INTEGER);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariable("simpleInt", d1, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM));
        variables.add(createProgramRuleVariable("assignedVariable", null, ProgramRuleVariableSourceType.CALCULATED_VALUE));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        Event simpleEvent = new Event();
        List<Event> allEvents = new ArrayList<Event>();

        Event e2 = new Event();
        e2.setEventDate(DateTime.now().minusDays(5));
        allEvents.add(e2);

        Event e3 = new Event();
        e3.setEventDate(DateTime.now().minusDays(1));
        allEvents.add(e3);

        Event e1 = new Event();
        e1.setEventDate(DateTime.now().minusDays(10));
        allEvents.add(e1);

        addDataValueToEvent(e1,d1,"31");
        //31 means the assigned value should be 3 - and no error not should be shown
        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, allEvents);

        assertErrorRuleNotInEffect(effects, errorMessage, null, null);

        addDataValueToEvent(e2,d1,"29");
        //29 means the assigned value should be 2 - and the error should be shown.
        effects = ruleEngine.execute(simpleEvent, allEvents);

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }
}
