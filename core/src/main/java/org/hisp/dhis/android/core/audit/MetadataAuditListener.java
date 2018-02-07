package org.hisp.dhis.android.core.audit;

import android.util.Log;

public class MetadataAuditListener implements MetadataAuditConsumer.MetadataAuditListener {

    private MetadataSyncedListener metadataSyncedListener;
    private final MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    public MetadataAuditListener(MetadataAuditHandlerFactory metadataAuditHandlerFactory) {
        this.metadataAuditHandlerFactory = metadataAuditHandlerFactory;
    }

    @Override
    public void onMetadataChanged(Class<?> klass, MetadataAudit metadataAudit) {
        try {
            MetadataAuditHandler metadataAuditHandler = metadataAuditHandlerFactory.getByClass(
                    klass);

            metadataAuditHandler.handle(metadataAudit);

            notifySyncedToMetadataSyncedListener(klass, metadataAudit);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            notifyErrorToMetadataSyncedListener(e);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(this.getClass().getSimpleName(), throwable.getMessage(), throwable);
        notifyErrorToMetadataSyncedListener(throwable);
    }

    public void setMetadataSyncedListener(MetadataSyncedListener metadataSyncedListener) {
        this.metadataSyncedListener = metadataSyncedListener;
    }

    private void notifySyncedToMetadataSyncedListener(Class<?> klass, MetadataAudit metadataAudit) {
        if (metadataSyncedListener != null) {

            metadataSyncedListener.onSynced(SyncedMetadataMapper.map(klass, metadataAudit));
        }
    }

    private void notifyErrorToMetadataSyncedListener(Throwable throwable) {
        if (metadataSyncedListener != null) {
            metadataSyncedListener.onError(throwable);
        }
    }
}
