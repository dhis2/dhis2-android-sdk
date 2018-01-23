package org.hisp.dhis.android.core.category;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;

import java.util.HashSet;
import java.util.Set;

public class CategoryOptionMetadataAuditHandler implements MetadataAuditHandler {

    private final CategoryFactory categoryFactory;

    public CategoryOptionMetadataAuditHandler(CategoryFactory categoryFactory) {
        this.categoryFactory = categoryFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        // MetadataAudit<Option> of CREATE type is ignored because OptionSetUid is null in payload.
        // when a option is create on server also send a MetadataAudit<OptionSet> of UPDATE type
        // then new option will be created by OptionSetMetadataAuditHandler.

        CategoryOption categoryOption = (CategoryOption) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata parent call

            CategoryOption oldCategoryOption = categoryFactory.getCategoryOptionStore().queryByUid(
                    metadataAudit.getUid());

            if (oldCategoryOption == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: "+this.getClass().getSimpleName()+" updated on server but does not exists in "
                                + "local: "
                                + metadataAudit);
            } else {
                Set<String> uIds = new HashSet<>();
                uIds.add(oldCategoryOption.uid());
                categoryFactory.newEndPointCall(uIds, metadataAudit.getCreatedAt()).call();
            }
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                categoryOption = categoryOption.toBuilder().deleted(true).build();

                categoryFactory.getCategoryOptionHandler().handle(categoryOption);
            }
        }
    }
}
