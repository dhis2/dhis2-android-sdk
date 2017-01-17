package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.option.CreateOptionSetUtils;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
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

    private static final String PROGRAM = "test_program";
    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    private static final String[] PROGRAM_STAGE_DATA_ELEMENT_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.DISPLAY_IN_REPORTS,
            Columns.COMPULSORY,
            Columns.ALLOW_PROVIDED_ELSEWHERE,
            Columns.SORT_ORDER,
            Columns.ALLOW_FUTURE_DATE,
            Columns.DATA_ELEMENT,
            Columns.PROGRAM_STAGE_SECTION
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
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
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
    public void delete_shouldDeleteProgramStageDataElementWhenDeletingDataElement() throws Exception {
        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(Tables.DATA_ELEMENT, null, dataElement);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        database().insert(Tables.PROGRAM_STAGE_DATA_ELEMENT, null, programStageDataElement);

        String[] projection = {Columns.ID, Columns.UID, Columns.DATA_ELEMENT};

        Cursor cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, projection, null, null, null, null, null);
        // Checking that programStageDataElement was inserted
        assertThatCursor(cursor).hasRow(ID, UID, DATA_ELEMENT).isExhausted();

        // deleting data element
        database().delete(Tables.DATA_ELEMENT, DataElementModel.Columns.UID + "=?", new String[]{DATA_ELEMENT});

        cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, projection, null, null, null, null, null);

        // program stage data element should now be deleted when foreign key was deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteProgramStageDataElementWhenDeletingOptionSetNestedForeignKey() throws Exception {
        ContentValues optionSet = CreateOptionSetUtils.create(ID, OPTION_SET);
        database().insert(Tables.OPTION_SET, null, optionSet);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, OPTION_SET);
        database().insert(Tables.DATA_ELEMENT, null, dataElement);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        database().insert(Tables.PROGRAM_STAGE_DATA_ELEMENT, null, programStageDataElement);

        String[] projection = {Columns.ID, Columns.UID, Columns.DATA_ELEMENT};

        Cursor cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, projection, null, null, null, null, null);
        // Checking that programStageDataElement was inserted
        assertThatCursor(cursor).hasRow(ID, UID, DATA_ELEMENT).isExhausted();

        // deleting optionSet
        database().delete(Tables.OPTION_SET, OptionSetModel.Columns.UID + "=?", new String[]{OPTION_SET});

        cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, projection, null, null, null, null, null);

        // program stage data element should now be deleted when nested foreign key was deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteProgramStageDataElementWhenDeletingProgramStageSection() throws Exception {
        String programStageUid = "test_programStageUid";

        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, TRACKED_ENTITY_UID);

        database().insert(DbOpenHelper.Tables.TRACKED_ENTITY, null, trackedEntity);
        database().insert(DbOpenHelper.Tables.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(DbOpenHelper.Tables.PROGRAM, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, programStageUid, PROGRAM);
        database().insert(Tables.PROGRAM_STAGE, null, programStage);

        ContentValues programStageSection =
                CreateProgramStageSectionUtils.create(ID, PROGRAM_STAGE_SECTION, programStageUid);

        database().insert(Tables.PROGRAM_STAGE_SECTION, null, programStageSection);

        ContentValues dataElement = CreateDataElementUtils.create(ID, DATA_ELEMENT, null);
        database().insert(Tables.DATA_ELEMENT, null, dataElement);

        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, ID);
        programStageDataElement.put(Columns.UID, UID);
        programStageDataElement.put(Columns.PROGRAM_STAGE_SECTION, PROGRAM_STAGE_SECTION);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);

        database().insert(Tables.PROGRAM_STAGE_DATA_ELEMENT, null, programStageDataElement);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM_STAGE_SECTION, Columns.DATA_ELEMENT};

        Cursor cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, projection, null, null, null, null, null);
        // Checking that programStageDataElement was inserted
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM_STAGE_SECTION, DATA_ELEMENT).isExhausted();

        // deleting referenced program stage section
        database().delete(
                Tables.PROGRAM_STAGE_SECTION,
                ProgramStageSectionModel.Columns.UID + "=?", new String[]{PROGRAM_STAGE_SECTION});

        cursor = database().query(Tables.PROGRAM_STAGE_DATA_ELEMENT, projection, null, null, null, null, null);

        // checking that program stage data element is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() throws Exception {
        programStageDataElementStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
