package org.hisp.dhis.android.core.trackedentity;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class TrackedEntityAttributeMetadataAuditHandler implements MetadataAuditHandler {

    private final TrackedEntityAttributeFactory trackedEntityAttributeFactory;

    public TrackedEntityAttributeMetadataAuditHandler(TrackedEntityAttributeFactory trackedEntityAttributeFactory) {
        this.trackedEntityAttributeFactory = trackedEntityAttributeFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {

        TrackedEntityAttribute trackedEntityAttribute = (TrackedEntityAttribute) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata parent call

            TrackedEntityAttribute trackedEntityAttributeInDb = trackedEntityAttributeFactory
                    .getTrackedEntityAttributeStore().queryByUid(
                    metadataAudit.getUid());

            if (trackedEntityAttributeInDb == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: "+TrackedEntityAttribute.class.getSimpleName()
                                +" updated on server but does not exists in "
                                + "local: "
                                + metadataAudit);
            } else {
                Set<String> uIds = new HashSet<>();
                uIds.add(trackedEntityAttributeInDb.optionSet().uid());
                TrackedEntityAttributeQuery trackedEntityAttributeQuery = new TrackedEntityAttributeQuery(uIds);
                trackedEntityAttributeFactory.newEndPointCall(trackedEntityAttributeQuery,
                        metadataAudit.getCreatedAt()).call();

            }
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                trackedEntityAttribute = trackedEntityAttribute.toBuilder().deleted(true).build();
            }

            trackedEntityAttributeFactory.getTrackedEntityAttributeHandler()
                    .handleTrackedEntityAttribute(trackedEntityAttribute);
        }
    }
}
