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
import static org.bouncycastle.asn1.x500.style.RFC4519Style.l;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramStoreTests extends AbsStoreTestCase {

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

    private ProgramStore programStore;

    public ProgramStoreTests() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.programStore = new ProgramStoreImpl(database());

        //RelationshipType foreign key corresponds to table entry
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID, RELATIONSHIP_TYPE);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);

        //TrackedEntity foreign key corresponds to table entry
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
    }

    @Test
    public void insert_shouldPersistProgramInDatabase() {
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
    public void insert_shouldPersistProgramWithDeferredForeignKeyInDatabase() {
        final String deferredRelationshipTypeUid = "deferredRelationshipTypeUid";
        final String deferredTrackedEntityUid = "deferredTrackedEntityUid";

        database().beginTransaction();
        long rowId = programStore.insert(
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

        long rowId2 = programStore.insert(
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

    @Test
    public void delete_shouldDeleteProgramWhenDeletingRelatedProgramForeignKey() {
        database().beginTransaction();
        programStore.insert(
                UID,
                CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL, DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL, REGISTRATION, SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS, RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE, RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT,
                UID2,
                TRACKED_ENTITY
        );
        programStore.insert(
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

        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + "=?", new String[]{UID});

        Cursor cursor = database().query(ProgramModel.TABLE, PROGRAM_PROJECTION, null, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistProgramWithInvalidRelatedProgramForeignKey() {
        database().beginTransaction();
        programStore.insert(
                UID,
                CODE, NAME, DISPLAY_NAME, date, date, SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION,
                DISPLAY_DESCRIPTION, VERSION, ONLY_ENROLL_ONCE, ENROLLMENT_DATE_LABEL, DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL, REGISTRATION, SELECT_ENROLLMENT_DATES_IN_FUTURE, DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS, RELATIONSHIP_FROM_A, SELECT_INCIDENT_DATES_IN_FUTURE, CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION, DISPLAY_FRONT_PAGE_LIST, PROGRAM_TYPE, RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT,
                "wrong",
                TRACKED_ENTITY
        );
        database().setTransactionSuccessful();
        database().endTransaction();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistProgramWithInvalidRelationshipTypeForeignKey() {
        programStore.insert(UID, null, NAME, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, RELATIONSHIP_FROM_A, null, null, null, null, PROGRAM_TYPE,
                "wrong", null, null, TRACKED_ENTITY);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistProgramWithInvalidTrackedEntityForeignKey() {
        programStore.insert(UID, null, NAME, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, RELATIONSHIP_FROM_A, null, null, null, null, PROGRAM_TYPE,
                RELATIONSHIP_TYPE, null, null, "wrong");
    }

    @Test
    public void insert_shouldPersistProgramNullableInDatabase() {
        long rowId = programStore.insert(
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
    public void delete_shouldDeleteProgramWhenDeletingRelationshipTypeForeignKey() {
        programStore.insert(
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
    public void delete_shouldDeleteProgramWhenDeletingTrackedEntityForeignKey() {
        programStore.insert(
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

}
