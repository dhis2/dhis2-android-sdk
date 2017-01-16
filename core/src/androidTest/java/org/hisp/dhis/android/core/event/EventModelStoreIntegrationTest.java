package org.hisp.dhis.android.core.event;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.event.EventModel.Columns;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.program.CreateProgramStageUtils;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class EventModelStoreIntegrationTest extends AbsStoreTestCase {
    private static final String[] EVENT_PROJECTION = {
            Columns.UID,
            Columns.ENROLLMENT_UID,
            Columns.CREATED, // created
            Columns.LAST_UPDATED, // lastUpdated
            Columns.STATUS,
            Columns.LATITUDE,
            Columns.LONGITUDE,
            Columns.PROGRAM,
            Columns.PROGRAM_STAGE,
            Columns.ORGANISATION_UNIT,
            Columns.EVENT_DATE, // eventDate
            Columns.COMPLETE_DATE, // completedDate
            Columns.DUE_DATE, // dueDate
            Columns.STATE
    };
    private EventModelStore eventModelStore;

    private static final Long ID = 3L;
    private static final String EVENT_UID = "test_uid";
    private static final String ENROLLMENT_UID = "test_enrollment";
    private static final EventStatus STATUS = EventStatus.ACTIVE;
    private static final String LATITUDE = "10.832152";
    private static final String LONGITUDE = "59.345231";
    private static final String PROGRAM = "test_program";
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final State STATE = State.TO_POST;

    // timestamp
    private static final String DATE = "2017-01-12T11:31:00.000";

    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.eventModelStore = new EventModelStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistEventInDatabase() throws Exception {
//Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, TRACKED_ENTITY_UID);

        database().insert(DbOpenHelper.Tables.TRACKED_ENTITY, null, trackedEntity);
        database().insert(DbOpenHelper.Tables.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(DbOpenHelper.Tables.PROGRAM, null, program);

        ContentValues organisationUnit = CreateOrganisationUnitUtils.create(1L, ORGANISATION_UNIT);
        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);

        database().insert(Tables.ORGANISATION_UNIT, null, organisationUnit);
        database().insert(Tables.PROGRAM_STAGE, null, programStage);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = eventModelStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                timeStamp, // created
                timeStamp, // lastUpdated
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                timeStamp, // eventDate
                timeStamp, // completedDate
                timeStamp, // dueDate
                STATE
        );

        Cursor cursor = database().query(Tables.EVENT, EVENT_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                EVENT_UID,
                ENROLLMENT_UID,
                DATE, // created
                DATE, // lastUpdated
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                DATE, // eventDate
                DATE, // completedDate
                DATE, // dueDate
                STATE
        ).isExhausted();
    }

    /**
     * trying to insert event with program, stage and org unit without
     * inserting them to db.
     * @throws Exception
     */
    @Test(expected = SQLiteConstraintException.class)
    public void insert_shouldThrowSqliteConstraintException() throws Exception {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = eventModelStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                timeStamp, // created
                timeStamp, // lastUpdated
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                timeStamp, // eventDate
                timeStamp, // completedDate
                timeStamp, // dueDate
                STATE
        );

    }

    @Test
    public void close_shouldNotCloseDatabase() {
        eventModelStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
