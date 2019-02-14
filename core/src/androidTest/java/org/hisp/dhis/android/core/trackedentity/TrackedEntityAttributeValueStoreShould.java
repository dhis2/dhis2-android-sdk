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

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeValueStoreShould extends AbsStoreTestCase {
    //TrackedEntityAttributeValueModel:
    private static final String VALUE = "test_value";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttributeUid";
    private static final String TRACKED_ENTITY_ATTRIBUTE_2 = "test_trackedEntityAttributeUid_2";
    private static final String TRACKED_ENTITY_INSTANCE = "test_trackedEntityInstanceUid";
    private static final String TRACKED_ENTITY_INSTANCE_2 = "test_trackedEntityInstanceUid_2";
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
    private Date date;
    private String dateString;
    private TrackedEntityAttributeValue trackedEntityAttributeValue;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);

        this.store = TrackedEntityAttributeValueStoreImpl.create(databaseAdapter());

        OrganisationUnit organisationUnit = OrganisationUnitSamples.getOrganisationUnit(ORGANIZATION_UNIT);
        ContentValues trackedEntityType = CreateTrackedEntityUtils.create(1L, TRACKED_ENTITY);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE, ORGANIZATION_UNIT, TRACKED_ENTITY);
        ContentValues trackedEntityInstance_2 = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE_2, ORGANIZATION_UNIT, TRACKED_ENTITY);

        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils
                .create(1L, TRACKED_ENTITY_ATTRIBUTE, null);

        database().insert(OrganisationUnitTableInfo.TABLE_INFO.name(), null, organisationUnit.toContentValues());
        database().insert(TrackedEntityTypeModel.TABLE, null, trackedEntityType);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance_2);
        database().insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute);

        trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
                .value(VALUE).created(date).lastUpdated(date).trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE)
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE).build();

    }

    @Test
    public void insert_tracked_entity_attribute_value_in_data_base_when_insert() {
        long rowId = store.insert(trackedEntityAttributeValue);

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(VALUE, dateString, dateString, TRACKED_ENTITY_ATTRIBUTE,
                        TRACKED_ENTITY_INSTANCE)
                .isExhausted();
    }

    @Test
    public void insert_deferrable_tracked_entity_attribute_value_in_data_base_when_insert() {
        final String deferredTrackedEntityAttribute = "deferredTrackedEntityAttribute";
        final String deferredTrackedEntityInstance = "deferredTrackedEntityInstance";

        database().beginTransaction();
        long rowId = store.insert(trackedEntityAttributeValue.toBuilder()
                .trackedEntityAttribute(deferredTrackedEntityAttribute)
                .trackedEntityInstance(deferredTrackedEntityInstance).build());
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                deferredTrackedEntityInstance, ORGANIZATION_UNIT, TRACKED_ENTITY);
        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils.create(3L,
                deferredTrackedEntityAttribute, null);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().insert(TrackedEntityAttributeTableInfo.TABLE_INFO.name(), null, trackedEntityAttribute);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(VALUE, dateString, dateString, deferredTrackedEntityAttribute,
                        deferredTrackedEntityInstance)
                .isExhausted();
    }

    @Test
    public void insert_nullable_tracked_entity_attribute_value_in_data_base_when_insert_nullable_tracked_entity_attribute_value() {
        long rowId = store.insert(trackedEntityAttributeValue.toBuilder().value(null).build());

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(null, dateString, dateString, TRACKED_ENTITY_ATTRIBUTE,
                        TRACKED_ENTITY_INSTANCE)
                .isExhausted();
    }

    @Test
    public void update_event_in_data_base_after_update() throws Exception {
        store.insert(trackedEntityAttributeValue.toBuilder().value("0").build());
        store.updateOrInsertWhere(trackedEntityAttributeValue.toBuilder().value("4").build());

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE, PROJECTION, null,
                null, null,
                null, null);

        assertThatCursor(cursor).hasRow(
                "4",
                dateString,
                dateString,
                TRACKED_ENTITY_ATTRIBUTE,
                TRACKED_ENTITY_INSTANCE
        ).isExhausted();
    }

    @Test
    public void delete_tracked_entity_attribute_value_by_instance_and_attribute_uids() {
        store.insert(trackedEntityAttributeValue.toBuilder().build());

        store.insert(trackedEntityAttributeValue.toBuilder()
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE_2).build());

        store.insert(trackedEntityAttributeValue.toBuilder()
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE_2).build());

        store.insert(trackedEntityAttributeValue.toBuilder()
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE_2)
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE_2).build());

        store.deleteByInstanceAndNotInAttributes(TRACKED_ENTITY_INSTANCE,
                Lists.newArrayList(TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE_2));
        Assert.assertThat(store.count(), is(4));
        Assert.assertThat(store.queryByTrackedEntityInstance(TRACKED_ENTITY_INSTANCE).size(), is(2));
        Assert.assertThat(store.queryByTrackedEntityInstance(TRACKED_ENTITY_INSTANCE_2).size(), is(2));

        store.deleteByInstanceAndNotInAttributes(TRACKED_ENTITY_INSTANCE, Lists.newArrayList(TRACKED_ENTITY_ATTRIBUTE));
        Assert.assertThat(store.count(), is(3));
        Assert.assertThat(store.queryByTrackedEntityInstance(TRACKED_ENTITY_INSTANCE).size(), is(1));

        store.deleteByInstanceAndNotInAttributes(TRACKED_ENTITY_INSTANCE, new ArrayList<>());
        Assert.assertThat(store.count(), is(2));
        Assert.assertThat(store.queryByTrackedEntityInstance(TRACKED_ENTITY_INSTANCE).size(), is(0));
    }

    //@Test(expected = SQLiteConstraintException.class)
    //TODO Solve the foreign keys for missing attributes
    public void
    throw_sqlite_constraint_exception_when_insert_tracked_entity_attribute_value_with_invalid_tracked_entity_attribute() {
        store.insert(trackedEntityAttributeValue.toBuilder().trackedEntityAttribute("wrong").build());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void
    throw_sqlite_constraint_exception_when_insert_tracked_entity_attribute_value_with_invalid_tracked_entity_instance() {
        store.insert(trackedEntityAttributeValue.toBuilder().trackedEntityInstance("wrong").build());
    }

    //@Test
    //TODO Solve the Foreign keys for missing attributes
    public void delete_tracked_entity_attribute_value_in_data_base_when_delete_tracked_entity_attribute() {
        insert_nullable_tracked_entity_attribute_value_in_data_base_when_insert_nullable_tracked_entity_attribute_value();

        database().delete(TrackedEntityAttributeTableInfo.TABLE_INFO.name(),
                BaseIdentifiableObjectModel.Columns.UID + "=?",
                new String[]{TRACKED_ENTITY_ATTRIBUTE});

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_tracked_entity_attribute_value_in_data_base_when_delete_tracked_entity_instance() {
        insert_nullable_tracked_entity_attribute_value_in_data_base_when_insert_nullable_tracked_entity_attribute_value();

        database().delete(TrackedEntityInstanceModel.TABLE,
                TrackedEntityInstanceModel.Columns.UID + "=?",
                new String[]{TRACKED_ENTITY_INSTANCE});

        Cursor cursor = database().query(TrackedEntityAttributeValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }


    @Test(expected = SQLiteConstraintException.class)
    public void throw_illegal_argument_exception_when_insert_null_tracked_entity() {
        store.insert(trackedEntityAttributeValue.toBuilder().trackedEntityAttribute(null).build());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_illegal_argument_exception_when_insert_null_tracked_entity_instance() {
        store.insert(trackedEntityAttributeValue.toBuilder().trackedEntityInstance(null).build());
    }
}