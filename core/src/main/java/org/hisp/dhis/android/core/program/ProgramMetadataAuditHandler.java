package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class ProgramMetadataAuditHandler implements MetadataAuditHandler {

    private final ProgramFactory programFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public ProgramMetadataAuditHandler(ProgramFactory programFactory, boolean isTranslationOn,
            String translationLocale) {
        this.programFactory = programFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public void handle(MetadataAudit metadataAudit) throws Exception {
        Program program = (Program) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());

            ProgramQuery programQuery = ProgramQuery.defaultQuery(uIds, isTranslationOn,
                    translationLocale);
            programFactory.newEndPointCall(programQuery, metadataAudit.getCreatedAt()).call();
        } else {

            if (metadataAudit.getType() == AuditType.DELETE) {
                program = program.toBuilder().deleted(true).build();
            }

            programFactory.getProgramHandler().handleProgram(program);
        }
    }
}
