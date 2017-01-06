package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramStageContract.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramStageModelStoreIntegrationTest extends AbsStoreTestCase {
    public static final String[] PROGRAM_STAGE_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME, Columns.DISPLAY_NAME,
            Columns.CREATED, Columns.LAST_UPDATED, Columns.EXECUTION_DATE_LABEL,
            Columns.ALLOW_GENERATE_NEXT_VISIT, Columns.VALID_COMPLETE_ONLY,
            Columns.REPORT_DATE_TO_USE, Columns.OPEN_AFTER_ENROLLMENT,
            Columns.REPEATABLE, Columns.CAPTURE_COORDINATES, Columns.FORM_TYPE,
            Columns.DISPLAY_GENERATE_EVENT_BOX, Columns.GENERATED_BY_ENROLMENT_DATE,
            Columns.AUTO_GENERATE_EVENT, Columns.SORT_ORDER, Columns.HIDE_DUE_DATE,
            Columns.BLOCK_ENTRY_FORM, Columns.MIN_DAYS_FROM_START,
            Columns.STANDARD_INTERVAL, Columns.PROGRAM
    };

    private ProgramStageStore programStageStore;

    private static final long ID = 3L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String EXECUTION_DATE_LABEL = "test_executionDateLabel";
    private static final Boolean ALLOW_GENERATE_NEXT_VISIT = Boolean.FALSE;
    private static final Boolean VALID_COMPLETE_ONLY = Boolean.FALSE;
    private static final String REPORT_DATE_TO_USE = "test_reportDateToUse";
    private static final Boolean OPEN_AFTER_ENROLLMENT = Boolean.FALSE;
    private static final Boolean REPEATABLE = Boolean.TRUE;
    private static final Boolean CAPTURE_COORDINATES = Boolean.TRUE;
    private static final FormType FORM_TYPE = FormType.CUSTOM;
    private static final Boolean DISPLAY_GENERATE_EVENT_BOX = Boolean.FALSE;
    private static final Boolean GENERATED_BY_ENROLMENT_DATE = Boolean.TRUE;
    private static final Boolean AUTO_GENERATE_EVENT = Boolean.FALSE;
    private static final Integer SORT_ORDER = 0;
    private static final Boolean HIDE_DUE_DATE = Boolean.TRUE;
    private static final Boolean BLOCK_ENTRY_FORM = Boolean.FALSE;
    private static final Integer MIN_DAYS_FROM_START = 2;
    private static final Integer STANDARD_INTERVAL = 3;
    private static final String PROGRAM = "test_program";


    // timestamp
    private static final String DATE = "2017-01-05T10:40:00.000";

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        programStageStore = new ProgramStageStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws ParseException {
        // inserting necessary foreign key

        ContentValues program = ProgramModelIntegrationTest.create(ID, PROGRAM);
        database().insert(Tables.PROGRAM, null, program);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programStageStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, EXECUTION_DATE_LABEL, ALLOW_GENERATE_NEXT_VISIT,
                VALID_COMPLETE_ONLY, REPORT_DATE_TO_USE, OPEN_AFTER_ENROLLMENT,
                REPEATABLE, CAPTURE_COORDINATES, FORM_TYPE, DISPLAY_GENERATE_EVENT_BOX,
                GENERATED_BY_ENROLMENT_DATE, AUTO_GENERATE_EVENT, SORT_ORDER,
                HIDE_DUE_DATE, BLOCK_ENTRY_FORM, MIN_DAYS_FROM_START, STANDARD_INTERVAL,
                PROGRAM
        );
        Cursor cursor = database().query(Tables.PROGRAM_STAGE, PROGRAM_STAGE_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                EXECUTION_DATE_LABEL,
                0, // ALLOW_GENERATE_NEXT_VISIT = Boolean.FALSE
                0, // VALID_COMPLETE_ONLY = Boolean.FALSE
                REPORT_DATE_TO_USE,
                0, // OPEN_AFTER_ENROLLMENT = Boolean.FALSE
                1, // REPEATABLE = Boolean.TRUE
                1, // CAPTURE_COORDINATES = Boolean.TRUE
                FORM_TYPE,
                0, // DISPLAY_GENERATE_EVENT_BOX = Boolean.FALSE
                1, // GENERATED_BY_ENROLMENT_DATE = Boolean.TRUE
                0, // AUTO_GENERATE_EVENT = Boolean.FALSE
                SORT_ORDER,
                1, // HIDE_DUE_DATE = Boolean.TRUE
                0, // BLOCK_ENTRY_FORM = Boolean.FALSE
                MIN_DAYS_FROM_START,
                STANDARD_INTERVAL,
                PROGRAM
        ).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void exception_shouldPersistProgramStageInDatabaseWithoutProgram() throws ParseException {

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programStageStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, EXECUTION_DATE_LABEL, ALLOW_GENERATE_NEXT_VISIT,
                VALID_COMPLETE_ONLY, REPORT_DATE_TO_USE, OPEN_AFTER_ENROLLMENT,
                REPEATABLE, CAPTURE_COORDINATES, FORM_TYPE, DISPLAY_GENERATE_EVENT_BOX,
                GENERATED_BY_ENROLMENT_DATE, AUTO_GENERATE_EVENT, SORT_ORDER,
                HIDE_DUE_DATE, BLOCK_ENTRY_FORM, MIN_DAYS_FROM_START, STANDARD_INTERVAL,
                null
        );
        Cursor cursor = database().query(Tables.PROGRAM_STAGE, PROGRAM_STAGE_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                EXECUTION_DATE_LABEL,
                0, // ALLOW_GENERATE_NEXT_VISIT = Boolean.FALSE
                0, // VALID_COMPLETE_ONLY = Boolean.FALSE
                REPORT_DATE_TO_USE,
                0, // OPEN_AFTER_ENROLLMENT = Boolean.FALSE
                1, // REPEATABLE = Boolean.TRUE
                1, // CAPTURE_COORDINATES = Boolean.TRUE
                FORM_TYPE,
                0, // DISPLAY_GENERATE_EVENT_BOX = Boolean.FALSE
                1, // GENERATED_BY_ENROLMENT_DATE = Boolean.TRUE
                0, // AUTO_GENERATE_EVENT = Boolean.FALSE
                SORT_ORDER,
                1, // HIDE_DUE_DATE = Boolean.TRUE
                0, // BLOCK_ENTRY_FORM = Boolean.FALSE
                MIN_DAYS_FROM_START,
                STANDARD_INTERVAL,
                null
        ).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        programStageStore.close();

        assertThat(database().isOpen()).isTrue();
    }

}
