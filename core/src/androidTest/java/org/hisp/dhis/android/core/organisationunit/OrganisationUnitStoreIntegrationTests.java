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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class OrganisationUnitStoreIntegrationTests extends AbsStoreTestCase {
    public static final String[] PROJECTION = {
            OrganisationUnitModel.Columns.UID,
            OrganisationUnitModel.Columns.CODE,
            OrganisationUnitModel.Columns.NAME,
            OrganisationUnitModel.Columns.DISPLAY_NAME,
            OrganisationUnitModel.Columns.CREATED,
            OrganisationUnitModel.Columns.LAST_UPDATED,
            OrganisationUnitModel.Columns.SHORT_NAME,
            OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME,
            OrganisationUnitModel.Columns.DESCRIPTION,
            OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION,
            OrganisationUnitModel.Columns.PATH,
            OrganisationUnitModel.Columns.OPENING_DATE,
            OrganisationUnitModel.Columns.CLOSED_DATE,
            OrganisationUnitModel.Columns.PARENT,
            OrganisationUnitModel.Columns.LEVEL
    };

    private static final String UID = "organisation_unit_uid";
    private static final String CODE = "organisation_unit_code";
    private static final String NAME = "organisation_unit_name";
    private static final String DISPLAY_NAME = "organisation_unit_display_name";
    private static final String SHORT_NAME = "organisation_unit_short_name";
    private static final String DISPLAY_SHORT_NAME = "organisation_unit_display_short_name";
    private static final String DESCRIPTION = "organisation_unit_description";
    private static final String DISPLAY_DESCRIPTION = "organisation_unit_display_description";
    private static final String PATH = "organisation_unit_path";
    private static final int LEVEL = 11;

    private OrganisationUnitStore organisationUnitStore;

    // timestamp
    private final Date date;
    private final String dateString;

    public OrganisationUnitStoreIntegrationTests() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        organisationUnitStore = new OrganisationUnitStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        long rowId = organisationUnitStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date, date,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                PATH,
                date, date, null, LEVEL
        );

        Cursor cursor = database().query(OrganisationUnitModel.ORGANISATION_UNIT, PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                dateString,
                dateString,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                PATH,
                dateString,
                dateString,
                null, LEVEL
        ).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        organisationUnitStore.close();
        assertThat(database().isOpen()).isTrue();
    }

    @Test
    public void delete_shouldDeleteAllRows() {
        ContentValues organisationUnitOne = CreateOrganisationUnitUtils.createOrgUnit(1L, "organisation_unit_one");
        ContentValues organisationUnitTwo = CreateOrganisationUnitUtils.createOrgUnit(2L, "organisation_unit_two");

        database().insert(OrganisationUnitModel.ORGANISATION_UNIT, null, organisationUnitOne);
        database().insert(OrganisationUnitModel.ORGANISATION_UNIT, null, organisationUnitTwo);

        int deleted = organisationUnitStore.delete();
        Cursor cursor = database().query(OrganisationUnitModel.ORGANISATION_UNIT, null, null, null, null, null, null);

        assertThat(deleted).isEqualTo(2);
        assertThatCursor(cursor).isExhausted();
    }
}
