package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementModelIntegrationTest;
import org.hisp.dhis.android.core.option.OptionSetModelIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

//TODO: Add test when persisting with programStageSection foreign key
@RunWith(AndroidJUnit4.class)
public class ProgramStageDataElementStoreIntegrationTest extends AbsStoreTestCase {
    private static final long ID = 11L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final Boolean DISPLAY_IN_REPORTS = Boolean.TRUE;
    private static final Boolean COMPULSORY = Boolean.FALSE;
    private static final Boolean ALLOW_PROVIDED_ELSEWHERE = Boolean.FALSE;
    private static final Integer SORT_ORDER = 7;
    private static final Boolean ALLOW_FUTURE_DATE = Boolean.TRUE;
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String PROGRAM_STAGE_SECTION = "test_program_stage_section";

    // timestamp
    private static final String DATE = "2017-01-04T17:04:00.000";


    // Nested foreign key
    private static final String OPTION_SET = "test_optionSet";

    private static final String[] PROGRAM_STAGE_DATA_ELEMENT_PROJECTION = {
            ProgramStageDataElementModel.Columns.UID,
            ProgramStageDataElementModel.Columns.CODE,
            ProgramStageDataElementModel.Columns.NAME,
            ProgramStageDataElementModel.Columns.DISPLAY_NAME,
            ProgramStageDataElementModel.Columns.CREATED,
            ProgramStageDataElementModel.Columns.LAST_UPDATED,
            ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS,
            ProgramStageDataElementModel.Columns.COMPULSORY,
            ProgramStageDataElementModel.Columns.ALLOW_PROVIDED_ELSEWHERE,
            ProgramStageDataElementModel.Columns.SORT_ORDER,
            ProgramStageDataElementModel.Columns.ALLOW_FUTURE_DATE,
            ProgramStageDataElementModel.Columns.DATA_ELEMENT,
            ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION
    };

    private ProgramStageDataElementStore programStageDataElementStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.programStageDataElementStore = new ProgramStageDataElementStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramStageDataElementInDatabase() throws ParseException {
        // inserting necessary foreign key
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(Tables.DATA_ELEMENT, null, dataElement);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programStageDataElementStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                DISPLAY_IN_REPORTS,
                COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER,
                ALLOW_FUTURE_DATE,
                DATA_ELEMENT,
                null
        );

        Cursor cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, PROGRAM_STAGE_DATA_ELEMENT_PROJECTION,
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
                1, // DISPLAY_IN_REPORTS = Boolean.FALSE
                0, // COMPULSORY = Boolean.FALSE
                0, // ALLOW_PROVIDED_ELSEWHERE = Boolean.FALSE
                SORT_ORDER,
                1, // ALLOW_FUTURE_DATE = Boolean.TRUE
                DATA_ELEMENT,
                null
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistProgramStageDataElementInDatabaseWithOptionSet() throws Exception {
        // inserting necessary foreign key
        ContentValues optionSet = OptionSetModelIntegrationTest.create(ID, OPTION_SET);
        database().insert(Tables.OPTION_SET, null, optionSet);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, OPTION_SET);
        database().insert(Tables.DATA_ELEMENT, null, dataElement);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programStageDataElementStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                DISPLAY_IN_REPORTS,
                COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER,
                ALLOW_FUTURE_DATE,
                DATA_ELEMENT,
                null
        );

        Cursor cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, PROGRAM_STAGE_DATA_ELEMENT_PROJECTION,
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
                1, // DISPLAY_IN_REPORTS = Boolean.FALSE
                0, // COMPULSORY = Boolean.FALSE
                0, // ALLOW_PROVIDED_ELSEWHERE = Boolean.FALSE
                SORT_ORDER,
                1, // ALLOW_FUTURE_DATE = Boolean.TRUE
                DATA_ELEMENT,
                null
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistProgramStageDataElementWithInvalidForeignKey() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        String fakeDataElementId = "fake_data_element_id";
        long rowId = programStageDataElementStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                DISPLAY_IN_REPORTS,
                COMPULSORY,
                ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER,
                ALLOW_FUTURE_DATE,
                fakeDataElementId,
                null
        );

        assertThat(rowId).isEqualTo(-1);
    }

    @Test
    public void close_shouldNotCloseDatabase() throws Exception {
        programStageDataElementStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
