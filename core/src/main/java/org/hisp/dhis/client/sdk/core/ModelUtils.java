package org.hisp.dhis.client.sdk.core;

import org.hisp.dhis.client.sdk.models.common.IdentifiableObject;

import java.util.Collection;
import java.util.HashMap;
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
}
