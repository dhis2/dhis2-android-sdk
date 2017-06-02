package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLinkModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;
//TODO CLean this mess up
public class ProgramStageSectionDataElementLinkStoreTests extends AbsStoreTestCase {
//    public static final long ID = 1L;
//    public static final String PROGRAM_STAGE_SECTION = "test_program_stage_section";
//    public static final String DATA_ELEMENT = "test_data_element";
//    private static final String PROGRAM_STAGE = "test_program_stage";
//    // nested foreign key
//    private static final String PROGRAM = "test_program";
//    //foreign keys to program:
//    private static final long TRACKED_ENTITY_ID = 1L;
//    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
//    private static final long RELATIONSHIP_TYPE_ID = 1L;
//    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";
//
//    public static final String[] PROJECTION = {Columns.PROGRAM_STAGE_SECTION, Columns.DATA_ELEMENT};
//
//    private ProgramStageSectionDataElementLinkStore store;
//
//    @Override
//    public void setUp() throws IOException {
//        super.setUp();
//        store = new ProgramStageSectionDataElementLinkStoreImpl(databaseAdapter());
//
//        //ProgramStageSection requires ProgramStage, ProgramStage requires Program and Program requires
//        // relationshipType and TrackedEntity. thus all of these need to be present in order for the rows to be
//        // inserted in the tables properly (These are set as NON NULL Foreign keys in the dbOpenHelper)
//        //Create Program & insert a row in the table.
//        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
//        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
//                RELATIONSHIP_TYPE_UID);
//        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
//        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
//        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
//        database().insert(ProgramModel.TABLE, null, program);
//        //Create ProgramStage & insert a row in the table:
//        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
//        database().insert(ProgramStageModel.TABLE, null, programStage);
//        //Insert ProgramStageSection:
//        database().insert(ProgramStageSectionModel.TABLE, null,
//                CreateProgramStageSectionUtils.create(1L, PROGRAM_STAGE_SECTION, PROGRAM_STAGE));
//        //Insert DataElement:
//        database().insert(DataElementModel.TABLE, null, CreateDataElementUtils.create(1L, DATA_ELEMENT, null));
//
//    }
//
//    @Test
//    public void insert() {
//        long rowId = store.insert(PROGRAM_STAGE_SECTION, DATA_ELEMENT);
//        Cursor cursor = database().query(ProgramStageSectionDataElementLinkModel.TABLE, PROJECTION,
//                null, null, null, null, null);
//
//        assertThat(rowId).isEqualTo(1L);
//        assertThatCursor(cursor).hasRow(PROGRAM_STAGE_SECTION, DATA_ELEMENT).isExhausted();
//
//    }
//
//    @Test
//    public void insert_deferrable() {
//        final String deferrableProgramStageSection = "deferrable_programStageSection";
//        final String deferrableDataElement = "deferrable_data_element";
//        Transaction transaction = databaseAdapter().beginNewTransaction();
//        long rowId = store.insert(deferrableProgramStageSection, deferrableDataElement);
//        database().insert(ProgramStageSectionModel.TABLE, null, CreateProgramStageSectionUtils.create(2L,
//                deferrableProgramStageSection, PROGRAM_STAGE));
//        database().insert(DataElementModel.TABLE, null, CreateDataElementUtils.create(2L, deferrableDataElement, null));
//        transaction.setSuccessful();
//        transaction.end();
//
//        Cursor cursor = database().query(ProgramStageSectionDataElementLinkModel.TABLE, PROJECTION, null, null, null,
//                null, null);
//        assertThat(rowId).isEqualTo(1L);
//        assertThatCursor(cursor).hasRow(deferrableProgramStageSection, deferrableDataElement).isExhausted();
//    }
//
//    @Test(expected = SQLiteConstraintException.class)
//    public void insert_wrong_programStageSection() {
//        store.insert("wrong", DATA_ELEMENT);
//    }
//
//    @Test(expected = SQLiteConstraintException.class)
//    public void insert_wrong_dataElement() {
//        store.insert(PROGRAM_STAGE_SECTION, "wrong");
//    }
//
//    @Test
//    public void update_ProgramStageSection() {
//        final String UPDATED_UID = "updatedUid";
//        database().insert(ProgramStageSectionModel.TABLE, null,
//                CreateProgramStageSectionUtils.create(2L, UPDATED_UID, PROGRAM_STAGE));
//        ContentValues model = new ContentValues();
//        model.put(Columns.PROGRAM_STAGE_SECTION, PROGRAM_STAGE_SECTION);
//        model.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
//        database().insert(ProgramStageSectionDataElementLinkModel.TABLE, null, model);
//
//        int returnValue = store.update(UPDATED_UID, DATA_ELEMENT, PROGRAM_STAGE_SECTION, DATA_ELEMENT);
//
//        Cursor cursor = database().query(ProgramStageSectionDataElementLinkModel.TABLE, PROJECTION, null, null, null,
//                null, null, null);
//        assertThat(returnValue).isEqualTo(1);
//        assertThatCursor(cursor).hasRow(UPDATED_UID, DATA_ELEMENT);
//    }
//
//    @Test
//    public void update_DataElement() {
//        final String UPDATED_UID = "updatedUid";
//        database().insert(DataElementModel.TABLE, null, CreateDataElementUtils.create(2L, UPDATED_UID, null));
//        ContentValues model = new ContentValues();
//        model.put(Columns.PROGRAM_STAGE_SECTION, PROGRAM_STAGE_SECTION);
//        model.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
//        database().insert(ProgramStageSectionDataElementLinkModel.TABLE, null, model);
//
//        int returnValue = store.update(PROGRAM_STAGE_SECTION, UPDATED_UID, PROGRAM_STAGE_SECTION, DATA_ELEMENT);
//
//        Cursor cursor = database().query(ProgramStageSectionDataElementLinkModel.TABLE, PROJECTION, null, null, null,
//                null, null, null);
//        assertThat(returnValue).isEqualTo(1);
//        assertThatCursor(cursor).hasRow(PROGRAM_STAGE_SECTION, UPDATED_UID);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void insert_null_program() {
//        store.insert(null, DATA_ELEMENT);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void insert_null_dataElement() {
//        store.insert(PROGRAM, null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void update_null_program() {
//        store.update(null, DATA_ELEMENT, PROGRAM, DATA_ELEMENT);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void update_null_dataElement() {
//        store.update(PROGRAM, null, PROGRAM, DATA_ELEMENT);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void update_null_whereProgram() {
//        store.update(PROGRAM, DATA_ELEMENT, null, DATA_ELEMENT);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void update_null_whereDataElement() {
//        store.update(PROGRAM, DATA_ELEMENT, PROGRAM, null);
//    }
}