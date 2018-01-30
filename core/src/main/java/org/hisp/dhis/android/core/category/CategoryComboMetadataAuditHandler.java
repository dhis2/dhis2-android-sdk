package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class CategoryComboMetadataAuditHandler implements MetadataAuditHandler {

    private final CategoryComboFactory categoryComboFactory;

    public CategoryComboMetadataAuditHandler(CategoryComboFactory categoryComboFactory) {
        this.categoryComboFactory = categoryComboFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        CategoryCombo categoryCombo = (CategoryCombo) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());
            categoryComboFactory.newEndPointCall(CategoryComboQuery.defaultQuery(uIds),
                    metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                categoryCombo = categoryCombo.toBuilder().deleted(true).build();
            }

            categoryComboFactory.getCategoryComboHandler().handle(categoryCombo);
        }
    }
}
