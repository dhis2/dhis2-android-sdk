package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;
import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.HashSet;
import java.util.Set;

public class TrackedEntityMetadataAuditHandler implements MetadataAuditHandler {

    private final TrackedEntityFactory mTrackedEntityFactory;

    public TrackedEntityMetadataAuditHandler(TrackedEntityFactory trackedEntityFactory) {
        this.mTrackedEntityFactory = trackedEntityFactory;
    }

    @Override
    public void handle(MetadataAudit metadataAudit) throws Exception {
        TrackedEntity trackedEntity = (TrackedEntity) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());
            TrackedEntityQuery trackedEntityQuery = TrackedEntityQuery.defaultQuery(
                    uIds, BaseQuery.DEFAULT_IS_TRANSLATION_ON,
                    BaseQuery.DEFAULT_TRANSLATION_LOCALE);

            mTrackedEntityFactory.newEndPointCall(trackedEntityQuery, metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                trackedEntity = trackedEntity.toBuilder().deleted(true).build();
            }

            mTrackedEntityFactory.getHandler().handleTrackedEntity(trackedEntity);
        }
    }
}
