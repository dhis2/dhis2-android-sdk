/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples;
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo.Columns;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitModelShould {
    private static final String UID = "test_uid";

    private final OrganisationUnit organisationUnit = OrganisationUnitSamples.getOrganisationUnit(UID);

    @Test
    public void create_model_when_created_from_database_cursor() {

        String[] columnsWithId = Utils.appendInNewArray(OrganisationUnitTableInfo.TABLE_INFO.columns().all(),
                BaseIdentifiableObjectModel.Columns.ID);
        MatrixCursor cursor = new MatrixCursor(columnsWithId);

        cursor.addRow(new Object[]{
                UID,
                organisationUnit.code(),
                organisationUnit.name(),
                organisationUnit.displayName(),
                FillPropertiesTestUtils.CREATED_STR,
                FillPropertiesTestUtils.LAST_UPDATED_STR,
                organisationUnit.shortName(),
                organisationUnit.displayShortName(),
                organisationUnit.description(),
                organisationUnit.displayDescription(),
                organisationUnit.path(),
                FillPropertiesTestUtils.CREATED_STR,
                FillPropertiesTestUtils.LAST_UPDATED_STR,
                organisationUnit.level(),
                organisationUnit.parent(),
                organisationUnit.displayNamePath(),
                organisationUnit.id()
        });
        cursor.moveToFirst();

        OrganisationUnit model = OrganisationUnit.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(organisationUnit.id());
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(organisationUnit.code());
        assertThat(model.name()).isEqualTo(organisationUnit.name());
        assertThat(model.displayName()).isEqualTo(organisationUnit.displayName());
        assertThat(model.created()).isEqualTo(FillPropertiesTestUtils.CREATED);
        assertThat(model.lastUpdated()).isEqualTo(FillPropertiesTestUtils.LAST_UPDATED);
        assertThat(model.shortName()).isEqualTo(organisationUnit.shortName());
        assertThat(model.displayShortName()).isEqualTo(organisationUnit.displayShortName());
        assertThat(model.description()).isEqualTo(organisationUnit.description());
        assertThat(model.displayDescription()).isEqualTo(organisationUnit.displayDescription());
        assertThat(model.path()).isEqualTo(organisationUnit.path());
        assertThat(model.openingDate()).isEqualTo(FillPropertiesTestUtils.CREATED);
        assertThat(model.closedDate()).isEqualTo(FillPropertiesTestUtils.LAST_UPDATED);
        assertThat(model.parent()).isEqualTo(organisationUnit.parent());
        assertThat(model.level()).isEqualTo(organisationUnit.level());
        assertThat(model.displayNamePath()).isEqualTo(organisationUnit.displayNamePath());
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        OrganisationUnit model = OrganisationUnitSamples.getOrganisationUnit(UID);
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(organisationUnit.id());
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(organisationUnit.code());
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(organisationUnit.name());
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(organisationUnit.displayName());
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(FillPropertiesTestUtils.CREATED_STR);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(FillPropertiesTestUtils.LAST_UPDATED_STR);
        assertThat(contentValues.getAsString(Columns.SHORT_NAME)).isEqualTo(organisationUnit.shortName());
        assertThat(contentValues.getAsString(Columns.DISPLAY_SHORT_NAME)).isEqualTo(organisationUnit.displayShortName());
        assertThat(contentValues.getAsString(Columns.DESCRIPTION)).isEqualTo(organisationUnit.description());
        assertThat(contentValues.getAsString(Columns.DISPLAY_DESCRIPTION)).isEqualTo(organisationUnit.displayDescription());
        assertThat(contentValues.getAsString(OrganisationUnitFields.PATH)).isEqualTo(organisationUnit.path());
        assertThat(contentValues.getAsString(OrganisationUnitFields.OPENING_DATE)).isEqualTo(FillPropertiesTestUtils.CREATED_STR);
        assertThat(contentValues.getAsString(OrganisationUnitFields.CLOSED_DATE)).isEqualTo(FillPropertiesTestUtils.LAST_UPDATED_STR);
        assertThat(contentValues.getAsString(OrganisationUnitFields.PARENT)).isEqualTo(organisationUnit.parent());
        assertThat(contentValues.getAsInteger(OrganisationUnitFields.LEVEL)).isEqualTo(organisationUnit.level());
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME_PATH)).isEqualTo(organisationUnit.displayNamePath());
    }
}
