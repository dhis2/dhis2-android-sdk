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

package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel.Columns;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitProgramLinkStoreShould extends AbsStoreTestCase {
    private static final Long ID = 3L;
    private static final String ORGANISATION_UNIT_UID = "test_organisation_unit_uid";
    private static final String PROGRAM_UID = "test_program_uid";
    private static final String[] PROJECTION = {Columns.ORGANISATION_UNIT, Columns.PROGRAM};

    private OrganisationUnitProgramLinkStore store;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new OrganisationUnitProgramLinkStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void persist_in_data_base_after_insert() throws Exception {
        // inserting mandatory foreign keys
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(ID, ORGANISATION_UNIT_UID);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM_UID, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        long rowId = store.insert(ORGANISATION_UNIT_UID, PROGRAM_UID);

        // checking if successful insert
        assertThat(rowId).isEqualTo(1L);

        Cursor cursor = database().query(OrganisationUnitProgramLinkModel.TABLE, PROJECTION,
                null, null, null, null, null);

        assertThatCursor(cursor).hasRow(ORGANISATION_UNIT_UID, PROGRAM_UID).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_sqlite_constraint_exception_after_persist_in_database_with_missing_organisation_unit_foreign_key() throws Exception {
        ContentValues program = CreateProgramUtils.create(ID, PROGRAM_UID, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        store.insert(ORGANISATION_UNIT_UID, PROGRAM_UID);
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_sqlite_constraint_exception_after_persist_in_database_with_missing_program_foreign_key() throws Exception {
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(ID, ORGANISATION_UNIT_UID);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);

        store.insert(ORGANISATION_UNIT_UID, PROGRAM_UID);
    }

    @Test
    @MediumTest
    public void delete_link_when_delete_organisation_unit() throws Exception {
        // inserting mandatory foreign keys
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(ID, ORGANISATION_UNIT_UID);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM_UID, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues organisationUnitProgramLink = new ContentValues();
        organisationUnitProgramLink.put(Columns.ID, ID);
        organisationUnitProgramLink.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT_UID);
        organisationUnitProgramLink.put(Columns.PROGRAM, PROGRAM_UID);

        database().insert(
                OrganisationUnitProgramLinkModel.TABLE, null, organisationUnitProgramLink);

        String[] projection = {Columns.ID, Columns.ORGANISATION_UNIT, Columns.PROGRAM};

        Cursor cursor = database().query(OrganisationUnitProgramLinkModel.TABLE, projection,
                null, null, null, null, null);

        // checking that link was successfully inserted
        assertThatCursor(cursor).hasRow(ID, ORGANISATION_UNIT_UID, PROGRAM_UID).isExhausted();

        database().delete(OrganisationUnitModel.TABLE,
                OrganisationUnitModel.Columns.UID + " =?", new String[]{ORGANISATION_UNIT_UID});

        cursor = database().query(OrganisationUnitProgramLinkModel.TABLE, projection,
                null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_link_when_delete_program() throws Exception {
        // inserting mandatory foreign keys
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(ID, ORGANISATION_UNIT_UID);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);

        ContentValues program = CreateProgramUtils.create(ID, PROGRAM_UID, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues organisationUnitProgramLink = new ContentValues();
        organisationUnitProgramLink.put(Columns.ID, ID);
        organisationUnitProgramLink.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT_UID);
        organisationUnitProgramLink.put(Columns.PROGRAM, PROGRAM_UID);

        database().insert(
                OrganisationUnitProgramLinkModel.TABLE, null, organisationUnitProgramLink);

        String[] projection = {Columns.ID, Columns.ORGANISATION_UNIT, Columns.PROGRAM};

        Cursor cursor = database().query(OrganisationUnitProgramLinkModel.TABLE, projection,
                null, null, null, null, null);

        assertThatCursor(cursor).hasRow(ID, ORGANISATION_UNIT_UID, PROGRAM_UID).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_organisationUnit() {
        store.insert(null, PROGRAM_UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_program() {
        store.insert(ORGANISATION_UNIT_UID, null);
    }

}
