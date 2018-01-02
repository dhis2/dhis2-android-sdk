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
package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.filters.MediumTest;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkModel.Columns;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class UserRoleProgramLinkStoreShould extends AbsStoreTestCase {
    private static final String[] PROJECTION = {Columns.USER_ROLE, Columns.PROGRAM};

    public static final long ID = 1L;
    private static final String USER_ROLE_UID = "test_user_role_uid";
    private static final String PROGRAM_UID = "test_program_uid";
    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;

    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    private UserRoleProgramLinkStore store;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        store = new UserRoleProgramLinkStoreImpl(databaseAdapter());
        ContentValues userRole = CreateUserRoleUtils.create(ID, USER_ROLE_UID);
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM_UID,
                RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(UserRoleModel.TABLE, null, userRole);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
    }

    @Test
    @MediumTest
    public void insert_in_data_base_when_insert() {
        long rowId = store.insert(USER_ROLE_UID, PROGRAM_UID);
        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(USER_ROLE_UID, PROGRAM_UID).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_in_data_base_when_insert_deferrable_row() {
        final String deferredUserRole = "deferredUserRole";
        final String deferredProgram = "deferredProgram";

        database().beginTransaction();
        long rowId = store.insert(deferredUserRole, deferredProgram);
        ContentValues userRole = CreateUserRoleUtils.create(3L, deferredUserRole);
        ContentValues program = CreateProgramUtils.create(3L, deferredProgram,
                RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(UserRoleModel.TABLE, null, userRole);
        database().insert(ProgramModel.TABLE, null, program);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(deferredUserRole, deferredProgram).isExhausted();
    }

    @Test
    @MediumTest
    public void update_and_not_insert_when_update() {
        long rowId = store.update(USER_ROLE_UID, PROGRAM_UID, USER_ROLE_UID, PROGRAM_UID);
        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(0);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void update_when_update_existing_user_role_program_link() {
        final String oldUserRoleUid = "oldUserRoleUid";
        final String oldProgramUid = "oldProgramUid";
        //insert old foreign key tables:
        ContentValues userRole = CreateUserRoleUtils.create(3L, oldUserRoleUid);
        ContentValues program = CreateProgramUtils.create(3L, oldProgramUid,
                RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        database().insert(UserRoleModel.TABLE, null, userRole);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.USER_ROLE, oldUserRoleUid);
        contentValues.put(Columns.PROGRAM, oldProgramUid);
        database().insert(UserRoleProgramLinkModel.TABLE, null, contentValues);

        long returnValue = store.update(USER_ROLE_UID, PROGRAM_UID, oldUserRoleUid, oldProgramUid);

        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(returnValue).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(USER_ROLE_UID, PROGRAM_UID).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_in_data_base_when_delete_row() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.USER_ROLE, USER_ROLE_UID);
        contentValues.put(Columns.PROGRAM, PROGRAM_UID);

        database().insert(UserRoleProgramLinkModel.TABLE, null, contentValues);
        int returnValue = store.delete(USER_ROLE_UID, PROGRAM_UID);

        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(returnValue).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_user_role_program_link_in_data_base_when_delete_user_role_foreign_key() {
        store.insert(USER_ROLE_UID, PROGRAM_UID);
        database().delete(UserRoleModel.TABLE, UserRoleModel.Columns.UID + "=?", new String[]{USER_ROLE_UID});
        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_user_role_program_link_in_data_base_when_delete_program_foreign_key() {
        store.insert(USER_ROLE_UID, PROGRAM_UID);
        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + "=?", new String[]{PROGRAM_UID});
        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_sqlite_constraint_exception_when_insert_user_role_program_link_with_invalid_user_foreign_key() {
        store.insert("wrong", PROGRAM_UID);
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_sqlite_constraint_exception_when_insert_user_role_program_link_with_organisation_unit_foreign_key() {
        store.insert(USER_ROLE_UID, "wrong");
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_uid_arg() {
        store.insert(null, PROGRAM_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_program_arg() {
        store.insert(USER_ROLE_UID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_user_role_arg() {
        store.update(null, PROGRAM_UID, USER_ROLE_UID, PROGRAM_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_program_arg() {
        store.update(USER_ROLE_UID, null, USER_ROLE_UID, PROGRAM_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_where_user_role_arg() {
        store.update(USER_ROLE_UID, PROGRAM_UID, null, PROGRAM_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_where_program_arg() {
        store.update( USER_ROLE_UID, PROGRAM_UID, USER_ROLE_UID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_delete_user_role_arg() {
        store.delete(null, PROGRAM_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_delete_program_arg() {
        store.delete(USER_ROLE_UID, null);
    }
}
