package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.dataelement.DataElementModelIntegrationTest;
import org.hisp.dhis.android.core.program.ProgramRuleVariableContract.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleVariableModelStoreIntegrationTest extends AbsStoreTestCase {
    private static final long ID = 2L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2017-01-09T14:48:00.000";

    private static final Boolean USE_CODE_FOR_OPTION_SET = Boolean.TRUE;
    private static final String PROGRAM = "test_program";
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";
    private static final String DATA_ELEMENT = "test_dataElement";

    private static final ProgramRuleVariableSourceType PROGRAM_RULE_VARIABLE_SOURCE_TYPE =
            ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM;

    private static final String[] PROGRAM_RULE_VARIABLE_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.USE_CODE_FOR_OPTION_SET,
            Columns.PROGRAM,
            Columns.PROGRAM_STAGE,
            Columns.DATA_ELEMENT,
            Columns.TRACKED_ENTITY_ATTRIBUTE,
            Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE
    };

    private ProgramRuleVariableModelStore programRuleVariableModelStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        programRuleVariableModelStore = new ProgramRuleVariableModelStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramRuleVariableInDatabase() throws Exception {
        // inserting necessary foreign key

        ContentValues program = ProgramModelIntegrationTest.create(ID, PROGRAM);
        database().insert(Tables.PROGRAM, null, program);

        ContentValues programStage = ProgramStageModelIntegrationTest.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(Tables.PROGRAM_STAGE, null, programStage);

        ContentValues dataElement = DataElementModelIntegrationTest.createWithoutOptionSet(ID, DATA_ELEMENT);
        database().insert(Tables.DATA_ELEMENT, null, dataElement);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleVariableModelStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                USE_CODE_FOR_OPTION_SET,
                PROGRAM,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        );

        Cursor cursor = database().query(Tables.PROGRAM_RULE_VARIABLE, PROGRAM_RULE_VARIABLE_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE,
                DATE,
                1, // USE_CODE_FOR_OPTION_SET = Boolean.TRUE
                PROGRAM,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        ).isExhausted();

    }

    @Test
    public void insert_shouldPersistProgramRuleVariableInDatabaseWithProgramForeignKey() throws Exception {
        ContentValues program = ProgramModelIntegrationTest.create(ID, PROGRAM);
        database().insert(Tables.PROGRAM, null, program);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleVariableModelStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                USE_CODE_FOR_OPTION_SET,
                PROGRAM,
                null,
                null,
                null,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        );

        Cursor cursor = database().query(Tables.PROGRAM_RULE_VARIABLE, PROGRAM_RULE_VARIABLE_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE,
                DATE,
                1, // USE_CODE_FOR_OPTION_SET = Boolean.TRUE
                PROGRAM,
                null,
                null,
                null,
                PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        ).isExhausted();

    }

    @Test
    public void close_shouldNotCloseDatabase() throws Exception {
        programRuleVariableModelStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
