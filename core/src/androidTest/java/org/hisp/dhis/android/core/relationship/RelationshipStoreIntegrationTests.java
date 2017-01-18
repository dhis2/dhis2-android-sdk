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
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class RelationshipStoreIntegrationTests extends AbsStoreTestCase {

    //Relationship attributes:
    private static final String TRACKED_ENTITY_INSTANCE_A = "Tei A uid";
    private static final String TRACKED_ENTITY_INSTANCE_B = "Tei B uid";

    //RelationshipType (foreign key):
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE = "test relationshipType uid";

    //Relationship projection:
    private static final String[] RELATIONSHIP_PROJECTION = {
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A,
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B,
            RelationshipModel.Columns.RELATIONSHIP_TYPE
    };

    private RelationshipStore relationshipStore;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        relationshipStore = new RelationshipStoreImpl(database());
        //Insert RelationshipType in RelationshipType table, such that it can be used as foreign key:
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(
                RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE
        );
        database().insert(RelationshipTypeModel.RELATIONSHIP_TYPE, null, relationshipType);
    }

    @Test
    public void insert_shouldPersistRelationshipInDatabase() {
        long rowId = relationshipStore.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        );

        Cursor cursor = database().query(
                RelationshipModel.RELATIONSHIP,
                RELATIONSHIP_PROJECTION,
                null, null, null, null, null, null
        );

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistRelationshipNullableInDatabase() {
        //Insert foreign keys in their respective tables:
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(
                RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE
        );
        database().insert(RelationshipTypeModel.RELATIONSHIP_TYPE, null, relationshipType);

        long rowId = relationshipStore.insert(null, null, RELATIONSHIP_TYPE);
        Cursor cursor = database().query(RelationshipModel.RELATIONSHIP, RELATIONSHIP_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(null, null, RELATIONSHIP_TYPE).isExhausted();
    }

    @Test
    public void delete_shouldDeleteRelationshipWhenDeletingRelationshipType() {
        relationshipStore.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        );
        database().delete(RelationshipTypeModel.RELATIONSHIP_TYPE,
                RelationshipTypeModel.Columns.UID + "=?", new String[] {RELATIONSHIP_TYPE});

    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistRelationshipWithInvalidRelationshipTypeForeignKey() {
        String WRONG_UID = "wrong";
        relationshipStore.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                WRONG_UID //supply the wrong uid
        );
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        relationshipStore.close();
        assertThat(database().isOpen()).isTrue();
    }
}
