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
import android.database.MatrixCursor;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserOrganisationUnitLinkModelsShould {
    private static final long ID = 2L;
    private static final String USER = "test_user_uid";
    private static final String ORGANISATION_UNIT = "test_organisation_unit_uid";
    private static final String ORGANISATION_UNIT_SCOPE = "test_organisation_unit_scope";

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.USER, Columns.ORGANISATION_UNIT, Columns.ORGANISATION_UNIT_SCOPE
        });
        cursor.addRow(new Object[]{ID, USER, ORGANISATION_UNIT, ORGANISATION_UNIT_SCOPE});
        cursor.moveToFirst();

        UserOrganisationUnitLinkModel model = UserOrganisationUnitLinkModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.user()).isEqualTo(USER);
        assertThat(model.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder() {
        UserOrganisationUnitLinkModel model = UserOrganisationUnitLinkModel.builder()
                        .id(ID)
                        .user(USER)
                        .organisationUnit(ORGANISATION_UNIT)
                        .organisationUnitScope(ORGANISATION_UNIT_SCOPE)
                        .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.USER)).isEqualTo(USER);
        assertThat(contentValues.getAsString(Columns.ORGANISATION_UNIT)).isEqualTo(ORGANISATION_UNIT);
        assertThat(contentValues.getAsString(Columns.ORGANISATION_UNIT_SCOPE)).isEqualTo(ORGANISATION_UNIT_SCOPE);
    }
}
