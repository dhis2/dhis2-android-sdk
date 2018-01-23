package org.hisp.dhis.android.core.category;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;

import java.util.HashSet;
import java.util.Set;

public class CategoryMetadataAuditHandler implements MetadataAuditHandler {

    private final CategoryFactory categoryFactory;

    public CategoryMetadataAuditHandler(CategoryFactory categoryFactory) {
        this.categoryFactory = categoryFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        // MetadataAudit<Option> of CREATE type is ignored because OptionSetUid is null in payload.
        // when a option is create on server also send a MetadataAudit<OptionSet> of UPDATE type
        // then new option will be created by OptionSetMetadataAuditHandler.

        Category category = (Category) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());
            categoryFactory.newEndPointCall(CategoryQuery.defaultQuery(uIds), metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                category = category.toBuilder().deleted(true).build();
            }

            categoryFactory.getCategoryHandler().handle(category);
        }
    }
}
