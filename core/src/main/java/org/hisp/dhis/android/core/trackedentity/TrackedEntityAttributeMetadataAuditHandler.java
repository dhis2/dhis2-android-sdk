package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class TrackedEntityAttributeMetadataAuditHandler implements MetadataAuditHandler {

    private final TrackedEntityAttributeFactory trackedEntityAttributeFactory;

    public TrackedEntityAttributeMetadataAuditHandler(
            TrackedEntityAttributeFactory trackedEntityAttributeFactory) {
        this.trackedEntityAttributeFactory = trackedEntityAttributeFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {

        TrackedEntityAttribute trackedEntityAttribute =
                (TrackedEntityAttribute) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());

            TrackedEntityAttributeQuery trackedEntityAttributeQuery =
                    TrackedEntityAttributeQuery.Builder.create()
                            .withUIds(uIds).build();

            trackedEntityAttributeFactory.newEndPointCall(trackedEntityAttributeQuery,
                    metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                trackedEntityAttribute = trackedEntityAttribute.toBuilder().deleted(true).build();
            }

            trackedEntityAttributeFactory.getTrackedEntityAttributeHandler()
                    .handleTrackedEntityAttribute(trackedEntityAttribute);
        }
    }
}
