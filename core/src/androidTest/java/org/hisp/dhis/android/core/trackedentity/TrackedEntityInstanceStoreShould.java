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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstanceStoreShould extends AbsStoreTestCase {
    private static final String UID = "test_uid";
    private static final String ORGANISATION_UNIT = "test_organisationUnit";
    private static final String TRACKED_ENTITY = "test_trackedEntity";
    private static final State STATE = State.ERROR;
    private static final String CREATED_AT_CLIENT = "2016-04-28T23:44:28.126";
    private static final String LAST_UPDATED_AT_CLIENT = "2016-04-28T23:44:28.126";

    private final Date date;
    private final String dateString;

    private TrackedEntityInstanceStore trackedEntityInstanceStore;

    public TrackedEntityInstanceStoreShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    public static final String[] PROJECTION = {
            Columns.UID,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.CREATED_AT_CLIENT,
            Columns.LAST_UPDATED_AT_CLIENT,
            Columns.ORGANISATION_UNIT,
            Columns.TRACKED_ENTITY,
            Columns.STATE
    };

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter());
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANISATION_UNIT);
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(1L, TRACKED_ENTITY);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
    }

    @Test
    public void insert_in_data_base_when_insert() {
        long rowId = trackedEntityInstanceStore.insert(UID, date, date,
                CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT, TRACKED_ENTITY, STATE);

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, dateString, dateString,
                CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT, TRACKED_ENTITY, STATE).isExhausted();
    }

    @Test
    public void insert_in_data_base_when_insert_deferrable() {
        String deferredOrganisationUnit = "deferredOrganisationUnit";
        String deferredTrackedEntity = "deferredTrackedEntity";

        database().beginTransaction();

        long rowId = trackedEntityInstanceStore.insert(UID, date, date, CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                deferredOrganisationUnit, deferredTrackedEntity, STATE);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(11L, deferredOrganisationUnit);
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(11L, deferredTrackedEntity);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(UID, dateString, dateString,
                CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                deferredOrganisationUnit, deferredTrackedEntity, STATE
        ).isExhausted();
    }

    @Test
    public void insert_in_data_base_when_insert_nullable_row() {
        long rowId = trackedEntityInstanceStore.insert(UID, null, null, null, null,
                ORGANISATION_UNIT, TRACKED_ENTITY, null);

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, null, null, null, null, ORGANISATION_UNIT, TRACKED_ENTITY, null
        ).isExhausted();
    }

    @Test
    public void update_in_data_base_when_update() throws Exception {
        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(Columns.UID, UID);
        trackedEntityInstance.put(Columns.TRACKED_ENTITY, TRACKED_ENTITY);
        trackedEntityInstance.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        trackedEntityInstance.put(Columns.LAST_UPDATED, dateString);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);

        String[] projection = {Columns.UID, Columns.LAST_UPDATED};
        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity instance was successfully inserted
        assertThatCursor(cursor).hasRow(UID, dateString).isExhausted();

        Date newLastUpdated = new Date();

        trackedEntityInstanceStore.update(UID, null, newLastUpdated, null, null,
                ORGANISATION_UNIT, TRACKED_ENTITY, null, UID);

        String newLastUpdatedString = BaseIdentifiableObject.DATE_FORMAT.format(newLastUpdated);
        cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity instance was successfully updated
        assertThatCursor(cursor).hasRow(UID, newLastUpdatedString).isExhausted();

    }

    @Test
    public void delete_in_data_base_when_delete() throws Exception {
        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(Columns.UID, UID);
        trackedEntityInstance.put(Columns.TRACKED_ENTITY, TRACKED_ENTITY);
        trackedEntityInstance.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);

        String[] projection = {Columns.UID};
        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity instance was successfully inserted
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        trackedEntityInstanceStore.delete(UID);

        cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);

        // check that tracked entity instance is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_all_rows_in_data_base_when_delete_without_param() {
        database().insert(TrackedEntityInstanceModel.TABLE, null,
                CreateTrackedEntityInstanceUtils.create(UID, ORGANISATION_UNIT, TRACKED_ENTITY));

        trackedEntityInstanceStore.delete();

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_tei_in_data_base_when_delete_organisation_unit_foreign_key() {

        trackedEntityInstanceStore.insert(UID, date, date, CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT, TRACKED_ENTITY, STATE);


        database().delete(OrganisationUnitModel.TABLE,
                OrganisationUnitModel.Columns.UID + "=?", new String[]{
                        ORGANISATION_UNIT
                });

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).

                isExhausted();
    }

    @Test
    public void delete_tracked_entity_instance_in_data_base_when_delete_tracked_entity_foreign_key() {
        trackedEntityInstanceStore.insert(UID, date, date, CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT, TRACKED_ENTITY, STATE);

        database().delete(TrackedEntityModel.TABLE,
                TrackedEntityModel.Columns.UID + "=?", new String[]{TRACKED_ENTITY});
        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_set_state_in_data_base_when_update_tracked_entity_instance_stateshouldUpdateTrackedEntityInstanceState() throws Exception {
        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(Columns.UID, UID);
        trackedEntityInstance.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        trackedEntityInstance.put(Columns.TRACKED_ENTITY, TRACKED_ENTITY);
        trackedEntityInstance.put(Columns.STATE, STATE.name());

        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);

        String[] projection = {
                Columns.UID,
                Columns.ORGANISATION_UNIT,
                Columns.TRACKED_ENTITY,
                Columns.STATE
        };

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);
        // check that tracked entity instance was successfully inserted
        assertThatCursor(cursor).hasRow(UID, ORGANISATION_UNIT, TRACKED_ENTITY, STATE).isExhausted();
        State updatedState = State.SYNCED;
        trackedEntityInstanceStore.setState(UID, updatedState);

        cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);

        // check that trackedEntityInstance is updated with updated state
        assertThatCursor(cursor).hasRow(UID, ORGANISATION_UNIT, TRACKED_ENTITY, updatedState).isExhausted();
    }

    @Test
    public void return_list_of_tracked_entity_instance_when_query() throws Exception {
        ContentValues trackedEntityInstanceContentValues = new ContentValues();
        trackedEntityInstanceContentValues.put(Columns.UID, UID);
        trackedEntityInstanceContentValues.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        trackedEntityInstanceContentValues.put(Columns.TRACKED_ENTITY, TRACKED_ENTITY);
        trackedEntityInstanceContentValues.put(Columns.STATE, State.TO_POST.name());
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstanceContentValues);

        String[] projection = {Columns.UID};
        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);
        // verify that tei was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        Map<String, TrackedEntityInstance> map = trackedEntityInstanceStore.query();
        assertThat(map.containsKey(UID)).isTrue();

        TrackedEntityInstance trackedEntityInstance = map.get(UID);
        assertThat(trackedEntityInstance.uid()).isEqualTo(UID);
        assertThat(trackedEntityInstance.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
        assertThat(trackedEntityInstance.trackedEntity()).isEqualTo(TRACKED_ENTITY);

    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_tracked_entity_instance_with_invalid_org_unit_foreign_key() {

        trackedEntityInstanceStore.insert(UID, date, date, CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                "wrong", TRACKED_ENTITY, STATE);

    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_tracked_entity_instance_with_invalid_tracked_entity_foreign_key() {
        trackedEntityInstanceStore.insert(UID, date, date, CREATED_AT_CLIENT, LAST_UPDATED_AT_CLIENT,
                ORGANISATION_UNIT, "wrong", STATE);
    }

    // ToDo: consider introducing conflict resolution strategy
    @Test
    public void not_close_data_base_on_close() {
        assertThat(database().isOpen()).isTrue();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_null_uid() {
        trackedEntityInstanceStore.insert(
                null, date, date, dateString, dateString, ORGANISATION_UNIT, TRACKED_ENTITY, STATE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_null_organisation_unit() {
        trackedEntityInstanceStore.insert(UID, date, date, dateString, dateString, null, TRACKED_ENTITY, STATE);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_null_tracked_entity() {
        trackedEntityInstanceStore.insert(UID, date, date, dateString, dateString, ORGANISATION_UNIT, null, STATE);
    }
}
