package org.hisp.dhis.android.core.program;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class ProgramRuleVariableMetadataAuditHandler implements MetadataAuditHandler {

    private final ProgramFactory programFactory;

    public ProgramRuleVariableMetadataAuditHandler(ProgramFactory programFactory) {
        this.programFactory = programFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        ProgramRuleVariable programRuleVariable = (ProgramRuleVariable) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            ProgramRuleVariable programRuleVariableInDB =
                    programFactory.getProgramRuleVariableStore().queryByUid(
                            metadataAudit.getUid());

            if (programRuleVariableInDB == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: programRuleVariable updated on server but does not "
                                + "exists "
                                + "in "
                                + "local: "
                                + metadataAudit);
            } else {
                Set<String> uIds = new HashSet<>();

                uIds.add(programRuleVariableInDB.program().uid());

                programFactory.newEndPointCall(uIds, metadataAudit.getCreatedAt()).call();
            }
        } else {

            if (metadataAudit.getType() == AuditType.DELETE) {
                programRuleVariable = programRuleVariable.toBuilder().deleted(true).build();
            }

            programFactory.getProgramRuleVariableHandler().handle(programRuleVariable);
        }
    }
}
