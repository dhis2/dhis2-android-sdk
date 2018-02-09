package org.hisp.dhis.android.core.program;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramRuleMetadataAuditHandler implements MetadataAuditHandler {

    private final ProgramFactory programFactory;

    public ProgramRuleMetadataAuditHandler(ProgramFactory programFactory) {
        this.programFactory = programFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        ProgramRule programRule = (ProgramRule) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            ProgramRule programRuleInDB = programFactory.getProgramRuleStore().queryByUid(
                    metadataAudit.getUid());

            if (programRuleInDB == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: programRule updated on server but does not exists "
                                + "in "
                                + "local: "
                                + metadataAudit);
            } else {
                Set<String> uIds = new HashSet<>();

                uIds.add(programRuleInDB.program().uid());

                programFactory.newEndPointCall(uIds, metadataAudit.getCreatedAt()).call();
            }
        } else {

            if (metadataAudit.getType() == AuditType.DELETE) {
                programRule = programRule.toBuilder().deleted(true).build();
            }

            if (metadataAudit.getType() == AuditType.CREATE) {
                programRule = assignProgramRuleToActions(programRule);
            }

            programFactory.getProgramRuleHandler().handle(programRule);
        }
    }

    private ProgramRule assignProgramRuleToActions(ProgramRule programRule) {
        List<ProgramRuleAction> programRuleActions = new ArrayList<>();

        for (ProgramRuleAction programRuleAction : programRule.programRuleActions()) {
            programRuleActions.add(programRuleAction.toBuilder().programRule(programRule).build());
        }

        return programRule.toBuilder().programRuleActions(programRuleActions).build();
    }
}
