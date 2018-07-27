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

import org.hisp.dhis.android.core.relationship.RelationshipConstraintModel.Columns;
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

    @Test
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.RELATIONSHIP_TYPE,
                Columns.CONSTRAINT_TYPE,
                Columns.RELATIONSHIP_ENTITY,
                Columns.TRACKED_ENTITY_TYPE,
                Columns.PROGRAM,
                Columns.PROGRAM_STAGE
        });
        cursor.addRow(new Object[]{ID, RELATIONSHIP_TYPE, CONSTRAINT_TYPE, RELATIONSHIP_ENTITY, TRACKED_ENTITY_TYPE,
                PROGRAM, PROGRAM_STAGE});

        cursor.moveToFirst();
        RelationshipConstraintModel model = RelationshipConstraintModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.relationshipType()).isEqualTo(RELATIONSHIP_TYPE);
        assertThat(model.constraintType()).isEqualTo(RelationshipConstraintType.FROM);
        assertThat(model.relationshipEntity()).isEqualTo(RelationshipEntityType.TRACKED_ENTITY_INSTANCE);
        assertThat(model.trackedEntityType()).isEqualTo(TRACKED_ENTITY_TYPE);
        assertThat(model.program()).isEqualTo(PROGRAM);
        assertThat(model.programStage()).isEqualTo(PROGRAM_STAGE);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        RelationshipConstraintModel model = RelationshipConstraintModel.builder()
                .id(ID)
                .relationshipType(RELATIONSHIP_TYPE)
                .constraintType(RelationshipConstraintType.FROM)
                .relationshipEntity(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
                .trackedEntityType(TRACKED_ENTITY_TYPE)
                .program(PROGRAM)
                .programStage(PROGRAM_STAGE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.RELATIONSHIP_TYPE)).isEqualTo(RELATIONSHIP_TYPE);
        assertThat(contentValues.getAsString(Columns.CONSTRAINT_TYPE)).isEqualTo(CONSTRAINT_TYPE);
        assertThat(contentValues.getAsString(Columns.RELATIONSHIP_ENTITY)).isEqualTo(RELATIONSHIP_ENTITY);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_TYPE)).isEqualTo(TRACKED_ENTITY_TYPE);
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
    }
}

