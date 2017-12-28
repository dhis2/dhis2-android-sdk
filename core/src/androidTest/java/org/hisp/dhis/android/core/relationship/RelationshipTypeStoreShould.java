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
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class RelationshipTypeStoreShould extends AbsStoreTestCase {

    //BaseIdentifiable attributes:
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";
    private final Date date;

    //RelationshipType attributes:
    private static final String A_IS_TO_B = " a cat";
    private static final String B_IS_TO_A = " a cat owner ";

    private static final String[] RELATIONSHIP_TYPE_PROJECTION = {
            RelationshipTypeModel.Columns.UID,
            RelationshipTypeModel.Columns.CODE,
            RelationshipTypeModel.Columns.NAME,
            RelationshipTypeModel.Columns.DISPLAY_NAME,
            RelationshipTypeModel.Columns.CREATED,
            RelationshipTypeModel.Columns.LAST_UPDATED,
            RelationshipTypeModel.Columns.A_IS_TO_B,
            RelationshipTypeModel.Columns.B_IS_TO_A
    };

    private RelationshipTypeStore store;

    public RelationshipTypeStoreShould() throws ParseException {
        this.date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
    }

    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.store = new RelationshipTypeStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void insert_relationship_type_in_data_base_when_insert() throws ParseException {

        long rowId = store.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                A_IS_TO_B,
                B_IS_TO_A
        );

        Cursor cursor = database().query(
                RelationshipTypeModel.TABLE,
                RELATIONSHIP_TYPE_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                A_IS_TO_B,
                B_IS_TO_A
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_relationship_type_in_data_base_when_insert_nullable() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = store.insert(
                UID,
                null,
                NAME,
                null, null, null,
                A_IS_TO_B,
                B_IS_TO_A
        );

        Cursor cursor = database().query(
                RelationshipTypeModel.TABLE,
                RELATIONSHIP_TYPE_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                null,
                NAME,
                null, null, null,
                A_IS_TO_B,
                B_IS_TO_A
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void update_relationship_type_in_data_base_when_update() throws Exception {
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(1L, UID);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        String[] projection = {RelationshipTypeModel.Columns.UID};
        Cursor cursor = database().query(RelationshipTypeModel.TABLE, projection,
                null, null, null, null, null);

        // check that relationshipType was successfully inserted in database
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        int update = store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, A_IS_TO_B, B_IS_TO_A, UID);

        // check that update returns 1
        assertThat(update).isEqualTo(1);

        cursor = database().query(RelationshipTypeModel.TABLE, projection,
                null, null, null, null, null);

        // check that row exists in database
        assertThatCursor(cursor).hasRow(UID).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_relationship_type_when_delete() throws Exception {
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(1L, UID);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);

        String[] projection = {RelationshipTypeModel.Columns.UID};
        Cursor cursor = database().query(RelationshipTypeModel.TABLE, projection,
                null, null, null, null, null);
        // check that relationshipType was successfully inserted in database
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        int delete = store.delete(UID);
        assertThat(delete).isEqualTo(1);

        cursor = database().query(RelationshipTypeModel.TABLE, projection,
                null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, date, date, A_IS_TO_B, B_IS_TO_A);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_aIsToB() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, null, B_IS_TO_A);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_bIsToA() {
        store.insert(UID, CODE, NAME, DISPLAY_NAME, date, date, A_IS_TO_B, null);
    }   
    
    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null, CODE, NAME, DISPLAY_NAME, date, date, A_IS_TO_B, B_IS_TO_A, UID);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_aIsToB() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, null, B_IS_TO_A, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_bIsToA() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, A_IS_TO_B, null, UID);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_update_null_whereUid() {
        store.update(UID, CODE, NAME, DISPLAY_NAME, date, date, A_IS_TO_B, B_IS_TO_A, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}
