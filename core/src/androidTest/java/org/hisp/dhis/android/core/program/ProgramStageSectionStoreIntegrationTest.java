package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramStageSectionContract.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramStageSectionStoreIntegrationTest extends AbsStoreTestCase {
    private static final long ID = 2L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final Integer SORT_ORDER = 7;
    private static final String PROGRAM_STAGE = "test_program_stage";

    // timestamp
    private static final String DATE = "2017-01-05T10:40:00.000";

    // nested foreign key
    private static final String PROGRAM = "test_program";

    private static final String[] PROGRAM_STAGE_SECTION_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.SORT_ORDER,
            Columns.PROGRAM_STAGE
    };

    private ProgramStageSectionStore programStageSectionStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.programStageSectionStore = new ProgramStageSectionStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramStageSectionInDatabase() throws Exception {
        // inserting necessary foreign key

        ContentValues program = CreateUtils.createProgram(ID, PROGRAM);
        database().insert(Tables.PROGRAM, null, program);

        ContentValues programStage = ProgramStageModelIntegrationTest.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(Tables.PROGRAM_STAGE, null, programStage);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programStageSectionStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, SORT_ORDER,
                PROGRAM_STAGE
        );

        Cursor cursor = database().query(Tables.PROGRAM_STAGE_SECTION, PROGRAM_STAGE_SECTION_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME, DISPLAY_NAME,
                DATE, DATE, SORT_ORDER, PROGRAM_STAGE
        ).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() throws Exception {
        programStageSectionStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
