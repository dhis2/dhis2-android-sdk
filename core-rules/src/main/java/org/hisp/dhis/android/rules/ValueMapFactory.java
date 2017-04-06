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
                = mapDeToAllValues(currentEvent, events);

        // iterate over variables and collect values
        for (ProgramRuleVariable variable : programRuleVariables) {
            switch (variable.sourceType()) {
                case DATAELEMENT_CURRENT_EVENT: {
                    valueMap.put(variable.name(),
                            dataElementCurrentEvent(variable, currentEventValues));
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM: {
                    valueMap.put(variable.name(),
                            dataElementNewestEventProgram(variable, eventsValues));
                    break;
                }
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE: {
                    valueMap.put(variable.name(),
                            dataElementNewestEventProgramStage(variable, eventsValues));
                    break;
                }
                case DATAELEMENT_PREVIOUS_EVENT: {
                    break;
                }
                case TEI_ATTRIBUTE: {
                    valueMap.put(variable.name(), trackedEntityAttribute(variable, teiValues));
                    break;
                }
                case CALCULATED_VALUE: {
                    // Do nothing - ASSIGN actions will populate these values
                    break;
                }
                default: {
                    throw new IllegalArgumentException(String.format(Locale.US,
                            "sourceType %s is not supported", variable.sourceType()));
                }
            }
        }

        // do not let outer world to alter variable value map
        return Collections.unmodifiableMap(valueMap);
    }

    @Nonnull
    private static ProgramRuleVariableValue dataElementCurrentEvent(
            @Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, TrackedEntityDataValue> valueMap) {
        if (valueMap.containsKey(variable.dataElement())) {
            TrackedEntityDataValue dataValue = valueMap.get(variable.dataElement());
            return create(dataValue.value(), variable.dataElementValueType(), true);
        }

        return create(variable.dataElementValueType());
    }

    // Note: we assume that we are operating in the scope of one program. Hence, we don't check
    // if event actually belongs to the given program. This is something what can be improved or
    // communicated through documentation.
    @Nonnull
    private static ProgramRuleVariableValue dataElementNewestEventProgram(
            @Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, List<TrackedEntityDataValue>> valueMap) {
        if (valueMap.containsKey(variable.dataElement())) {
            List<TrackedEntityDataValue> dataValues = valueMap.get(variable.dataElement());

            if (!dataValues.isEmpty()) {
                // this data value corresponds to the data value within
                // latest / newest event for given data element
                TrackedEntityDataValue latestDataValue = dataValues.get(0);
                return create(latestDataValue.value(), transformDataValues(dataValues),
                        variable.dataElementValueType(), true);
            }
        }

        return create(variable.dataElementValueType());
    }

    @Nonnull
    private static ProgramRuleVariableValue dataElementNewestEventProgramStage(
            @Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, List<TrackedEntityDataValue>> valueMap) {
        if (valueMap.containsKey(variable.dataElement())) {
            List<TrackedEntityDataValue> dataValues = valueMap.get(variable.dataElement());

            for (TrackedEntityDataValue dataValue : dataValues) {
                // found best candidate
                if (dataValue.programStage().equals(variable.programStage())) {
                    return create(dataValue.value(), transformDataValues(dataValues),
                            variable.dataElementValueType(), true);
                }
            }
        }

        return create(variable.dataElementValueType());
    }

    // ToDo: tests
    @Nonnull
    private static ProgramRuleVariableValue trackedEntityAttribute(
            @Nonnull ProgramRuleVariable variable,
            @Nonnull Map<String, TrackedEntityAttributeValue> valueMap) {
        if (valueMap.containsKey(variable.trackedEntityAttribute())) {
            TrackedEntityAttributeValue attributeValue
                    = valueMap.get(variable.trackedEntityAttribute());
            return create(attributeValue.value(), variable.trackedEntityAttributeType(), true);
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
     * @param currentEvent Current event
     * @param allEvents    List of events
     * @return Map of data elements to list of all data values
     */
    @Nonnull
    private static Map<String, List<TrackedEntityDataValue>> mapDeToAllValues(
            @Nonnull Event currentEvent, @Nonnull List<Event> allEvents) {

        // avoid list resizing
        List<Event> events = new ArrayList<>(allEvents.size() + 1);
        events.addAll(allEvents);

        // add current event to list by hand,
        // in order not to lose values
        events.add(currentEvent);

        // using current event to predict size of the map based on amount of data values.
        // this helps to improve performance, since map doesn't have to resize during iteration.
        // Since keys in the map are data elements, we can approximately
        // set the size of the map based on data values in event
        Map<String, List<TrackedEntityDataValue>> valueMap
                = new HashMap<>(currentEvent.dataValues().size());

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

        return valueMap;
    }

    @Nonnull
    private static List<String> transformDataValues(@Nonnull List<TrackedEntityDataValue> dataValues) {
        List<String> values = new ArrayList<>(dataValues.size());
        for (TrackedEntityDataValue dataValue : dataValues) {
            values.add(dataValue.value());
        }
        return values;
    }
}
