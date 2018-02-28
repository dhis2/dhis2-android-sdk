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
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.utils.api.ValueType;
import org.hisp.dhis.android.sdk.utils.support.DateUtils;
import org.hisp.dhis.android.sdk.utils.support.ExpressionUtils;
import org.hisp.dhis.android.sdk.utils.support.MathUtils;
import org.hisp.dhis.android.sdk.utils.support.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author Chau Thu Tran
 */

/**
 * Handles logic related to ProgramIndicators such as calculating values based on expressions.
 * This class has been copied from the dhis 2 core repository and been stripped down.
 */
public class ProgramIndicatorService {
    public static final String CLASS_TAG = ProgramIndicatorService.class.getSimpleName();
    private static final String NULL_REPLACEMENT = "null";
    /**
     * Calculate an program indicator value based on program instance and an
     * indicator defined for a TrackedEntityInstance
     *
     * @param programInstance  ProgramInstance
     * @param programIndicator ProgramIndicator
     * @return Indicator value
     */
    public static String getProgramIndicatorValue(Enrollment programInstance, ProgramIndicator programIndicator) {
        if(programIndicator == null) {
            return null;
        }
        
        Double value = getValue(programInstance, null, programIndicator);

        return TextUtils.fromDouble(value);
    }

    /**
     * Calculate an program indicator value based on a single event
     *
     * @param event            Event
     * @param programIndicator ProgramIndicator
     * @return Indicator value
     */
    public static String getProgramIndicatorValue(Event event, ProgramIndicator programIndicator) {
        if(programIndicator == null) {
            return null;
        }
        
        Double value = getValue(null, event, programIndicator);

        return TextUtils.fromDouble(value);
    }

    /**
     * Get indicator values of all program indicators defined for a TrackedEntityInstance
     *
     * @param programInstance ProgramInstance
     * @return Map<Indicator name, Indicator value>
     */
    public static Map<String, String> getProgramIndicatorValues(Enrollment programInstance) {
        Map<String, String> result = new HashMap<>();

        Collection<ProgramIndicator> programIndicators = new HashSet(programInstance.getProgram().getProgramIndicators());

        for (ProgramIndicator programIndicator : programIndicators) {
            String value = getProgramIndicatorValue(programInstance, programIndicator);

            if (value != null) {
                result.put(programIndicator.getDisplayName(),
                    getProgramIndicatorValue(programInstance, programIndicator));
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
    public static String getExpressionDescription(String expression) {
        StringBuffer description = new StringBuffer();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher(expression);

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (ProgramIndicator.KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);

                ProgramStage programStage = MetaDataController.getProgramStage(uid);
                DataElement dataElement = MetaDataController.getDataElement(de);

                if (programStage != null && dataElement != null) {
                    String programStageName = programStage.getDisplayName();

                    String dataelementName = dataElement.getDisplayName();

                    matcher.appendReplacement(description, programStageName + ProgramIndicator.SEPARATOR_ID + dataelementName);
                }
            } else if (ProgramIndicator.KEY_ATTRIBUTE.equals(key)) {
                TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute(uid);

                if (attribute != null) {
                    matcher.appendReplacement(description, attribute.getDisplayName());
                }
            } else if (ProgramIndicator.KEY_CONSTANT.equals(key)) {
                Constant constant = MetaDataController.getConstant(uid);

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
                } else if (ProgramIndicator.EVENT_DATE.equals(uid)) {
                        matcher.appendReplacement(description, "Event date");
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
    public static String expressionIsValid(String expression) {
        StringBuffer description = new StringBuffer();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher(expression);

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (ProgramIndicator.KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);

                ProgramStage programStage = MetaDataController.getProgramStage(uid);
                DataElement dataElement = MetaDataController.getDataElement(de);

                if (programStage != null && dataElement != null) {
                    matcher.appendReplacement(description, String.valueOf(1));
                } else {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            } else if (ProgramIndicator.KEY_ATTRIBUTE.equals(key)) {
                TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute(uid);

                if (attribute != null) {
                    matcher.appendReplacement(description, String.valueOf(1));
                } else {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            } else if (ProgramIndicator.KEY_CONSTANT.equals(key)) {
                Constant constant = MetaDataController.getConstant(uid);

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

        if (MathUtils.expressionHasErrors(description.toString())) {
            return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
        }

        return ProgramIndicator.VALID;
    }

    /**
     * Get all {@link org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement} part of the expression of the
     * given indicator.
     *
     * @param indicator the ProgramIndicator.
     * @return a set of ProgramStageDataElements.
     */
    public static Set<ProgramStageDataElement> getProgramStageDataElementsInExpression(ProgramIndicator indicator) {
        Set<ProgramStageDataElement> elements = new HashSet<>();

        Matcher matcher = ProgramIndicator.DATAELEMENT_PATTERN.matcher(indicator.getExpression());

        while (matcher.find()) {
            String ps = matcher.group(1);
            String de = matcher.group(2);

            ProgramStage programStage = MetaDataController.getProgramStage(ps);
            DataElement dataElement = MetaDataController.getDataElement(de);

            if (programStage != null && dataElement != null) {
                elements.add(programStage.getProgramStageDataElement(dataElement.getUid()));
            }
        }

        return elements;
    }

    public static List<String> getDataElementsInExpression(ProgramIndicator indicator) {
        List<String> elements = new ArrayList<>();

        Matcher matcher = ProgramIndicator.DATAELEMENT_PATTERN.matcher(indicator.getExpression());

        while (matcher.find()) {
            String ps = matcher.group(1);
            String de = matcher.group(2);
            elements.add(de);
        }

        return elements;
    }

    /**
     * Get all {@link org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute} part of the expression of the
     * given indicator.
     *
     * @param indicator the ProgramIndicator.
     * @return a set of TrackedEntityAttributes.
     */
    public static Set<TrackedEntityAttribute> getAttributesInExpression(ProgramIndicator indicator) {
        Set<TrackedEntityAttribute> attributes = new HashSet<>();

        Matcher matcher = ProgramIndicator.ATTRIBUTE_PATTERN.matcher(indicator.getExpression());

        while (matcher.find()) {
            String at = matcher.group(1);

            TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute(at);

            if (attribute != null) {
                attributes.add(attribute);
            }
        }

        return attributes;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * @param enrollmentProgramInstance can be null if event is not null in case single event without reg
     * @param event           can be null if programInstance is not null
     * @param indicator
     * @return
     */
    private static Double getValue(Enrollment enrollmentProgramInstance, Event event, ProgramIndicator indicator) {
        StringBuffer buffer = new StringBuffer();

        String expression = indicator.getExpression();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher(expression);

        int valueCount = 0;
        int zeroPosValueCount = 0;
        Event eventProgramStageInstance = null;
        Map<String, DataValue> dataElementToDataValues = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (ProgramIndicator.KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);
                String programStageUid = uid;

                if (programStageUid != null && de != null) {
                    if (enrollmentProgramInstance == null) { //in case single event without reg
                        if(eventProgramStageInstance == null) {
                            eventProgramStageInstance = event;
                            if (eventProgramStageInstance.getDataValues() != null) {
                                for (DataValue dataValue : eventProgramStageInstance.getDataValues()) {
                                    dataElementToDataValues.put(dataValue.getDataElement(), dataValue);
                                }
                            }
                        }
                    } else {
                        if (eventProgramStageInstance == null || !eventProgramStageInstance.getUid().equals(programStageUid)) {
                            eventProgramStageInstance = TrackerController.getEvent(enrollmentProgramInstance.getLocalId(), programStageUid);
                            if(eventProgramStageInstance == null){
                                continue;
                            }
                            dataElementToDataValues.clear();
                            if (eventProgramStageInstance.getDataValues() != null) {
                                for(DataValue dataValue: eventProgramStageInstance.getDataValues()) {
                                    dataElementToDataValues.put(dataValue.getDataElement(), dataValue);
                                }
                            }
                        }
                    }

                    DataValue dataValue;
                    if (eventProgramStageInstance.getDataValues() == null) {
                        continue;
                    }
                    dataValue = dataElementToDataValues.get(de);

                    String value;
                    if (dataValue == null || dataValue.getValue() == null || dataValue.getValue().isEmpty()) {
                        value = "0";
                    } else {
                        if(MetaDataController.getDataElement(dataValue.getDataElement()).getValueType()== ValueType.BOOLEAN){
                            if(dataValue.getValue().equals("true")){
                                value="1";
                            }else{
                                value="0";
                            }
                        }
                        else if(dataValue.getValue().endsWith(".")) {
                            value = (dataValue.getValue() + "0");
                        }
                        else if(!(dataValue.getValue().contains("."))) {
                            value = dataValue.getValue() + ".0";
                        }
                        else {
                            value = dataValue.getValue();

                        }

                        valueCount++;
                        zeroPosValueCount = isZeroOrPositive(value) ? (zeroPosValueCount + 1) : zeroPosValueCount;
                    }

                    matcher.appendReplacement(buffer, TextUtils.quote(value));
                } else {
                    continue;
                }
            } else if (ProgramIndicator.KEY_ATTRIBUTE.equals(key)) {
                if (enrollmentProgramInstance != null) { //in case single event without reg

                    if (uid != null) {
                        TrackedEntityAttributeValue attributeValue = TrackerController.getTrackedEntityAttributeValue(
                                uid, enrollmentProgramInstance.getLocalTrackedEntityInstanceId());
                        String value;
                        if (attributeValue == null || attributeValue.getValue() == null || attributeValue.getValue().isEmpty()) {
                            value = NULL_REPLACEMENT;
                        } else {
                            value = attributeValue.getValue();

                            valueCount++;
                            zeroPosValueCount = isZeroOrPositive(value) ? (zeroPosValueCount + 1) : zeroPosValueCount;
                        }
                        matcher.appendReplacement(buffer, TextUtils.quote(value));
                    } else {
                        continue;
                    }
                }
            } else if (ProgramIndicator.KEY_CONSTANT.equals(key)) {
                Constant constant = MetaDataController.getConstant(uid);

                if (constant != null) {
                    matcher.appendReplacement(buffer, String.valueOf(constant.getValue()));
                } else {
                    continue;
                }
            } else if (ProgramIndicator.KEY_PROGRAM_VARIABLE.equals(key)) {
                if (enrollmentProgramInstance != null) { //in case of single event without reg
                    Date currentDate = new Date();
                    Date date = null;

                    if (ProgramIndicator.ENROLLMENT_DATE.equals(uid)) {
                        date = DateUtils.parseDate(enrollmentProgramInstance.getEnrollmentDate());
                    } else if (ProgramIndicator.INCIDENT_DATE.equals(uid)) {
                        date = DateUtils.parseDate(enrollmentProgramInstance.getIncidentDate());
                    } else if (ProgramIndicator.CURRENT_DATE.equals(uid)) {
                        date = currentDate;
                    } else if (ProgramIndicator.EVENT_DATE.equals(uid)) {
                        if(enrollmentProgramInstance.getEvents().size()>0) {
                            date = DateUtils.parseDate(
                                    enrollmentProgramInstance.getEvents().get(0).getEventDate());
                        }
                    }

                    if (date != null) {
                        matcher.appendReplacement(buffer, TextUtils.quote(DateUtils.getMediumDateString(date)));
                    }
                }
            }
        }
        
        if(valueCount <= 0) {
            //returning null in case there are now values in the expression.
            return null;
        }

        expression = TextUtils.appendTail(matcher, buffer);

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

        expression = TextUtils.appendTail(matcher, buffer);
        Double value;
        try {
            value = ExpressionUtils.evaluateToDouble(expression, null);
        } catch (JexlException e) {
            e.printStackTrace();
            value = null;
        } catch (IllegalStateException e){
            e.printStackTrace();
            value = null;
        }
        return value;
    }

    private static boolean isZeroOrPositive(String value) {
        return MathUtils.isNumeric(value) && Double.valueOf(value) >= 0d;
    }
}
