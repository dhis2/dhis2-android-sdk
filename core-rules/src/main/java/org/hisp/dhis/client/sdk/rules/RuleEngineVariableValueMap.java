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
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.utils.LocaleUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Part of RuleEngine implementation detail. Hence, class visibility defined as package private */
class RuleEngineVariableValueMap {
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private Map<String, ProgramRuleVariableValue> programRuleVariableValueMap;

    public RuleEngineVariableValueMap(List<ProgramRuleVariable> variables,
                                      Event currentEvent, List<Event> allEvents) {
        programRuleVariableValueMap = new HashMap<>();

        // if we don't have list of variables, we can't do anything
        if (variables != null) {
            Map<String, TrackedEntityDataValue> currentEventToValuesMap =
                    initEventToValuesMap(currentEvent);
            Map<String, List<TrackedEntityDataValue>> allEventsToValuesMap =
                    initEventsToValuesMap(allEvents);
            initProgramRuleVariableMap(currentEvent, currentEventToValuesMap,
                    allEventsToValuesMap, variables);
        }

        //Regardless of variables defined, we might need environment variables:
        addEnvironmentVariables(currentEvent);
    }

    private Map<String, TrackedEntityDataValue> initEventToValuesMap(Event currentEvent) {
        Map<String, TrackedEntityDataValue> eventToValueMap = new HashMap<>();

        if (currentEvent != null && currentEvent.trackedEntityDataValues() != null) {
            for (TrackedEntityDataValue value : currentEvent.trackedEntityDataValues()) {

                if (value.value() != null && value.value().length() != 0) {
                    eventToValueMap.put(value.dataElement(), value);
                }
            }
        }

        return eventToValueMap;
    }

    private Map<String, List<TrackedEntityDataValue>> initEventsToValuesMap(List<Event> allEvents) {
        Map<String, List<TrackedEntityDataValue>> eventsToValuesMap = new HashMap<>();

        if (allEvents == null || allEvents.isEmpty()) {
            return eventsToValuesMap;
        }

        Collections.sort(allEvents, Event.DATE_COMPARATOR);

        for (Event event : allEvents) {

            // if event does not contain values, skip it
            if (event.trackedEntityDataValues() == null) {
                continue;
            }

            for (TrackedEntityDataValue value : event.trackedEntityDataValues()) {
                if (!eventsToValuesMap.containsKey(value.dataElement())) {
                    eventsToValuesMap.put(value.dataElement(),
                            new ArrayList<TrackedEntityDataValue>());
                }

                // make sure the event is assigned, it is used later to check event date for
                // the data values

                if (value.event() == null) {
                    value = TrackedEntityDataValue.builder()
                            .event(event.uid())
                            .value(value.value())
                            .dataElement(value.dataElement())
                            .storedBy(value.storedBy()).build();
                }

                if (value.value() != null && value.value().length() != 0) {
                    eventsToValuesMap.get(value.dataElement()).add(value);
                }
            }
        }

        return eventsToValuesMap;
    }

    private void initProgramRuleVariableMap(
            Event currentEvent, Map<String, TrackedEntityDataValue> currentEventToValuesMap,
            Map<String, List<TrackedEntityDataValue>> allEventsToValuesMap,
            List<ProgramRuleVariable> programRuleVariables) {

        for (ProgramRuleVariable variable : programRuleVariables) {

            boolean valueFound = false;
            switch (variable.programRuleVariableSourceType()) {
                case DATAELEMENT_CURRENT_EVENT: {
                    if (currentEventToValuesMap.containsKey(variable.dataElement().uid())) {
                        TrackedEntityDataValue dataValue = currentEventToValuesMap
                                .get(variable.dataElement().uid());
                        valueFound = true;
                        addProgramRuleVariableValueToMap(variable, dataValue, null, valueFound);
                    }
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM: {
                    if (allEventsToValuesMap.containsKey(variable.dataElement().uid())) {
                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.dataElement().uid());
                        TrackedEntityDataValue dataValue = valueList.get(valueList.size() - 1);
                        valueFound = true;
                        addProgramRuleVariableValueToMap(variable, dataValue, valueList, valueFound);
                    }
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE: {
                    if (variable.programStage() != null && allEventsToValuesMap.containsKey(
                            variable.dataElement().uid())) {

                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.dataElement().uid());

                        TrackedEntityDataValue bestCandidate = null;

                        // TODO fix before running RuleEngine
                        for (TrackedEntityDataValue candidate : valueList) {

                            // if (variable.getProgramStage().getUid().equals(candidate.getEventUid().getProgramStage())) {
                            // The candidate matches the program stage, and will be newer than
                            // the potential previous candidate:
                            // bestCandidate = candidate;
                            // }
                        }

                        if (bestCandidate != null) {
                            valueFound = true;
                            addProgramRuleVariableValueToMap(variable, bestCandidate, valueList, valueFound);
                        }
                    }
                    break;
                }
                case DATAELEMENT_PREVIOUS_EVENT: {
                    if (currentEvent != null && allEventsToValuesMap.containsKey(
                            variable.dataElement().uid())) {
                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.dataElement().uid());

                        TrackedEntityDataValue bestCandidate = null;
                        for (TrackedEntityDataValue candidate : valueList) {
                            // TODO fix before running RuleEngine
                            // if (candidate.getEventUid().getEventDate().compareTo(
                            //        currentEvent.getEventDate()) >= 0) {
                            // we have reached the current event time, stop iterating, keep the
                            // previous candidate, if any
                            //    break;
                            // } else {
                            // we have not yet reached the current event, keep this candidate
                            // as it is the newest one examined:
                            //    bestCandidate = candidate;
                            // }
                        }

                        if (bestCandidate != null) {
                            valueFound = true;
                            addProgramRuleVariableValueToMap(variable, bestCandidate, valueList, valueFound);
                        }
                    }
                    break;
                }
                case CALCULATED_VALUE: {
                    //Do nothing - ASSIGN actions will populate these values
                    break;
                }
                default: {
                    // TODO: Use logger to output the not implemented source type
                    throw new UnsupportedOperationException();
                }
            }

            if (!valueFound) {
                TrackedEntityDataValue defaultValue = null;

                if (variable.dataElement() != null && variable.dataElement().valueType() != null) {
                    if (variable.dataElement().valueType() == ValueType.TEXT
                            || variable.dataElement().valueType() == ValueType.LONG_TEXT
                            || variable.dataElement().valueType() == ValueType.EMAIL
                            || variable.dataElement().valueType() == ValueType.PHONE_NUMBER) {
                        defaultValue = TrackedEntityDataValue.builder().value("''").build();
                    } else if (variable.dataElement().valueType() == ValueType.INTEGER
                            || variable.dataElement().valueType() == ValueType.INTEGER_POSITIVE
                            || variable.dataElement().valueType() == ValueType.INTEGER_NEGATIVE
                            || variable.dataElement().valueType() == ValueType.INTEGER_ZERO_OR_POSITIVE
                            || variable.dataElement().valueType() == ValueType.NUMBER
                            || variable.dataElement().valueType() == ValueType.PERCENTAGE) {
                        defaultValue = TrackedEntityDataValue.builder().value("0").build();
                    } else if (variable.dataElement().valueType() == ValueType.DATE
                            || variable.dataElement().valueType() == ValueType.DATETIME) {
                        defaultValue = TrackedEntityDataValue.builder()
                                .value("'" + (new Date()).toString() + "'").build();
                    } else if (variable.dataElement().valueType() == ValueType.BOOLEAN
                            || variable.dataElement().valueType() == ValueType.TRUE_ONLY) {
                        defaultValue = TrackedEntityDataValue.builder().value("false").build();
                    }
                } else {
                    defaultValue = TrackedEntityDataValue.builder().value("''").build();
                }

                addProgramRuleVariableValueToMap(variable, defaultValue, null, valueFound);
            }
        }
    }

    private void addEnvironmentVariables(Event currentEvent) {
        DateFormat df = new SimpleDateFormat(DATE_PATTERN, LocaleUtils.getLocale());

        if (currentEvent != null) {
            Date eventDate = currentEvent.eventDate() != null ? currentEvent.eventDate() : new Date();
            addEnviromentVariableValueToMap("event_date", df.format(eventDate), ValueType.DATE, currentEvent.eventDate() != null);
        } else {
            addEnviromentVariableValueToMap("event_date", df.format(new Date()), ValueType.DATE, false);
        }

        addEnviromentVariableValueToMap("current_date", df.format(new Date()), ValueType.DATE, true);

    }

    public ProgramRuleVariableValue getProgramRuleVariableValue(String variableName) {
        return programRuleVariableValueMap.get(variableName);
    }

    public Map<String, ProgramRuleVariableValue> getProgramRuleVariableValueMap() {
        return programRuleVariableValueMap;
    }

    private void addProgramRuleVariableValueToMap(ProgramRuleVariable programRuleVariable,
                                                  TrackedEntityDataValue value,
                                                  List<TrackedEntityDataValue> allValues,
                                                  boolean hasValue) {
        ValueType valueType = programRuleVariable.dataElement() != null ? programRuleVariable.dataElement().valueType() :
                programRuleVariable.trackedEntityAttribute() != null ? programRuleVariable.trackedEntityAttribute().valueType() : determineValueType(value.toString());
        ProgramRuleVariableValue variableValue = new ProgramRuleVariableValue(value, allValues, valueType, hasValue);
        programRuleVariableValueMap.put(programRuleVariable.displayName(), variableValue);
    }

    private void addEnviromentVariableValueToMap(String name, String value,
                                                 ValueType valueType, boolean hasValue) {
        ProgramRuleVariableValue variableValue = new ProgramRuleVariableValue(value, valueType, hasValue);
        programRuleVariableValueMap.put(name, variableValue);
    }

    private ValueType determineValueType(String value) {
        //TODO: Implement richer detection
        return ValueType.TEXT;
    }

    @Override
    public String toString() {
        return "RuleEngineVariableValueMap{" +
                "programRuleVariableValueMap=" + programRuleVariableValueMap +
                '}';
    }
}
