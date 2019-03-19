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

package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;
import android.database.MatrixCursor;
import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.relationship.RelationshipModel.Columns;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RelationshipModelShould {
    //BaseIdentifiableModel attributes:
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String NAME = "test_display_name";
    private static final String RELATIONSHIP_TYPE = "RelationshipType uid";

    private final Date date;
    private final String dateString;

    public RelationshipModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    public void create_model_when_created_from_database_cursor() {
        String[] columnsWithId = Utils.appendInNewArray(new RelationshipModel.Columns().all(),
                BaseModel.Columns.ID);
        MatrixCursor cursor = new MatrixCursor(columnsWithId);

        cursor.addRow(new Object[]{
                UID,
                NAME,
                dateString,
                dateString,
                RELATIONSHIP_TYPE,
                ID
        });
        cursor.moveToFirst();

        Relationship relationship = Relationship.create(cursor);
        cursor.close();

        assertThat(relationship.id()).isEqualTo(ID);
        assertThat(relationship.uid()).isEqualTo(UID);
        assertThat(relationship.name()).isEqualTo(NAME);
        assertThat(relationship.created()).isEqualTo(date);
        assertThat(relationship.lastUpdated()).isEqualTo(date);
        assertThat(relationship.relationshipType()).isEqualTo(RELATIONSHIP_TYPE);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        Relationship relationship = Relationship.builder()
                .id(ID)
                .uid(UID)
                .name(NAME)
                .created(date)
                .lastUpdated(date)
                .relationshipType(RELATIONSHIP_TYPE)
                .build();
        ContentValues contentValues = relationship.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(BaseIdentifiableObjectModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(BaseIdentifiableObjectModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(BaseIdentifiableObjectModel.Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(BaseIdentifiableObjectModel.Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.RELATIONSHIP_TYPE)).isEqualTo(RELATIONSHIP_TYPE);
    }
}
