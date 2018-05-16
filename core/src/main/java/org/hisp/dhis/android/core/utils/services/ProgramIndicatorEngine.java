/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.utils.services;


import org.apache.commons.jexl2.JexlException;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.IdentifiableObjectStoreImpl;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.constant.ConstantModel;
import org.hisp.dhis.android.core.constant.ConstantStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.utils.support.DateUtils;
import org.hisp.dhis.android.core.utils.support.ExpressionUtils;
import org.hisp.dhis.android.core.utils.support.MathUtils;
import org.hisp.dhis.android.core.utils.support.TextUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chau Thu Tran
 */


public class ProgramIndicatorEngine {
    public static final String CLASS_TAG = ProgramIndicatorEngine.class.getSimpleName();
    private static final String NULL_REPLACEMENT = "null";

    private static final String SEPARATOR_ID = "\\.";
    private static final String KEY_DATAELEMENT = "#";
    private static final String KEY_ATTRIBUTE = "A";
    private static final String KEY_PROGRAM_VARIABLE = "V";
    private static final String KEY_CONSTANT = "C";
    private static final String INCIDENT_DATE = "incident_date";
    private static final String ENROLLMENT_DATE = "enrollment_date";
    private static final String EVENT_DATE = "event_date";
    private static final String CURRENT_DATE = "current_date";
    private static final String VALUE_COUNT = "value_count";
    private static final String VAR_VALUE_COUNT = "value_count";
    private static final String VAR_ZERO_POS_VALUE_COUNT = "zero_pos_value_count";
    private static final String VALUE_TYPE_DATE = "date";
    private static final String VALUE_TYPE_INT = "int";
    private static final String EXPRESSION_REGEXP = "(" + KEY_DATAELEMENT + "|" + KEY_ATTRIBUTE + "|" + KEY_PROGRAM_VARIABLE + "|" + KEY_CONSTANT + ")\\{(\\w+|" +
            INCIDENT_DATE + "|" + ENROLLMENT_DATE + "|" + CURRENT_DATE + "|" + EVENT_DATE + ")" + SEPARATOR_ID + "?(\\w*)\\}";
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_REGEXP);
    private static final Pattern DATAELEMENT_PATTERN = Pattern.compile(KEY_DATAELEMENT + "\\{(\\w{11})" + SEPARATOR_ID + "(\\w{11})\\}");
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(KEY_ATTRIBUTE + "\\{(\\w{11})\\}");
    private static final Pattern VALUECOUNT_PATTERN = Pattern.compile("V\\{(" + VAR_VALUE_COUNT + "|" + VAR_ZERO_POS_VALUE_COUNT + ")\\}");
    private static final String VALID = "valid";
    private static final String EXPRESSION_NOT_WELL_FORMED = "expression_not_well_formed";
    private static final String SEP_OBJECT = ":";

    private final IdentifiableObjectStore<DataElementModel> dataElementStore;
    private final IdentifiableObjectStore<ConstantModel> constantStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    ProgramIndicatorEngine(IdentifiableObjectStore<DataElementModel> dataElementStore,
                           IdentifiableObjectStore<ConstantModel> constantStore,
                           TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.dataElementStore = dataElementStore;
        this.constantStore = constantStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    /**
     * Calculate an program indicator value based on program instance and an
     * indicator defined for a TrackedEntityInstance
     *
     * @param programInstance  ProgramInstance
     * @param programIndicator ProgramIndicator
     * @return Indicator value
     */
    public String getProgramIndicatorValue(Enrollment programInstance, ProgramIndicator programIndicator) {
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
    public String getProgramIndicatorValue(Event event, ProgramIndicator programIndicator) {
        if(programIndicator == null) {
            return null;
        }

        Double value = getValue(null, event, programIndicator);

        return TextUtils.fromDouble(value);
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * @param enrollment can be null if event is not null in case single event without reg
     * @param event           can be null if programInstance is not null
     * @param indicator
     * @return
     */
    private Double getValue(Enrollment enrollment, Event event, ProgramIndicator indicator) {
        StringBuffer buffer = new StringBuffer();

        String expression = indicator.expression();

        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);

        int valueCount = 0;
        int zeroPosValueCount = 0;
        Event eventProgramStageInstance = null;
        Map<String, TrackedEntityDataValue> dataElementToDataValues = new HashMap<>();
        Enrollment programInstance = null;
        Map<String, TrackedEntityAttributeValue> attributeToAttributeValues = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);
                String programStageUid = uid;

                if (programStageUid != null && de != null) {
                    // If event is passed. Data values are cached
                    if (enrollment == null) { //in case single event without reg
                        if(eventProgramStageInstance == null) {
                            eventProgramStageInstance = event;
                            if (eventProgramStageInstance.trackedEntityDataValues() != null) {
                                for (TrackedEntityDataValue dataValue : eventProgramStageInstance
                                        .trackedEntityDataValues()) {
                                    dataElementToDataValues.put(dataValue.dataElement(), dataValue);
                                }
                            }
                        }
                    } else {
                        /*
                        if (eventProgramStageInstance == null || !eventProgramStageInstance.uid().equals
                                (programStageUid)) {
                            eventProgramStageInstance = TrackerController.getEvent(enrollmentProgramInstance
                                    .getLocalId(), programStageUid);
                            if(eventProgramStageInstance == null){
                                continue;
                            }
                            dataElementToDataValues.clear();
                            if (eventProgramStageInstance.trackedEntityDataValues() != null) {
                                for(TrackedEntityDataValue dataValue : eventProgramStageInstance
                                        .trackedEntityDataValues()) {
                                    dataElementToDataValues.put(dataValue.dataElement(), dataValue);
                                }
                            }
                        }
                        */
                    }

                    TrackedEntityDataValue dataValue;
                    if (eventProgramStageInstance.trackedEntityDataValues() == null) {
                        continue;
                    }
                    dataValue = dataElementToDataValues.get(de);

                    String value;
                    if (dataValue == null || dataValue.value() == null || dataValue.value().isEmpty()) {
                        value = "0";
                    } else {
                        String v = dataValue.dataElement();
                        if(dataElementStore.selectByUid(dataValue.dataElement(), DataElementModel.factory)
                                .valueType() == ValueType.BOOLEAN){
                            if(dataValue.value().equals("true")){
                                value="1";
                            }else{
                                value="0";
                            }
                        }
                        else if(dataValue.value().endsWith(".")) {
                            value = (dataValue.value() + "0");
                        }
                        else if(!(dataValue.value().contains("."))) {
                            value = dataValue.value() + ".0";
                        }
                        else {
                            value = dataValue.value();
                        }

                        valueCount++;
                        zeroPosValueCount = isZeroOrPositive(value) ? (zeroPosValueCount + 1) : zeroPosValueCount;
                    }

                    matcher.appendReplacement(buffer, TextUtils.quote(value));
                }
            } else if (KEY_ATTRIBUTE.equals(key)) {
                if (enrollment != null) { //in case single event without reg

                    if (uid != null) {
                        if (programInstance == null) {
                            programInstance = enrollment;
                            List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                                    trackedEntityAttributeValueStore.queryByTrackedEntityInstance(enrollment
                                            .trackedEntityInstance());
                            for (TrackedEntityAttributeValue value : trackedEntityAttributeValues) {
                                attributeToAttributeValues.put(value.trackedEntityAttribute(), value);
                            }
                        }
                        TrackedEntityAttributeValue attributeValue = attributeToAttributeValues.get(uid);
                        String value;
                        if (attributeValue == null || attributeValue.value() == null || attributeValue.value().isEmpty()) {
                            value = NULL_REPLACEMENT;
                        } else {
                            value = attributeValue.value();

                            valueCount++;
                            zeroPosValueCount = isZeroOrPositive(value) ? (zeroPosValueCount + 1) : zeroPosValueCount;
                        }
                        matcher.appendReplacement(buffer, TextUtils.quote(value));
                    }
                }
            } else if (KEY_CONSTANT.equals(key)) {
                ConstantModel constant = constantStore.selectByUid(uid, ConstantModel.factory);

                if (constant != null) {
                    matcher.appendReplacement(buffer, String.valueOf(constant.value()));
                }
            } else if (KEY_PROGRAM_VARIABLE.equals(key)) {
                if (enrollment != null) { //in case of single event without reg
                    Date currentDate = new Date();
                    Date date = null;

                    if (ENROLLMENT_DATE.equals(uid)) {
                        date = enrollment.dateOfEnrollment();
                    } else if (INCIDENT_DATE.equals(uid)) {
                        date = enrollment.dateOfIncident();
                    } else if (CURRENT_DATE.equals(uid)) {
                        date = currentDate;
                    } else if (EVENT_DATE.equals(uid)) {
                        if(enrollment.events().size()>0) {
                            date = enrollment.events().get(0).eventDate();
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
        matcher = VALUECOUNT_PATTERN.matcher(expression);

        while (matcher.find()) {
            String var = matcher.group(1);

            if (VAR_VALUE_COUNT.equals(var)) {
                matcher.appendReplacement(buffer, String.valueOf(valueCount));
            } else if (VAR_ZERO_POS_VALUE_COUNT.equals(var)) {
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

    public static ProgramIndicatorEngine create(DatabaseAdapter databaseAdapter) {
        return new ProgramIndicatorEngine(DataElementStore.create(databaseAdapter),
                ConstantStore.create(databaseAdapter), new TrackedEntityAttributeValueStoreImpl(databaseAdapter));
    }
}
