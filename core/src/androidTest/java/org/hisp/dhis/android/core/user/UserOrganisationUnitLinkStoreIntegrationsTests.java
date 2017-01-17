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

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class UserOrganisationUnitLinkStoreIntegrationsTests extends AbsStoreTestCase {
    private static final String[] USER_ORGANISATION_UNITS_PROJECTION = {
            UserOrganisationUnitLinkModel.Columns.USER,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE
    };

    private UserOrganisationUnitLinkStore organisationUnitLinkStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        organisationUnitLinkStore = new UserOrganisationUnitLinkStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        // insert a parent user and organisation unit
        ContentValues user = UserStoreIntegrationTests
                .create(1L, "test_user_uid");
        ContentValues organisationUnit = CreateOrganisationUnitUtils
                .createOrgUnit(1L, "test_organisation_unit_uid");
        database().insert(DbOpenHelper.Tables.USER, null, user);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        long rowId = organisationUnitLinkStore.insert(
                "test_user_uid",
                "test_organisation_unit_uid",
                "test_organisation_unit_scope"
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNITS_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow("test_user_uid",
                        "test_organisation_unit_uid",
                        "test_organisation_unit_scope"
                ).isExhausted();
    }

    @Test
    public void delete_shouldDeleteAllRows() {
        ContentValues user = UserStoreIntegrationTests
                .create(1L, "test_user_uid");
        ContentValues organisationUnit = CreateOrganisationUnitUtils
                .createOrgUnit(1L, "test_organisation_unit_uid");
        ContentValues userOrganisationUnitLink = new ContentValues();
        userOrganisationUnitLink.put(UserOrganisationUnitLinkModel.Columns.USER, "test_user_uid");
        userOrganisationUnitLink.put(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT, "test_organisation_unit_uid");
        userOrganisationUnitLink.put(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE, "test_organisation_unit_scope");

        database().insert(DbOpenHelper.Tables.USER, null, user);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);
        database().insert(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrganisationUnitLink);

        int deleted = organisationUnitLinkStore.delete();

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1L);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        organisationUnitLinkStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
