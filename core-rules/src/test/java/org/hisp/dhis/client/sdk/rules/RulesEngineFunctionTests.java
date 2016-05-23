/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.rules;

import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.dataelement.ValueType;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.*;

public class RulesEngineFunctionTests {
    @Test
    public void ruleEngineExecuteWarningSimpleD2Function() {
        //Metadata
        String errorMessage = "this error will occur if simpleBoolean has a value";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "d2:hasValue('simpleBoolean')",
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

        //Payload
        Event simpleEvent = new Event();
        addDataValueToEvent(simpleEvent,d1,"false");

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteHasValueFunctionWithCurrentEventSourceType() {
        //Metadata
        String errorMessage = "this error will occur if simpleBoolean has a value";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "d2:hasValue('simpleBoolean')",
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

        //Payload
        Event simpleEvent = new Event();

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleNotInEffect(effects, errorMessage, null, null);

        addDataValueToEvent(simpleEvent,d1,"false");

        effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteHasValueWithBlank() {
        //Metadata
        String errorMessage = "this error will occur if simpleInt does not have a value";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "!d2:hasValue('simpleInt')",
                errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Boolean DataElement", ValueType.BOOLEAN);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariableCurrentEvent("simpleInt", d1));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        Event simpleEvent = new Event();

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);

        addDataValueToEvent(simpleEvent,d1,"");

        effects = ruleEngine.execute(simpleEvent, new ArrayList<Event>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteHasValueFunctionWithNewestEventSourceType() {
        //Metadata
        String errorMessage = "this error will occur if simpleBoolean has a value";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "d2:hasValue('simpleBoolean')",
                errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Boolean DataElement", ValueType.BOOLEAN);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariable("simpleBoolean", d1, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        Event simpleEvent = new Event();
        List<Event> allEvents = new ArrayList<>();

        Event e1 = new Event();
        e1.setEventDate(DateTime.now().minusDays(10));
        allEvents.add(e1);

        Event e2 = new Event();
        e2.setEventDate(DateTime.now().minusDays(5));
        allEvents.add(e2);

        Event e3 = new Event();
        e3.setEventDate(DateTime.now().minusDays(1));
        allEvents.add(e3);


        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, allEvents);

        assertErrorRuleNotInEffect(effects, errorMessage, null, null);

        addDataValueToEvent(e2,d1,"false");

        effects = ruleEngine.execute(simpleEvent, allEvents);

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteFloorFunction() {
        //Metadata
        String errorMessage = "this error will occur if simpleNumber is more than two";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1",
                "a1",
                "d2:floor(2 / #{simpleInt}) == 0",
                errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Integer DataElement", ValueType.INTEGER);
        dataElements.add(d1);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariable("simpleInt", d1, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        Event simpleEvent = new Event();
        List<Event> allEvents = new ArrayList<>();

        Event e2 = new Event();
        e2.setEventDate(DateTime.now().minusDays(5));
        allEvents.add(e2);

        Event e3 = new Event();
        e3.setEventDate(DateTime.now().minusDays(1));
        allEvents.add(e3);

        Event e1 = new Event();
        e1.setEventDate(DateTime.now().minusDays(10));
        allEvents.add(e1);

        addDataValueToEvent(e1,d1,"2");

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, allEvents);

        assertErrorRuleNotInEffect(effects, errorMessage, null, null);

        addDataValueToEvent(e2,d1,"3");

        effects = ruleEngine.execute(simpleEvent, allEvents);

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }
}
