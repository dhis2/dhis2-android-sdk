package org.hisp.dhis.android.core.program;


import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class ProgramRuleActionMetadataAuditHandler implements MetadataAuditHandler {

    private final ProgramFactory programFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public ProgramRuleActionMetadataAuditHandler(ProgramFactory programFactory,
            boolean isTranslationOn, String translationLocale) {
        this.programFactory = programFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public void handle(MetadataAudit metadataAudit) throws Exception {
        // MetadataAudit<ProgramRuleAction> of CREATE type is ignored because programRule does
        // not exists
        // in payload. When a programRuleAction is created on server, two messages are sent with
        // RabbitMQ.
        // one is the created programRuleAction that we are going to ignore and other is created
        // ProgramRule that
        // contains programRuleActions and ProgramHandler will create this programRuleActions.

        ProgramRuleAction programRuleAction = (ProgramRuleAction) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            ProgramRuleAction programRuleActionInDB =
                    programFactory.getProgramRuleActionStore().queryByUid(metadataAudit.getUid());

            if (programRuleActionInDB == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: programRuleAction updated on server but does not "
                                + "exists "
                                + "in "
                                + "local: "
                                + metadataAudit);
            } else {

                ProgramRule programRuleInDB = programFactory.getProgramRuleStore().queryByUid(
                        programRuleActionInDB.programRule().uid());

                Set<String> uIds = new HashSet<>();

                uIds.add(programRuleInDB.program().uid());
                ProgramQuery programQuery = ProgramQuery.defaultQuery(uIds,
                        isTranslationOn, translationLocale);
                programFactory.newEndPointCall(programQuery, metadataAudit.getCreatedAt()).call();
            }
        } else {

            if (metadataAudit.getType() == AuditType.DELETE) {
                programRuleAction = programRuleAction.toBuilder().deleted(true).build();

                programFactory.getProgramRuleActionHandler().handle(programRuleAction);
            }
        }
    }
}
