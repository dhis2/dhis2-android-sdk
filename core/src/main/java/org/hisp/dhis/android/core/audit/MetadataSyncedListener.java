package org.hisp.dhis.android.core.audit;

public interface MetadataSyncedListener {
    void onSynced(SyncedMetadata syncedMetadata);

    void onError(Throwable throwable);
}
