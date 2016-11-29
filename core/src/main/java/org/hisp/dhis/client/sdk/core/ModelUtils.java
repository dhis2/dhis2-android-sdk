package org.hisp.dhis.client.sdk.core;

import org.hisp.dhis.client.sdk.models.common.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelUtils {

    public ModelUtils() {
        // no instances
    }

    public static <T extends IdentifiableObject> Map<String, T> toMap(Collection<T> objects) {
        Map<String, T> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                if (object.uid() != null) {
                    map.put(object.uid(), object);
                }
            }
        }
        return map;
    }

    /**
     * @param objects
     * @return map of event uid and event
     */

    public static Map<String, Event> toEventMap(Collection<Event> objects) {
        Map<String, Event> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (Event object : objects) {
                if (object.uid() != null) {
                    map.put(object.uid(), object);
                }
            }
        }
        return map;
    }

    /**
     * @param trackedEntityAttributeValues
     * @return map of trackedEntityAttribute uid and trackedEntityAttributeValue
     */
    public static Map<String, TrackedEntityAttributeValue> toAttributeAttributeValueMap(List<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        Map<String, TrackedEntityAttributeValue> attributeValueMap = new HashMap<>();

        if (trackedEntityAttributeValues != null && !trackedEntityAttributeValues.isEmpty()) {
            for (TrackedEntityAttributeValue trackedEntityAttributeValue : trackedEntityAttributeValues) {
                attributeValueMap.put(trackedEntityAttributeValue.trackedEntityAttributeUid(), trackedEntityAttributeValue);
            }
        }
        return attributeValueMap;
    }

    /**
     * @param trackedEntityDataValues
     * @return map of dataElement uid and trackedEntityDataValue
     */
    public static Map<String, TrackedEntityDataValue> toDataElementDataValueMap(List<TrackedEntityDataValue> trackedEntityDataValues) {
        Map<String, TrackedEntityDataValue> dataValueMap = new HashMap<>();
        if (trackedEntityDataValues != null && !trackedEntityDataValues.isEmpty()) {
            for (TrackedEntityDataValue dataValue : trackedEntityDataValues) {
                dataValueMap.put(dataValue.dataElement(), dataValue);
            }
        }
        return dataValueMap;
    }
}
