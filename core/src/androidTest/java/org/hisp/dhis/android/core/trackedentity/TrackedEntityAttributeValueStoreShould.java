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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeValueStoreShould extends AbsStoreTestCase {
    //TrackedEntityAttributeValueModel:
    private static final String VALUE = "test_value";
    private static final String CREATED = "test_created";
    private static final String LAST_UPDATED = "test_lastUpdated";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttributeUid";
    private static final String TRACKED_ENTITY_INSTANCE = "test_trackedEntityInstanceUid";
    private static final String ORGANIZATION_UNIT = "test_organizationUnitUid";
    private static final String TRACKED_ENTITY = "test_trackedEntity";

    private static final String[] PROJECTION = {
            TrackedEntityAttributeValueModel.Columns.VALUE,
            TrackedEntityAttributeValueModel.Columns.CREATED,
            TrackedEntityAttributeValueModel.Columns.LAST_UPDATED,
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE
    };

    private TrackedEntityAttributeValueStore store;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        this.store = new TrackedEntityAttributeValueStoreImpl(databaseAdapter());

        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANIZATION_UNIT);
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(1L, TRACKED_ENTITY);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE, ORGANIZATION_UNIT, TRACKED_ENTITY);
        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils
                .create(1L, TRACKED_ENTITY_ATTRIBUTE, null);

        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);
    }

    @Test
    public void insert_teav_in_data_base_when_insert() {
        long rowId = store.insert(VALUE, CREATED, LAST_UPDATED,
                TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE);

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(VALUE, CREATED, LAST_UPDATED, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE)
                .isExhausted();
    }

    @Test
    public void insert_deferrable_teav_in_data_base_when_insert() {
        final String deferredTrackedEntityAttribute = "deferredTrackedEntityAttribute";
        final String deferredTrackedEntityInstance = "deferredTrackedEntityInstance";

        database().beginTransaction();
        long rowId = store.insert(VALUE, CREATED, LAST_UPDATED,
                deferredTrackedEntityAttribute, deferredTrackedEntityInstance);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                deferredTrackedEntityInstance, ORGANIZATION_UNIT, TRACKED_ENTITY);
        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils.create(3L,
                deferredTrackedEntityAttribute, null);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().insert(TrackedEntityAttributeModel.TABLE, null, trackedEntityAttribute);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(VALUE, CREATED, LAST_UPDATED, deferredTrackedEntityAttribute, deferredTrackedEntityInstance)
                .isExhausted();
    }

    @Test
    public void insert_nullable_teav_in_data_base_when_insert_nullable_teav() {
        long rowId = store.insert(null, CREATED, LAST_UPDATED, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE);

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(null, CREATED, LAST_UPDATED, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE)
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_teav_with_invalid_tracked_entity_attribute() {
        store.insert(VALUE, CREATED, LAST_UPDATED, "wrong", TRACKED_ENTITY_INSTANCE);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_teav_with_invalid_tracked_entity_instance() {
        store.insert(VALUE, CREATED, LAST_UPDATED, TRACKED_ENTITY_ATTRIBUTE, "wrong");
    }

    @Test
    public void delete_teav_in_data_base_when_delete_tracked_entity_attribute() {
        insert_nullable_teav_in_data_base_when_insert_nullable_teav();

        database().delete(TrackedEntityAttributeModel.TABLE,
                TrackedEntityAttributeModel.Columns.UID + "=?", new String[]{TRACKED_ENTITY_ATTRIBUTE});

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_teav_in_data_base_when_delete_tracked_entity_instance() {
        insert_nullable_teav_in_data_base_when_insert_nullable_teav();

        database().delete(TrackedEntityInstanceModel.TABLE,
                TrackedEntityInstanceModel.Columns.UID + "=?", new String[]{TRACKED_ENTITY_INSTANCE});

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }


    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_tracked_entity() {
        store.insert(VALUE, CREATED, LAST_UPDATED, null, TRACKED_ENTITY_INSTANCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_tracked_entity_instance() {
        store.insert(VALUE, CREATED, LAST_UPDATED, TRACKED_ENTITY_ATTRIBUTE, null);
    }
}
