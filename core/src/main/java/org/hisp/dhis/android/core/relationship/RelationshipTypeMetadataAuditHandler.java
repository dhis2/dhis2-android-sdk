package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

public class RelationshipTypeMetadataAuditHandler implements MetadataAuditHandler {

    private final RelationshipTypeFactory relationshipTypeFactory;

    public RelationshipTypeMetadataAuditHandler(RelationshipTypeFactory relationshipTypeFactory) {
        this.relationshipTypeFactory = relationshipTypeFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        // MetadataAudit<Option> of CREATE type is ignored because OptionSetUid is null in payload.
        // when a option is create on server also send a MetadataAudit<OptionSet> of UPDATE type
        // then new option will be created by OptionSetMetadataAuditHandler.

        RelationshipType relationshipType = (RelationshipType) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata parent call
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                relationshipType = relationshipType.toBuilder().deleted(true).build();

                relationshipTypeFactory.getRelationshipTypeHandler().handleRelationshipType(relationshipType);
            }
        }
    }
}
