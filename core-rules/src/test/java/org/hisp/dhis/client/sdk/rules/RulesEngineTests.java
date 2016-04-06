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

import org.hisp.dhis.client.sdk.models.common.ValueType;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.assertErrorRuleInEffect;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.assertErrorRuleNotInEffect;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.createDataElement;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers
        .createProgramRuleVariableCurrentEvent;
import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers
        .createSimpleProgramRuleShowError;

public class RulesEngineTests {
    private static List<Program> programs;

    // executed only once (note, since list of programs is
    // static, it means unit tests know will share state, so it means
    // programs should not be modified during runtime)
    @BeforeClass
    public static void setUp() throws IOException {
        ApiClient.init(
                "https://play.dhis2.org/demo",
                "android",
                "Android123"
        );

        programs = ApiClient.getPrograms();

        for (Program program : programs) {
            System.out.println("Program: " + program.getDisplayName());
        }
    }

    @Test
    public void ruleEngineExecuteSimpleWarningRule() {
        String errorMessage = "this error will always always occur";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "true", errorMessage));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .trackedEntityAttributes(new ArrayList<>())
                .programRules(rules)
                .dataElements(new ArrayList<>())
                .optionSets(new ArrayList<>())
                .constants(new ArrayList<>())
                .build();


        List<RuleEffect> effects = ruleEngine.execute(new Event(), new ArrayList<>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteSimpleWarningRuleNotInEffect() {
        String errorMessage = "this error will always always occur";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "false", errorMessage));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .build();


        List<RuleEffect> effects = ruleEngine.execute(new Event(), new ArrayList<>());

        assertErrorRuleNotInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteWarningRuleWithCurrentEventVariable() {
        //Metadata
        String errorMessage = "this error will occur based on the value of variable simpleBoolean";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "#{simpleBoolean}", errorMessage));

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
        ArrayList<TrackedEntityDataValue> dataValues = new ArrayList<>();
        TrackedEntityDataValue trueDataValue = new TrackedEntityDataValue();
        trueDataValue.setDataElement(d1.getUId());
        trueDataValue.setValue("true");
        dataValues.add(trueDataValue);

        Event simpleEvent = new Event();
        simpleEvent.setTrackedEntityDataValues(dataValues);

        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<>());

        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }

    @Test
    public void ruleEngineExecuteRuleWithTwoVariables() {
        //Metadata
        String errorMessage = "this error will occur if both simpleBoolean1 and 2 is true";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "#{simpleBoolean1} && #{simpleBoolean2}", errorMessage));

        ArrayList<DataElement> dataElements = new ArrayList<>();
        DataElement d1 = createDataElement("d1", "Boolean DataElement 1", ValueType.BOOLEAN);
        dataElements.add(d1);
        DataElement d2 = createDataElement("d2", "Boolean DataElement 2", ValueType.BOOLEAN);
        dataElements.add(d2);

        ArrayList<ProgramRuleVariable> variables = new ArrayList<>();
        variables.add(createProgramRuleVariableCurrentEvent("simpleBoolean1", d1));
        variables.add(createProgramRuleVariableCurrentEvent("simpleBoolean2", d2));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .programRules(rules)
                .dataElements(dataElements)
                .programRuleVariables(variables)
                .build();

        //Payload
        ArrayList<TrackedEntityDataValue> dataValues = new ArrayList<>();
        TrackedEntityDataValue boolean1DataValue = new TrackedEntityDataValue();
        boolean1DataValue.setDataElement(d1.getUId());
        boolean1DataValue.setValue("false");
        dataValues.add(boolean1DataValue);

        TrackedEntityDataValue boolean2DataValue = new TrackedEntityDataValue();
        boolean2DataValue.setDataElement(d2.getUId());
        boolean2DataValue.setValue("true");
        dataValues.add(boolean2DataValue);

        Event simpleEvent = new Event();
        simpleEvent.setTrackedEntityDataValues(dataValues);

        //Execute with one false and one true - expecting no effect:
        List<RuleEffect> effects = ruleEngine.execute(simpleEvent, new ArrayList<>());
        assertErrorRuleNotInEffect(effects, errorMessage, null, null);

        //Change the last variable to true - expecting the error message effect:
        boolean1DataValue.setValue("true");
        effects = ruleEngine.execute(simpleEvent, new ArrayList<>());
        assertErrorRuleInEffect(effects, errorMessage, null, null);
    }
}
