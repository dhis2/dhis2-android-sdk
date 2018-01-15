package org.hisp.dhis.android.core.audit;

import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityMetadataAuditHandler;

public final class MetadataAuditHandlerFactory {

    private final TrackedEntityHandler trackedEntityHandler;

    public MetadataAuditHandlerFactory(TrackedEntityHandler trackedEntityHandler) {
        this.trackedEntityHandler = trackedEntityHandler;
    }

    public MetadataAuditHandler getByClass(Class<?> klass) {
        if (klass == TrackedEntity.class) {
            return new TrackedEntityMetadataAuditHandler(trackedEntityHandler);
        } else {
            throw new IllegalArgumentException("No exists a metadata audit handler for: " + klass);
        }
    }
}
