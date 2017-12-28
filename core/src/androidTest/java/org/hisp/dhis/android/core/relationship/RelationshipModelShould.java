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
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.relationship.RelationshipModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RelationshipModelShould {
    //table id:
    private static final long ID = 11L;
    //from BaseDataModel:
    private static final State STATE = State.SYNCED;
    // RelationshipModel attributes:
    private static final String TRACKED_ENTITY_INSTANCE_A = "Tei A uid";
    private static final String TRACKED_ENTITY_INSTANCE_B = "Tei B uid";
    private static final String RELATIONSHIP_TYPE = "RelationshipType uid";

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.STATE,
                Columns.TRACKED_ENTITY_INSTANCE_A,
                Columns.TRACKED_ENTITY_INSTANCE_B,
                Columns.RELATIONSHIP_TYPE
        });
        cursor.addRow(new Object[]{
                ID, STATE.name(),
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        });
        cursor.moveToFirst();

        RelationshipModel model = RelationshipModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.state()).isEqualTo(STATE);
        assertThat(model.trackedEntityInstanceA()).isEqualTo(TRACKED_ENTITY_INSTANCE_A);
        assertThat(model.trackedEntityInstanceB()).isEqualTo(TRACKED_ENTITY_INSTANCE_B);
        assertThat(model.relationshipType()).isEqualTo(RELATIONSHIP_TYPE);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        RelationshipModel model = RelationshipModel.builder()
                .id(ID)
                .state(STATE)
                .trackedEntityInstanceA(TRACKED_ENTITY_INSTANCE_A)
                .trackedEntityInstanceB(TRACKED_ENTITY_INSTANCE_B)
                .relationshipType(RELATIONSHIP_TYPE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.STATE)).isEqualTo(STATE.name());
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_INSTANCE_A)).isEqualTo(TRACKED_ENTITY_INSTANCE_A);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_INSTANCE_B)).isEqualTo(TRACKED_ENTITY_INSTANCE_B);
        assertThat(contentValues.getAsString(Columns.RELATIONSHIP_TYPE)).isEqualTo(RELATIONSHIP_TYPE);
    }
}
