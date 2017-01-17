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
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitModelIntegrationTests {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final String PATH = "test_path";
    private static final String PARENT = "test_parent";
    private static final int LEVEL = 100;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                OrganisationUnitModel.Columns.ID,
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
                OrganisationUnitModel.Columns.LEVEL,
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, PATH, DATE, DATE, PARENT, LEVEL
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        OrganisationUnitModel organisationUnitModel = OrganisationUnitModel.create(matrixCursor);

        assertThat(organisationUnitModel.id()).isEqualTo(ID);
        assertThat(organisationUnitModel.uid()).isEqualTo(UID);
        assertThat(organisationUnitModel.code()).isEqualTo(CODE);
        assertThat(organisationUnitModel.name()).isEqualTo(NAME);
        assertThat(organisationUnitModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(organisationUnitModel.created()).isEqualTo(date);
        assertThat(organisationUnitModel.lastUpdated()).isEqualTo(date);
        assertThat(organisationUnitModel.shortName()).isEqualTo(SHORT_NAME);
        assertThat(organisationUnitModel.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(organisationUnitModel.description()).isEqualTo(DESCRIPTION);
        assertThat(organisationUnitModel.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(organisationUnitModel.path()).isEqualTo(PATH);
        assertThat(organisationUnitModel.openingDate()).isEqualTo(date);
        assertThat(organisationUnitModel.closedDate()).isEqualTo(date);
        assertThat(organisationUnitModel.parent()).isEqualTo(PARENT);
        assertThat(organisationUnitModel.level()).isEqualTo(LEVEL);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        OrganisationUnitModel organisationUnitModel = OrganisationUnitModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .path(PATH)
                .openingDate(date)
                .closedDate(date)
                .parent(PARENT)
                .level(LEVEL)
                .build();

        ContentValues contentValues = organisationUnitModel.toContentValues();

        assertThat(contentValues.getAsLong(OrganisationUnitModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.PATH)).isEqualTo(PATH);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.OPENING_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.CLOSED_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(OrganisationUnitModel.Columns.PARENT)).isEqualTo(PARENT);
        assertThat(contentValues.getAsInteger(OrganisationUnitModel.Columns.LEVEL)).isEqualTo(LEVEL);
    }
}
