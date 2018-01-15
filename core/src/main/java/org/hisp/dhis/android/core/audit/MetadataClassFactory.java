package org.hisp.dhis.android.core.audit;

import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;

import java.util.HashMap;
import java.util.Map;

final class MetadataClassFactory {
    private static final Map<String, Class<?>> metadataClassMap = createMap();

    private static Map<String, Class<?>> createMap() {
        Map<String, Class<?>> myMap = new HashMap<>();
        myMap.put("trackedEntity", TrackedEntity.class);
        myMap.put("relationshipType", RelationshipType.class);
        return myMap;
    }

    public static Class<?> getByName(String name) {
        if (metadataClassMap.containsKey(name)) {
            return metadataClassMap.get(name);
        } else {
            throw new IllegalArgumentException("No exists a metadata class for name: " + name);
        }
    }

    private MetadataClassFactory() {
    }
}
