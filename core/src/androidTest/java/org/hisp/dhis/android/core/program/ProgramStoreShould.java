/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.program.ProgramModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramStoreShould extends AbsStoreTestCase {

    private static final String[] PROGRAM_PROJECTION = {
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.SHORT_NAME,
            Columns.DISPLAY_SHORT_NAME,
            Columns.DESCRIPTION,
            Columns.DISPLAY_DESCRIPTION,
            Columns.VERSION,
            Columns.ONLY_ENROLL_ONCE,
            Columns.ENROLLMENT_DATE_LABEL,
            Columns.DISPLAY_INCIDENT_DATE,
            Columns.INCIDENT_DATE_LABEL,
            Columns.REGISTRATION,
            Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE,
            Columns.DATA_ENTRY_METHOD,
            Columns.IGNORE_OVERDUE_EVENTS,
            Columns.RELATIONSHIP_FROM_A,
            Columns.SELECT_INCIDENT_DATES_IN_FUTURE,
            Columns.CAPTURE_COORDINATES,
            Columns.USE_FIRST_STAGE_DURING_REGISTRATION,
            Columns.DISPLAY_FRONT_PAGE_LIST,
            Columns.PROGRAM_TYPE,
            Columns.RELATIONSHIP_TYPE,
            Columns.RELATIONSHIP_TEXT,
            Columns.RELATED_PROGRAM,
            Columns.TRACKED_ENTITY
    };

    //BaseIdentifiableModel attributes:
    private static final String UID = "test_uid";
    private final static String UID2 = "second_test_program";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";

    private static final String DISPLAY_NAME = "test_display_name";

    //BaseNameableModel attributes:
    private static final String SHORT_NAME = "test_program";
    private static final String DISPLAY_SHORT_NAME = "test_program";
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

    private final Date date;
    private final String dateString;

    private ProgramStore store;

    public ProgramStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.store = new ProgramStoreImpl(databaseAdapter());

        //RelationshipType foreign key corresponds to table entry
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID, RELATIONSHIP_TYPE);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);

        //TrackedEntity foreign key corresponds to table entry
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
    }

    @Test
    public void insert_program_in_data_base_when_insert() {
        long rowId = store.insert(
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
                null,
                TRACKED_ENTITY
        );

        Cursor cursor = database().query(ProgramModel.TABLE, PROGRAM_PROJECTION, null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                dateString,
                dateString,
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
                null,
                TRACKED_ENTITY
        ).isExhausted();
    }

    @Test
    public void insert_program_with_deferred_foreign_key_in_data_base_when_insert() {
        final String deferredRelationshipTypeUid = "deferredRelationshipTypeUid";
        final String deferredTrackedEntityUid = "deferredTrackedEntityUid";

        database().beginTransaction();
        long rowId = store.insert(
                UID,
                CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL, DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL, REGISTRATION, SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS, RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE, deferredRelationshipTypeUid,
                RELATIONSHIP_TEXT,
                UID2,
                deferredTrackedEntityUid
        );
        //RelationshipType foreign key corresponds to table entry
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(2L, deferredRelationshipTypeUid);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        //TrackedEntity foreign key corresponds to table entry
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(2L, deferredTrackedEntityUid);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);

        long rowId2 = store.insert(
                UID2,
                CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL, DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL, REGISTRATION, SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS, RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE, RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT,
                UID,
                TRACKED_ENTITY
        );
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(ProgramModel.TABLE, PROGRAM_PROJECTION, null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThat(rowId2).isEqualTo(2L);
        assertThatCursor(cursor).hasRow(
                UID,
                CODE, NAME, DISPLAY_NAME, dateString, dateString, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, toInteger(ONLY_ENROLL_ONCE), ENROLLMENT_DATE_LABEL,
                toInteger(DISPLAY_INCIDENT_DATE), INCIDENT_DATE_LABEL, toInteger(REGISTRATION),
                toInteger(SELECT_ENROLLMENT_DATES_IN_FUTURE), toInteger(DATA_ENTRY_METHOD),
                toInteger(IGNORE_OVERDUE_EVENTS), toInteger(RELATIONSHIP_FROM_A),
                toInteger(SELECT_INCIDENT_DATES_IN_FUTURE), toInteger(CAPTURE_COORDINATES),
                toInteger(USE_FIRST_STAGE_DURING_REGISTRATION), toInteger(DISPLAY_FRONT_PAGE_LIST),
                PROGRAM_TYPE, deferredRelationshipTypeUid, RELATIONSHIP_TEXT,
                UID2,
                deferredTrackedEntityUid
        );
        assertThatCursor(cursor).hasRow(
                UID2,
                CODE, NAME, DISPLAY_NAME, dateString, dateString, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, toInteger(ONLY_ENROLL_ONCE), ENROLLMENT_DATE_LABEL,
                toInteger(DISPLAY_INCIDENT_DATE), INCIDENT_DATE_LABEL, toInteger(REGISTRATION),
                toInteger(SELECT_ENROLLMENT_DATES_IN_FUTURE), toInteger(DATA_ENTRY_METHOD),
                toInteger(IGNORE_OVERDUE_EVENTS), toInteger(RELATIONSHIP_FROM_A),
                toInteger(SELECT_INCIDENT_DATES_IN_FUTURE), toInteger(CAPTURE_COORDINATES),
                toInteger(USE_FIRST_STAGE_DURING_REGISTRATION), toInteger(DISPLAY_FRONT_PAGE_LIST), PROGRAM_TYPE,
                RELATIONSHIP_TYPE, RELATIONSHIP_TEXT,
                UID,
                TRACKED_ENTITY
        );
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when__persistProgramWithInvalidRelationshipTypeForeignKey() {
        store.insert(UID, null, NAME, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, RELATIONSHIP_FROM_A, null, null, null, null, PROGRAM_TYPE,
                "wrong", null, null, TRACKED_ENTITY);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when__persistProgramWithInvalidTrackedEntityForeignKey() {
        store.insert(UID, null, NAME, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, RELATIONSHIP_FROM_A, null, null, null, null, PROGRAM_TYPE,
                RELATIONSHIP_TYPE, null, null, "wrong");
    }

    @Test
    public void insert_program_in_data_base_when_insert_nullable_program() {
        long rowId = store.insert(
                UID, null, NAME, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, RELATIONSHIP_FROM_A, null,
                null, null, null, PROGRAM_TYPE, null, null, null, null);

        Cursor cursor = database().query(ProgramModel.TABLE, PROGRAM_PROJECTION, null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, null, NAME, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, toInteger(RELATIONSHIP_FROM_A), null,
                null, null, null, PROGRAM_TYPE, null, null, null, null).isExhausted();
    }

    @Test
    public void delete_program_when_delete_relationship_type_foreign_key() {
        store.insert(
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
                null,
                TRACKED_ENTITY
        );

        database().delete(RelationshipTypeModel.TABLE,
                RelationshipTypeModel.Columns.UID + "=?", new String[]{RELATIONSHIP_TYPE});

        Cursor cursor = database().query(ProgramModel.TABLE, PROGRAM_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_program_when_delete_tracked_entity_foreign_key() {
        store.insert(
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
                null,
                TRACKED_ENTITY
        );

        database().delete(TrackedEntityModel.TABLE,
                TrackedEntityModel.Columns.UID + "=?", new String[]{TRACKED_ENTITY});

        Cursor cursor = database().query(ProgramModel.TABLE, PROGRAM_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_program_in_data_base_when_update() throws Exception {
        // insert program into database
        ContentValues program = new ContentValues();
        program.put(Columns.UID, UID);
        program.put(Columns.CODE, CODE);
        program.put(Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);

        database().insert(ProgramModel.TABLE, null, program);

        String[] projection = {Columns.UID, Columns.CODE, Columns.DISPLAY_SHORT_NAME};
        Cursor cursor = database().query(ProgramModel.TABLE, projection, null, null, null, null, null);

        // check that program was successfully inserted
        assertThatCursor(cursor).hasRow(UID, CODE, DISPLAY_SHORT_NAME);

        String updatedCode = "updated_program_code";
        String updatedDisplayShortName = "updated_program_display_short_name";
        // update the program with updatedCode and updatedDisplayShortName
        int update = store.update(
                UID, updatedCode, NAME, DISPLAY_NAME, date, date,
                SHORT_NAME, updatedDisplayShortName, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL,
                DISPLAY_INCIDENT_DATE, INCIDENT_DATE_LABEL, REGISTRATION,
                SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD, IGNORE_OVERDUE_EVENTS,
                RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE,
                null, null, null, null, UID
        );

        // check that store returns 1 when successfully update
        assertThat(update).isEqualTo(1);

        cursor = database().query(ProgramModel.TABLE, projection, null, null, null, null, null);

        // check that program is updated in database
        assertThatCursor(cursor).hasRow(UID, updatedCode, updatedDisplayShortName).isExhausted();

    }

    @Test
    public void delete_program_in_data_base_when_delete() throws Exception {
        // insert program into database
        ContentValues program = new ContentValues();
        program.put(Columns.UID, UID);
        database().insert(ProgramModel.TABLE, null, program);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(ProgramModel.TABLE, projection, null, null, null, null, null);

        // check that program was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID);

        // delete the program
        int delete = store.delete(UID);

        // check that store returns 1 on successful delete
        assertThat(delete).isEqualTo(1);

        cursor = database().query(ProgramModel.TABLE, projection, null, null, null, null, null);

        // check that program doesn't exist in database
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL, DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL, REGISTRATION, SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS, RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE, RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT, null, TRACKED_ENTITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL, DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL, REGISTRATION, SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS, RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE, RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT, null, TRACKED_ENTITY, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_where_uid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL, DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL, REGISTRATION, SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS, RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE, RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT, null, TRACKED_ENTITY, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }

}
