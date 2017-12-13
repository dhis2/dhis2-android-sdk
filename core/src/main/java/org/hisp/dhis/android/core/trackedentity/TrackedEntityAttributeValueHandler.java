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

        removeNoExistedAttributeValuesInServer(trackedEntityInstanceUid, attributeValues);

        persistTrackedEntityDataValues(trackedEntityInstanceUid, attributeValues);
    }

    private void removeNoExistedAttributeValuesInServer(String trackedEntityInstanceUid,
            List<TrackedEntityAttributeValue> attributeValues) {

        List<String> uIds = getAttributeUIdsToRemove(trackedEntityInstanceUid, attributeValues);

        if (uIds.size() > 0) {
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
            if (!existsDataElement(attributeValues, attributeValue.trackedEntityAttribute())) {
                attributeUIdsToRemove.add(attributeValue.trackedEntityAttribute());
            }
        }

        return attributeUIdsToRemove;
    }

    private boolean existsDataElement(List<TrackedEntityAttributeValue> attributeValues,
            String trackedEntityAttributeUid) {
        for (TrackedEntityAttributeValue attributeValue : attributeValues) {
            if (attributeValue.trackedEntityAttribute().equals(trackedEntityAttributeUid)) {
                return true;
            }
        }

        return false;
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

