package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class CategoryMetadataAuditHandler implements MetadataAuditHandler {

    private final CategoryFactory categoryFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public CategoryMetadataAuditHandler(CategoryFactory categoryFactory, boolean isTranslationOn,
            String translationLocale) {
        this.categoryFactory = categoryFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public void handle(MetadataAudit metadataAudit) throws Exception {
        Category category = (Category) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());

            categoryFactory.newEndPointCall(
                    CategoryQuery.defaultQuery(uIds, isTranslationOn, translationLocale),
                    metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                category = category.toBuilder().deleted(true).build();
            }

            categoryFactory.getCategoryHandler().handle(category);
        }
    }
}
