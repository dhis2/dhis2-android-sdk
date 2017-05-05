package org.hisp.dhis.android.core.program;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkModel.Columns;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class ProgramStageSectionProgramIndicatorLinkStoreTests extends AbsStoreTestCase {
    private static final String[] PROJECTION = {Columns.PROGRAM_STAGE_SECTION, Columns.PROGRAM_INDICATOR};
    private static final String PROGRAM_STAGE_SECTION = "programStageSection_uid";
    private static final String PROGRAM_INDICATOR = "programIndicator_uid";
    private static final String PROGRAM = "program_uid";
    private static final String TRACKED_ENTITY_UID = "tracked_entity_uid";
    private static final String RELATIONSHIP_TYPE_UID = "relationship_uid";
    public static final String PROGRAM_STAGE = "programStage_uid";
    public static final String UPDATED_PROGRAM_STAGE_SECTION = "updated_ProgramStageSection_uid";
    public static final String UPDATED_PROGRAM_INDICATOR = "updated_ProgramIndicator_uid";

    private ProgramStageSectionProgramIndicatorLinkStore store;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.store = new ProgramStageSectionProgramIndicatorLinkStoreImpl(databaseAdapter());

        //prerequisites for ProgramStageSection:
        database().insert(TrackedEntityModel.TABLE, null,
                CreateTrackedEntityUtils.create(1L, TRACKED_ENTITY_UID));
        database().insert(RelationshipTypeModel.TABLE, null,
                CreateRelationshipTypeUtils.create(1L, RELATIONSHIP_TYPE_UID));

        //for both ProgramStageSection and ProgramIndicator:
        database().insert(ProgramModel.TABLE, null,
                CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID));
        database().insert(ProgramStageModel.TABLE, null, CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM));

        //insert direct foreign keys for ProgramStageSectionProgramIndicatorLink Table:
        database().insert(ProgramStageSectionModel.TABLE, null,
                CreateProgramStageSectionUtils.create(1L, PROGRAM_STAGE_SECTION, PROGRAM_STAGE));
        database().insert(ProgramIndicatorModel.TABLE, null,
                CreateProgramIndicatorUtils.create(1L, PROGRAM_INDICATOR, PROGRAM));
    }

    @Test
    public void insert() {
        store.insert(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR);

        Cursor cursor = database().query(ProgramStageSectionProgramIndicatorLinkModel.TABLE, PROJECTION,
                null, null, null, null, null);

        assertThatCursor(cursor).hasRow(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR).isExhausted();
    }

    @Test
    public void insert_deferred() {
        String deferredProgramStageSection = "deferredProgramStageSection";
        String deferredProgramIndicator = "deferredProgramIndicator";

        Transaction transaction = databaseAdapter().beginNewTransaction();
        store.insert(deferredProgramStageSection, deferredProgramIndicator);

        database().insert(ProgramStageSectionModel.TABLE, null,
                CreateProgramStageSectionUtils.create(2L, deferredProgramStageSection, PROGRAM_STAGE));
        database().insert(ProgramIndicatorModel.TABLE, null,
                CreateProgramIndicatorUtils.create(2L, deferredProgramIndicator, PROGRAM));
        transaction.setSuccessful();
        transaction.end();

        Cursor cursor = database().query(ProgramStageSectionProgramIndicatorLinkModel.TABLE, PROJECTION,
                null, null, null, null, null);

        assertThatCursor(cursor).hasRow(deferredProgramStageSection, deferredProgramIndicator).isExhausted();

    }

    @Test (expected = SQLiteConstraintException.class)
    public void insert_wrong_ProgramStageSection() {
        store.insert("wrong", PROGRAM_INDICATOR);
    }

    @Test (expected = SQLiteConstraintException.class)
    public void insert_wrong_DataElement() {
        store.insert("wrong", PROGRAM_INDICATOR);
    }

    @Test
    public void update() {
       //insert foreign keys for the update:
        database().insert(ProgramStageSectionModel.TABLE, null,
                CreateProgramStageSectionUtils.create(2L, UPDATED_PROGRAM_STAGE_SECTION, PROGRAM_STAGE));
        database().insert(ProgramIndicatorModel.TABLE, null, CreateProgramIndicatorUtils.create(2L,
                UPDATED_PROGRAM_INDICATOR, PROGRAM));

        //insert old values:
        database().insert(ProgramStageSectionProgramIndicatorLinkModel.TABLE, null,
                CreateProgramStageSectionProgramIndicatorLinkUtils.create(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR));

        int ret = store.update(UPDATED_PROGRAM_STAGE_SECTION, UPDATED_PROGRAM_INDICATOR,
                PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR );

        Cursor cursor = database().query(ProgramStageSectionProgramIndicatorLinkModel.TABLE, PROJECTION,
                null, null, null, null, null);

        assertThat(ret).isEqualTo(1);
        assertThatCursor(cursor).hasRow(UPDATED_PROGRAM_STAGE_SECTION, UPDATED_PROGRAM_INDICATOR).isExhausted();
    }

    @Test
    public void update_non_existing() {
        int ret = store.update(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR,
                UPDATED_PROGRAM_STAGE_SECTION, UPDATED_PROGRAM_INDICATOR);

        Cursor cursor = database().query(ProgramStageSectionProgramIndicatorLinkModel.TABLE, PROJECTION, null, null,
                null, null, null);

        assertThat(ret).isEqualTo(0);
        assertThatCursor(cursor).isExhausted(); //ie: no changes to database
    }

    @Test (expected = SQLiteConstraintException.class)
    public void update_wrong_ProgramStageSection() {
        //insert foreign keys for the update:
        database().insert(ProgramStageSectionModel.TABLE, null,
                CreateProgramStageSectionUtils.create(2L, UPDATED_PROGRAM_STAGE_SECTION, PROGRAM_STAGE));
        database().insert(ProgramIndicatorModel.TABLE, null, CreateProgramIndicatorUtils.create(2L,
                UPDATED_PROGRAM_INDICATOR, PROGRAM));

        //insert old values:
        database().insert(ProgramStageSectionProgramIndicatorLinkModel.TABLE, null,
                CreateProgramStageSectionProgramIndicatorLinkUtils.create(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR));

        int ret = store.update("wrong", UPDATED_PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR );
    }

    @Test (expected = SQLiteConstraintException.class)
    public void update_wrong_DataElement() {
        //insert foreign keys for the update:
        database().insert(ProgramStageSectionModel.TABLE, null,
                CreateProgramStageSectionUtils.create(2L, UPDATED_PROGRAM_STAGE_SECTION, PROGRAM_STAGE));
        database().insert(ProgramIndicatorModel.TABLE, null, CreateProgramIndicatorUtils.create(2L,
                UPDATED_PROGRAM_INDICATOR, PROGRAM));

        //insert old values:
        database().insert(ProgramStageSectionProgramIndicatorLinkModel.TABLE, null,
                CreateProgramStageSectionProgramIndicatorLinkUtils.create(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR));

        int ret = store.update(UPDATED_PROGRAM_STAGE_SECTION, "wrong", PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR );
    }

    @Test
    public void update_wrong_whereProgramStageSection() {
        //insert foreign keys for the update:
        database().insert(ProgramStageSectionModel.TABLE, null,
                CreateProgramStageSectionUtils.create(2L, UPDATED_PROGRAM_STAGE_SECTION, PROGRAM_STAGE));
        database().insert(ProgramIndicatorModel.TABLE, null, CreateProgramIndicatorUtils.create(2L,
                UPDATED_PROGRAM_INDICATOR, PROGRAM));

        //insert old values:
        database().insert(ProgramStageSectionProgramIndicatorLinkModel.TABLE, null,
                CreateProgramStageSectionProgramIndicatorLinkUtils.create(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR));

        int ret = store.update(UPDATED_PROGRAM_STAGE_SECTION, UPDATED_PROGRAM_INDICATOR, "wrong", PROGRAM_INDICATOR );

        Cursor cursor = database().query(ProgramStageSectionProgramIndicatorLinkModel.TABLE, PROJECTION,
                null, null, null, null, null);

        assertThat(ret).isEqualTo(0);
        //ie: the update didn't change anything:
        assertThatCursor(cursor).hasRow(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR).isExhausted();
    }

    @Test
    public void update_wrong_where_programIndicator() {
        //insert foreign keys for the update:
        database().insert(ProgramStageSectionModel.TABLE, null,
                CreateProgramStageSectionUtils.create(2L, UPDATED_PROGRAM_STAGE_SECTION, PROGRAM_STAGE));
        database().insert(ProgramIndicatorModel.TABLE, null, CreateProgramIndicatorUtils.create(2L,
                UPDATED_PROGRAM_INDICATOR, PROGRAM));

        //insert old values:
        database().insert(ProgramStageSectionProgramIndicatorLinkModel.TABLE, null,
                CreateProgramStageSectionProgramIndicatorLinkUtils.create(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR));

        int ret = store.update(UPDATED_PROGRAM_STAGE_SECTION, UPDATED_PROGRAM_INDICATOR,
                PROGRAM_STAGE_SECTION, "wrong" );

        Cursor cursor = database().query(ProgramStageSectionProgramIndicatorLinkModel.TABLE, PROJECTION,
                null, null, null, null, null);

        assertThat(ret).isEqualTo(0);
        //ie: the update didn't change anything:
        assertThatCursor(cursor).hasRow(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR).isExhausted();

    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_null_programStageSection() {
        store.insert(null, PROGRAM_INDICATOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_null_ProgramInidcator() {
        store.insert(PROGRAM_STAGE_SECTION, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_programStageSection() {
        store.update(null, PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_ProgramInidcator() {
        store.update(PROGRAM_STAGE_SECTION, null, PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_whereProgramStageSection() {
        store.update(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR, null, PROGRAM_INDICATOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_whereProgramInidcator() {
        store.update(PROGRAM_STAGE_SECTION, PROGRAM_INDICATOR, PROGRAM_STAGE_SECTION, null);
    }
}
