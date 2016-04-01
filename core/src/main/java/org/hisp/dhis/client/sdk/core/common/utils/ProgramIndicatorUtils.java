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

package org.hisp.dhis.client.sdk.core.common.utils;

import org.apache.commons.jexl2.JexlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.hisp.dhis.client.sdk.core.constant.IConstantService;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementService;
import org.hisp.dhis.client.sdk.core.program.IProgramService;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementService;
import org.hisp.dhis.client.sdk.core.program.IProgramStageService;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Handles logic related to ProgramIndicators such as calculating values based on expressions.
 * This class has been copied from the dhis 2 core repository and been stripped down.
 */
public class ProgramIndicatorUtils {
    private static final String NULL_REPLACEMENT = "null";

    private final IConstantService constantService;
    private final IDataElementService dataElementService;
    private final ITrackedEntityAttributeService trackedEntityAttributeService;
    private final IProgramService programService;
    private final IProgramStageService programStageService;
    private final IProgramStageDataElementService programStageDataElementService;
    private final ExpressionUtils expressionUtils;

    public ProgramIndicatorUtils(IConstantService constantService, IDataElementService
            dataElementService,
                                 ITrackedEntityAttributeService trackedEntityAttributeService,
                                 IProgramService programService, IProgramStageService
                                         programStageService,
                                 IProgramStageDataElementService programStageDataElementService,
                                 ExpressionUtils expressionUtils) {
        this.constantService = constantService;
        this.dataElementService = dataElementService;
        this.trackedEntityAttributeService = trackedEntityAttributeService;
        this.programService = programService;
        this.programStageService = programStageService;
        this.programStageDataElementService = programStageDataElementService;
        this.expressionUtils = expressionUtils;
    }

    private static boolean isZeroOrPositive(String value) {
        return StringUtils.isNumeric(value) && Double.valueOf(value) >= 0d;
    }

    /**
     * Calculate an program indicator value based on enrollment and an
     * indicator defined for a TrackedEntityInstance
     *
     * @param enrollment       enrollment
     * @param programIndicator ProgramIndicator
     * @return Indicator value
     */
    public String getProgramIndicatorValue(Enrollment enrollment, ProgramIndicator
            programIndicator) {
        if (programIndicator == null) {
            return null;
        }

        Double value = getValue(enrollment, null, programIndicator);

        if (value != null && !Double.isNaN(value)) {
            value = Precision.round(value, 2);
            return String.valueOf(value);
        }

        return null;
    }

    /**
     * Calculate an program indicator value based on a single event
     *
     * @param event            Event
     * @param programIndicator ProgramIndicator
     * @return Indicator value
     */
    public String getProgramIndicatorValue(Event event, ProgramIndicator programIndicator) {
        if (programIndicator == null) {
            return null;
        }

        Double value = getValue(null, event, programIndicator);

        if (value != null && !Double.isNaN(value)) {
            value = Precision.round(value, 2);
            return String.valueOf(value);
        }

        return null;
    }

    /**
     * Get indicator values of all program indicators defined for a TrackedEntityInstance
     *
     * @param enrollment enrollment
     * @return Map<Indicator name, Indicator value>
     */
    public Map<String, String> getProgramIndicatorValues(Enrollment enrollment) {
        Map<String, String> result = new HashMap<>();

        Program program = programService.get(enrollment.getProgram());
        Collection<ProgramIndicator> programIndicators = new HashSet(program.getProgramIndicators
                ());

        for (ProgramIndicator programIndicator : programIndicators) {
            String value = getProgramIndicatorValue(enrollment, programIndicator);

            if (value != null) {
                result.put(programIndicator.getDisplayName(),
                        getProgramIndicatorValue(enrollment, programIndicator));
            }
        }

        return result;
    }

    /**
     * Get description of an indicator expression
     *
     * @param expression A expression string
     * @return The description
     */
    public String getExpressionDescription(String expression) {
        StringBuffer description = new StringBuffer();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher(expression);

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (ProgramIndicator.KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);

                ProgramStage programStage = programStageService.get(uid);
                DataElement dataElement = dataElementService.get(de);

                if (programStage != null && dataElement != null) {
                    String programStageName = programStage.getDisplayName();

                    String dataelementName = dataElement.getDisplayName();

                    matcher.appendReplacement(description, programStageName + ProgramIndicator
                            .SEPARATOR_ID + dataelementName);
                }
            } else if (ProgramIndicator.KEY_ATTRIBUTE.equals(key)) {
                TrackedEntityAttribute attribute = trackedEntityAttributeService.get(uid);

                if (attribute != null) {
                    matcher.appendReplacement(description, attribute.getDisplayName());
                }
            } else if (ProgramIndicator.KEY_CONSTANT.equals(key)) {
                Constant constant = constantService.get(uid);

                if (constant != null) {
                    matcher.appendReplacement(description, constant.getDisplayName());
                }
            } else if (ProgramIndicator.KEY_PROGRAM_VARIABLE.equals(key)) {
                if (ProgramIndicator.CURRENT_DATE.equals(uid)) {
                    matcher.appendReplacement(description, "Current date");
                } else if (ProgramIndicator.ENROLLMENT_DATE.equals(uid)) {
                    matcher.appendReplacement(description, "Enrollment date");
                } else if (ProgramIndicator.INCIDENT_DATE.equals(uid)) {
                    matcher.appendReplacement(description, "Incident date");
                } else if (ProgramIndicator.VALUE_COUNT.equals(uid)) {
                    matcher.appendReplacement(description, "Value count");
                }
            }
        }

        matcher.appendTail(description);

        return description.toString();

    }

    /**
     * Get description of an indicator expression
     *
     * @param expression A expression string
     * @return The expression is valid or not
     */
    public String expressionIsValid(String expression) {
        StringBuffer description = new StringBuffer();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher(expression);

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (ProgramIndicator.KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);

                ProgramStage programStage = programStageService.get(uid);
                DataElement dataElement = dataElementService.get(de);

                if (programStage != null && dataElement != null) {
                    matcher.appendReplacement(description, String.valueOf(1));
                } else {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            } else if (ProgramIndicator.KEY_ATTRIBUTE.equals(key)) {
                TrackedEntityAttribute attribute = trackedEntityAttributeService.get(uid);

                if (attribute != null) {
                    matcher.appendReplacement(description, String.valueOf(1));
                } else {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            } else if (ProgramIndicator.KEY_CONSTANT.equals(key)) {
                Constant constant = constantService.get(uid);

                if (constant != null) {
                    matcher.appendReplacement(description, String.valueOf(constant.getValue()));
                } else {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            } else if (ProgramIndicator.KEY_PROGRAM_VARIABLE.equals(key)) {
                matcher.appendReplacement(description, String.valueOf(0));
            }
        }

        matcher.appendTail(description);

        // ---------------------------------------------------------------------
        // Well-formed expression
        // ---------------------------------------------------------------------
        if (expressionUtils.isValid(description.toString(), null)) {
            return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
        }

        return ProgramIndicator.VALID;
    }

    /**
     * Get all {@link ProgramStageDataElement} part of the expression of the
     * given indicator.
     *
     * @param indicator the ProgramIndicator.
     * @return a set of ProgramStageDataElements.
     */
    public Set<ProgramStageDataElement> getProgramStageDataElementsInExpression(ProgramIndicator
                                                                                        indicator) {
        Set<ProgramStageDataElement> elements = new HashSet<>();

        Matcher matcher = ProgramIndicator.DATAELEMENT_PATTERN.matcher(indicator.getExpression());

        while (matcher.find()) {
            String ps = matcher.group(1);
            String de = matcher.group(2);

            ProgramStage programStage = programStageService.get(ps);
            programStage.setProgramStageDataElements(programStageDataElementService.list
                    (programStage));
            Map<String, ProgramStageDataElement> dataElementToProgramStageDataElementMap = new
                    HashMap<>();
            for (ProgramStageDataElement programStageDataElement : programStage
                    .getProgramStageDataElements()) {
                dataElementToProgramStageDataElementMap.put(programStageDataElement
                        .getDataElement().getUId(), programStageDataElement);
            }
            DataElement dataElement = dataElementService.get(de);

            if (programStage != null && dataElement != null) {
                elements.add(dataElementToProgramStageDataElementMap.get(dataElement.getUId()));
            }
        }

        return elements;
    }

    public List<String> getDataElementsInExpression(ProgramIndicator indicator) {
        List<String> elements = new ArrayList<>();

        Matcher matcher = ProgramIndicator.DATAELEMENT_PATTERN.matcher(indicator.getExpression());

        while (matcher.find()) {
            String ps = matcher.group(1);
            String de = matcher.group(2);
            elements.add(de);
        }

        return elements;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Get all {@link TrackedEntityAttribute} part of the expression of the
     * given indicator.
     *
     * @param indicator the ProgramIndicator.
     * @return a set of TrackedEntityAttributes.
     */
    public Set<TrackedEntityAttribute> getAttributesInExpression(ProgramIndicator indicator) {
        Set<TrackedEntityAttribute> attributes = new HashSet<>();

        Matcher matcher = ProgramIndicator.ATTRIBUTE_PATTERN.matcher(indicator.getExpression());

        while (matcher.find()) {
            String at = matcher.group(1);

            TrackedEntityAttribute attribute = trackedEntityAttributeService.get(at);

            if (attribute != null) {
                attributes.add(attribute);
            }
        }

        return attributes;
    }

    /**
     * @param currentEnrollment can be null if currentEvent is not null in case single
     *                          currentEvent without reg
     * @param currentEvent      can be null if currentEnrollment is not null
     * @param indicator
     * @return
     */
    private Double getValue(Enrollment currentEnrollment, Event currentEvent, ProgramIndicator
            indicator) {
        StringBuffer buffer = new StringBuffer();

        String expression = indicator.getExpression();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher(expression);

        int valueCount = 0;
        int zeroPosValueCount = 0;
        Event event = null;
        Program program = programService.get(currentEnrollment.getUId());
        program.setProgramStages(programStageService.list(program));
        Map<String, Event> programStageToEventMap = new HashMap<>();
        for (Event event1 : currentEnrollment.getEvents()) {
            programStageToEventMap.put(event1.getProgramStage(), event1);
        }
        Map<String, ProgramStage> programStageMap = new HashMap<>();
        for (ProgramStage programStage : program.getProgramStages()) {
            programStageMap.put(programStage.getUId(), programStage);
        }
        Map<String, TrackedEntityAttributeValue> trackedEntityAttributeValueMap = new HashMap<>();
        for (TrackedEntityAttributeValue trackedEntityAttributeValue : currentEnrollment
                .getTrackedEntityAttributeValues()) {
            trackedEntityAttributeValueMap.put(trackedEntityAttributeValue
                    .getTrackedEntityAttributeUId(), trackedEntityAttributeValue);
        }


        Map<String, TrackedEntityDataValue> dataElementToDataValues = new HashMap<>();
        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (ProgramIndicator.KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);
                String programStageUid = uid;

                if (programStageUid != null && de != null) {
                    if (currentEnrollment == null) { //in case single currentEvent without reg
                        if (event == null) {
                            event = currentEvent;
                            if (event.getDataValues() != null) {
                                for (TrackedEntityDataValue dataValue : event
                                        .getDataValues()) {
                                    dataElementToDataValues.put(dataValue.getDataElement(),
                                            dataValue);
                                }
                            }
                        }
                    } else {
                        if (event == null || !event.getUId().equals(programStageUid)) {
                            event = programStageToEventMap.get(programStageUid);
                            dataElementToDataValues.clear();
                            if (event.getDataValues() != null) {
                                for (TrackedEntityDataValue dataValue : event.getDataValues()) {
                                    dataElementToDataValues.put(dataValue.getDataElement(),
                                            dataValue);
                                }
                            }
                        }
                    }

                    TrackedEntityDataValue dataValue;
                    if (event.getDataValues() == null) {
                        continue;
                    }
                    dataValue = dataElementToDataValues.get(de);

                    String value;
                    if (dataValue == null || dataValue.getValue() == null || dataValue.getValue()
                            .isEmpty()) {
                        value = NULL_REPLACEMENT;
                    } else {
                        value = dataValue.getValue();

                        valueCount++;
                        zeroPosValueCount = isZeroOrPositive(value) ? (zeroPosValueCount + 1) :
                                zeroPosValueCount;
                    }

                    matcher.appendReplacement(buffer, value);
                } else {
                    continue;
                }
            } else if (ProgramIndicator.KEY_ATTRIBUTE.equals(key)) {
                if (currentEnrollment != null) { //in case single currentEvent without reg

                    if (uid != null) {
                        TrackedEntityAttributeValue attributeValue =
                                trackedEntityAttributeValueMap.get(uid);
                        String value;
                        if (attributeValue == null || attributeValue.getValue() == null ||
                                attributeValue.getValue().isEmpty()) {
                            value = NULL_REPLACEMENT;
                        } else {
                            value = attributeValue.getValue();

                            valueCount++;
                            zeroPosValueCount = isZeroOrPositive(value) ? (zeroPosValueCount + 1)
                                    : zeroPosValueCount;
                        }
                        matcher.appendReplacement(buffer, value);
                    } else {
                        continue;
                    }
                }
            } else if (ProgramIndicator.KEY_CONSTANT.equals(key)) {
                Constant constant = constantService.get(uid);

                if (constant != null) {
                    matcher.appendReplacement(buffer, String.valueOf(constant.getValue()));
                } else {
                    continue;
                }
            } else if (ProgramIndicator.KEY_PROGRAM_VARIABLE.equals(key)) {
                if (currentEnrollment != null) { //in case of single currentEvent without reg
                    DateTime currentDate = DateTime.now();
                    DateTime date = null;

                    if (ProgramIndicator.ENROLLMENT_DATE.equals(uid)) {
                        date = currentEnrollment.getDateOfEnrollment();
                    } else if (ProgramIndicator.INCIDENT_DATE.equals(uid)) {
                        date = currentEnrollment.getDateOfIncident();
                    } else if (ProgramIndicator.CURRENT_DATE.equals(uid)) {
                        date = currentDate;
                    }

                    if (date != null) {
                        matcher.appendReplacement(buffer, Days.daysBetween(currentDate, date)
                                .toString());
                    }
                }
            }
        }

        if (valueCount <= 0) {
            //returning null in case there are now values in the expression.
            return null;
        }
        matcher.appendTail(buffer);
        expression = buffer.toString();

        // ---------------------------------------------------------------------
        // Value count variable
        // ---------------------------------------------------------------------

        buffer = new StringBuffer();
        matcher = ProgramIndicator.VALUECOUNT_PATTERN.matcher(expression);

        while (matcher.find()) {
            String var = matcher.group(1);

            if (ProgramIndicator.VAR_VALUE_COUNT.equals(var)) {
                matcher.appendReplacement(buffer, String.valueOf(valueCount));
            } else if (ProgramIndicator.VAR_ZERO_POS_VALUE_COUNT.equals(var)) {
                matcher.appendReplacement(buffer, String.valueOf(zeroPosValueCount));
            }
        }

        matcher.appendTail(buffer);
        expression = buffer.toString();
        Double value;
        try {
            value = expressionUtils.evaluateToDouble(expression, null);
        } catch (JexlException e) {
            e.printStackTrace();
            value = new Double(0);
        }
        return value;
    }
}
