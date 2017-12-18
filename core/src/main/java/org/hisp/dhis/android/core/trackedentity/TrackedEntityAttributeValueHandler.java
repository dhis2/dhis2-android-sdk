package org.hisp.dhis.android.core.trackedentity;

import java.util.ArrayList;
import java.util.List;

public class TrackedEntityAttributeValueHandler {
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    public TrackedEntityAttributeValueHandler(
            TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    public void handle(String trackedEntityInstanceUid,
            List<TrackedEntityAttributeValue> attributeValues) {
        if (trackedEntityInstanceUid == null || attributeValues == null) {
            return;
        }

        removeAttributeValuesNotExistingInServer(trackedEntityInstanceUid, attributeValues);

        for (TrackedEntityAttributeValue attValue : attributeValues) {
            handle(trackedEntityInstanceUid, attValue);
        }
    }

    private void removeAttributeValuesNotExistingInServer(String trackedEntityInstanceUid,
            List<TrackedEntityAttributeValue> attributeValues) {

        List<String> uIds = getAttributeUIdsToRemove(trackedEntityInstanceUid, attributeValues);

        if (!uIds.isEmpty()) {
            trackedEntityAttributeValueStore.deleteByInstanceAndAttributes(
                    trackedEntityInstanceUid, uIds);
        }
    }

    private List<String> getAttributeUIdsToRemove(String trackedEntityInstanceUid,
            List<TrackedEntityAttributeValue> attributeValues) {
        List<String> attributeUIdsToRemove = new ArrayList<>();

        List<TrackedEntityAttributeValue> attributeValuesInDB =
                trackedEntityAttributeValueStore.queryByTrackedEntityInstance(
                        trackedEntityInstanceUid);

        for (TrackedEntityAttributeValue attributeValue : attributeValuesInDB) {
            if (!existsTrackedEntityAttribute(attributeValues,
                    attributeValue.trackedEntityAttribute())) {
                attributeUIdsToRemove.add(attributeValue.trackedEntityAttribute());
            }
        }

        return attributeUIdsToRemove;
    }

    private boolean existsTrackedEntityAttribute(List<TrackedEntityAttributeValue> attributeValues,
            String trackedEntityAttributeUid) {
        for (TrackedEntityAttributeValue attributeValue : attributeValues) {
            if (attributeValue.trackedEntityAttribute().equals(trackedEntityAttributeUid)) {
                return true;
            }
        }

        return false;
    }

    private void handle(String trackedEntityInstanceUid,
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

