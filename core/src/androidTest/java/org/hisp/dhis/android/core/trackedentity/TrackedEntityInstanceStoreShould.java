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
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstanceStoreShould extends AbsStoreTestCase {
    private static final String UID = "test_uid";
    private static final String ORGANISATION_UNIT = "test_organisationUnit";
    private static final String TRACKED_ENTITY = "test_trackedEntity";
    private static final String COORDINATES = "[9,9]";
    private static final FeatureType FEATURE_TYPE = FeatureType.POINT;
    private static final State STATE = State.ERROR;
    private static final String CREATED_AT_CLIENT = "2016-04-28T23:44:28.126";
    private static final String LAST_UPDATED_AT_CLIENT = "2016-04-28T23:44:28.126";

    private final Date date;
    private final String dateString;

    private TrackedEntityInstance trackedEntityInstance;
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
            Columns.TRACKED_ENTITY_TYPE,
            Columns.COORDINATES,
            Columns.FEATURE_TYPE,
            Columns.STATE
    };

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter());
        OrganisationUnit organisationUnit = OrganisationUnitSamples.getOrganisationUnit(ORGANISATION_UNIT);
        ContentValues trackedEntityType = CreateTrackedEntityUtils.create(1L, TRACKED_ENTITY);
        database().insert(OrganisationUnitTableInfo.TABLE_INFO.name(), null, organisationUnit.toContentValues());
        database().insert(TrackedEntityTypeModel.TABLE, null, trackedEntityType);
        trackedEntityInstance = TrackedEntityInstance.builder()
                .uid(UID)
                .created(date)
                .lastUpdated(date)
                .createdAtClient(CREATED_AT_CLIENT)
                .lastUpdatedAtClient(LAST_UPDATED_AT_CLIENT)
                .organisationUnit(ORGANISATION_UNIT)
                .trackedEntityType(TRACKED_ENTITY)
                .coordinates(COORDINATES)
                .featureType(FEATURE_TYPE)
                .state(STATE)
                .build();
    }

    @Test
    public void delete_tei_in_data_base_when_delete_organisation_unit_foreign_key() {
        trackedEntityInstanceStore.insert(trackedEntityInstance);

        database().delete(OrganisationUnitTableInfo.TABLE_INFO.name(),
                BaseIdentifiableObjectModel.Columns.UID + "=?",
                new String[]{ ORGANISATION_UNIT });

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null,
                null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_tracked_entity_instance_in_data_base_when_delete_tracked_entity_foreign_key() {
        trackedEntityInstanceStore.insert(trackedEntityInstance);

        database().delete(TrackedEntityTypeModel.TABLE,
                TrackedEntityTypeModel.Columns.UID + "=?",
                new String[]{ TRACKED_ENTITY} );

        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null,
                null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_set_state_in_data_base_when_update_tracked_entity_instance_stateshouldUpdateTrackedEntityInstanceState() throws Exception {
        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(Columns.UID, UID);
        trackedEntityInstance.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        trackedEntityInstance.put(Columns.TRACKED_ENTITY_TYPE, TRACKED_ENTITY);
        trackedEntityInstance.put(Columns.STATE, STATE.name());

        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);

        String[] projection = {
                Columns.UID,
                Columns.ORGANISATION_UNIT,
                Columns.TRACKED_ENTITY_TYPE,
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
        trackedEntityInstanceContentValues.put(Columns.TRACKED_ENTITY_TYPE, TRACKED_ENTITY);
        trackedEntityInstanceContentValues.put(Columns.STATE, State.TO_POST.name());
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstanceContentValues);

        String[] projection = {Columns.UID};
        Cursor cursor = database().query(TrackedEntityInstanceModel.TABLE, projection, null, null, null, null, null);
        // verify that tei was successfully inserted into database
        assertThatCursor(cursor).hasRow(UID).isExhausted();

        List<TrackedEntityInstance> trackedEntityInstances = trackedEntityInstanceStore.queryTrackedEntityInstancesToPost();

        List<String> teiUids = new ArrayList<>();
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            teiUids.add(trackedEntityInstance.uid());
        }
        assertThat(teiUids.contains(UID)).isTrue();

        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceStore.selectByUid(UID);
        assertThat(trackedEntityInstance.uid()).isEqualTo(UID);
        assertThat(trackedEntityInstance.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
        assertThat(trackedEntityInstance.trackedEntityType()).isEqualTo(TRACKED_ENTITY);

    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_tracked_entity_instance_with_invalid_org_unit_foreign_key() {
        trackedEntityInstanceStore.insert(trackedEntityInstance.toBuilder().organisationUnit("wrong").build());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_tracked_entity_instance_with_invalid_tracked_entity_foreign_key() {
        trackedEntityInstanceStore.insert(trackedEntityInstance.toBuilder().trackedEntityType("wrong").build());
    }

    // ToDo: consider introducing conflict resolution strategy
    @Test
    public void not_close_data_base_on_close() {
        assertThat(database().isOpen()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_sqlite_constraint_exception_when_insert_null_uid() {
        trackedEntityInstanceStore.insert(trackedEntityInstance.toBuilder().uid(null).build());
    }

    //Removed NOT NULL constraint in 2.30 to allow TEI with just a UID
    //@Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_null_organisation_unit() {
        trackedEntityInstanceStore.insert(trackedEntityInstance.toBuilder().organisationUnit(null).build());

    }

    //Removed NOT NULL constraint in 2.30 to allow TEI with just a UID
    //@Test(expected = SQLiteConstraintException.class)
    public void throw_sqlite_constraint_exception_when_insert_null_tracked_entity() {
        trackedEntityInstanceStore.insert(trackedEntityInstance.toBuilder().trackedEntityType(null).build());

    }
}