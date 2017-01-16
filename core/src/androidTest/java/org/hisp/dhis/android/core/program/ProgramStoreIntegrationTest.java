package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramStoreIntegrationTest extends AbsStoreTestCase {

    //BaseIdentifiableModel attributes:
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    //BaseNameableModel attributes:
    private static final String SHORT_NAME = "test_program";
    private static final String DISPLAY_SHORT_NAME = "test_prog";
    private static final String DESCRIPTION = "A test program for the integration tests.";
    private static final String DISPLAY_DESCRIPTION = "A test program for the integration tests.";

    //ProgramModel attributes:
    private static final Integer VERSION = 1;
    private static final Boolean ONLY_ENROLL_ONCE = true;
    private static final String ENROLLMENT_DATE_LABEL = "enrollment date";
    private static final Boolean DISPLAY_INCIDENT_DATE = true;
    private static final String INCIDENT_DATE_LABEL = "incident date label";
    private static final Boolean REGISTRATION = true;
    private static final Boolean SELECT_ENROLLMENT_DATES_IN_FUTURE = true;
    private static final Boolean DATA_ENTRY_METHOD = true;
    private static final Boolean IGNORE_OVERDUE_EVENTS = false;
    private static final Boolean RELATIONSHIP_FROM_A = true;
    private static final Boolean SELECT_INCIDENT_DATES_IN_FUTURE = true;
    private static final Boolean CAPTURE_COORDINATES = true;
    private static final Boolean USE_FIRST_STAGE_DURING_REGISTRATION = true;
    private static final Boolean DISPLAY_FRONT_PAGE_LIST = true;
    private static final ProgramType PROGRAM_TYPE = ProgramType.WITH_REGISTRATION;
    private static final Long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE = "relationshipUid";
    private static final String RELATIONSHIP_TEXT = "test relationship";
    private static final String RELATED_PROGRAM = "RelatedProgramUid";
    private static final Long TRACKED_ENTITY_ID = 4L;
    private static final String TRACKED_ENTITY = "TrackedEntityUid";

    private static final String[] PROGRAM_PROJECTION = {
            ProgramModel.Columns.UID,
            ProgramModel.Columns.CODE,
            ProgramModel.Columns.NAME,
            ProgramModel.Columns.DISPLAY_NAME,
            ProgramModel.Columns.CREATED,
            ProgramModel.Columns.LAST_UPDATED,
            ProgramModel.Columns.SHORT_NAME,
            ProgramModel.Columns.DISPLAY_SHORT_NAME,
            ProgramModel.Columns.DESCRIPTION,
            ProgramModel.Columns.DISPLAY_DESCRIPTION,
            ProgramModel.Columns.VERSION,
            ProgramModel.Columns.ONLY_ENROLL_ONCE,
            ProgramModel.Columns.ENROLLMENT_DATE_LABEL,
            ProgramModel.Columns.DISPLAY_INCIDENT_DATE,
            ProgramModel.Columns.INCIDENT_DATE_LABEL,
            ProgramModel.Columns.REGISTRATION,
            ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE,
            ProgramModel.Columns.DATA_ENTRY_METHOD,
            ProgramModel.Columns.IGNORE_OVERDUE_EVENTS,
            ProgramModel.Columns.RELATIONSHIP_FROM_A,
            ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE,
            ProgramModel.Columns.CAPTURE_COORDINATES,
            ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION,
            ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST,
            ProgramModel.Columns.PROGRAM_TYPE,
            ProgramModel.Columns.RELATIONSHIP_TYPE,
            ProgramModel.Columns.RELATIONSHIP_TEXT,
            ProgramModel.Columns.RELATED_PROGRAM,
            ProgramModel.Columns.TRACKED_ENTITY
    };

    private ProgramStore programStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.programStore = new ProgramStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramInDatabase() throws ParseException {
        //make sure that the foreign keys are in the database.
        insert_foreignKeyRows();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VERSION,
                ONLY_ENROLL_ONCE,
                ENROLLMENT_DATE_LABEL,
                DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL,
                REGISTRATION,
                SELECT_ENROLLMENT_DATES_IN_FUTURE,
                DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS,
                RELATIONSHIP_FROM_A,
                SELECT_INCIDENT_DATES_IN_FUTURE,
                CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION,
                DISPLAY_FRONT_PAGE_LIST,
                PROGRAM_TYPE,
                RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT,
                RELATED_PROGRAM,
                TRACKED_ENTITY
        );

        Cursor cursor = database().query(Tables.PROGRAM, PROGRAM_PROJECTION, null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VERSION,
                toInteger(ONLY_ENROLL_ONCE),
                ENROLLMENT_DATE_LABEL,
                toInteger(DISPLAY_INCIDENT_DATE),
                INCIDENT_DATE_LABEL,
                toInteger(REGISTRATION),
                toInteger(SELECT_ENROLLMENT_DATES_IN_FUTURE),
                toInteger(DATA_ENTRY_METHOD),
                toInteger(IGNORE_OVERDUE_EVENTS),
                toInteger(RELATIONSHIP_FROM_A),
                toInteger(SELECT_INCIDENT_DATES_IN_FUTURE),
                toInteger(CAPTURE_COORDINATES),
                toInteger(USE_FIRST_STAGE_DURING_REGISTRATION),
                toInteger(DISPLAY_FRONT_PAGE_LIST),
                PROGRAM_TYPE,
                RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT,
                RELATED_PROGRAM,
                TRACKED_ENTITY
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistProgramNullableInDatabase() throws ParseException {
        //make sure that the foreign keys are in the database.
        insert_foreignKeyRows();

        long rowId = programStore.insert(
                UID, null, NAME, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, RELATIONSHIP_FROM_A, null,
                null, null, null, PROGRAM_TYPE, RELATIONSHIP_TYPE, null, null, TRACKED_ENTITY);

        Cursor cursor = database().query(Tables.PROGRAM, PROGRAM_PROJECTION, null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, null, NAME, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, toInteger(RELATIONSHIP_FROM_A), null,
                null, null, null, PROGRAM_TYPE, RELATIONSHIP_TYPE, null, null, TRACKED_ENTITY).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        programStore.close();
        assertThat(database().isOpen()).isTrue();
    }

    private void insert_foreignKeyRows() {

        //RelationshipType foreign key corresponds to table entry
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID, RELATIONSHIP_TYPE);
        database().insert(Tables.RELATIONSHIP_TYPE, null, relationshipType);

        //TrackedEntity foreign key corresponds to table entry
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY);
        database().insert(Tables.TRACKED_ENTITY, null, trackedEntity);
    }
}
