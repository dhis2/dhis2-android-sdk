package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.handlers.ObjectWithoutUidSyncHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal"})
public class TrackedEntityDataValueHandler extends ObjectWithoutUidSyncHandlerImpl<TrackedEntityDataValue> {
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;

    private TrackedEntityDataValueHandler(TrackedEntityDataValueStore trackedEntityDataValueStore) {
        super(trackedEntityDataValueStore);
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
    }

    @Override
    protected void afterCollectionHandled(Collection<TrackedEntityDataValue> trackedEntityDataValues) {
        removeNotExistingDataValuesInServer(trackedEntityDataValues);
    }

    private void removeNotExistingDataValuesInServer(Collection<TrackedEntityDataValue> trackedEntityDataValues) {
        String eventUid = trackedEntityDataValues.iterator().next().event();

        List<String> dataElementUids = new ArrayList<>();
        for (TrackedEntityDataValue dataValue : trackedEntityDataValues) {
            dataElementUids.add(dataValue.dataElement());
        }

        trackedEntityDataValueStore.deleteByEventAndNotInDataElements(eventUid, dataElementUids);
    }

    public static TrackedEntityDataValueHandler create(DatabaseAdapter databaseAdapter) {
        return new TrackedEntityDataValueHandler(TrackedEntityDataValueStoreImpl.create(databaseAdapter));
    }
}