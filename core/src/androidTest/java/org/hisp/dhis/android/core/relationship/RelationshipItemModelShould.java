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
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.relationship.RelationshipItemModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RelationshipItemModelShould {
    private static final long ID = 11L;
    private static final String RELATIONSHIP = "relationship";
    private static final String RELATIONSHIP_ITEM_TYPE = "FROM";
    private static final String TRACKED_ENTITY_INSTANCE = "tei_uid";
    private static final String ENROLLMENT = "enrollment_uid";
    private static final String EVENT = "event_uid";

    @Test
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.RELATIONSHIP,
                Columns.RELATIONSHIP_ITEM_TYPE,
                Columns.TRACKED_ENTITY_INSTANCE,
                Columns.ENROLLMENT,
                Columns.EVENT
        });
        cursor.addRow(new Object[]{ID, RELATIONSHIP, RELATIONSHIP_ITEM_TYPE, TRACKED_ENTITY_INSTANCE, ENROLLMENT,
                EVENT});

        cursor.moveToFirst();
        RelationshipItemModel model = RelationshipItemModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.relationship()).isEqualTo(RELATIONSHIP);
        assertThat(model.relationshipItemType()).isEqualTo(RelationshipConstraintType.FROM);
        assertThat(model.trackedEntityInstance()).isEqualTo(TRACKED_ENTITY_INSTANCE);
        assertThat(model.enrollment()).isEqualTo(ENROLLMENT);
        assertThat(model.event()).isEqualTo(EVENT);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        RelationshipItemModel model = RelationshipItemModel.builder()
                .id(ID)
                .relationship(RELATIONSHIP)
                .relationshipItemType(RelationshipConstraintType.FROM)
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE)
                .enrollment(ENROLLMENT)
                .event(EVENT)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.RELATIONSHIP)).isEqualTo(RELATIONSHIP);
        assertThat(contentValues.getAsString(Columns.RELATIONSHIP_ITEM_TYPE)).isEqualTo(RELATIONSHIP_ITEM_TYPE);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_INSTANCE)).isEqualTo(TRACKED_ENTITY_INSTANCE);
        assertThat(contentValues.getAsString(Columns.ENROLLMENT)).isEqualTo(ENROLLMENT);
        assertThat(contentValues.getAsString(Columns.EVENT)).isEqualTo(EVENT);
    }
}

