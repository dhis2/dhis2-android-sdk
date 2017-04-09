package org.hisp.dhis.android.rules;

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
    private final List<RuleAttributeValue> teRuleAttributeValues;
    private final List<RuleEvent> ruleEvents;

    RuleVariableValueMapFactory(List<RuleVariable> ruleVariables,
            List<RuleAttributeValue> teRuleAttributeValues,
            List<RuleEvent> ruleEvents) {
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        this.ruleVariables = ruleVariables;
        this.teRuleAttributeValues = teRuleAttributeValues;
        this.ruleEvents = ruleEvents;
    }

    @Nonnull
    Map<String, RuleVariableValue> build(@Nonnull RuleEvent currentRuleEvent) {
        Map<String, RuleVariableValue> valueMap = new HashMap<>();

        // add environment ruleVariables
        valueMap.put(VAR_CURRENT_DATE, create(dateFormat.format(new Date()), RuleValueType.TEXT));
        valueMap.put(VAR_EVENT_DATE, create(dateFormat.format(currentRuleEvent.eventDate()), RuleValueType.TEXT));

        Map<String, RuleAttributeValue> teiValues = mapTeaToSingleValue(teRuleAttributeValues);
        Map<String, RuleDataValue> currentEventValues = mapDeToSingleValue(currentRuleEvent.dataValues());
        Map<String, List<RuleDataValue>> eventsValues = mapDeToAllValues(currentRuleEvent, ruleEvents);

        for (RuleVariable ruleVariable : ruleVariables) {
            if (ruleVariable instanceof RuleVariableAttribute) {
                valueMap.put(ruleVariable.name(), ((RuleVariableAttribute) ruleVariable).value(teiValues));
            } else if (ruleVariable instanceof RuleVariableCurrentEvent) {
                valueMap.put(ruleVariable.name(), ((RuleVariableCurrentEvent) ruleVariable).value(currentEventValues));
            } else if (ruleVariable instanceof RuleVariableNewestEvent) {
                valueMap.put(ruleVariable.name(), ((RuleVariableNewestEvent) ruleVariable).value(eventsValues));
            } else if (ruleVariable instanceof RuleVariableNewestStageEvent) {
                valueMap.put(ruleVariable.name(), ((RuleVariableNewestStageEvent) ruleVariable).value(eventsValues));
            } else if (ruleVariable instanceof RuleVariablePreviousEvent) {
                valueMap.put(ruleVariable.name(), ((RuleVariablePreviousEvent) ruleVariable).value(currentRuleEvent, eventsValues));
            } else {
                throw new IllegalArgumentException("Unsupported RuleVariable type: " + ruleVariable.getClass());
            }
        }

        // do not let outer world to alter variable value map
        return Collections.unmodifiableMap(valueMap);
    }

    /**
     * Returns a map where the key is uid of TrackedEntityAttribute
     * and value is the actual RuleAttributeValue.
     *
     * @param values List of attribute values
     * @return Map of tracked entity attributes to data values
     */
    @Nonnull
    private static Map<String, RuleAttributeValue> mapTeaToSingleValue(
            @Nonnull List<RuleAttributeValue> values) {
        Map<String, RuleAttributeValue> valueMap = new HashMap<>(values.size());
        for (RuleAttributeValue ruleAttributeValue : values) {
            valueMap.put(ruleAttributeValue.trackedEntityAttribute(), ruleAttributeValue);
        }
        return valueMap;
    }

    /**
     * Returns a map where the key is uid of DataElement
     * and value is the actual RuleDataValue.
     *
     * @param ruleDataValues List of data values
     * @return Map of data elements to data values
     */
    @Nonnull
    private static Map<String, RuleDataValue> mapDeToSingleValue(
            @Nonnull List<RuleDataValue> ruleDataValues) {
        Map<String, RuleDataValue> valueMap = new HashMap<>(ruleDataValues.size());
        for (RuleDataValue ruleDataValue : ruleDataValues) {
            valueMap.put(ruleDataValue.dataElement(), ruleDataValue);
        }
        return valueMap;
    }

    /**
     * Returns a map where the key is uid of DataElement
     * and value is the list of data values from given ruleEvents
     *
     * @param currentRuleEvent Current event
     * @param allRuleEvents    List of ruleEvents
     * @return Map of data elements to list of all data values
     */
    @Nonnull
    private static Map<String, List<RuleDataValue>> mapDeToAllValues(
            @Nonnull RuleEvent currentRuleEvent, @Nonnull List<RuleEvent> allRuleEvents) {

        // avoid list resizing
        List<RuleEvent> ruleEvents = new ArrayList<>(allRuleEvents.size() + 1);
        ruleEvents.addAll(allRuleEvents);

        // add current event to list by hand,
        // in order not to lose values
        ruleEvents.add(currentRuleEvent);

        // using current event to predict size of the map based on amount of data values.
        // this helps to improve performance, since map doesn't have to resize during iteration.
        // Since keys in the map are data elements, we can approximately
        // set the size of the map based on data values in event
        Map<String, List<RuleDataValue>> valueMap
                = new HashMap<>(currentRuleEvent.dataValues().size());

        // sort ruleEvents by event date:
        Collections.sort(ruleEvents, RuleEvent.EVENT_DATE_COMPARATOR);

        // build the value map
        for (RuleEvent ruleEvent : ruleEvents) {
            for (RuleDataValue ruleDataValue : ruleEvent.dataValues()) {
                // push new list if it is not there
                // for the given data element
                if (!valueMap.containsKey(ruleDataValue.dataElement())) {
                    valueMap.put(ruleDataValue.dataElement(),
                            new ArrayList<RuleDataValue>(ruleEvents.size()));
                }

                // append data value to the list
                valueMap.get(ruleDataValue.dataElement()).add(ruleDataValue);
            }
        }

        return valueMap;
    }
}
