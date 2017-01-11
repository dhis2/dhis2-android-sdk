package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramRuleModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleModelStoreIntegrationTest extends AbsStoreTestCase {

    private static final String[] PROGRAM_RULE_MODEL_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME, Columns.DISPLAY_NAME,
            Columns.CREATED, Columns.LAST_UPDATED, Columns.PRIORITY, Columns.CONDITION,
            Columns.PROGRAM, Columns.PROGRAM_STAGE
    };

    private ProgramRuleModelStore programRuleStore;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2017-01-11T13:48:00.000";


    // bound to Program Rule
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String PROGRAM = "test_program";
    private static final Integer PRIORITY = 2;
    private static final String CONDITION = "test_condition";


    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.programRuleStore = new ProgramRuleModelStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramRuleInDatabase() throws ParseException {
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM);
        database().insert(Tables.PROGRAM, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);
        database().insert(Tables.PROGRAM_STAGE, null, programStage);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, PRIORITY,
                CONDITION, PROGRAM, PROGRAM_STAGE);

        Cursor cursor = database().query(Tables.PROGRAM_RULE, PROGRAM_RULE_MODEL_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID, CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                PRIORITY, CONDITION,
                PROGRAM, PROGRAM_STAGE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistProgramRuleInDatabaseWithoutProgramStageForeignKey() throws Exception {
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM);
        database().insert(Tables.PROGRAM, null, program);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, PRIORITY,
                CONDITION, PROGRAM, null);

        Cursor cursor = database().query(Tables.PROGRAM_RULE, PROGRAM_RULE_MODEL_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID, CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                PRIORITY, CONDITION,
                PROGRAM, null
        ).isExhausted();

    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_shouldNotPersistProgramRuleInDatabaseWithoutProgram() throws Exception {

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, PRIORITY,
                CONDITION, null, null);

        Cursor cursor = database().query(Tables.PROGRAM_RULE, PROGRAM_RULE_MODEL_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID, CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                PRIORITY, CONDITION,
                null, null // Program and programStage == null
        ).isExhausted();

    }

    @Test
    public void close_shouldNotCloseDatabase() {
        programRuleStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
