package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class OptionSetMetadataAuditHandler implements MetadataAuditHandler {

    private final OptionSetFactory optionSetFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public OptionSetMetadataAuditHandler(OptionSetFactory optionSetFactory, boolean isTranslationOn,
            String translationLocale) {
        this.optionSetFactory = optionSetFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public void handle(MetadataAudit metadataAudit) throws Exception {
        OptionSet optionSet = (OptionSet) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());

            OptionSetQuery optionSetQuery = OptionSetQuery.defaultQuery(
                    uIds, isTranslationOn, translationLocale);

            optionSetFactory.newEndPointCall(optionSetQuery, metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                optionSet = optionSet.toBuilder().deleted(true).build();
            }

            optionSetFactory.getOptionSetHandler().handleOptionSet(optionSet);
        }
    }
}
