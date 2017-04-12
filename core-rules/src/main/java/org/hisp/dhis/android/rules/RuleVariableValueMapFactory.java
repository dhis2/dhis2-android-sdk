package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleAttributeValue;
import org.hisp.dhis.android.rules.models.RuleDataValue;
import org.hisp.dhis.android.rules.models.RuleEvent;
import org.hisp.dhis.android.rules.models.RuleValueType;
import org.hisp.dhis.android.rules.models.RuleVariable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import static org.hisp.dhis.android.rules.RuleVariableValue.create;

final class RuleVariableValueMapFactory {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    // environment ruleVariables
    private static final String VAR_CURRENT_DATE = "current_date";
    private static final String VAR_EVENT_DATE = "event_date";

    // format of the date
    private final SimpleDateFormat dateFormat;

    // context for execution
    private final List<RuleVariable> ruleVariables;
    private final List<RuleAttributeValue> ruleAttributeValues;
    private final List<RuleEvent> ruleEvents;

    RuleVariableValueMapFactory(List<RuleVariable> ruleVariables,
            List<RuleAttributeValue> ruleAttributeValues,
            List<RuleEvent> ruleEvents) {
        this.ruleVariables = ruleVariables;
        this.ruleEvents = ruleEvents;
        this.ruleAttributeValues = ruleAttributeValues;
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
    }

    @Nonnull
    Map<String, RuleVariableValue> build(@Nonnull RuleEvent currentEvent) {
        Map<String, RuleVariableValue> valueMap = new HashMap<>();

        // add environment ruleVariables
        valueMap.put(VAR_CURRENT_DATE, create(dateFormat.format(new Date()), RuleValueType.TEXT));
        valueMap.put(VAR_EVENT_DATE, create(dateFormat.format(currentEvent.eventDate()), RuleValueType.TEXT));

        Map<String, RuleDataValue> currentEventDataValues = mapDeToSingleValue(currentEvent);
        Map<String, RuleAttributeValue> trackedEntityAttributeValues = mapTeaToSingleValue();
        Map<String, List<RuleDataValue>> eventsValues = mapDeToAllValues(currentEvent);

        // ToDo: can assign rule set values in another event or enrollment?

//        for (RuleVariable ruleVariable : ruleVariables) {
//            if (ruleVariable instanceof RuleVariableAttribute) {
//
//                valueMap.put(ruleVariable.name(), ((RuleVariableAttribute)
//                        ruleVariable).value(trackedEntityAttributeValues));
//            } else if (ruleVariable instanceof RuleVariableCurrentEvent) {
//                valueMap.put(ruleVariable.name(), ((RuleVariableCurrentEvent)
//                        ruleVariable).value(currentEventDataValues));
//            } else if (ruleVariable instanceof RuleVariableNewestEvent) {
//                valueMap.put(ruleVariable.name(), ((RuleVariableNewestEvent)
//                        ruleVariable).value(eventsValues));
//            } else if (ruleVariable instanceof RuleVariableNewestStageEvent) {
//                valueMap.put(ruleVariable.name(), ((RuleVariableNewestStageEvent)
//                        ruleVariable).value(eventsValues));
//            } else if (ruleVariable instanceof RuleVariablePreviousEvent) {
//                valueMap.put(ruleVariable.name(), ((RuleVariablePreviousEvent)
//                        ruleVariable).value(currentEvent, eventsValues));
//            } else {
//                throw new IllegalArgumentException("Unsupported RuleVariable type: " + ruleVariable.getClass());
//            }
//        }

        // do not let outer world to alter variable value map
        return Collections.unmodifiableMap(valueMap);
    }

    /**
     * Returns a map where the key is uid of TrackedEntityAttribute
     * and value is the RuleAttributeValue.
     *
     * @return Map of tracked entity attributes to data values
     */
    @Nonnull
    private Map<String, RuleAttributeValue> mapTeaToSingleValue() {
        Map<String, RuleAttributeValue> valueMap = new HashMap<>(ruleAttributeValues.size());
        for (RuleAttributeValue ruleAttributeValue : ruleAttributeValues) {
            valueMap.put(ruleAttributeValue.trackedEntityAttribute(), ruleAttributeValue);
        }
        return valueMap;
    }

    /**
     * Returns a map where the key is uid of DataElement
     * and value is the actual RuleDataValue.
     *
     * @return Map of data elements to data values
     */
    @Nonnull
    private Map<String, RuleDataValue> mapDeToSingleValue(@Nonnull RuleEvent event) {
        Map<String, RuleDataValue> valueMap = new HashMap<>(event.dataValues().size());
        for (RuleDataValue ruleDataValue : event.dataValues()) {
            valueMap.put(ruleDataValue.dataElement(), ruleDataValue);
        }
        return valueMap;
    }

    /**
     * Returns a map where the key is uid of DataElement
     * and value is the list of data values from given ruleEvents
     *
     * @param event Current event
     * @return Map of data elements to list of all data values
     */
    @Nonnull
    private Map<String, List<RuleDataValue>> mapDeToAllValues(@Nonnull RuleEvent event) {
        // try to avoid resizing list
        List<RuleEvent> events = new ArrayList<>(ruleEvents.size() + 1);
        events.addAll(ruleEvents);

        // add current event to list by hand,
        // in order not to lose values
        events.add(event);

        // using current event to predict size of the map based on amount of data values.
        // this helps to improve performance, since map doesn't have to resize during iteration.
        // Since keys in the map are data elements, we can approximately
        // set the size of the map based on data values in event
        Map<String, List<RuleDataValue>> valueMap
                = new HashMap<>(event.dataValues().size());

        // sort ruleEvents by event date:
        Collections.sort(events, RuleEvent.EVENT_DATE_COMPARATOR);

        // build the value map
        for (RuleEvent ruleEvent : events) {
            for (RuleDataValue ruleDataValue : ruleEvent.dataValues()) {
                // push new list if it is not there
                // for the given data element
                if (!valueMap.containsKey(ruleDataValue.dataElement())) {
                    valueMap.put(ruleDataValue.dataElement(),
                            new ArrayList<RuleDataValue>(events.size()));
                }

                // append data value to the list
                valueMap.get(ruleDataValue.dataElement()).add(ruleDataValue);
            }
        }

        return valueMap;
    }
}
