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

import org.hisp.dhis.client.sdk.models.dataelement.ValueType;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

        if (currentEvent != null && currentEvent.getDataValues() != null) {
            for (TrackedEntityDataValue value : currentEvent.getDataValues()) {

                if (value.getValue() != null && value.getValue().length() != 0) {
                    eventToValueMap.put(value.getDataElement(), value);
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
            if (event.getDataValues() == null) {
                continue;
            }

            for (TrackedEntityDataValue value : event.getDataValues()) {
                if (!eventsToValuesMap.containsKey(value.getDataElement())) {
                    eventsToValuesMap.put(value.getDataElement(),
                            new ArrayList<TrackedEntityDataValue>());
                }

                // make sure the event is assigned, it is used later to check event date for
                // the data values
                if (value.getEvent() == null) {
                    value.setEvent(event);
                }

                if (value.getValue() != null && value.getValue().length() != 0) {
                    eventsToValuesMap.get(value.getDataElement()).add(value);
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
            switch (variable.getSourceType()) {
                case DATAELEMENT_CURRENT_EVENT: {
                    if (currentEventToValuesMap.containsKey(variable.getDataElement().getUId())) {
                        TrackedEntityDataValue dataValue = currentEventToValuesMap
                                .get(variable.getDataElement().getUId());
                        valueFound = true;
                        addProgramRuleVariableValueToMap(variable, dataValue, null, valueFound);
                    }
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM: {
                    if (allEventsToValuesMap.containsKey(variable.getDataElement().getUId())) {
                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.getDataElement().getUId());

                        if (!valueList.isEmpty()) {
                            TrackedEntityDataValue dataValue = valueList.get(valueList.size() - 1);
                            valueFound = true;

                            addProgramRuleVariableValueToMap(variable, dataValue, valueList, valueFound);
                        }
                    }
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE: {
                    if (variable.getProgramStage() != null && allEventsToValuesMap.containsKey(
                            variable.getDataElement().getUId())) {

                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.getDataElement().getUId());

                        TrackedEntityDataValue bestCandidate = null;
                        for (TrackedEntityDataValue candidate : valueList) {

                            if (variable.getProgramStage().getUId().equals(
                                    candidate.getEvent().getProgramStage())) {

                                // The candidate matches the program stage, and will be newer than
                                // the potential previous candidate:
                                bestCandidate = candidate;
                            }
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
                            variable.getDataElement().getUId())) {
                        List<TrackedEntityDataValue> valueList = allEventsToValuesMap.get(
                                variable.getDataElement().getUId());

                        TrackedEntityDataValue bestCandidate = null;
                        for (TrackedEntityDataValue candidate : valueList) {
                            if (candidate.getEvent().getEventDate().compareTo(
                                    currentEvent.getEventDate()) >= 0) {
                                // we have reached the current event time, stop iterating, keep the
                                // previous candidate, if any
                                break;
                            } else {
                                // we have not yet reached the current event, keep this candidate
                                // as it is the newest one examined:
                                bestCandidate = candidate;
                            }
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
                    throw new NotImplementedException();
                }
            }

            if (!valueFound) {
                TrackedEntityDataValue defaultValue = new TrackedEntityDataValue();

                if (variable.getDataElement() != null) {
                    if (variable.getDataElement().getValueType() == ValueType.TEXT
                            || variable.getDataElement().getValueType() == ValueType.LONG_TEXT
                            || variable.getDataElement().getValueType() == ValueType.EMAIL
                            || variable.getDataElement().getValueType() == ValueType.PHONE_NUMBER) {
                        defaultValue.setValue("''");
                    } else if (variable.getDataElement().getValueType() == ValueType.INTEGER
                            || variable.getDataElement().getValueType() == ValueType.INTEGER_POSITIVE
                            || variable.getDataElement().getValueType() == ValueType.INTEGER_NEGATIVE
                            || variable.getDataElement().getValueType() == ValueType.INTEGER_ZERO_OR_POSITIVE
                            || variable.getDataElement().getValueType() == ValueType.NUMBER
                            || variable.getDataElement().getValueType() == ValueType.PERCENTAGE) {
                        defaultValue.setValue("0");
                    } else if (variable.getDataElement().getValueType() == ValueType.DATE
                            || variable.getDataElement().getValueType() == ValueType.DATETIME) {
                        defaultValue.setValue("'" + DateTime.now().toString() + "'");
                    } else if (variable.getDataElement().getValueType() == ValueType.BOOLEAN
                            || variable.getDataElement().getValueType() == ValueType.TRUE_ONLY) {
                        defaultValue.setValue("false");
                    }
                } else {
                    defaultValue.setValue("''");
                }

                addProgramRuleVariableValueToMap(variable, defaultValue, null, valueFound);
            }
        }
    }

    private void addEnvironmentVariables(Event currentEvent)
    {
        DateFormat df = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        if(currentEvent != null) {
            DateTime eventDate = currentEvent.getEventDate() != null ? currentEvent.getEventDate() : DateTime.now();
            addEnviromentVariableValueToMap("event_date", df.format(eventDate.toDate()), ValueType.DATE, currentEvent.getEventDate() != null);
        }
        else
        {
            addEnviromentVariableValueToMap("event_date", df.format(DateTime.now().toDate()), ValueType.DATE, false);
        }

        addEnviromentVariableValueToMap("current_date", df.format(DateTime.now().toDate()), ValueType.DATE, true);

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
        ValueType valueType = programRuleVariable.getDataElement() != null ? programRuleVariable.getDataElement().getValueType() :
                programRuleVariable.getTrackedEntityAttribute() != null ? programRuleVariable.getTrackedEntityAttribute().getValueType() : determineValueType(value.toString());
        ProgramRuleVariableValue variableValue = new ProgramRuleVariableValue(value, allValues, valueType, hasValue);
        programRuleVariableValueMap.put(programRuleVariable.getDisplayName(), variableValue);
    }

    private void addEnviromentVariableValueToMap( String name, String value,
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
