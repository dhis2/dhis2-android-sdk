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
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariableSourceType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RuleEngineVariableValueMap {

    private Map<String, ProgramRuleVariableValue> programRuleVariableValueMap;

    public RuleEngineVariableValueMap(List<ProgramRuleVariable> variables, Event currentEvent, List<Event> allEvents) {

        Collections.sort(allEvents, new Comparator<Event>() {
            public int compare(Event e1, Event e2) {
                return e1.getEventDate().compareTo(e2.getEventDate());
            }
        });

        programRuleVariableValueMap = new HashMap<>();
        if (variables != null) {
            Map<String, TrackedEntityDataValue> currentEventTrackedEntityDataValueMap =
                    new HashMap<>();
            if (currentEvent != null && currentEvent.getTrackedEntityDataValues() != null) {
                for (TrackedEntityDataValue value : currentEvent.getTrackedEntityDataValues()) {
                    currentEventTrackedEntityDataValueMap.put(value.getDataElement(), value);
                }
            }

            Map<String, List<TrackedEntityDataValue>> allEventsTrackedEntityDataValueMap =
                    new HashMap<>();
            for (Event e
                    : allEvents ) {
                if(e.getTrackedEntityDataValues() != null) {
                    for (TrackedEntityDataValue value : e.getTrackedEntityDataValues()) {
                        if(!allEventsTrackedEntityDataValueMap.containsKey(value.getDataElement())) {
                            allEventsTrackedEntityDataValueMap.put(value.getDataElement(),
                                   new ArrayList<>());
                        }
                        //Make sure the event is assigned, it is used later to check event date for
                        //the data values
                        if(value.getEvent() == null) {
                            value.setEvent(e);
                        }
                        allEventsTrackedEntityDataValueMap.get(value.getDataElement()).add(value);
                    }
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
                        addProgramRuleVariableValueToMap(variable, dataValue, null);
                        valueFound = true;
                    }
                } else if (variable.getSourceType() ==
                        ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM) {
                    if (allEventsTrackedEntityDataValueMap.containsKey(
                            variable.getDataElement().getUId())) {
                        List<TrackedEntityDataValue> valueList =
                                allEventsTrackedEntityDataValueMap.get(
                                        variable.getDataElement().getUId());
                        TrackedEntityDataValue dataValue = valueList.get(valueList.size() - 1);
                        addProgramRuleVariableValueToMap(variable, dataValue, valueList);
                        valueFound = true;
                    }
                } else if (variable.getSourceType() ==
                        ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE) {
                    if (variable.getProgramStage() != null &&
                            allEventsTrackedEntityDataValueMap.containsKey(
                                    variable.getDataElement().getUId())) {
                        List<TrackedEntityDataValue> valueList =
                                allEventsTrackedEntityDataValueMap.get(
                                        variable.getDataElement().getUId());
                        TrackedEntityDataValue bestCandidate = null;

                        for( TrackedEntityDataValue candidate : valueList ) {
                            if(candidate.getEvent().getProgramStageId() ==
                                    variable.getProgramStage().getUId()) {
                                //The candidate matches the program stage, and will be newer than
                                // the potential previous candidate:
                                bestCandidate = candidate;
                            }
                        }

                        if(bestCandidate != null) {
                            addProgramRuleVariableValueToMap(variable, bestCandidate, valueList);
                            valueFound = true;
                        }
                    }
                } else if(variable.getSourceType() ==
                        ProgramRuleVariableSourceType.DATAELEMENT_PREVIOUS_EVENT) {
                    if (currentEvent != null &&
                            allEventsTrackedEntityDataValueMap.containsKey(
                            variable.getDataElement().getUId())) {
                        List<TrackedEntityDataValue> valueList =
                                allEventsTrackedEntityDataValueMap.get(
                                        variable.getDataElement().getUId());
                        TrackedEntityDataValue bestCandidate = null;

                        for( TrackedEntityDataValue candidate : valueList ) {
                            if(candidate.getEvent().getEventDate().compareTo(currentEvent.getEventDate()) >= 0) {
                                //we have reached the current event time, stop iterating, keep the
                                //previous candidate, if any
                                break;
                            }
                            else
                            {
                                //we have not yet reached the current event, keep this candidate
                                //as it is the newest one examined:
                                bestCandidate = candidate;
                            }
                        }

                        if(bestCandidate != null) {
                            addProgramRuleVariableValueToMap(variable, bestCandidate, valueList);
                            valueFound = true;
                        }
                    }
                } else {
                    throw new NotImplementedException();
                }

                if(!valueFound) {

                    TrackedEntityDataValue defaultValue = new TrackedEntityDataValue();

                    if(variable.getDataElement().getValueType() == ValueType.TEXT
                            || variable.getDataElement().getValueType() == ValueType.LONG_TEXT
                            || variable.getDataElement().getValueType() == ValueType.EMAIL
                            || variable.getDataElement().getValueType() == ValueType.PHONE_NUMBER) {
                        defaultValue.setValue("''");
                    }
                    else if(variable.getDataElement().getValueType() == ValueType.INTEGER
                            || variable.getDataElement().getValueType() == ValueType.INTEGER_ZERO_OR_POSITIVE
                            || variable.getDataElement().getValueType() == ValueType.NUMBER
                            || variable.getDataElement().getValueType() == ValueType.PERCENTAGE) {
                        defaultValue.setValue("0");
                    }
                    else if(variable.getDataElement().getValueType() == ValueType.BOOLEAN) {
                        defaultValue.setValue("false");
                    }

                    addProgramRuleVariableValueToMap(variable, defaultValue, null);

                }
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
                                                  TrackedEntityDataValue value,
                                                  List<TrackedEntityDataValue> allValues) {
        ProgramRuleVariableValue variableValue = new ProgramRuleVariableValue(value, allValues);
        programRuleVariableValueMap.put(programRuleVariable.getDisplayName(), variableValue);
    }
}
