package org.hisp.dhis.android.core.trackedentity;

import java.util.List;

public class TrackedEntityAttributeValueHandler {
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    public TrackedEntityAttributeValueHandler(
            TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    public void handle(String trackedEntityInstanceUid,
            List<TrackedEntityAttributeValue> attValues) {
        if (trackedEntityInstanceUid == null || attValues == null) {
            return;
        }

        persistTrackedEntityDataValues(trackedEntityInstanceUid, attValues);
    }


    private void persistTrackedEntityDataValues(String trackedEntityInstanceUid,
            List<TrackedEntityAttributeValue> attValues) {

        for (TrackedEntityAttributeValue attValue : attValues) {
            persistTrackedEntityDataValue(trackedEntityInstanceUid, attValue);
        }
    }

    private void persistTrackedEntityDataValue(String trackedEntityInstanceUid,
            TrackedEntityAttributeValue dataValue) {

        int updatedRow = trackedEntityAttributeValueStore.update(
                dataValue.value(), dataValue.created(), dataValue.lastUpdated(),
                dataValue.trackedEntityAttribute(),
                trackedEntityInstanceUid);

        if (updatedRow <= 0) {
            trackedEntityAttributeValueStore.insert(
                    dataValue.value(), dataValue.created(), dataValue.lastUpdated()
                    , dataValue.trackedEntityAttribute(),
                    trackedEntityInstanceUid);
        }
    }
}

