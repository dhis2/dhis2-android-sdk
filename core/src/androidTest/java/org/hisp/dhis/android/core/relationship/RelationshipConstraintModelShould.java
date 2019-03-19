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

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RelationshipConstraintModelShould {
    private static final long ID = 11L;
    private static final String RELATIONSHIP_TYPE = "relationship_type";
    private static final String CONSTRAINT_TYPE = "FROM";
    private static final String RELATIONSHIP_ENTITY = "TRACKED_ENTITY_INSTANCE";
    private static final String TRACKED_ENTITY_TYPE = "tracked_entity_type";
    private static final String PROGRAM = "program";
    private static final String PROGRAM_STAGE = "program_stage";

    private static final RelationshipConstraint expectedConstraint = RelationshipConstraint.builder()
            .id(ID)
            .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE))
            .constraintType(RelationshipConstraintType.FROM)
            .relationshipEntity(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
            .trackedEntityType(ObjectWithUid.create(TRACKED_ENTITY_TYPE))
            .program(ObjectWithUid.create(PROGRAM))
            .programStage(ObjectWithUid.create(PROGRAM_STAGE))
            .build();

    @Test
    public void create_model_when_created_from_database_cursor() {
        String[] columnsWithId = Utils.appendInNewArray(new RelationshipConstraintTableInfo.Columns().all(),
                RelationshipConstraintTableInfo.Columns.ID);
        MatrixCursor cursor = new MatrixCursor(columnsWithId);

        cursor.addRow(new Object[]{RELATIONSHIP_TYPE, CONSTRAINT_TYPE, RELATIONSHIP_ENTITY, TRACKED_ENTITY_TYPE,
                PROGRAM, PROGRAM_STAGE, ID});

        cursor.moveToFirst();
        RelationshipConstraint constraintFromDb = RelationshipConstraint.create(cursor);
        cursor.close();

        assertThat(constraintFromDb).isEqualTo(expectedConstraint);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        ContentValues contentValues = expectedConstraint.toContentValues();

        assertThat(contentValues.getAsLong(BaseModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(RelationshipConstraintFields.RELATIONSHIP_TYPE)).isEqualTo(RELATIONSHIP_TYPE);
        assertThat(contentValues.getAsString(RelationshipConstraintFields.CONSTRAINT_TYPE)).isEqualTo(CONSTRAINT_TYPE);
        assertThat(contentValues.getAsString(RelationshipConstraintFields.RELATIONSHIP_ENTITY)).isEqualTo(RELATIONSHIP_ENTITY);
        assertThat(contentValues.getAsString(RelationshipConstraintFields.TRACKED_ENTITY_TYPE)).isEqualTo(TRACKED_ENTITY_TYPE);
        assertThat(contentValues.getAsString(RelationshipConstraintFields.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(RelationshipConstraintFields.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
    }
}

