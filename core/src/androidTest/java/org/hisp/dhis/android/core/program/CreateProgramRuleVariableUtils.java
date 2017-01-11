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
        programRuleVariable.put(ProgramRuleVariableModel.Columns.ID, id);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.UID, uid);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.CODE, CODE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.NAME, NAME);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.CREATED, DATE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.LAST_UPDATED, DATE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.USE_CODE_FOR_OPTION_SET, USE_CODE_FOR_OPTION_SET);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.DATA_ELEMENT, DATA_ELEMENT);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE, PROGRAM_RULE_VARIABLE_SOURCE_TYPE.name());
        return programRuleVariable;
    }
}
