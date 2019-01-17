package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.handlers.ObjectWithoutUidSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TrackedEntityAttributeValueHandler
        extends ObjectWithoutUidSyncHandlerImpl<TrackedEntityAttributeValue> {
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    TrackedEntityAttributeValueHandler(TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        super(trackedEntityAttributeValueStore);
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    @Override
    protected void afterCollectionHandled(Collection<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        removeNotExistingAttributeValuesInServer(trackedEntityAttributeValues);
    }

    private void removeNotExistingAttributeValuesInServer(
            Collection<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        if (trackedEntityAttributeValues.isEmpty()) {
            return;
        }

        String teiUid = trackedEntityAttributeValues.iterator().next().trackedEntityInstance();

        List<String> attributeUids = new ArrayList<>();
        for (TrackedEntityAttributeValue value : trackedEntityAttributeValues) {
            attributeUids.add(value.trackedEntityAttribute());
        }

        trackedEntityAttributeValueStore.deleteByInstanceAndNotInAttributes(teiUid, attributeUids);
    }

    public static SyncHandlerWithTransformer<TrackedEntityAttributeValue> create(DatabaseAdapter databaseAdapter) {
        return new TrackedEntityAttributeValueHandler(TrackedEntityAttributeValueStoreImpl.create(databaseAdapter));
    }
}