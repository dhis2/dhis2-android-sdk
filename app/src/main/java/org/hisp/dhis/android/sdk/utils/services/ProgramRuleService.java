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

import org.apache.commons.jexl2.JexlException;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.utils.api.ContextVariableType;
import org.hisp.dhis.android.sdk.utils.api.ProgramRuleActionType;
import org.hisp.dhis.android.sdk.utils.api.ProgramRuleVariableSourceType;
import org.hisp.dhis.android.sdk.utils.api.ValueType;
import org.hisp.dhis.android.sdk.utils.comparators.EventDateComparator;
import org.hisp.dhis.android.sdk.utils.support.ExpressionUtils;
import org.hisp.dhis.android.sdk.utils.support.TextUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;
import static org.hisp.dhis.android.sdk.utils.support.ExpressionUtils.isBoolean;
import static org.hisp.dhis.android.sdk.utils.support.ExpressionUtils.isNumeric;

public class ProgramRuleService {

    private static final String CLASS_TAG = ProgramRuleService.class.getSimpleName();

    private static final Pattern CONDITION_PATTERN = Pattern.compile("([#AV])\\{(.+?)\\}");

    private Map<String, ProgramRuleVariable> programRuleVariableMap;
    private List<Event> eventsForEnrollment;
    private Enrollment currentEnrollment;
    private Event currentEvent;
    private Map<String, DataElement> dataElementMap;
    private Map<String, TrackedEntityAttribute> trackedEntityAttributeMap;
    private Map<String, List<Event>> eventsForProgramStages;
    private Map<Event, Map<String, DataValue>> eventDataValueMaps;

    private static ProgramRuleService programRuleService;

    public static ProgramRuleService getInstance() {
        return programRuleService;
    }

    public Map<String, ProgramRuleVariable> getProgramRuleVariableMap() {
        return programRuleVariableMap;
    }

    public static void setProgramRuleVariableMap(Map<String, ProgramRuleVariable> programRuleVariableMap) {
        getInstance().programRuleVariableMap = programRuleVariableMap;
    }

    public List<Event> getEventsForEnrollment() {
        return eventsForEnrollment;
    }

    public void setEventsForEnrollment(List<Event> eventsForEnrollment) {
        this.eventsForEnrollment = eventsForEnrollment;
    }

    public Enrollment getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(Enrollment currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public Map<String, DataElement> getDataElementMap() {
        return dataElementMap;
    }

    public void setDataElementMap(Map<String, DataElement> dataElementMap) {
        this.dataElementMap = dataElementMap;
    }

    public Map<String, TrackedEntityAttribute> getTrackedEntityAttributeMap() {
        return trackedEntityAttributeMap;
    }

    public void setTrackedEntityAttributeMap(Map<String, TrackedEntityAttribute> trackedEntityAttributeMap) {
        this.trackedEntityAttributeMap = trackedEntityAttributeMap;
    }

    public Map<String, List<Event>> getEventsForProgramStages() {
        return eventsForProgramStages;
    }

    public void setEventsForProgramStages(Map<String, List<Event>> eventsForProgramStages) {
        this.eventsForProgramStages = eventsForProgramStages;
    }

    public Map<Event, Map<String, DataValue>> getEventDataValueMaps() {
        return eventDataValueMaps;
    }

    public void setEventDataValueMaps(Map<Event, Map<String, DataValue>> eventDataValueMaps) {
        this.eventDataValueMaps = eventDataValueMaps;
    }

    public static ProgramRuleService getProgramRuleService() {
        return programRuleService;
    }

    public static void setProgramRuleService(ProgramRuleService programRuleService) {
        ProgramRuleService.programRuleService = programRuleService;
    }

    static {
        programRuleService = new ProgramRuleService();
    }

    /**
     * Initializes the ProgramRuleService before evaluations can be made
     * @param enrollment
     * @param currentEvent
     */
    public static void initialize(Enrollment enrollment, Event currentEvent) {
        //setting current enrollment and event
        getInstance().setCurrentEnrollment(enrollment);
        getInstance().setCurrentEvent(currentEvent);

        //setting list of events for enrollment
        List<Event> events;
        if(getInstance().getCurrentEnrollment() != null) {
            events = getInstance().getCurrentEnrollment().getEvents();
        } else {
            events = new ArrayList<>();
        }
        getInstance().setEventsForEnrollment(events);
        if(getInstance().getCurrentEvent() != null &&
                !getInstance().getEventsForEnrollment().contains(getInstance().getCurrentEvent())) {
            getInstance().getEventsForEnrollment().add(getInstance().getCurrentEvent());
        }
        Collections.sort(getInstance().getEventsForEnrollment(), new EventDateComparator());

        //setting data elements map
        List<DataElement> dataElements = MetaDataController.getDataElements();
        getInstance().setDataElementMap(new HashMap<String, DataElement>());
        for(DataElement dataElement : dataElements) {
            getInstance().getDataElementMap().put(dataElement.getUid(), dataElement);
        }

        //setting trackedEntityAttribute map
        List<TrackedEntityAttribute> trackedEntityAttributes = MetaDataController.getTrackedEntityAttributes();
        getInstance().setTrackedEntityAttributeMap(new HashMap<String, TrackedEntityAttribute>());
        for(TrackedEntityAttribute trackedEntityAttribute : trackedEntityAttributes) {
            getInstance().getTrackedEntityAttributeMap().put(trackedEntityAttribute.getUid(), trackedEntityAttribute);
        }

        //setting events in map for each program stage
        getInstance().setEventsForProgramStages(new HashMap<String, List<Event>>());
        for(Event event : getInstance().getEventsForEnrollment()) {
            List<Event> eventsForProgramStage = getInstance().getEventsForProgramStages().get(event.getProgramStageId());
            if(eventsForProgramStage == null) {
                eventsForProgramStage = new ArrayList<>();
                getInstance().getEventsForProgramStages().put(event.getProgramStageId(), eventsForProgramStage);
            }
            eventsForProgramStage.add(event);
        }

        //setting data values in map for each event
        getInstance().setEventDataValueMaps(new HashMap<Event, Map<String, DataValue>>());
        for(Event event : getInstance().getEventsForEnrollment()) {
            Map<String, DataValue> dataValueMap = new HashMap<>();
            for(DataValue dataValue : event.getDataValues()) {
                dataValueMap.put(dataValue.getDataElement(), dataValue);
            }
            getInstance().getEventDataValueMaps().put(event, dataValueMap);
        }

        //setting programRuleVariables
        List<ProgramRuleVariable> programRuleVariables = MetaDataController.getProgramRuleVariables();
        ProgramRuleVariableService.populateDataElementAndAttributeVariables(programRuleVariables);
        programRuleVariables.addAll(ProgramRuleVariableService.createContextVariables());
        programRuleVariables.addAll(ProgramRuleVariableService.createConstantVariables());
        Map<String, ProgramRuleVariable> programRuleVariableMap = new HashMap<>();
        for(ProgramRuleVariable programRuleVariable : programRuleVariables) {
            programRuleVariableMap.put(programRuleVariable.getName(), programRuleVariable);
        }
        getInstance().setProgramRuleVariableMap(programRuleVariableMap);
    }

    public static boolean evaluate(final String condition) {
        String conditionReplaced = getReplacedCondition(condition);
        boolean isTrue = false;
        try {
            isTrue = ExpressionUtils.isTrue(conditionReplaced, null);
        } catch(JexlException jxlException) {
            jxlException.printStackTrace();
        }
        return isTrue;
    }

    public static String getReplacedCondition(String condition) {
        StringBuffer buffer = new StringBuffer();

        Matcher matcher = CONDITION_PATTERN.matcher(condition);

        while (matcher.find()) {
            String value;
            String variablePrefix = matcher.group(1);
            String variableName = matcher.group(2);
            value = ProgramRuleVariableService.getReplacementForProgramRuleVariable(variableName);

            if(!isNumeric(value) && !isBoolean(value)) {
                value = '\'' + value + '\'';
            }
            matcher.appendReplacement(buffer, value);
        }

        return TextUtils.appendTail(matcher, buffer);
    }

    public static String getCalculatedConditionValue(String condition) {
        String conditionReplaced = getReplacedCondition(condition);
        Object result = ExpressionUtils.evaluate(conditionReplaced, null);
        String stringResult = String.valueOf(result);
        return stringResult;
    }

    public static String getDefaultValue(ValueType valueType) {
        String value;
        switch (valueType) {
            case BOOLEAN:
            case TRUE_ONLY:
                value = "false";
                break;
            case INTEGER_POSITIVE:
            case INTEGER:
            case INTEGER_ZERO_OR_POSITIVE:
            case INTEGER_NEGATIVE:
            case NUMBER:
            case PERCENTAGE:
            case UNIT_INTERVAL:
                value = "0";
                break;
            case DATE:
            case DATETIME:
            case LONG_TEXT:
            case TEXT:
                value = "";
                break;
            default: value = "";
        }
        return value;
    }

    public static List<String> getDataElementsInRule(ProgramRule programRule) {
        String condition = programRule.getCondition();
        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        List<String> dataElementsInRule = new ArrayList<>();

        while (matcher.find()) {
            String variableName = matcher.group(2);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable != null && programRuleVariable.getDataElement() != null) {
                dataElementsInRule.add(programRuleVariable.getDataElement());
            }
        }

        for(ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
            if(programRuleAction.getProgramRuleActionType().equals(ProgramRuleActionType.ASSIGN) && programRuleAction.getContent() != null) {
                String programRuleVariableName = programRuleAction.getContent().substring(2, programRuleAction.getContent().length()-1);
                ProgramRuleVariable programRuleVariable = getInstance().getProgramRuleVariableMap().get(programRuleVariableName);
                if(programRuleVariable.getDataElement() != null) {
                    dataElementsInRule.add(programRuleVariable.getDataElement());
                }
            }
            if(programRuleAction.getDataElement() != null) {
                dataElementsInRule.add(programRuleAction.getDataElement());
            }
        }

        return dataElementsInRule;
    }

    public static List<String> getTrackedEntityAttributesInRule(ProgramRule programRule) {
        String condition = programRule.getCondition();
        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        List<String> trackedEntityAttributesInRule = new ArrayList<>();

        while (matcher.find()) {
            String variableName = matcher.group(2);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if (programRuleVariable != null && programRuleVariable.getTrackedEntityAttribute() != null) {
                trackedEntityAttributesInRule.add(programRuleVariable.getTrackedEntityAttribute());
            }
        }

        for(ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
            if(programRuleAction.getProgramRuleActionType().equals(ProgramRuleActionType.ASSIGN) && programRuleAction.getContent() != null) {
                String programRuleVariableName = programRuleAction.getContent().substring(2, programRuleAction.getContent().length()-1);
                ProgramRuleVariable programRuleVariable = getInstance().getProgramRuleVariableMap().get(programRuleVariableName);
                if(programRuleVariable.getTrackedEntityAttribute() != null) {
                    trackedEntityAttributesInRule.add(programRuleVariable.getTrackedEntityAttribute());
                }
            }
        }

        return trackedEntityAttributesInRule;
    }
}
