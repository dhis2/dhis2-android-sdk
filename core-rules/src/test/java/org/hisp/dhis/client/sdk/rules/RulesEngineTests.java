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

import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.hisp.dhis.client.sdk.rules.RulesEngineTestHelpers.*;

public class RulesEngineTests {


    @Before
    public void setUp() {

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


        List<RuleEffect> effects = ruleEngine.execute(new Event(), new ArrayList<Event>());

        assertErrorRuleInEffect(effects,errorMessage,null,null);
    }

    @Test
    public void ruleEngineExecuteSimpleWarningRuleNotInEffect() {
        String errorMessage = "this error will always always occur";
        ArrayList<ProgramRule> rules = new ArrayList<>();
        rules.add(createSimpleProgramRuleShowError("r1", "a1", "false", errorMessage));

        RuleEngine ruleEngine = new RuleEngine.Builder()
                .trackedEntityAttributes(new ArrayList<>())
                .programRules(rules)
                .dataElements(new ArrayList<>())
                .optionSets(new ArrayList<>())
                .constants(new ArrayList<>())
                .build();


        List<RuleEffect> effects = ruleEngine.execute(new Event(), new ArrayList<Event>());

        assertErrorRuleNotInEffect(effects,errorMessage,null,null);
    }
}
