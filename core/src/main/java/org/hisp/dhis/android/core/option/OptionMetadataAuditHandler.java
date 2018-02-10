package org.hisp.dhis.android.core.option;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class OptionMetadataAuditHandler implements MetadataAuditHandler {

    private final OptionSetFactory optionSetFactory;

    public OptionMetadataAuditHandler(OptionSetFactory optionSetFactory) {
        this.optionSetFactory = optionSetFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        // MetadataAudit<Option> of CREATE type is ignored because OptionSetUid is null in payload.
        // when a option is create on server also send a MetadataAudit<OptionSet> of UPDATE type
        // then new option will be created by OptionSetMetadataAuditHandler.

        Option option = (Option) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata parent call

            Option optionInDB = optionSetFactory.getOptionStore().queryByUid(
                    metadataAudit.getUid());

            if (optionInDB == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: Option updated on server but does not exists in "
                                + "local: "
                                + metadataAudit);
            } else {
                Set<String> uIds = new HashSet<>();
                uIds.add(optionInDB.optionSet().uid());

                optionSetFactory.newEndPointCall(uIds, metadataAudit.getCreatedAt()).call();

            }
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                option = option.toBuilder().deleted(true).build();

                optionSetFactory.getOptionHandler().handleOption(option);
            }
        }
    }
}
