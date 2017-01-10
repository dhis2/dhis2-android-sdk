package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.program.ProgramRuleVariableContract.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleVariableModelIntegrationTest {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String PROGRAM_STAGE = "test_programStage";
    private static final ProgramRuleVariableSourceType PROGRAM_RULE_VARIABLE_SOURCE_TYPE =
            ProgramRuleVariableSourceType.CALCULATED_VALUE;

    private static final Integer USE_CODE_FOR_OPTION_SET = 1; // true
    private static final String PROGRAM = "test_program";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";

    // used for timestamps
    private static final String DATE = "2017-01-09T13:28:00.000";

    public static ContentValues create(long id, String uid) {
        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(Columns.ID, id);
        programRuleVariable.put(Columns.UID, uid);
        programRuleVariable.put(Columns.CODE, CODE);
        programRuleVariable.put(Columns.NAME, NAME);
        programRuleVariable.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRuleVariable.put(Columns.CREATED, DATE);
        programRuleVariable.put(Columns.LAST_UPDATED, DATE);
        programRuleVariable.put(Columns.USE_CODE_FOR_OPTION_SET, USE_CODE_FOR_OPTION_SET);
        programRuleVariable.put(Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        programRuleVariable.put(Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);
        programRuleVariable.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        programRuleVariable.put(Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE, PROGRAM_RULE_VARIABLE_SOURCE_TYPE.name());
        return programRuleVariable;
    }

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.UID, Columns.CODE, Columns.NAME, Columns.DISPLAY_NAME,
                Columns.CREATED, Columns.LAST_UPDATED, Columns.USE_CODE_FOR_OPTION_SET,
                Columns.PROGRAM, Columns.PROGRAM_STAGE, Columns.TRACKED_ENTITY_ATTRIBUTE,
                Columns.DATA_ELEMENT, Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                USE_CODE_FOR_OPTION_SET, PROGRAM, PROGRAM_STAGE, TRACKED_ENTITY_ATTRIBUTE,
                DATA_ELEMENT, PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        });

        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramRuleVariableModel programRuleVariable = ProgramRuleVariableModel.create(matrixCursor);

        assertThat(programRuleVariable.id()).isEqualTo(ID);
        assertThat(programRuleVariable.uid()).isEqualTo(UID);
        assertThat(programRuleVariable.code()).isEqualTo(CODE);
        assertThat(programRuleVariable.name()).isEqualTo(NAME);
        assertThat(programRuleVariable.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programRuleVariable.created()).isEqualTo(timeStamp);
        assertThat(programRuleVariable.lastUpdated()).isEqualTo(timeStamp);
        assertThat(programRuleVariable.useCodeForOptionSet()).isTrue();
        assertThat(programRuleVariable.program()).isEqualTo(PROGRAM);
        assertThat(programRuleVariable.programStage()).isEqualTo(PROGRAM_STAGE);
        assertThat(programRuleVariable.trackedEntityAttribute()).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(programRuleVariable.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(programRuleVariable.programRuleVariableSourceType()).isEqualTo(PROGRAM_RULE_VARIABLE_SOURCE_TYPE);
    }

    @Test
    public void create_shouldConvertToContentValues() throws Exception {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramRuleVariableModel programRuleVariable = ProgramRuleVariableModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .useCodeForOptionSet(Boolean.TRUE)
                .program(PROGRAM)
                .programStage(PROGRAM_STAGE)
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE)
                .dataElement(DATA_ELEMENT)
                .programRuleVariableSourceType(PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
                .build();

        ContentValues contentValues = programRuleVariable.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsBoolean(Columns.USE_CODE_FOR_OPTION_SET)).isTrue();
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_ATTRIBUTE)).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(contentValues.getAsString(Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
        assertThat(contentValues.getAsString(Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE))
                .isEqualTo(PROGRAM_RULE_VARIABLE_SOURCE_TYPE.name());

    }
}
