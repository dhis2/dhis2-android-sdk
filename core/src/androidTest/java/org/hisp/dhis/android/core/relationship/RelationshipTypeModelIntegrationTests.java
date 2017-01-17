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

 package org.hisp.dhis.android.core.relationship;

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
public class RelationshipTypeModelIntegrationTests {
    //BaseIdentifiableModel attributes:
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    //RelationshipTypeModel attributes:
    private static final String A_IS_TO_B = "cat of";
    private static final String B_IS_TO_A = "owner of";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                RelationshipTypeModel.Columns.ID,
                RelationshipTypeModel.Columns.UID,
                RelationshipTypeModel.Columns.CODE,
                RelationshipTypeModel.Columns.NAME,
                RelationshipTypeModel.Columns.DISPLAY_NAME,
                RelationshipTypeModel.Columns.CREATED,
                RelationshipTypeModel.Columns.LAST_UPDATED,
                RelationshipTypeModel.Columns.A_IS_TO_B,
                RelationshipTypeModel.Columns.B_IS_TO_A,
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, A_IS_TO_B, B_IS_TO_A});

        matrixCursor.moveToFirst();
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        RelationshipTypeModel relationshipTypeModel = RelationshipTypeModel.create(matrixCursor);

        assertThat(relationshipTypeModel.id()).isEqualTo(ID);
        assertThat(relationshipTypeModel.uid()).isEqualTo(UID);
        assertThat(relationshipTypeModel.code()).isEqualTo(CODE);
        assertThat(relationshipTypeModel.name()).isEqualTo(NAME);
        assertThat(relationshipTypeModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(relationshipTypeModel.created()).isEqualTo(timeStamp);
        assertThat(relationshipTypeModel.lastUpdated()).isEqualTo(timeStamp);
        assertThat(relationshipTypeModel.aIsToB()).isEqualTo(A_IS_TO_B);
        assertThat(relationshipTypeModel.bIsToA()).isEqualTo(B_IS_TO_A);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        RelationshipTypeModel relationshipTypeModel = RelationshipTypeModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .aIsToB(A_IS_TO_B)
                .bIsToA(B_IS_TO_A)
                .build();

        ContentValues contentValues = relationshipTypeModel.toContentValues();
        assertThat(contentValues.getAsLong(RelationshipTypeModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(RelationshipTypeModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(RelationshipTypeModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(RelationshipTypeModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(RelationshipTypeModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(RelationshipTypeModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(RelationshipTypeModel.Columns.A_IS_TO_B)).isEqualTo(A_IS_TO_B);
        assertThat(contentValues.getAsString(RelationshipTypeModel.Columns.B_IS_TO_A)).isEqualTo(B_IS_TO_A);
    }
}

