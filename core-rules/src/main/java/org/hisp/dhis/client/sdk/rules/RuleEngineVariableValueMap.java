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
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by markusbekken on 24.03.2016.
 */
public class RuleEngineVariableValueMap {

    private Map<String, ProgramRuleVariableValue> programRuleVariableValueMap;

    public RuleEngineVariableValueMap(List<ProgramRuleVariable> variables, Event currentEvent) {

        programRuleVariableValueMap = new HashMap<>();
        if (variables != null) {
            Map<String, TrackedEntityDataValue> currentEventTrackedEntityDataValueMap = new
                    HashMap<>();
            if (currentEvent != null && currentEvent.getTrackedEntityDataValues() != null) {
                for (TrackedEntityDataValue value : currentEvent.getTrackedEntityDataValues()) {
                    currentEventTrackedEntityDataValueMap.put(value.getDataElement(), value);
                }
            }

            for (ProgramRuleVariable variable : variables) {
                boolean valueFound = true;
                if (variable.getSourceType() ==
                        ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT) {
                    if (currentEventTrackedEntityDataValueMap.containsKey(
                            variable.getDataElement().getUId())) {
                        TrackedEntityDataValue dataValue = currentEventTrackedEntityDataValueMap
                                .get(variable.getDataElement().getUId());
                        addProgramRuleVariableValueToMap(variable, dataValue);
                        valueFound = true;
                    }
                } else {
                    throw new NotImplementedException();
                }

                //TODO: Add general handling when value is not found.
            }
        }
    }

    public ProgramRuleVariableValue getProgramRuleVariableValue(String variableName) {
        return programRuleVariableValueMap.get(variableName);
    }

    public Map<String, ProgramRuleVariableValue> getProgramRuleVariableValueMap() {
        return programRuleVariableValueMap;
    }

    private void addProgramRuleVariableValueToMap(ProgramRuleVariable programRuleVariable,
                                                  TrackedEntityDataValue dataValue) {
        ProgramRuleVariableValue variableValue = new ProgramRuleVariableValue(dataValue.getValue());
        programRuleVariableValueMap.put(programRuleVariable.getDisplayName(), variableValue);
    }


}
