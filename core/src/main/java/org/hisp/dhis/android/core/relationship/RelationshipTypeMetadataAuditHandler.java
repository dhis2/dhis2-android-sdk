package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class RelationshipTypeMetadataAuditHandler implements MetadataAuditHandler {

    private final RelationshipTypeFactory relationshipTypeFactory;

    public RelationshipTypeMetadataAuditHandler(RelationshipTypeFactory relationshipTypeFactory) {
        this.relationshipTypeFactory = relationshipTypeFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        RelationshipType relationshipType = (RelationshipType) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by relationshipType call
            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());

            relationshipTypeFactory.newEndPointCall(uIds, metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                relationshipType = relationshipType.toBuilder().deleted(true).build();
            }
            relationshipTypeFactory.getRelationshipTypeHandler().handleRelationshipType(relationshipType);
        }
    }
}
