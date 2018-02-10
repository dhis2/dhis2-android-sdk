package org.hisp.dhis.android.core.category;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryOptionMetadataAuditHandler implements MetadataAuditHandler {

    private final CategoryFactory categoryFactory;

    public CategoryOptionMetadataAuditHandler(CategoryFactory categoryFactory) {
        this.categoryFactory = categoryFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        // MetadataAudit<CategoryOption> of CREATE type is ignored because Category parent is
        // null in payload.
        // when a categoryOption is create on server also send a MetadataAudit<Category> of
        // UPDATE type
        // then new categoryOption will be created by CategoryMetadataAuditHandler.

        CategoryOption categoryOption = (CategoryOption) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata parent call

            CategoryOption oldCategoryOption = categoryFactory.getCategoryOptionStore().queryByUid(
                    metadataAudit.getUid());

            if (oldCategoryOption == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: " + this.getClass().getSimpleName()
                                + " updated on server but does not exists in local: "
                                + metadataAudit);
            } else {
                Set<String> uIds = new HashSet<>();
                List<String> parentUIds = categoryFactory.getCategoryOptionLinkStore().
                        queryCategoryUidListFromCategoryOptionUid(oldCategoryOption.uid());
                uIds.add(categoryFactory.getCategoryStore().queryByUid(parentUIds.get(0)).uid());

                categoryFactory.newEndPointCall(CategoryQuery.defaultQuery(uIds),
                        metadataAudit.getCreatedAt()).call();
            }
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                categoryOption = categoryOption.toBuilder().deleted(true).build();

                categoryFactory.getCategoryOptionHandler().handle("", categoryOption);
            }
        }
    }
}
