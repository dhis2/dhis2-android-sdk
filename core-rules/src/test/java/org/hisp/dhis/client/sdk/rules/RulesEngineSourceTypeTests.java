package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.common.ValueType;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.addDataValueToEvent;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.assertErrorRuleInEffect;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.assertErrorRuleNotInEffect;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createDataElement;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createProgramRuleVariable;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createProgramStage;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createSimpleProgramRuleShowError;

/**
 * Created by markusbekken on 08.05.2016.
 */
public class RulesEngineSourceTypeTests {

    @Test
    public void ruleEngineExecuteRuleWithNewestEventSourceType() {
        //Metadata
        String errorMessage = "this error will occur if both simpleBoolean1 is true";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "#{simpleBoolean1}", errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Boolean DataElement 1", ValueType.BOOLEAN);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariable("simpleBoolean1", d1, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        Event olderEvent = new Event();
        olderEvent.setEventDate(DateTime.now().minusDays(10));

        addDataValueToEvent(olderEvent, d1, "true");

        Event currentEvent = new Event();
        currentEvent.setEventDate(DateTime.now());

        ArrayList<Event> allEvents = new ArrayList<>();
        allEvents.add(currentEvent);
        allEvents.add(olderEvent);

        //Execute with an older value that is true, and a current event that is not filled
        //Expecting to get an effect, as there exists a true value
        List<RuleEffect> effects = ruleEngine.execute(currentEvent, allEvents);
        assertErrorRuleInEffect(effects, errorMessage, null, null);

        //Insert a false value in between the old true value and the current blank value
        //The false should stop the effect form happening:
        Event middleEvent = new Event();
        middleEvent.setEventDate(DateTime.now().minusDays(2));
        addDataValueToEvent(middleEvent, d1, "false");
        allEvents.add(middleEvent);
        effects = ruleEngine.execute(currentEvent, allEvents);
        assertErrorRuleNotInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteRuleWithNewestEventProgramStageSourceType() {
        //Metadata
        String errorMessage = "this error will occur if simpleBoolean1 is true";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "#{simpleBoolean1}", errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Boolean DataElement 1", ValueType.BOOLEAN);
        dataElements.add(d1);

        ProgramStage ps1 = createProgramStage("Stage1");
        ProgramStage ps2 = createProgramStage("Stage2");

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        ProgramRuleVariable ps1Variable =
                createProgramRuleVariable(
                        "simpleBoolean1",
                        d1,
                        ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE);
        ps1Variable.setProgramStage(ps1);
        variables.add(ps1Variable);

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        Event eventProgramStage2 = new Event();
        eventProgramStage2.setEventDate(DateTime.now().minusDays(10));
        eventProgramStage2.setProgramStageId(ps2.getUId());
        addDataValueToEvent(eventProgramStage2, d1, "true");

        Event currentEventProgramStage2 = new Event();
        currentEventProgramStage2.setEventDate(DateTime.now());
        currentEventProgramStage2.setProgramStageId(ps2.getUId());

        Event veryOldEventProgramStage1 = new Event();
        veryOldEventProgramStage1.setProgramStageId(ps1.getUId());
        veryOldEventProgramStage1.setEventDate(DateTime.now().minusDays(365));
        addDataValueToEvent(veryOldEventProgramStage1, d1, "false");

        ArrayList<Event> allEvents = new ArrayList<>();
        allEvents.add(eventProgramStage2);
        allEvents.add(currentEventProgramStage2);
        allEvents.add(veryOldEventProgramStage1);

        //Execute with two irrelevant events, as they have the wrong program stage, and one
        //relevant event where thr value is false
        List<RuleEffect> effects = ruleEngine.execute(currentEventProgramStage2, allEvents);
        assertErrorRuleNotInEffect(effects, errorMessage, null, null);

        //Insert new event with program stage 1 and a true value
        Event oldEventProgramStage1 = new Event();
        oldEventProgramStage1.setEventDate(DateTime.now().minusDays(15));
        eventProgramStage2.setProgramStageId(ps1.getUId());
        addDataValueToEvent(oldEventProgramStage1, d1, "true");

        allEvents.add(oldEventProgramStage1);

        //Execute with the new true value, making sure that the rule is now in effect
        effects = ruleEngine.execute(currentEventProgramStage2, allEvents);
        assertErrorRuleInEffect(effects, errorMessage, null, null);


        //Add a new event of the relevant program stage 1, setting the value to false.
        //the new event will also be the current value
        Event newCurrent = new Event();
        newCurrent.setEventDate(DateTime.now());
        addDataValueToEvent(newCurrent, d1, "false");
        newCurrent.setProgramStageId(ps1.getUId());
        allEvents.add(newCurrent);

        //Run the rule and make sure that the new current event overrides the previous true value
        effects = ruleEngine.execute(newCurrent, allEvents);
        assertErrorRuleNotInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteRuleWithPreviousEventSourceType() {
        //Metadata
        String errorMessage = "this error will occur if both simpleBoolean1 is true";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "#{simpleBoolean1}", errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Boolean DataElement 1", ValueType.BOOLEAN);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariable("simpleBoolean1", d1, ProgramRuleVariableSourceType.DATAELEMENT_PREVIOUS_EVENT));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        Event olderEvent = new Event();
        olderEvent.setEventDate(DateTime.now().minusDays(10));
        addDataValueToEvent(olderEvent, d1, "true");

        Event currentEvent = new Event();
        currentEvent.setEventDate(DateTime.now());
        addDataValueToEvent(currentEvent, d1, "false");

        Event newerEvent = new Event();
        newerEvent.setEventDate(DateTime.now().plusDays(2));
        addDataValueToEvent(newerEvent, d1, "false");

        ArrayList<Event> allEvents = new ArrayList<>();
        allEvents.add(currentEvent);
        allEvents.add(olderEvent);

        //Execute with a previous value that is true, and a current event that false,
        //and a newer event that is false
        //Expecting to get an effect, as there exists a true value in previous events, and
        //the newer event and current event should be disregarded.
        List<RuleEffect> effects = ruleEngine.execute(currentEvent, allEvents);
        assertErrorRuleInEffect(effects, errorMessage, null, null);

        //Insert a false value in between the old true value and the current blank value
        //The false should stop the effect form happening:
        Event middleEvent = new Event();
        middleEvent.setEventDate(DateTime.now().minusDays(2));
        addDataValueToEvent(middleEvent, d1, "false");
        allEvents.add(middleEvent);
        effects = ruleEngine.execute(currentEvent, allEvents);
        assertErrorRuleNotInEffect(effects, errorMessage, null, null);
    }
}
