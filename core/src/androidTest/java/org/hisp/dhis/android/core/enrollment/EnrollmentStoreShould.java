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

package org.hisp.dhis.android.core.enrollment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.AndroidTestUtils;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel.Columns;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityInstanceUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;
import static org.hisp.dhis.android.core.enrollment.EnrollmentModel.TABLE;

@RunWith(AndroidJUnit4.class)
public class EnrollmentStoreShould extends AbsStoreTestCase {
    private static final String[] PROJECTION = {
            Columns.UID,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.CREATED_AT_CLIENT,
            Columns.LAST_UPDATED_AT_CLIENT,
            Columns.ORGANISATION_UNIT,
            Columns.PROGRAM,
            Columns.DATE_OF_ENROLLMENT,
            Columns.DATE_OF_INCIDENT,
            Columns.FOLLOW_UP,
            Columns.ENROLLMENT_STATUS,
            Columns.TRACKED_ENTITY_INSTANCE,
            Columns.LATITUDE,
            Columns.LONGITUDE,
            Columns.STATE
    };

    private EnrollmentStore store;

    private static final String UID = "test_uid";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final String PROGRAM = "test_program";
    private static final Boolean FOLLOW_UP = true;
    private static final EnrollmentStatus ENROLLMENT_STATUS = EnrollmentStatus.COMPLETED;
    private static final String TRACKED_ENTITY_INSTANCE = "test_trackedEntityInstance";
    private static final String LATITUDE = "10.832152";
    private static final String LONGITUDE = "59.345231";
    private static final String CREATED_AT_CLIENT = "2016-04-28T23:44:28.126";
    private static final String LAST_UPDATED_AT_CLIENT = "2016-04-28T23:44:28.126";
    private static final State STATE = State.TO_UPDATE;

    // foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    private final Date date;
    private final String dateString;
    private final String WRONG_UID = "wrong";

    public EnrollmentStoreShould() throws ParseException {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.store = new EnrollmentStoreImpl(databaseAdapter());
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANISATION_UNIT);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE, ORGANISATION_UNIT, TRACKED_ENTITY_UID);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
    }

    @Test
    public void  persist_in_data_base_when_insert() {
        long rowId = store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT,
                PROGRAM,
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE,
                LATITUDE,
                LONGITUDE,
                STATE
        );

        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                dateString,
                dateString,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT,
                PROGRAM,
                dateString,
                dateString,
                AndroidTestUtils.toInteger(FOLLOW_UP),
                ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE,
                LATITUDE,
                LONGITUDE,
                STATE
        ).isExhausted();
    }

    @Test
    public void persist_deferrable_in_data_base_when_insert() {
        final String deferredOrganisationUnit = "deferredOrganisationUnit";
        final String deferredProgram = "deferredProgram";
        final String deferredTrackedEntityInstance = "deferredTrackedEntityInstance";

        database().beginTransaction();

        long rowId = store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                deferredOrganisationUnit,
                deferredProgram,
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                deferredTrackedEntityInstance,
                LATITUDE,
                LONGITUDE,
                STATE
        );
        ContentValues program = CreateProgramUtils.create(11L, deferredProgram,
                RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(ProgramModel.TABLE, null, program);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(11L, deferredOrganisationUnit);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                deferredTrackedEntityInstance, ORGANISATION_UNIT, TRACKED_ENTITY_UID);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                dateString,
                dateString,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                deferredOrganisationUnit,
                deferredProgram,
                dateString,
                dateString,
                AndroidTestUtils.toInteger(FOLLOW_UP),
                ENROLLMENT_STATUS,
                deferredTrackedEntityInstance,
                LATITUDE,
                LONGITUDE,
                STATE
        ).isExhausted();
    }

    @Test
    public void persist_nullable_in_data_base_when_insert() {
        long rowId = store.insert(UID, null, null, null, null, ORGANISATION_UNIT, PROGRAM,
                null, null, null, null, TRACKED_ENTITY_INSTANCE, null, null, null);

        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, null, null, null, null, ORGANISATION_UNIT, PROGRAM,
                null, null, null, null, TRACKED_ENTITY_INSTANCE, null, null, null).isExhausted();
    }

    @Test
    public void update_enrollment_in_database_when_update_on_store() throws Exception {
        ContentValues enrollment = new ContentValues();
        enrollment.put(Columns.UID, UID);
        enrollment.put(Columns.ENROLLMENT_STATUS, ENROLLMENT_STATUS.name());
        enrollment.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        enrollment.put(Columns.PROGRAM, PROGRAM);
        enrollment.put(Columns.TRACKED_ENTITY_INSTANCE, TRACKED_ENTITY_INSTANCE);
        database().insert(EnrollmentModel.TABLE, null, enrollment);

        String[] projection = {Columns.UID, Columns.ENROLLMENT_STATUS};
        Cursor cursor = database().query(TABLE, projection, null, null, null, null, null);

        // check that enrollment was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID, ENROLLMENT_STATUS.name()).isExhausted();

        EnrollmentStatus updatedStatus = EnrollmentStatus.CANCELLED;
        store.update(UID, null, null, null, null,
                ORGANISATION_UNIT,
                PROGRAM,
                null, null, null, updatedStatus,
                TRACKED_ENTITY_INSTANCE,
                null, null, null, UID);

        cursor = database().query(TABLE, projection, null, null, null, null, null);

        // check that enrollment was successfully updated
        assertThatCursor(cursor).hasRow(UID, updatedStatus.name()).isExhausted();

    }

    @Test
    public void delete_enrollment_in_database_when_delete() throws Exception {
        ContentValues enrollment = new ContentValues();
        enrollment.put(Columns.UID, UID);
        enrollment.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        enrollment.put(Columns.PROGRAM, PROGRAM);
        enrollment.put(Columns.TRACKED_ENTITY_INSTANCE, TRACKED_ENTITY_INSTANCE);
        database().insert(EnrollmentModel.TABLE, null, enrollment);

        String[] projection = {Columns.UID};
        Cursor cursor = database().query(TABLE, projection, null, null, null, null, null);

        // check that enrollment was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        store.delete(UID);

        cursor = database().query(TABLE, projection, null, null, null, null, null);

        // check that enrollment is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_enrollment_in_database_when_delete_org_unit() {
        store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT,
                PROGRAM,
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE,
                LATITUDE,
                LONGITUDE,
                STATE
        );
        //Delete OrgUnit:
        database().delete(OrganisationUnitModel.TABLE,
                OrganisationUnitModel.Columns.UID + " =?", new String[]{ORGANISATION_UNIT});
        //Query for Enrollment:
        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_enrollment_in_database_when_delete_program() {

        store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT,
                PROGRAM,
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE,
                LATITUDE,
                LONGITUDE,
                STATE
        );

        database().delete(ProgramModel.TABLE,
                ProgramModel.Columns.UID + " =?", new String[]{PROGRAM});

        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_enrollment_in_database_when_delete_tracked_entity_instance() {
        store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT,
                PROGRAM,
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE,
                LATITUDE,
                LONGITUDE,
                STATE
        );

        database().delete(TrackedEntityInstanceModel.TABLE,
                ProgramModel.Columns.UID + " =?", new String[]{TRACKED_ENTITY_INSTANCE});

        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_enrollment_state_in_database_when_set_state_on_store() throws Exception {
        ContentValues enrollment = new ContentValues();
        enrollment.put(Columns.UID, UID);
        enrollment.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        enrollment.put(Columns.PROGRAM, PROGRAM);
        enrollment.put(Columns.TRACKED_ENTITY_INSTANCE, TRACKED_ENTITY_INSTANCE);
        enrollment.put(Columns.STATE, STATE.name());

        database().insert(EnrollmentModel.TABLE, null, enrollment);

        String[] projection = {Columns.UID, Columns.STATE};

        Cursor cursor = database().query(EnrollmentModel.TABLE, projection, null, null, null, null, null);

        // check that enrollment was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID, STATE).isExhausted();

        State updatedState = State.ERROR;
        store.setState(UID, updatedState);

        cursor = database().query(EnrollmentModel.TABLE, projection, null, null, null, null, null);

        // check that state is updated
        assertThatCursor(cursor).hasRow(UID, updatedState);

    }

    @Test
    public void return_list_of_enrollment_when_query_after_insert() throws Exception {
        ContentValues enrollmentContentValues = new ContentValues();
        enrollmentContentValues.put(Columns.UID, UID);
        enrollmentContentValues.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        enrollmentContentValues.put(Columns.PROGRAM, PROGRAM);
        enrollmentContentValues.put(Columns.TRACKED_ENTITY_INSTANCE, TRACKED_ENTITY_INSTANCE);
        enrollmentContentValues.put(Columns.STATE, STATE.name());
        database().insert(EnrollmentModel.TABLE, null, enrollmentContentValues);

        String[] projection = {Columns.UID};

        Cursor cursor = database().query(EnrollmentModel.TABLE, projection, null, null, null, null, null);

        // check that enrollment was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        Map<String, List<Enrollment>> map = store.query();
        assertThat(map.size()).isEqualTo(1);

        List<Enrollment> enrollments = map.get(TRACKED_ENTITY_INSTANCE);
        assertThat(enrollments.size()).isEqualTo(1);

        Enrollment enrollment = enrollments.get(0);
        assertThat(enrollment.uid()).isEqualTo(UID);
        assertThat(enrollment.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
        assertThat(enrollment.program()).isEqualTo(PROGRAM);
        assertThat(enrollment.trackedEntityInstance()).isEqualTo(TRACKED_ENTITY_INSTANCE);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_persist_enrollment_with_invalid_org_unit_foreign_key() {
        store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                WRONG_UID, //supply wrong uid
                PROGRAM,
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE,
                LATITUDE,
                LONGITUDE,
                STATE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_persist_enrollment_with_invalid_program_foreign_key() {
        store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT,
                WRONG_UID, //supply wrong uid
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE,
                LATITUDE,
                LONGITUDE,
                STATE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_persist_enrollment_with_invalid_tracked_entity_instance_foreign_key() {
        store.insert(
                UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT,
                PROGRAM,
                date,
                date,
                FOLLOW_UP,
                ENROLLMENT_STATUS,
                WRONG_UID, //supply wrong uid
                LATITUDE,
                LONGITUDE,
                STATE
        );
    }
}
