package org.hisp.dhis.android.core.common.audit;

import org.hisp.dhis.android.core.trackedentity.TrackedEntity;

import java.util.HashMap;
import java.util.Map;

public class MetadataClassFactory {
    private static final Map<String, Class<?>> metadataClassMap = createMap();

    private static Map<String, Class<?>> createMap() {
        Map<String, Class<?>> myMap = new HashMap<>();
        myMap.put("TrackedEntity", TrackedEntity.class);

        return myMap;
    }

    public static Class<?> getByName(String name) {
        if (metadataClassMap.containsKey(name)) {
            return metadataClassMap.get(name);
        } else {
            throw new IllegalArgumentException("No exists a metadata class for name: " + name);
        }
    }
}
