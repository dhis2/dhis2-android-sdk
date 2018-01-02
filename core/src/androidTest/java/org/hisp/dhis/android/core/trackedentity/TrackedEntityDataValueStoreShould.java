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

import static com.google.common.truth.Truth.assertThat;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.filters.MediumTest;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValueStoreShould extends AbsStoreTestCase {

    private static final long TRACKED_ENTITY_ID = 1L;

    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";
    private static final String EVENT_1 = "test_event_1";
    private static final String EVENT_2 = "test_event_2";

    private static final String DATA_ELEMENT_1 = "test_dataElement_1";
    private static final String DATA_ELEMENT_2 = "test_dataElement_2";

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

        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID,
                TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null,
                TRACKED_ENTITY_UID);
        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L,
                ORGANISATION_UNIT);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(
                TRACKED_ENTITY_INSTANCE, ORGANISATION_UNIT, TRACKED_ENTITY_UID);

        ContentValues enrollment = CreateEnrollmentUtils.create(
                ENROLLMENT, PROGRAM, ORGANISATION_UNIT, TRACKED_ENTITY_INSTANCE);

        ContentValues event1 = CreateEventUtils.create(EVENT_1, PROGRAM, PROGRAM_STAGE,
                ORGANISATION_UNIT, ENROLLMENT);
        ContentValues event2 = CreateEventUtils.create(EVENT_2, PROGRAM, PROGRAM_STAGE,
                ORGANISATION_UNIT, ENROLLMENT);
        ContentValues dataElement1 = CreateDataElementUtils.create(1L, DATA_ELEMENT_1, null);
        ContentValues dataElement2 = CreateDataElementUtils.create(2L, DATA_ELEMENT_2, null);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null,
                relationshipType);
        database().insert(ProgramModel.TABLE, null, program);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(ProgramStageModel.TABLE, null, programStage);
        database().insert(DataElementModel.TABLE, null, dataElement1);
        database().insert(DataElementModel.TABLE, null, dataElement2);
        database().insert(TrackedEntityInstanceModel.TABLE, null,
                trackedEntityInstance);
        database().insert(EnrollmentModel.TABLE, null, enrollment);
        database().insert(EventModel.TABLE, null, event1);
        database().insert(EventModel.TABLE, null, event2);
    }

    @Test
    @MediumTest
    public void insert_tracked_entity_data_value_in_data_base_when_insert() {
        long rowId = trackedEntityDataValueStore.insert(
                EVENT_1,
                date,
                date,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                EVENT_1,
                dateString,
                dateString,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE)
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_deferrable_tracked_entity_data_value_in_data_base_when_insert() {
        final String deferredEvent = "deferredEvent";
        database().beginTransaction();
        long rowId = trackedEntityDataValueStore.insert(
                deferredEvent,
                date,
                date,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        ContentValues event = CreateEventUtils.create(deferredEvent, PROGRAM, PROGRAM_STAGE,
                ORGANISATION_UNIT, null);
        long eventId = database().insert(EventModel.TABLE, null, event);

        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE, PROJECTION, null, null,
                null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                deferredEvent,
                dateString,
                dateString,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE)
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_tracked_entity_data_value_in_data_base_with_deferrable_data_element_when_inserte() {
        final String deferredDataElement = "deferredDataElement";
        database().beginTransaction();
        long rowId = trackedEntityDataValueStore.insert(
                EVENT_1,
                date,
                date,
                deferredDataElement,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        ContentValues dataElement = CreateDataElementUtils.create(3L, deferredDataElement, null);
        database().insert(DataElementModel.TABLE, null, dataElement);

        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE, PROJECTION, null, null,
                null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                EVENT_1,
                dateString,
                dateString,
                deferredDataElement,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE)
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void insert_nullable_tracked_entity_data_value_in_data_base_when_insert_null_fields() {
        long rowId = trackedEntityDataValueStore.insert(EVENT_1, null, null, DATA_ELEMENT_1, null, null, null);

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(EVENT_1, null, null, DATA_ELEMENT_1, null, null,
                null).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_tracked_entity_data_value_when_delete_event_foreign_key() {
        trackedEntityDataValueStore.insert(
                EVENT_1,
                date,
                date,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        database().delete(EventModel.TABLE, EventModel.Columns.UID + "=?", new String[]{EVENT_1});

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_tracked_entity_data_value_when_delete_data_element_foreign_key() {
        trackedEntityDataValueStore.insert(
                EVENT_1,
                date,
                date,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        database().delete(DataElementModel.TABLE, DataElementModel.Columns.UID + "=?",
                new String[]{DATA_ELEMENT_1});

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void return_list_of_tracked_entity_data_value_when_query_tracked_entity_data_value() throws Exception {
        ContentValues dataValue = new ContentValues();
        dataValue.put(Columns.CREATED, dateString);
        dataValue.put(Columns.LAST_UPDATED, dateString);
        dataValue.put(Columns.PROVIDED_ELSEWHERE, 0);
        dataValue.put(Columns.STORED_BY, STORED_BY);
        dataValue.put(Columns.VALUE, VALUE);
        dataValue.put(Columns.EVENT, EVENT_1);
        dataValue.put(Columns.DATA_ELEMENT, DATA_ELEMENT_1);
        database().insert(TrackedEntityDataValueModel.TABLE, null, dataValue);

        String[] projection = {Columns.EVENT};
        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE, projection,
                Columns.EVENT + "=?",
                new String[]{EVENT_1}, null, null, null);

        // verify that TEDV was successfully inserted
        assertThatCursor(cursor).hasRow(EVENT_1).isExhausted();

        Map<String, List<TrackedEntityDataValue>> map =
                trackedEntityDataValueStore.queryTrackedEntityDataValues(Boolean.FALSE);

        assertThat(map.size()).isEqualTo(1);

        List<TrackedEntityDataValue> dataValues = map.get(EVENT_1);
        assertThat(dataValues.size()).isEqualTo(1);

        TrackedEntityDataValue trackedEntityDataValue = dataValues.get(0);
        assertThat(trackedEntityDataValue.created()).isEqualTo(date);
        assertThat(trackedEntityDataValue.lastUpdated()).isEqualTo(date);
        assertThat(trackedEntityDataValue.dataElement()).isEqualTo(DATA_ELEMENT_1);
        assertThat(trackedEntityDataValue.providedElsewhere()).isFalse();
        assertThat(trackedEntityDataValue.storedBy()).isEqualTo(STORED_BY);
        assertThat(trackedEntityDataValue.value()).isEqualTo(VALUE);

    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_tracked_entity_data_value_with_invalid_event() {
        trackedEntityDataValueStore.insert(
                "wrong",
                date,
                date,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_illegal_argument_exception_wheninsert_tracked_entity_data_value_with_invalid_data_element() {
        trackedEntityDataValueStore.insert(
                EVENT_1,
                date,
                date,
                "wrong",
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
    }

    // ToDo: consider introducing conflict resolution strategy

    @Test
    @MediumTest
    public void update_tracked_entity_data_value() {
        database().insert(TrackedEntityDataValueModel.TABLE, null,
                CreateTrackedEntityDataValueUtils.create(1L, EVENT_1, DATA_ELEMENT_1));

        int updateReturn = trackedEntityDataValueStore.update(EVENT_1,
                date,
                date,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE);

        Cursor cursor = database().query(TrackedEntityDataValueModel.TABLE,
                PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(EVENT_1,
                dateString,
                dateString,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE));

        assertThat(updateReturn).isEqualTo(1);
    }

    @Test
    @MediumTest
    public void delete_tracked_entity_data_value_by_event_and_data_element_uids() {
        database().insert(TrackedEntityDataValueModel.TABLE, null,
                CreateTrackedEntityDataValueUtils.create(1L, EVENT_1, DATA_ELEMENT_1));

        database().insert(TrackedEntityDataValueModel.TABLE, null,
                CreateTrackedEntityDataValueUtils.create(2L, EVENT_1, DATA_ELEMENT_2));

        database().insert(TrackedEntityDataValueModel.TABLE, null,
                CreateTrackedEntityDataValueUtils.create(3L, EVENT_2, DATA_ELEMENT_1));


        trackedEntityDataValueStore.deleteByEventAndDataElementUIds(EVENT_1,
                Arrays.asList(DATA_ELEMENT_1, DATA_ELEMENT_2));

        List<TrackedEntityDataValue> dataValuesByEvent =
                trackedEntityDataValueStore.queryTrackedEntityDataValues(EVENT_1);

        Map<String, List<TrackedEntityDataValue>> allDataValues =
                trackedEntityDataValueStore.queryTrackedEntityDataValues(false);


        Assert.assertThat(dataValuesByEvent.size(), is(0));
        Assert.assertThat(allDataValues.get(EVENT_1), is(nullValue()));
        Assert.assertThat(allDataValues.get(EVENT_2).size(), is(1));
    }


    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        trackedEntityDataValueStore.insert(null, date, date, DATA_ELEMENT_1, STORED_BY, VALUE, PROVIDED_ELSEWHERE);
    }

    @Test(expected = IllegalArgumentException.class)
    @MediumTest
    public void throw_illegal_argument_exception_when_insert_null_data_element() {
        trackedEntityDataValueStore.insert(EVENT_1, date, date, null, STORED_BY, VALUE, PROVIDED_ELSEWHERE);
    }
}
