package org.hisp.dhis.android.core.program;

import android.content.ContentValues;

public class CreateProgramStageSectionUtils {
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Integer SORT_ORDER = 7;
    private static final String PROGRAM_STAGE = "test_program_stage";

    // timestamp
    private static final String DATE = "2017-01-05T10:26:00.000";

    public static ContentValues create(long id, String uid, String programStageUid) {
        ContentValues programStageSection = new ContentValues();
        programStageSection.put(ProgramStageSectionModel.Columns.ID, id);
        programStageSection.put(ProgramStageSectionModel.Columns.UID, uid);
        programStageSection.put(ProgramStageSectionModel.Columns.CODE, CODE);
        programStageSection.put(ProgramStageSectionModel.Columns.NAME, NAME);
        programStageSection.put(ProgramStageSectionModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageSection.put(ProgramStageSectionModel.Columns.CREATED, DATE);
        programStageSection.put(ProgramStageSectionModel.Columns.LAST_UPDATED, DATE);
        programStageSection.put(ProgramStageSectionModel.Columns.SORT_ORDER, SORT_ORDER);

        if (programStageUid == null) {
            programStageSection.putNull(ProgramStageSectionModel.Columns.PROGRAM_STAGE);
        } else {
            programStageSection.put(ProgramStageSectionModel.Columns.PROGRAM_STAGE, programStageUid);
        }

        return programStageSection;
    }
}
