package org.hisp.dhis.android.core.program;


import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class ProgramStageMetadataAuditHandler implements MetadataAuditHandler {

    private final ProgramFactory programFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public ProgramStageMetadataAuditHandler(ProgramFactory programFactory, boolean isTranslationOn,
            String translationLocale) {
        this.programFactory = programFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public void handle(MetadataAudit metadataAudit) throws Exception {
        // MetadataAudit<ProgramStage> of CREATE type is ignored because program does not exists
        // in payload. When a program is created on server, two messages are sent with RabbitMQ.
        // one is the created programStage that we are going to ignore and other is created
        // Program that
        // contains programStage and ProgramHandler will create this program Stage.

        ProgramStage programStage = (ProgramStage) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            ProgramStage programStageInDB = programFactory.getProgramStageStore().queryByUid(
                    metadataAudit.getUid());

            if (programStageInDB == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: programStage updated on server but does not exists "
                                + "in "
                                + "local: "
                                + metadataAudit);
            } else {
                Set<String> uIds = new HashSet<>();

                uIds.add(programStageInDB.program());
                ProgramQuery programQuery = ProgramQuery.defaultQuery(uIds, isTranslationOn,
                        translationLocale);

                programFactory.newEndPointCall(programQuery, metadataAudit.getCreatedAt()).call();
            }
        } else {

            if (metadataAudit.getType() == AuditType.DELETE) {
                programStage = programStage.toBuilder().deleted(true).build();

                programFactory.getProgramStageHandler().handle(programStage.program(),
                        programStage);
            }
        }
    }
}
