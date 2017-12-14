package org.hisp.dhis.android.core.trackedentity;

import java.util.ArrayList;
import java.util.List;

public class TrackedEntityDataValueHandler {
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;

    public TrackedEntityDataValueHandler(TrackedEntityDataValueStore trackedEntityDataValueStore) {
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
    }

    public void handleTrackedEntityDataValue(String eventUid,
            List<TrackedEntityDataValue> dataValues) {
        if (eventUid == null || dataValues == null) {
            return;
        }

        removeNoExistedDataValuesInServer(eventUid, dataValues);

        persistTrackedEntityDataValues(eventUid, dataValues);
    }

    private void removeNoExistedDataValuesInServer(String eventUid,
            List<TrackedEntityDataValue> dataValues) {

        List<String> uIds = getDataElementUIdsToRemove(eventUid, dataValues);

        if (!uIds.isEmpty()) {
            trackedEntityDataValueStore.deleteByEventAndDataElementUIds(eventUid, uIds);
        }
    }

    private List<String> getDataElementUIdsToRemove(String eventUid,
            List<TrackedEntityDataValue> dataValues) {
        List<String> dataElementUIdsToRemove = new ArrayList<>();

        List<TrackedEntityDataValue> dataValuesInDB =
                trackedEntityDataValueStore.queryTrackedEntityDataValues(eventUid);

        for (TrackedEntityDataValue dataValue : dataValuesInDB) {
            if (!existsDataElement(dataValues, dataValue.dataElement())) {
                dataElementUIdsToRemove.add(dataValue.dataElement());
            }
        }

        return dataElementUIdsToRemove;
    }

    private boolean existsDataElement(List<TrackedEntityDataValue> dataValues,
            String dataElementUid) {
        for (TrackedEntityDataValue dataValue : dataValues) {
            if (dataValue.dataElement().equals(dataElementUid)) {
                return true;
            }
        }

        return false;
    }

    private void persistTrackedEntityDataValues(String eventUid,
            List<TrackedEntityDataValue> trackedEntityDataValues) {

        for (TrackedEntityDataValue dataValue : trackedEntityDataValues) {
            persistTrackedEntityDataValue(eventUid, dataValue);
        }
    }

    private void persistTrackedEntityDataValue(String eventUid,
            TrackedEntityDataValue dataValue) {

        int updatedRow = trackedEntityDataValueStore.update(
                eventUid, dataValue.created(), dataValue.lastUpdated(), dataValue.dataElement(),
                dataValue.storedBy(), dataValue.value(), dataValue.providedElsewhere());

        if (updatedRow <= 0) {
            trackedEntityDataValueStore.insert(
                    eventUid, dataValue.created(), dataValue.lastUpdated(), dataValue.dataElement(),
                    dataValue.storedBy(), dataValue.value(), dataValue.providedElsewhere());
        }
    }
}

