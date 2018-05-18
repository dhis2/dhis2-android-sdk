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
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorModel;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
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

    private final IdentifiableObjectStore<ProgramIndicatorModel> programIndicatorStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final IdentifiableObjectStore<DataElementModel> dataElementStore;
    private final IdentifiableObjectStore<ConstantModel> constantStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    ProgramIndicatorEngine(IdentifiableObjectStore<ProgramIndicatorModel> programIndicatorStore,
                           TrackedEntityDataValueStore trackedEntityDataValueStore,
                           EnrollmentStore enrollmentStore,
                           EventStore eventStore,
                           IdentifiableObjectStore<DataElementModel> dataElementStore,
                           IdentifiableObjectStore<ConstantModel> constantStore,
                           TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.programIndicatorStore = programIndicatorStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
        this.dataElementStore = dataElementStore;
        this.constantStore = constantStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    /**
     * Calculate an program indicator value based on program instance and an
     * indicator defined for a TrackedEntityInstance
     *
     * @param enrollment  ProgramInstance
     * @param programIndicatorUid ProgramIndicator
     * @return Indicator value
     */
    public String getProgramIndicatorValueByEnrollment(String enrollment, String programIndicatorUid) {
        if(enrollment == null) {
            return null;
        }

        Double value = getValue(enrollment, null, programIndicatorUid);

        return TextUtils.fromDouble(value);
    }

    /**
     * Calculate an program indicator value based on a single event
     *
     * @param event            Event
     * @param programIndicatorUid ProgramIndicator
     * @return Indicator value
     */
    public String getProgramIndicatorValueByEvent(String event, String programIndicatorUid) {
        if(event == null) {
            return null;
        }

        Double value = getValue(null, event, programIndicatorUid);

        return TextUtils.fromDouble(value);
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    String parseIndicatorExpression(String enrollment, String event, String indicatorUid) {
        StringBuffer buffer = new StringBuffer();

        ProgramIndicatorModel programIndicator = this.programIndicatorStore.selectByUid(indicatorUid,
                ProgramIndicatorModel.factory);
        String expression = programIndicator.expression();

        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);

        int valueCount = 0;
        int zeroPosValueCount = 0;

        EventModel programStageInstance = null;
        Map<String, TrackedEntityDataValue> dataElementToDataValues = new HashMap<>();
        EnrollmentModel programInstance = null;
        Map<String, TrackedEntityAttributeValue> attributeToAttributeValues = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);
                String programStageUid = uid;

                if (programStageUid != null && de != null) {
                    if (enrollment == null) {
                        // Single event without registration
                        List<TrackedEntityDataValue> trackedEntityDataValues =
                                trackedEntityDataValueStore.queryTrackedEntityDataValues(event);
                        if (trackedEntityDataValues != null) {
                            for (TrackedEntityDataValue dataValue : trackedEntityDataValues) {
                                dataElementToDataValues.put(dataValue.dataElement(), dataValue);
                            }
                        }
                    } else {
                        if (programStageInstance == null ||
                                !programStageInstance.programStage().equals(programStageUid)) {
                            programStageInstance = eventStore.queryByEnrollmentAndProgramStage(enrollment,
                                    programStageUid);

                            dataElementToDataValues.clear();
                            if (programStageInstance != null) {
                                List<TrackedEntityDataValue> trackedEntityDataValues = trackedEntityDataValueStore
                                        .queryTrackedEntityDataValues(programStageInstance.uid());

                                if (trackedEntityDataValues != null) {
                                    for(TrackedEntityDataValue dataValue : trackedEntityDataValues) {
                                        dataElementToDataValues.put(dataValue.dataElement(), dataValue);
                                    }
                                }
                            }
                        }
                    }

                    TrackedEntityDataValue dataValue;
                    dataValue = dataElementToDataValues.get(de);

                    String value;
                    if (dataValue == null || dataValue.value() == null || dataValue.value().isEmpty()) {
                        value = "0";
                    } else {
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
                if (enrollment != null) {

                    if (uid != null) {
                        if (programInstance == null) {
                            programInstance = enrollmentStore.queryByUid(enrollment);
                            List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                                    trackedEntityAttributeValueStore.queryByTrackedEntityInstance(programInstance
                                            .trackedEntityInstance());
                            for (TrackedEntityAttributeValue value : trackedEntityAttributeValues) {
                                attributeToAttributeValues.put(value.trackedEntityAttribute(), value);
                            }
                        }
                        TrackedEntityAttributeValue attributeValue = attributeToAttributeValues.get(uid);
                        String value;
                        if (attributeValue == null || attributeValue.value() == null ||
                                attributeValue.value().isEmpty()) {
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
                Date currentDate = new Date();
                Date date = null;

                if (enrollment != null) { //in case of single event without reg
                    if (programInstance == null) {
                        programInstance = enrollmentStore.queryByUid(enrollment);
                    }

                    if (ENROLLMENT_DATE.equals(uid)) {
                        date = programInstance.dateOfEnrollment();
                    } else if (INCIDENT_DATE.equals(uid)) {
                        date = programInstance.dateOfIncident();
                    }
                }

                if (event != null) {
                    if (EVENT_DATE.equals(uid)) {
                        programStageInstance = eventStore.queryByUid(event);
                        date = programStageInstance.eventDate();
                    }
                }

                if (CURRENT_DATE.equals(uid)) {
                    date = currentDate;
                }

                if (date != null) {
                    valueCount++;
                    matcher.appendReplacement(buffer, TextUtils.quote(DateUtils.getMediumDateString(date)));
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

        return TextUtils.appendTail(matcher, buffer);
    }

    private Double getValue(String enrollment, String event, String indicatorUid) {
        String expression = parseIndicatorExpression(enrollment, event, indicatorUid);
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
        return new ProgramIndicatorEngine(ProgramIndicatorStore.create(databaseAdapter),
                new TrackedEntityDataValueStoreImpl(databaseAdapter),
                new EnrollmentStoreImpl(databaseAdapter),
                new EventStoreImpl(databaseAdapter),
                DataElementStore.create(databaseAdapter), ConstantStore.create(databaseAdapter),
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter));
    }
}
