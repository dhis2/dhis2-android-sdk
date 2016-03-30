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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    public static ProgramRuleVariable createProgramRuleVariableCurrentEvent(
            String variableName, DataElement dataElement) {
        ProgramRuleVariable prv = new ProgramRuleVariable();
        prv.setDataElement(dataElement);
        prv.setDisplayName(variableName);
        prv.setSourceType(ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT);
        return prv;
    }

    public static DataElement createDataElement(String identifier,
                                                String name, ValueType valueType) {
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
            ProgramStage programStage) {

        for (RuleEffect ruleEffect : allEffects) {
            if (isMatchingOrNotMatched(action, ruleEffect.getProgramRuleActionType()) &&
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
        return false;
    }

    private static boolean isMatchingOrNotMatched(String original, String toCompare) {
        if (original == null || !original.isEmpty()) {
            return true;
        } else {
            return original == toCompare;
        }
    }

    private static boolean isMatchingOrNotMatched(ProgramRuleActionType original,
                                                  ProgramRuleActionType toCompare) {
        if (original == null) {
            return true;
        } else {
            return original == toCompare;
        }
    }

    private static boolean isMatchingOrNotMatched(DataElement original, DataElement toCompare) {
        if (original == null) {
            return true;
        } else {
            return original.getUId() == toCompare.getUId();
        }
    }

    private static boolean isMatchingOrNotMatched(TrackedEntityAttribute original,
                                                  TrackedEntityAttribute toCompare) {
        if (original == null) {
            return true;
        } else {
            return original.getUId() == toCompare.getUId();
        }
    }

    private static boolean isMatchingOrNotMatched(ProgramIndicator original,
                                                  ProgramIndicator toCompare) {
        if (original == null) {
            return true;
        } else {
            return original.getUId() == toCompare.getUId();
        }
    }

    private static boolean isMatchingOrNotMatched(ProgramStage original, ProgramStage toCompare) {
        if (original == null) {
            return true;
        } else {
            return original.getUId() == toCompare.getUId();
        }
    }
}
