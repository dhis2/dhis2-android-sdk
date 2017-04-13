package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.RuleAttributeValue;
import org.hisp.dhis.android.rules.models.RuleDataValue;
import org.hisp.dhis.android.rules.models.RuleEvent;
import org.hisp.dhis.android.rules.models.RuleValueType;
import org.hisp.dhis.android.rules.models.RuleVariable;
import org.hisp.dhis.android.rules.models.RuleVariableAttribute;
import org.hisp.dhis.android.rules.models.RuleVariableCurrentEvent;
import org.hisp.dhis.android.rules.models.RuleVariableNewestEvent;
import org.hisp.dhis.android.rules.models.RuleVariableNewestStageEvent;
import org.hisp.dhis.android.rules.models.RuleVariablePreviousEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

        // set environment variables which can be used by rules
        addEnvironmentVariables(valueMap, currentEvent);

        // flatten out all values from events and enrollments into maps
        Map<String, RuleDataValue> currentDataValues
                = dataElementsToTrackedEntityDataValue(currentEvent);
        Map<String, List<RuleDataValue>> allDataValues
                = dataElementsToTrackedEntityDataValues(currentEvent);
        Map<String, RuleAttributeValue> attributeValues
                = attributesToTrackedEntityAttributeValues(ruleAttributeValues);

        // split values into variables
        for (RuleVariable ruleVariable : ruleVariables) {
            if (ruleVariable instanceof RuleVariableAttribute) {
                RuleVariableAttribute ruleVariableAttribute
                        = (RuleVariableAttribute) ruleVariable;
                valueMap.put(ruleVariable.name(), createAttributeVariableValue(
                        attributeValues, ruleVariableAttribute));
            } else if (ruleVariable instanceof RuleVariableCurrentEvent) {
                RuleVariableCurrentEvent currentEventVariable
                        = (RuleVariableCurrentEvent) ruleVariable;
                valueMap.put(currentEventVariable.name(), createCurrentEventVariableValue(
                        currentDataValues, currentEventVariable));
            } else if (ruleVariable instanceof RuleVariableNewestEvent) {
                RuleVariableNewestEvent ruleVariableNewestEvent
                        = (RuleVariableNewestEvent) ruleVariable;
                valueMap.put(ruleVariableNewestEvent.name(), createNewestEventVariableValue(
                        allDataValues, ruleVariableNewestEvent));
            } else if (ruleVariable instanceof RuleVariableNewestStageEvent) {
                RuleVariableNewestStageEvent ruleVariableNewestEvent
                        = (RuleVariableNewestStageEvent) ruleVariable;
                valueMap.put(ruleVariableNewestEvent.name(), createNewestStageEventVariableValue(
                        allDataValues, ruleVariableNewestEvent));
            } else if (ruleVariable instanceof RuleVariablePreviousEvent) {
                RuleVariablePreviousEvent ruleVariablePreviousEvent
                        = (RuleVariablePreviousEvent) ruleVariable;
                valueMap.put(ruleVariable.name(), createPreviousEventVariableValue(currentEvent,
                        allDataValues, ruleVariablePreviousEvent));
            } else {
                throw new IllegalArgumentException("Unsupported RuleVariable type: " +
                        ruleVariable.getClass());
            }
        }

        // do not let outer world to alter variable value map
        return Collections.unmodifiableMap(valueMap);
    }

    private void addEnvironmentVariables(
            @Nonnull Map<String, RuleVariableValue> valueMap,
            @Nonnull RuleEvent currentEvent) {
        valueMap.put(VAR_CURRENT_DATE, create(dateFormat.format(
                new Date()), RuleValueType.TEXT, new ArrayList<String>()));
        valueMap.put(VAR_EVENT_DATE, create(dateFormat.format(
                currentEvent.eventDate()), RuleValueType.TEXT, new ArrayList<String>()));
    }


    @Nonnull
    private RuleVariableValue createAttributeVariableValue(
            @Nonnull Map<String, RuleAttributeValue> valueMap,
            @Nonnull RuleVariableAttribute variable) {

        if (valueMap.containsKey(variable.trackedEntityAttribute())) {
            RuleAttributeValue value = valueMap.get(variable.trackedEntityAttribute());
            return create(value.value(), variable.trackedEntityAttributeType(),
                    Arrays.asList(value.value()));
        }

        return RuleVariableValue.create(variable.trackedEntityAttributeType());
    }

    @Nonnull
    private RuleVariableValue createCurrentEventVariableValue(
            @Nonnull Map<String, RuleDataValue> valueMap,
            @Nonnull RuleVariableCurrentEvent variable) {

        if (valueMap.containsKey(variable.dataElement())) {
            RuleDataValue value = valueMap.get(variable.dataElement());
            return create(value.value(), variable.dataElementType(), Arrays.asList(value.value()));
        }

        return create(variable.dataElementType());
    }

    @Nonnull
    private RuleVariableValue createNewestEventVariableValue(
            @Nonnull Map<String, List<RuleDataValue>> valueMap,
            @Nonnull RuleVariableNewestEvent variable) {

        List<RuleDataValue> ruleDataValues = valueMap.get(variable.dataElement());
        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
            return create(ruleDataValues.get(0).value(),
                    variable.dataElementType(), Utils.values(ruleDataValues));
        }

        return create(variable.dataElementType());
    }

    @Nonnull
    private RuleVariableValue createNewestStageEventVariableValue(
            @Nonnull Map<String, List<RuleDataValue>> valueMap,
            @Nonnull RuleVariableNewestStageEvent variable) {

        List<RuleDataValue> stageRuleDataValues = new ArrayList<>();
        List<RuleDataValue> sourceRuleDataValues = valueMap.get(variable.dataElement());
        if (sourceRuleDataValues != null && !sourceRuleDataValues.isEmpty()) {
            // filter data values based on program stage
            for (int i = 0; i < sourceRuleDataValues.size(); i++) {
                RuleDataValue ruleDataValue = sourceRuleDataValues.get(i);
                if (variable.programStage().equals(ruleDataValue.programStage())) {
                    stageRuleDataValues.add(ruleDataValue);
                }
            }
        }

        if (!stageRuleDataValues.isEmpty()) {
            return create(stageRuleDataValues.get(0).value(),
                    variable.dataElementType(), Utils.values(stageRuleDataValues));
        }

        return create(variable.dataElementType());
    }

    @Nonnull
    private RuleVariableValue createPreviousEventVariableValue(
            @Nonnull RuleEvent currentRuleEvent,
            @Nonnull Map<String, List<RuleDataValue>> valueMap,
            @Nonnull RuleVariablePreviousEvent variable) {

        List<RuleDataValue> ruleDataValues = valueMap.get(variable.dataElement());
        if (ruleDataValues != null && !ruleDataValues.isEmpty()) {
            for (RuleDataValue ruleDataValue : ruleDataValues) {
                // We found preceding value to the current event,
                // which is assumed to be best candidate.
                if (currentRuleEvent.eventDate().compareTo(ruleDataValue.eventDate()) > 0) {
                    return create(ruleDataValue.value(), variable.dataElementType(),
                            Utils.values(ruleDataValues));
                }
            }
        }

        return create(variable.dataElementType());
    }

    /**
     * Returns a map where the key is uid of TrackedEntityAttribute
     * and value is the RuleAttributeValue.
     *
     * @return Map of tracked entity attributes to data values
     */
    @Nonnull
    private static Map<String, RuleAttributeValue> attributesToTrackedEntityAttributeValues(
            @Nonnull List<RuleAttributeValue> ruleAttributeValues) {
        Map<String, RuleAttributeValue> valueMap
                = new HashMap<>(ruleAttributeValues.size());
        for (int index = 0; index < ruleAttributeValues.size(); index++) {
            RuleAttributeValue ruleAttributeValue = ruleAttributeValues.get(index);
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
    private static Map<String, RuleDataValue> dataElementsToTrackedEntityDataValue(
            @Nonnull RuleEvent event) {
        Map<String, RuleDataValue> valueMap = new HashMap<>(event.dataValues().size());
        for (int index = 0; index < event.dataValues().size(); index++) {
            RuleDataValue ruleDataValue = event.dataValues().get(index);
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
    private Map<String, List<RuleDataValue>> dataElementsToTrackedEntityDataValues(
            @Nonnull RuleEvent event) {
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
        for (int i = 0; i < events.size(); i++) {
            RuleEvent ruleEvent = events.get(i);

            for (int j = 0; j < ruleEvent.dataValues().size(); j++) {
                RuleDataValue ruleDataValue = ruleEvent.dataValues().get(j);

                // push new list if it is not there for the given data element
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
