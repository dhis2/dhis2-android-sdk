package org.hisp.dhis.android.core.program;

import android.content.ContentValues;

public class CreateProgramRuleUtils {
    /**
     * BaseIdentifiable propertites
     */
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String DATE = "2011-12-24T12:24:25.203";

    /**
     * Properties bound to ProgramRuleVariable
     */


    private static final String CONDITION = "test_condition";
    private static final Integer PRIORITY = 1; // true
    private static final String PROGRAM_STAGE = "test_programStage";

    public static ContentValues createWithProgramStage(long id,
                                                       String uid,
                                                       String programUid,
                                                       String programStageUid) {
        ContentValues programRule = new ContentValues();
        programRule.put(ProgramRuleModel.Columns.ID, id);
        programRule.put(ProgramRuleModel.Columns.UID, uid);
        programRule.put(ProgramRuleModel.Columns.CODE, CODE);
        programRule.put(ProgramRuleModel.Columns.NAME, NAME);
        programRule.put(ProgramRuleModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRule.put(ProgramRuleModel.Columns.CREATED, DATE);
        programRule.put(ProgramRuleModel.Columns.LAST_UPDATED, DATE);
        programRule.put(ProgramRuleModel.Columns.CONDITION, CONDITION);
        programRule.put(ProgramRuleModel.Columns.PRIORITY, PRIORITY);
        programRule.put(ProgramRuleModel.Columns.PROGRAM, programUid);
        programRule.put(ProgramRuleModel.Columns.PROGRAM_STAGE, programStageUid);
        return programRule;
    }

    public static ContentValues createWithoutProgramStage(long id, String uid, String programUid) {
        ContentValues programRule = new ContentValues();
        programRule.put(ProgramRuleModel.Columns.ID, id);
        programRule.put(ProgramRuleModel.Columns.UID, uid);
        programRule.put(ProgramRuleModel.Columns.CODE, CODE);
        programRule.put(ProgramRuleModel.Columns.NAME, NAME);
        programRule.put(ProgramRuleModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRule.put(ProgramRuleModel.Columns.CREATED, DATE);
        programRule.put(ProgramRuleModel.Columns.LAST_UPDATED, DATE);
        programRule.put(ProgramRuleModel.Columns.CONDITION, CONDITION);
        programRule.put(ProgramRuleModel.Columns.PRIORITY, PRIORITY);
        programRule.put(ProgramRuleModel.Columns.PROGRAM, programUid);
        programRule.putNull(ProgramRuleModel.Columns.PROGRAM_STAGE);
        return programRule;
    }
}
