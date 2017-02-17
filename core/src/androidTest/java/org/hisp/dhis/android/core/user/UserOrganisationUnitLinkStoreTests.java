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
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class UserOrganisationUnitLinkStoreTests extends AbsStoreTestCase {

    public static final long ID = 1L;
    private static final String USER_UID = "test_user_uid";
    private static final String ORGANISATION_UNIT_UID = "test_organisation_unit_uid";

    private static final String ORGANISATION_UNIT_SCOPE = "test_organisation_unit_scope";
    private static final String[] PROJECTION = {
            UserOrganisationUnitLinkModel.Columns.USER,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE
    };

    private UserOrganisationUnitLinkStore organisationUnitLinkStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        organisationUnitLinkStore = new UserOrganisationUnitLinkStoreImpl(databaseAdapter());

        // insert a parent user and organisation unit
        ContentValues user = UserStoreTests.create(ID, USER_UID);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANISATION_UNIT_UID);
        database().insert(UserModel.TABLE, null, user);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        long rowId = organisationUnitLinkStore.insert(
                USER_UID,
                ORGANISATION_UNIT_UID,
                ORGANISATION_UNIT_SCOPE
        );

        Cursor cursor = database().query(UserOrganisationUnitLinkModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(USER_UID,
                ORGANISATION_UNIT_UID,
                ORGANISATION_UNIT_SCOPE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistDeferrableRowInDatabase() {
        final String deferrableUserUid = "deferrableUser";
        final String deferrableOrgUnitUid = "deferrableOrgUnit";

        database().beginTransaction();

        long rowId = organisationUnitLinkStore.insert(
                deferrableUserUid,
                deferrableOrgUnitUid,
                ORGANISATION_UNIT_SCOPE
        );
        ContentValues user = UserStoreTests.create(2L, deferrableUserUid);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(2L, deferrableOrgUnitUid);
        database().insert(UserModel.TABLE, null, user);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);

        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(UserOrganisationUnitLinkModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                deferrableUserUid,
                deferrableOrgUnitUid,
                ORGANISATION_UNIT_SCOPE
        ).isExhausted();
    }

    @Test
    public void delete_shouldDeleteRow() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserOrganisationUnitLinkModel.Columns.USER, USER_UID);
        contentValues.put(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT, ORGANISATION_UNIT_UID);
        contentValues.put(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE, ORGANISATION_UNIT_SCOPE);

        database().insert(UserOrganisationUnitLinkModel.TABLE, null, contentValues);
        int returnValue = organisationUnitLinkStore.delete(USER_UID, ORGANISATION_UNIT_UID);

        Cursor cursor = database().query(UserOrganisationUnitLinkModel.TABLE, PROJECTION,
                null, null, null, null, null);

        assertThat(returnValue).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteAllRows() {
        organisationUnitLinkStore.insert(
                USER_UID,
                ORGANISATION_UNIT_UID,
                ORGANISATION_UNIT_SCOPE
        );

        int deleted = organisationUnitLinkStore.delete();

        Cursor cursor = database().query(UserOrganisationUnitLinkModel.TABLE,
                null, null, null, null, null, null);

        assertThat(deleted).isEqualTo(1L);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteUserOrganisationUnitLinkWhenDeletingUserForeignKey() {
        organisationUnitLinkStore.insert(
                USER_UID,
                ORGANISATION_UNIT_UID,
                ORGANISATION_UNIT_SCOPE
        );

        database().delete(UserModel.TABLE, UserModel.Columns.UID + "=?", new String[]{USER_UID});

        Cursor cursor = database().query(UserOrganisationUnitLinkModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteUserOrganisationUnitLinkWhenDeletingOrganisationUnitForeignKey() {
        organisationUnitLinkStore.insert(
                USER_UID,
                ORGANISATION_UNIT_UID,
                ORGANISATION_UNIT_SCOPE
        );

        database().delete(OrganisationUnitModel.TABLE,
                OrganisationUnitModel.Columns.UID + "=?", new String[]{ORGANISATION_UNIT_UID});

        Cursor cursor = database().query(UserOrganisationUnitLinkModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistUserOrganisationUnitLinkWithInvalidUserForeignKey() {
        organisationUnitLinkStore.insert(
                "wrong",
                ORGANISATION_UNIT_UID,
                ORGANISATION_UNIT_SCOPE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistUserOrganisationUnitLinkWithInvalidOrganisationUnitForeignKey() {
        organisationUnitLinkStore.insert(
                USER_UID,
                "wrong",
                ORGANISATION_UNIT_SCOPE
        );
    }

}
