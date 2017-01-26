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

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;
import static org.hisp.dhis.android.core.enrollment.EnrollmentModel.TABLE;

@RunWith(AndroidJUnit4.class)
public class EnrollmentStoreTests extends AbsStoreTestCase {
    private static final String[] PROJECTION = {
            EnrollmentModel.Columns.UID,
            EnrollmentModel.Columns.CREATED,
            EnrollmentModel.Columns.LAST_UPDATED,
            EnrollmentModel.Columns.ORGANISATION_UNIT,
            EnrollmentModel.Columns.PROGRAM,
            EnrollmentModel.Columns.DATE_OF_ENROLLMENT,
            EnrollmentModel.Columns.DATE_OF_INCIDENT,
            EnrollmentModel.Columns.FOLLOW_UP,
            EnrollmentModel.Columns.ENROLLMENT_STATUS,
            EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE,
            EnrollmentModel.Columns.LATITUDE,
            EnrollmentModel.Columns.LONGITUDE,
            EnrollmentModel.Columns.STATE
    };

    private EnrollmentStore enrollmentStore;

    private static final String UID = "test_uid";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final String PROGRAM = "test_program";
    private static final Boolean FOLLOW_UP = true;
    private static final EnrollmentStatus ENROLLMENT_STATUS = EnrollmentStatus.COMPLETED;
    private static final String TRACKED_ENTITY_INSTANCE = "test_trackedEntityInstance";
    private static final String LATITUDE = "10.832152";
    private static final String LONGITUDE = "59.345231";
    private static final State STATE = State.TO_UPDATE;
    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    private final Date date;
    private final String dateString;
    private final String WRONG_UID = "wrong";

    public EnrollmentStoreTests() throws ParseException {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.enrollmentStore = new EnrollmentStoreImpl(database());
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANISATION_UNIT);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.createWithOrgUnit(
                TRACKED_ENTITY_INSTANCE, ORGANISATION_UNIT);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
    }

    @Test
    public void insert_shouldPersistInDatabase() {
        long rowId = enrollmentStore.insert(
                UID,
                date,
                date,
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
    public void insert_shouldPersistDeferrableInDatabase() {
        final String deferredOrganisationUnit = "deferredOrganisationUnit";
        final String deferredProgram = "deferredProgram";
        final String deferredTrackedEntityInstance = "deferredTrackedEntityInstance";

        database().beginTransaction();
        long rowId = enrollmentStore.insert(UID, date, date,
                deferredOrganisationUnit,
                deferredProgram,
                date, date, FOLLOW_UP, ENROLLMENT_STATUS,
                deferredTrackedEntityInstance,
                LATITUDE, LONGITUDE, STATE
        );
        ContentValues program = CreateProgramUtils.create(11L, deferredProgram,
                RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(ProgramModel.TABLE, null, program);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(11L, deferredOrganisationUnit);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.createWithOrgUnit(
                deferredTrackedEntityInstance, ORGANISATION_UNIT);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, dateString, dateString,
                deferredOrganisationUnit,
                deferredProgram,
                dateString, dateString, AndroidTestUtils.toInteger(FOLLOW_UP), ENROLLMENT_STATUS,
                deferredTrackedEntityInstance,
                LATITUDE, LONGITUDE, STATE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistNullableInDatabase() {

        long rowId = enrollmentStore.insert(UID, null, null, ORGANISATION_UNIT, PROGRAM, null, null, null, null,
                TRACKED_ENTITY_INSTANCE, null, null, null);
        Cursor cursor = database().query(TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, null, null, ORGANISATION_UNIT, PROGRAM, null, null, null, null,
                TRACKED_ENTITY_INSTANCE, null, null, null).isExhausted();
    }

    @Test
    public void delete_shouldDeleteEnrollmentWhenDeletingOrgUnit() {
        enrollmentStore.insert(
                UID,
                date,
                date,
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
    public void delete_shouldDeleteEnrollmentWhenDeletingProgram() {

        enrollmentStore.insert(
                UID,
                date,
                date,
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
    public void delete_shouldDeleteEnrollmentWhenDeletingTrackedEntityInstance() {
        enrollmentStore.insert(
                UID,
                date,
                date,
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

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistEnrollWithInvalidOrgUnitForeignKey() {
        enrollmentStore.insert(
                UID,
                date,
                date,
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
    public void exception_persistEnrollWithInvalidProgramForeignKey() {
        enrollmentStore.insert(
                UID,
                date,
                date,
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
    public void exception_persistEnrollWithInvalidTrackedEntityInstanceForeignKey() {
        enrollmentStore.insert(
                UID,
                date,
                date,
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

    @Test
    public void close_shouldNotCloseDatabase() {
        enrollmentStore.close();
        assertThat(database().isOpen()).isTrue();
    }

}
