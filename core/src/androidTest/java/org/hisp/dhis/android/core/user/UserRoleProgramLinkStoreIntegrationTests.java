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

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkModel.Columns;

public class UserRoleProgramLinkStoreIntegrationTests extends AbsStoreTestCase {
    public static final long ID = 1L;
    private static final String USER_ROLE_UID = "test_user_role_uid";
    private static final String PROGRAM_UID = "test_program_uid";

    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    private static final String[] PROJECTION = {Columns.USER_ROLE, Columns.PROGRAM,};

    private UserRoleProgramLinkStore organisationUnitLinkStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        organisationUnitLinkStore = new UserRoleProgramLinkStoreImpl(database());

        ContentValues userRole = CreateUserRoleUtils.create(ID, USER_ROLE_UID);
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);

        ContentValues program = CreateProgramUtils.create(1L, PROGRAM_UID, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(UserRoleModel.TABLE, null, userRole);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        long rowId = organisationUnitLinkStore.insert(USER_ROLE_UID, PROGRAM_UID);

        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(USER_ROLE_UID, PROGRAM_UID).isExhausted();
    }

    @Test
    public void delete_shouldDeleteUserRoleProgramLinkWhenDeletingUserRoleForeignKey() {
        organisationUnitLinkStore.insert(USER_ROLE_UID, PROGRAM_UID);

        database().delete(UserRoleModel.TABLE, UserRoleModel.Columns.UID + "=?", new String[]{USER_ROLE_UID});

        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteUserRoleProgramLinkWhenDeletingProgramForeignKey() {
        organisationUnitLinkStore.insert(USER_ROLE_UID, PROGRAM_UID);

        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + "=?", new String[]{PROGRAM_UID});

        Cursor cursor = database().query(UserRoleProgramLinkModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistUserRoleProgramLinkWithInvalidUserForeignKey() {
        organisationUnitLinkStore.insert("wrong", PROGRAM_UID);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistUserRoleProgramLinkWithInvalidOrganisationUnitForeignKey() {
        organisationUnitLinkStore.insert(USER_ROLE_UID, "wrong");
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        organisationUnitLinkStore.close();
        assertThat(database().isOpen()).isTrue();
    }
}
