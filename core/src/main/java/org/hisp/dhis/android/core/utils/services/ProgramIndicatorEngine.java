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
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.constant.ConstantModel;
import org.hisp.dhis.android.core.constant.ConstantStore;
import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chau Thu Tran
 */

@SuppressWarnings({
        "PMD.ExcessiveImports",
        "PMD.ExcessiveMethodLength",
        "PMD.NPathComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.GodClass"
})
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
    private static final String VAR_VALUE_COUNT = "value_count";
    private static final String VAR_ZERO_POS_VALUE_COUNT = "zero_pos_value_count";
    private static final String EXPRESSION_REGEXP = "(" + KEY_DATAELEMENT + "|" + KEY_ATTRIBUTE + "|" +
            KEY_PROGRAM_VARIABLE + "|" + KEY_CONSTANT +
            ")\\{(\\w+|" + INCIDENT_DATE + "|" + ENROLLMENT_DATE + "|" + CURRENT_DATE + "|" + EVENT_DATE + ")" +
            SEPARATOR_ID + "?(\\w*)\\}";
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_REGEXP);
    private static final Pattern VALUECOUNT_PATTERN =
            Pattern.compile("V\\{(" + VAR_VALUE_COUNT + "|" + VAR_ZERO_POS_VALUE_COUNT + ")\\}");

    private final IdentifiableObjectStore<ProgramIndicatorModel> programIndicatorStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final IdentifiableObjectStore<DataElement> dataElementStore;
    private final IdentifiableObjectStore<ConstantModel> constantStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    ProgramIndicatorEngine(IdentifiableObjectStore<ProgramIndicatorModel> programIndicatorStore,
                           TrackedEntityDataValueStore trackedEntityDataValueStore,
                           EnrollmentStore enrollmentStore,
                           EventStore eventStore,
                           IdentifiableObjectStore<DataElement> dataElementStore,
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
     * @param enrollment  Enrollment uid
     * @param event Event uid
     * @param programIndicatorUid ProgramIndicator
     * @return Indicator value
     */
    public String getProgramIndicatorValue(String enrollment, String event, String programIndicatorUid) {
        if(enrollment == null && event == null) {
            return null;
        }

        Double value = getValue(enrollment, event, programIndicatorUid);

        return TextUtils.fromDouble(value);
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Double getValue(String enrollment, String event, String indicatorUid) {
        String expression = parseIndicatorExpression(enrollment, event, indicatorUid);
        Double value;
        try {
            value = ExpressionUtils.evaluateToDouble(expression, null);
        } catch (JexlException e) {
            value = null;
        } catch (IllegalStateException e){
            value = null;
        }
        return value;
    }

    String parseIndicatorExpression(String enrollment, String event, String indicatorUid) {
        StringBuffer buffer = new StringBuffer();

        ProgramIndicatorModel programIndicator = this.programIndicatorStore.selectByUid(indicatorUid);
        String expression = programIndicator.expression();

        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);

        int valueCount = 0;
        int zeroPosValueCount = 0;

        Map<String, List<Event>> cachedEvents = new HashMap<>();
        EnrollmentModel cachedEnrollment = null;
        Map<String, TrackedEntityAttributeValue> attributeToAttributeValues = new HashMap<>();

        Date currentDate = new Date();

        while (matcher.find()) {
            String key = matcher.group(1);
            String uid = matcher.group(2);

            if (KEY_DATAELEMENT.equals(key)) {
                String de = matcher.group(3);
                String programStageUid = uid;

                if (programStageUid == null || de == null) {
                    continue;
                }

                if (!cachedEvents.containsKey(programStageUid)) {
                    List<Event> events = getEventsInStage(enrollment, event, programStageUid);
                    cachedEvents.put(programStageUid, events);
                }

                TrackedEntityDataValue dataValue = evaluateDataElementInStage(de, cachedEvents.get(programStageUid),
                        programIndicator.aggregationType());

                String value;
                if (dataValue == null || dataValue.value() == null || dataValue.value().isEmpty()) {
                    value = "0";
                } else {
                    value = formatDataValue(dataValue);
                    valueCount++;
                    zeroPosValueCount = isZeroOrPositive(value) ? (zeroPosValueCount + 1) : zeroPosValueCount;
                }

                matcher.appendReplacement(buffer, TextUtils.quote(value));

            } else if (KEY_ATTRIBUTE.equals(key)) {
                if (enrollment == null) {
                    continue;
                }

                if (uid != null) {
                    if (cachedEnrollment == null) {
                        cachedEnrollment = enrollmentStore.queryByUid(enrollment);
                        attributeToAttributeValues = getTrackedEntityAttributeValues(cachedEnrollment
                                .trackedEntityInstance());
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
            } else if (KEY_CONSTANT.equals(key)) {
                ConstantModel constant = constantStore.selectByUid(uid);

                if (constant != null) {
                    matcher.appendReplacement(buffer, String.valueOf(constant.value()));
                }
            } else if (KEY_PROGRAM_VARIABLE.equals(key)) {
                Date date = null;

                if (enrollment != null) { //in case of single event without reg
                    if (cachedEnrollment == null) {
                        cachedEnrollment = enrollmentStore.queryByUid(enrollment);
                    }

                    if (ENROLLMENT_DATE.equals(uid)) {
                        date = cachedEnrollment.enrollmentDate();
                    } else if (INCIDENT_DATE.equals(uid)) {
                        date = cachedEnrollment.incidentDate();
                    }
                }

                if (EVENT_DATE.equals(uid) && event != null) {
                    Event targetEvent = eventStore.queryByUid(event);
                    date = targetEvent.eventDate();
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

        expression = TextUtils.appendTail(matcher, buffer);

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

    private Map<String, TrackedEntityAttributeValue> getTrackedEntityAttributeValues(String tei) {
        Map<String, TrackedEntityAttributeValue> attributeToAttributeValues = new HashMap<>();
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                trackedEntityAttributeValueStore.queryByTrackedEntityInstance(tei);
        if(trackedEntityAttributeValues != null) {
            for (TrackedEntityAttributeValue value : trackedEntityAttributeValues) {
                attributeToAttributeValues.put(value.trackedEntityAttribute(), value);
            }
        }
        return attributeToAttributeValues;
    }

    private List<TrackedEntityDataValue> getTrackedEntityDataValues(String eventUid) {
        return trackedEntityDataValueStore.queryTrackedEntityDataValues(eventUid);
    }

    private List<Event> getEventsInStage(String enrollmentUid, String eventUid, String programStageUid) {
        List<Event> events;
        if (enrollmentUid == null) {
            events = Collections.singletonList(eventStore.queryByUid(eventUid));
        } else {
            events = eventStore.queryOrderedForEnrollmentAndProgramStage(enrollmentUid, programStageUid);
        }

        List<Event> eventsWithValues = new ArrayList<>();
        for (Event event : events) {
            eventsWithValues.add(getEventWithValues(event));
        }
        return eventsWithValues;
    }

    private Event getEventWithValues(Event e) {
        List<TrackedEntityDataValue> dataValues = getTrackedEntityDataValues(e.uid());

        return Event.create(e.uid(), e.enrollmentUid(), e.created(), e.lastUpdated(), e.createdAtClient(),
                e.lastUpdatedAtClient(), e.program(), e.programStage(), e.organisationUnit(), e.eventDate(),
                e.status(), e.coordinates(), e.completedDate(), e.dueDate(), e.deleted(), dataValues,
                e.attributeOptionCombo(), e.trackedEntityInstance());
    }

    private TrackedEntityDataValue evaluateDataElementInStage(String deId,
                                                              List<Event> events,
                                                              AggregationType aggregationType) {
        List<TrackedEntityDataValue> candidates = new ArrayList<>();
        for (Event event : events) {
            if (event.trackedEntityDataValues() != null) {
                for (TrackedEntityDataValue dataValue : event.trackedEntityDataValues()) {
                    if (deId.equals(dataValue.dataElement())) {
                        candidates.add(dataValue);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        } else if (aggregationType.equals(AggregationType.LAST) ||
                aggregationType.equals(AggregationType.LAST_AVERAGE_ORG_UNIT)) {
            return candidates.get(candidates.size() - 1);
        } else {
            return candidates.get(0);
        }
    }

    private String formatDataValue(TrackedEntityDataValue dataValue) {
        if(dataElementStore.selectByUid(dataValue.dataElement())
                .valueType() == ValueType.BOOLEAN) {
            if(dataValue.value().equals("true")) {
                return "1";
            } else {
                return "0";
            }
        } else if(dataValue.value().endsWith(".")) {
            return (dataValue.value() + "0");
        } else if(dataValue.value().contains(".")) {
            return dataValue.value();
        } else {
            return dataValue.value() + ".0";
        }
    }

    private static boolean isZeroOrPositive(String value) {
        return MathUtils.isNumeric(value) && Double.valueOf(value) >= 0d;
    }

    public static ProgramIndicatorEngine create(DatabaseAdapter databaseAdapter) {
        return new ProgramIndicatorEngine(ProgramIndicatorStore.create(databaseAdapter),
                new TrackedEntityDataValueStoreImpl(databaseAdapter),
                new EnrollmentStoreImpl(databaseAdapter),
                new EventStoreImpl(databaseAdapter),
                DataElementStore.create(databaseAdapter),
                ConstantStore.create(databaseAdapter),
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter));
    }
}
