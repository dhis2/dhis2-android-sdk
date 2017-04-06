package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Event;
import org.hisp.dhis.android.rules.models.ProgramRuleVariable;
import org.hisp.dhis.android.rules.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.rules.models.TrackedEntityDataValue;
import org.hisp.dhis.android.rules.models.ValueType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import static org.hisp.dhis.android.rules.ProgramRuleVariableValue.create;

final class ValueMapFactory {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    // environment variables
    private static final String VAR_CURRENT_DATE = "current_date";
    private static final String VAR_EVENT_DATE = "event_date";

    // format of the date
    private final SimpleDateFormat dateFormat;

    // context for execution
    private final List<ProgramRuleVariable> programRuleVariables;
    private final List<TrackedEntityAttributeValue> teAttributeValues;
    private final List<Event> events;

    ValueMapFactory(List<ProgramRuleVariable> variables,
            List<TrackedEntityAttributeValue> teAttributeValues,
            List<Event> events) {
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        this.programRuleVariables = variables;
        this.teAttributeValues = teAttributeValues;
        this.events = events;
    }

    @Nonnull
    Map<String, ProgramRuleVariableValue> build(@Nonnull Event currentEvent) {
        Map<String, ProgramRuleVariableValue> valueMap = new HashMap<>();

        // add environment variables
        valueMap.put(VAR_CURRENT_DATE, create(dateFormat.format(
                new Date()), ValueType.DATE, true));
        valueMap.put(VAR_EVENT_DATE, create(dateFormat.format(
                currentEvent.eventDate()), ValueType.DATE, true));

        Map<String, TrackedEntityDataValue> currentEventValues
                = mapDeToSingleValue(currentEvent.dataValues());
        Map<String, TrackedEntityAttributeValue> teiValues
                = mapTeaToSingleValue(teAttributeValues);
        Map<String, List<TrackedEntityDataValue>> eventsValues
                = mapDeToAllValues(events);

        // iterate over variables and collect values
        for (ProgramRuleVariable programRuleVariable : programRuleVariables) {
            ProgramRuleVariableValue programRuleVariableValue
                    = match(programRuleVariable, teiValues, currentEventValues, eventsValues);
            valueMap.put(programRuleVariable.name(), programRuleVariableValue);
        }

        // do not let outer world to alter variable value map
        return Collections.unmodifiableMap(valueMap);
    }

    @Nonnull
    private static ProgramRuleVariableValue match(@Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, TrackedEntityAttributeValue> attributeValues,
            @Nonnull Map<String, TrackedEntityDataValue> currentEventValues,
            @Nonnull Map<String, List<TrackedEntityDataValue>> eventsValues) {
        switch (variable.sourceType()) {
            case DATAELEMENT_CURRENT_EVENT:
                return dataElementCurrentEvent(variable, currentEventValues);
            case DATAELEMENT_NEWEST_EVENT_PROGRAM:
                return dataElementNewestEvent(variable, eventsValues);
            case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE:
                return null;
            case DATAELEMENT_PREVIOUS_EVENT:
                return null;
            case TEI_ATTRIBUTE:
                return trackedEntityAttribute(variable, attributeValues);
            case CALCULATED_VALUE:
                // Do nothing - ASSIGN actions will populate these values
                break;
            default: {
                throw new IllegalArgumentException(String.format(Locale.US,
                        "sourceType %s is not supported", variable.sourceType()));
            }
        }

        return null;
    }

    @Nonnull
    private static ProgramRuleVariableValue dataElementCurrentEvent(
            @Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, TrackedEntityDataValue> valueMap) {
        assert variable.dataElement() != null : variable.dataElementValueType() != null;

        if (valueMap.containsKey(variable.dataElement())) {
            TrackedEntityDataValue dataValue = valueMap.get(variable.dataElement());
            return create(dataValue.value(),
                    variable.dataElementValueType(), true);
        }

        return create(variable.dataElementValueType());
    }

    // ToDo: tests
    @Nonnull
    private static ProgramRuleVariableValue dataElementNewestEvent(
            @Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, List<TrackedEntityDataValue>> valueMap) {
        assert variable.dataElement() != null : variable.dataElementValueType() != null;

        if (valueMap.containsKey(variable.dataElement())) {
            List<TrackedEntityDataValue> dataValues = valueMap.get(variable.dataElement());

            if (!dataValues.isEmpty()) {
                // this data value corresponds to the data value within
                // latest / newest event for given data element
                TrackedEntityDataValue latestDataValue = dataValues.get(dataValues.size() - 1);
                return create(latestDataValue.value(),
                        variable.dataElementValueType(), true);
            }
        }

        return create(variable.dataElementValueType());
    }

    // ToDo: tests
    @Nonnull
    private static ProgramRuleVariableValue trackedEntityAttribute(
            @Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, TrackedEntityAttributeValue> valueMap) {
        assert variable.trackedEntityAttribute() != null
                : variable.trackedEntityAttributeType() != null;

        if (valueMap.containsKey(variable.trackedEntityAttribute())) {
            TrackedEntityAttributeValue attributeValue
                    = valueMap.get(variable.trackedEntityAttribute());
            return create(attributeValue.value(),
                    variable.trackedEntityAttributeType(), true);
        }

        return create(variable.trackedEntityAttributeType());
    }

    /**
     * Returns a map where the key is uid of TrackedEntityAttribute
     * and value is the actual TrackedEntityAttributeValue.
     *
     * @param values List of attribute values
     * @return Map of tracked entity attributes to data values
     */
    @Nonnull
    private static Map<String, TrackedEntityAttributeValue> mapTeaToSingleValue(
            @Nonnull List<TrackedEntityAttributeValue> values) {
        Map<String, TrackedEntityAttributeValue> valueMap = new HashMap<>(values.size());
        for (TrackedEntityAttributeValue attributeValue : values) {
            valueMap.put(attributeValue.trackedEntityAttribute(), attributeValue);
        }
        return valueMap;
    }

    /**
     * Returns a map where the key is uid of DataElement
     * and value is the actual TrackedEntityDataValue.
     *
     * @param dataValues List of data values
     * @return Map of data elements to data values
     */
    @Nonnull
    private static Map<String, TrackedEntityDataValue> mapDeToSingleValue(
            @Nonnull List<TrackedEntityDataValue> dataValues) {
        Map<String, TrackedEntityDataValue> valueMap = new HashMap<>(dataValues.size());
        for (TrackedEntityDataValue dataValue : dataValues) {
            valueMap.put(dataValue.dataElement(), dataValue);
        }
        return valueMap;
    }

    /**
     * Returns a map where the key is uid of DataElement
     * and value is the list of data values from given events
     *
     * @param events List of events
     * @return Map of data elements to list of all data values
     */
    @Nonnull
    private static Map<String, List<TrackedEntityDataValue>> mapDeToAllValues(
            @Nonnull List<Event> events) {
        if (events.isEmpty()) {
            return new HashMap<>();
        }

        // using one event to predict size of the map based on amount of data values.
        // this helps to improve performance, since map doesn't have to resize
        // during iteration.
        Event firstEvent = events.get(0);

        // since keys in the map are data elements, we can approximately set the size of
        // the map based on data values in event
        Map<String, List<TrackedEntityDataValue>> valueMap
                = new HashMap<>(firstEvent.dataValues().size());

        // sort events by event date:
        Collections.sort(events, Event.EVENT_DATE_COMPARATOR);

        // build the value map
        for (Event event : events) {
            for (TrackedEntityDataValue dataValue : event.dataValues()) {
                // push new list if it is not there
                // for the given data element
                if (!valueMap.containsKey(dataValue.dataElement())) {
                    valueMap.put(dataValue.dataElement(),
                            new ArrayList<TrackedEntityDataValue>(events.size()));
                }

                // append data value to the list
                valueMap.get(dataValue.dataElement()).add(dataValue);
            }
        }

        return new HashMap<>();
    }
}
