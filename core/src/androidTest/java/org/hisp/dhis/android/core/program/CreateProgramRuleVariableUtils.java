package org.hisp.dhis.android.core.program;

import android.content.ContentValues;

public class CreateProgramRuleVariableUtils {

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

    private static final String PROGRAM_STAGE = "test_programStage";
    private static final ProgramRuleVariableSourceType PROGRAM_RULE_VARIABLE_SOURCE_TYPE =
            ProgramRuleVariableSourceType.CALCULATED_VALUE;

    private static final Integer USE_CODE_FOR_OPTION_SET = 1; // true
    private static final String PROGRAM = "test_program";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";


    public static ContentValues create(long id, String uid) {
        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(ProgramRuleVariableContract.Columns.ID, id);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.UID, uid);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.CODE, CODE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.NAME, NAME);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.CREATED, DATE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.LAST_UPDATED, DATE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.USE_CODE_FOR_OPTION_SET, USE_CODE_FOR_OPTION_SET);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.DATA_ELEMENT, DATA_ELEMENT);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE, PROGRAM_RULE_VARIABLE_SOURCE_TYPE.name());
        return programRuleVariable;
    }
}
