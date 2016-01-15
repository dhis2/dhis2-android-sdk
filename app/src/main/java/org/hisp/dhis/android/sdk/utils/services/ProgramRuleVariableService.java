/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.utils.services;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.utils.api.ContextVariableType;
import org.hisp.dhis.android.sdk.utils.api.ProgramRuleVariableSourceType;
import org.hisp.dhis.android.sdk.utils.api.ValueType;
import org.hisp.dhis.android.sdk.utils.comparators.EventDateComparator;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class ProgramRuleVariableService {
    public static String processSingleValue(String processedValue, ValueType valueType){
        switch (valueType) {
            case LONG_TEXT:
            case TEXT:
            case DATE:
                //Append single quotation marks in case the variable is of text or date type:
                if(processedValue != null) {
                    processedValue = "'" + processedValue + "'";
                } else {
                    processedValue = "''";
                }
                break;

            case BOOLEAN:
            case TRUE_ONLY:
                if(processedValue == null || processedValue.isEmpty() || !processedValue.equals("true")) {
                    processedValue = "false";
                }
                break;

            case INTEGER:
            case NUMBER:
            case INTEGER_POSITIVE:
            case INTEGER_NEGATIVE:
            case INTEGER_ZERO_OR_POSITIVE:
            case PERCENTAGE:
                if(processedValue != null) {
                    processedValue = String.valueOf(Double.valueOf(processedValue));
                } else {
                    processedValue = "0";
                }
                break;
        }

        return processedValue;
    }

    public static void populateDataElementAndAttributeVariables(List<ProgramRuleVariable> programRuleVariables) {
        for(ProgramRuleVariable programRuleVariable : programRuleVariables) {
            populateDataElementOrAttributeVariable(programRuleVariable);
        }
    }

    public static void populateDataElementOrAttributeVariable(ProgramRuleVariable programRuleVariable) {
        String value = null;
        String defaultValue = "";
        List<String> allValues = new ArrayList<>();
        switch (programRuleVariable.getSourceType()) {
            case CONSTANT:
            case CALCULATED_VALUE:
                break;
            case DATAELEMENT_NEWEST_EVENT_PROGRAM: {
                DataValue dataValue;
                for (Event event : ProgramRuleService.getInstance().getEventsForEnrollment()) {
                    dataValue = ProgramRuleService.getInstance().getEventDataValueMaps().get(event).get(programRuleVariable.getDataElement());
                    if (dataValue != null) {
                        if(value == null) {
                            value = dataValue.getValue();
                        }
                        allValues.add(dataValue.getValue());
                    }
                }
                break;
            }
            case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE: {
                DataValue dataValue = null;
                String programStage = programRuleVariable.getProgramStage();
                if(programStage != null) {
                    List<Event> eventsForProgramStage = ProgramRuleService.getInstance().getEventsForProgramStages().get(programStage);
                    //in some cases the program stage may not have been loaded onto the device, although all variables have
                    //in this case we the current variable doesn't matter as it is from a different program,
                    //so we can ignore it.
                    if(eventsForProgramStage != null) {
                        for (Event event : eventsForProgramStage) {
                            dataValue = ProgramRuleService.getInstance().getEventDataValueMaps().get(event).get(programRuleVariable.getDataElement());
                            if (dataValue != null) {
                                if(value == null) {
                                    value = dataValue.getValue();
                                }
                                allValues.add(dataValue.getValue());
                            }
                        }
                        if (dataValue != null) {
                            value = dataValue.getValue();
                        } else {
                            DataElement dataElement = ProgramRuleService.getInstance().getDataElementMap().get(programRuleVariable.getDataElement());
                            defaultValue = ProgramRuleService.getDefaultValue(dataElement.getValueType());
                        }
                    }
                }
                break;
            }
            case DATAELEMENT_PREVIOUS_EVENT: {
                if(ProgramRuleService.getInstance().getCurrentEvent() != null) {
                    DataValue dataValue;
                    Comparator<Event> comparator = new EventDateComparator();
                    //select a value from an event that precedes 'currentEvent'
                    for (Event event : ProgramRuleService.getInstance().getEventsForEnrollment()) {
                        if(event.getUid().equals(ProgramRuleService.getInstance().getCurrentEvent().getUid())) {
                            continue;
                        }
                        //if currentEvent is later than 'event'
                        if (comparator.compare(ProgramRuleService.getInstance().getCurrentEvent(), event) > 0) {
                            dataValue = ProgramRuleService.getInstance().getEventDataValueMaps().get(event).get(programRuleVariable.getDataElement());
                            if (dataValue != null) {
                                if (value == null) {
                                    value = dataValue.getValue();
                                }
                                allValues.add(dataValue.getValue());
                            }
                        }
                    }
                }
                break;
            }
            case TEI_ATTRIBUTE: {
                if(ProgramRuleService.getInstance().getCurrentEnrollment() != null) {
                    TrackedEntityAttributeValue trackedEntityAttributeValue = null;
                    for (TrackedEntityAttributeValue storedValue : ProgramRuleService.getInstance().getCurrentEnrollment().getAttributes()) {
                        if (storedValue.getTrackedEntityAttributeId().equals(programRuleVariable.getTrackedEntityAttribute())) {
                            trackedEntityAttributeValue = storedValue;
                            break;
                        }
                    }
                    if (trackedEntityAttributeValue != null) {
                        value = trackedEntityAttributeValue.getValue();
                        allValues.add(value);
                    }
                }
                break;
            }
            default:
            case DATAELEMENT_CURRENT_EVENT: {
                if(ProgramRuleService.getInstance().getCurrentEvent() != null) {
                    DataValue dataValue = ProgramRuleService.getInstance().getEventDataValueMaps().get(ProgramRuleService.getInstance().getCurrentEvent()).get(programRuleVariable.getDataElement());
                    if (dataValue != null) {
                        value = dataValue.getValue();
                        allValues.add(value);
                    }
                }
                break;
            }
        }
        if(isEmpty(value)) {
            programRuleVariable.setHasValue(false);
            value = defaultValue;
        } else {
            programRuleVariable.setHasValue(true);
        }
        programRuleVariable.setVariableValue(value);
        programRuleVariable.setAllValues(allValues);
    }

    public static List<ProgramRuleVariable> createConstantVariables() {
        List<Constant> constants = MetaDataController.getConstants();
        List<ProgramRuleVariable> programRuleVariables = new ArrayList<>();
        for(Constant constant : constants) {
            programRuleVariables.add(createConstantVariable(constant));
        }
        return programRuleVariables;
    }

    private static ProgramRuleVariable createConstantVariable(Constant constant) {
        if(constant != null) {
            ProgramRuleVariable programRuleVariable = new ProgramRuleVariable();
            programRuleVariable.setVariableValue(Double.toString(constant.getValue()));
            programRuleVariable.setHasValue(true);
            programRuleVariable.setName(constant.getName());
            programRuleVariable.setSourceType(ProgramRuleVariableSourceType.CONSTANT);
            return programRuleVariable;
        } else {
            return null;
        }
    }

    public static List<ProgramRuleVariable> createContextVariables() {
        List<ProgramRuleVariable> programRuleVariables = new ArrayList<>();
        for(ContextVariableType type : ContextVariableType.values()) {
            programRuleVariables.add(createContextVariable(type.toString(),
                    ProgramRuleService.getInstance().getCurrentEvent(), ProgramRuleService.getInstance().getCurrentEnrollment(),
                    ProgramRuleService.getInstance().getEventsForEnrollment()));
        }
        return programRuleVariables;
    }

    private static ProgramRuleVariable createContextVariable(String variableName, Event executingEvent, Enrollment executingEnrollment, List<Event> events) {
        ContextVariableType contextVariableType = ContextVariableType.fromValue(variableName);
        ProgramRuleVariable programRuleVariable = new ProgramRuleVariable();
        programRuleVariable.setName(variableName);
        programRuleVariable.setSourceType(ProgramRuleVariableSourceType.CALCULATED_VALUE);
        switch (contextVariableType) {
            case CURRENT_DATE:
                programRuleVariable.setVariableValue(DateTime.now().toString());
                programRuleVariable.setHasValue(true);
                programRuleVariable.setVariableType(ValueType.DATE);
                break;
            case EVENT_DATE:
                if(executingEvent != null) {
                    programRuleVariable.setVariableValue(executingEvent.getEventDate());
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.DATE);
                } else {
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableValue("");
                    programRuleVariable.setVariableType(ValueType.DATE);
                }
                break;
            case DUE_DATE:
                if(executingEvent != null) {
                    programRuleVariable.setVariableValue(executingEvent.getDueDate());
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.DATE);
                } else {
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableValue("");
                    programRuleVariable.setVariableType(ValueType.DATE);
                }
                break;
            case EVENT_COUNT:
                programRuleVariable.setVariableValue(Integer.toString(events.size()));
                programRuleVariable.setHasValue(true);
                programRuleVariable.setVariableType(ValueType.INTEGER);
                break;
            case ENROLLMENT_DATE:
                if(executingEnrollment != null) {
                    programRuleVariable.setVariableValue(executingEnrollment.getEnrollmentDate());
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.DATE);
                } else {
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableValue("");
                    programRuleVariable.setVariableType(ValueType.DATE);
                }
                break;
            case ENROLLMENT_ID:
                if(executingEnrollment != null) {
                    programRuleVariable.setVariableValue(executingEnrollment.getEnrollment());
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.TEXT);
                } else {
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableValue("");
                    programRuleVariable.setVariableType(ValueType.TEXT);
                }
                break;
            case EVENT_ID:
                if(executingEvent != null) {
                    programRuleVariable.setVariableValue(executingEvent.getEvent());
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.TEXT);
                } else {
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableValue("");
                    programRuleVariable.setVariableType(ValueType.TEXT);
                }
                break;
            case INCIDENT_DATE:
                if(executingEnrollment != null) {
                    programRuleVariable.setVariableValue(executingEnrollment.getIncidentDate());
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.DATE);
                } else {
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableValue("");
                    programRuleVariable.setVariableType(ValueType.DATE);
                }
                break;
            case ENROLLMENT_COUNT: {
                if(executingEnrollment != null) {
                    programRuleVariable.setVariableValue("1");
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.INTEGER);
                } else {
                    programRuleVariable.setVariableValue("0");
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableType(ValueType.INTEGER);
                }
                break;
            }
            case TEI_COUNT: {
                if(executingEnrollment != null) {
                    programRuleVariable.setVariableValue("1");
                    programRuleVariable.setHasValue(true);
                    programRuleVariable.setVariableType(ValueType.INTEGER);
                } else {
                    programRuleVariable.setVariableValue("0");
                    programRuleVariable.setHasValue(false);
                    programRuleVariable.setVariableType(ValueType.INTEGER);
                }
                break;
            }
        }
        return programRuleVariable;
    }

    public static String getReplacementForProgramRuleVariable(String variableName) {
        String value;
        ProgramRuleVariable programRuleVariable = ProgramRuleService.getInstance().getProgramRuleVariableMap().get(variableName);
        value = retrieveVariableValue(programRuleVariable);
        return value;
    }

    public static String retrieveVariableValue(ProgramRuleVariable programRuleVariable) {
        String defaultValue = "";
        if (programRuleVariable == null) {
            return defaultValue;
        }
        String value = programRuleVariable.getVariableValue();
        if(isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }
}
