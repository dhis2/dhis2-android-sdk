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
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.enrollment.CreateEnrollmentUtils;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.CreateEventUtils;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.CreateProgramStageUtils;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValueStoreTests extends AbsStoreTestCase {

    private static final long TRACKED_ENTITY_ID = 1L;

    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";
    private static final String EVENT = "test_event";

    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String STORED_BY = "test_storedBy";
    private static final String VALUE = "test_value";
    private static final Boolean PROVIDED_ELSEWHERE = false;
    private static final String ORGANISATION_UNIT = "test_orgUnit";

    private static final String PROGRAM = "test_program";
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String TRACKED_ENTITY_INSTANCE = "test_tei";
    private static final String ENROLLMENT = "test_enrollment";
    public static final String[] PROJECTION = {
            TrackedEntityDataValueModel.Columns.EVENT,
            TrackedEntityDataValueModel.Columns.CREATED,
            TrackedEntityDataValueModel.Columns.LAST_UPDATED,
            TrackedEntityDataValueModel.Columns.DATA_ELEMENT,
            TrackedEntityDataValueModel.Columns.STORED_BY,
            TrackedEntityDataValueModel.Columns.VALUE,
            TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE
    };

    private Date date;
    private String dateString;
    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);


        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(databaseAdapter());

        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANISATION_UNIT);
        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);

        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE, ORGANISATION_UNIT, TRACKED_ENTITY_UID
        );

        ContentValues enrollment = CreateEnrollmentUtils.create(
                ENROLLMENT, PROGRAM, ORGANISATION_UNIT, TRACKED_ENTITY_INSTANCE
        );

        ContentValues event = CreateEventUtils.create(EVENT, PROGRAM, PROGRAM_STAGE, ORGANISATION_UNIT, ENROLLMENT);
        ContentValues dataElement = CreateDataElementUtils.create(1L, DATA_ELEMENT, null);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(ProgramStageModel.TABLE, null, programStage);
        database().insert(DataElementModel.TABLE, null, dataElement);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().insert(EnrollmentModel.TABLE, null, enrollment);
        database().insert(EventModel.TABLE, null, event);

    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        long rowId = trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                EVENT,
                dateString,
                dateString,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE)
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistDeferrableEventInDatabase() {
        final String deferredEvent = "deferredEvent";
        database().beginTransaction();
        long rowId = trackedEntityDataValueStore.insert(
                deferredEvent,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        ContentValues event = CreateEventUtils.create(deferredEvent, PROGRAM, PROGRAM_STAGE, ORGANISATION_UNIT, null);
        database().insert(EventModel.TABLE, null, event);

        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                deferredEvent,
                dateString,
                dateString,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE)
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistDeferrableDataElementInDatabase() {
        final String deferredDataElement = "deferredDataElement";
        database().beginTransaction();
        long rowId = trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                deferredDataElement,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        ContentValues dataElement = CreateDataElementUtils.create(2L, deferredDataElement, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                EVENT,
                dateString,
                dateString,
                deferredDataElement,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE)
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistNullableRowInDatabase() {
        long rowId = trackedEntityDataValueStore.insert(EVENT, null, null, DATA_ELEMENT, null, null, null);

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(EVENT, null, null, DATA_ELEMENT, null, null, null).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutEvent_shouldThrowException() {
        trackedEntityDataValueStore.insert(
                null,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutDataElement_shouldThrowException() {
        trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                null,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
    }

    @Test
    public void delete_shouldDeleteTrackedEntityDataValueWhenDeletingEventForeignKey() {
        trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        database().delete(EventModel.TABLE, EventModel.Columns.UID + "=?", new String[]{EVENT});

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteTrackedEntityDataValueWhenDeletingDataElementForeignKey() {
        trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        database().delete(DataElementModel.TABLE, DataElementModel.Columns.UID + "=?", new String[]{DATA_ELEMENT});

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void query_shouldReturnListOfTrackedEntityDataValues() throws Exception {
        ContentValues dataValue = new ContentValues();
        dataValue.put(Columns.CREATED, dateString);
        dataValue.put(Columns.LAST_UPDATED, dateString);
        dataValue.put(Columns.PROVIDED_ELSEWHERE, 0);
        dataValue.put(Columns.STORED_BY, STORED_BY);
        dataValue.put(Columns.VALUE, VALUE);
        dataValue.put(Columns.EVENT, EVENT);
        dataValue.put(Columns.DATA_ELEMENT, DATA_ELEMENT);
        database().insert(TrackedEntityDataValueModel.TABLE, null, dataValue);

        String[] projection = {Columns.EVENT};
        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE, projection, Columns.EVENT + "=?",
                new String[]{EVENT}, null, null, null);

        // verify that TEDV was successfully inserted
        assertThatCursor(cursor).hasRow(EVENT).isExhausted();

        Map<String, List<TrackedEntityDataValue>> map = trackedEntityDataValueStore.query();
        assertThat(map.size()).isEqualTo(1);

        List<TrackedEntityDataValue> dataValues = map.get(EVENT);
        assertThat(dataValues.size()).isEqualTo(1);

        TrackedEntityDataValue trackedEntityDataValue = dataValues.get(0);
        assertThat(trackedEntityDataValue.created()).isEqualTo(date);
        assertThat(trackedEntityDataValue.lastUpdated()).isEqualTo(date);
        assertThat(trackedEntityDataValue.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(trackedEntityDataValue.providedElsewhere()).isFalse();
        assertThat(trackedEntityDataValue.storedBy()).isEqualTo(STORED_BY);
        assertThat(trackedEntityDataValue.value()).isEqualTo(VALUE);

    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistTrackedEntityDataValueWithInvalidEvent() {
        trackedEntityDataValueStore.insert(
                "wrong",
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
    }


    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistTrackedEntityDataValueWithInvalidDataElement() {
        trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                "wrong",
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
    }

    // ToDo: consider introducing conflict resolution strategy

}
