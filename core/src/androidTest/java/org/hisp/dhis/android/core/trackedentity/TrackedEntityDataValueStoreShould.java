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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples;
import org.hisp.dhis.android.core.dataelement.CreateDataElementUtils;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.enrollment.CreateEnrollmentUtils;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.event.CreateEventUtils;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.program.CreateProgramStageUtils;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramStageTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo.Columns;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValueStoreShould extends BaseRealIntegrationTest {

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
            TrackedEntityDataValueTableInfo.Columns.EVENT,
            TrackedEntityDataValueFields.CREATED,
            TrackedEntityDataValueFields.LAST_UPDATED,
            TrackedEntityDataValueFields.DATA_ELEMENT,
            TrackedEntityDataValueFields.STORED_BY,
            TrackedEntityDataValueFields.VALUE,
            TrackedEntityDataValueFields.PROVIDED_ELSEWHERE
    };

    private Date date;
    private String dateString;
    private TrackedEntityDataValue trackedEntityDataValue;
    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);

        trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter());

        ContentValues trackedEntityType = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID,
                TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null,
                TRACKED_ENTITY_UID);
        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);
        OrganisationUnit organisationUnit = OrganisationUnitSamples.getOrganisationUnit(ORGANISATION_UNIT);
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

        database().insert(TrackedEntityTypeTableInfo.TABLE_INFO.name(), null, trackedEntityType);
        database().insert(RelationshipTypeTableInfo.TABLE_INFO.name(), null,
                relationshipType);
        database().insert(ProgramTableInfo.TABLE_INFO.name(), null, program);
        database().insert(OrganisationUnitTableInfo.TABLE_INFO.name(), null, organisationUnit.toContentValues());
        database().insert(ProgramStageTableInfo.TABLE_INFO.name(), null, programStage);
        database().insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement1);
        database().insert(DataElementTableInfo.TABLE_INFO.name(), null, dataElement2);
        database().insert(TrackedEntityInstanceTableInfo.TABLE_INFO.name(), null,
                trackedEntityInstance);
        database().insert(EnrollmentTableInfo.TABLE_INFO.name(), null, enrollment);
        database().insert(EventTableInfo.TABLE_INFO.name(), null, event1);
        database().insert(EventTableInfo.TABLE_INFO.name(), null, event2);

        trackedEntityDataValue = TrackedEntityDataValue.builder()
                .event(EVENT_1)
                .created(date)
                .lastUpdated(date)
                .dataElement(DATA_ELEMENT_1)
                .storedBy(STORED_BY)
                .value(VALUE)
                .providedElsewhere(PROVIDED_ELSEWHERE)
                .build();
    }

    @Test
    public void insert_tracked_entity_data_value_in_data_base_when_insert() {
        long rowId = trackedEntityDataValueStore.insert(trackedEntityDataValue);
        Cursor cursor = database().query(TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
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
        cursor.close();
    }

    @Test
    public void insert_nullable_tracked_entity_data_value_in_data_base_when_insert_null_fields() {
        long rowId = trackedEntityDataValueStore.insert(trackedEntityDataValue.toBuilder()
                .created(null)
                .lastUpdated(null)
                .storedBy(null)
                .value(null)
                .providedElsewhere(null).build());

        Cursor cursor = database().query(TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(EVENT_1, null, null, DATA_ELEMENT_1, null, null,
                null).isExhausted();
        cursor.close();
    }

    @Test
    public void delete_tracked_entity_data_value_when_delete_event_foreign_key() {
        trackedEntityDataValueStore.insert(trackedEntityDataValue);

        database().delete(EventTableInfo.TABLE_INFO.name(), EventTableInfo.Columns.UID + "=?",
                new String[]{EVENT_1});

        Cursor cursor = database().query(TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
        cursor.close();
    }

    //@Test
    //TODO Pendding test
    public void delete_tracked_entity_data_value_when_delete_data_element_foreign_key() {
        trackedEntityDataValueStore.insert(trackedEntityDataValue);

        database().delete(DataElementTableInfo.TABLE_INFO.name(),
                BaseIdentifiableObjectModel.Columns.UID + "=?",
                new String[]{DATA_ELEMENT_1});

        Cursor cursor = database().query(TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
        cursor.close();
    }

    @Test
    public void return_list_of_tracked_entity_data_value_when_query_tracked_entity_data_value() throws Exception {
        trackedEntityDataValueStore.insert(trackedEntityDataValue);

        String[] projection = {Columns.EVENT};
        Cursor cursor =
                database().query(TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                        projection,
                Columns.EVENT + "=?",
                new String[]{EVENT_1}, null, null, null);
        assertThatCursor(cursor).hasRow(EVENT_1).isExhausted();
        cursor.close();

        Map<String, List<TrackedEntityDataValue>> map =
                trackedEntityDataValueStore.queryTrackerTrackedEntityDataValues();

        assertThat(map.size()).isEqualTo(1);

        List<TrackedEntityDataValue> dataValues = map.get(EVENT_1);
        assertThat(dataValues.size()).isEqualTo(1);

        TrackedEntityDataValue trackedEntityDataValueInDB = dataValues.get(0);
        assertThat(trackedEntityDataValueInDB.created()).isEqualTo(date);
        assertThat(trackedEntityDataValueInDB.lastUpdated()).isEqualTo(date);
        assertThat(trackedEntityDataValueInDB.dataElement()).isEqualTo(DATA_ELEMENT_1);
        assertThat(trackedEntityDataValueInDB.providedElsewhere()).isFalse();
        assertThat(trackedEntityDataValueInDB.storedBy()).isEqualTo(STORED_BY);
        assertThat(trackedEntityDataValueInDB.value()).isEqualTo(VALUE);

    }

    @Test(expected = SQLiteConstraintException.class)
    public void throw_illegal_argument_exception_when_insert_tracked_entity_data_value_with_invalid_event() {
        trackedEntityDataValueStore.insert(trackedEntityDataValue.toBuilder().event("wrong").build());
    }

    //@Test(expected = SQLiteConstraintException.class)
    //TODO Pendding test
    public void throw_illegal_argument_exception_wheninsert_tracked_entity_data_value_with_invalid_data_element() {
        trackedEntityDataValueStore.insert(trackedEntityDataValue.toBuilder().dataElement("wrong").build());
    }

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void update_tracked_entity_data_value() {
        trackedEntityDataValueStore.insert(TrackedEntityDataValue.builder()
                .id(1L)
                .event(EVENT_1)
                .dataElement(DATA_ELEMENT_1).build());

        trackedEntityDataValueStore.updateWhere(trackedEntityDataValue);

        Cursor cursor = database().query(TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(EVENT_1,
                dateString,
                dateString,
                DATA_ELEMENT_1,
                STORED_BY,
                VALUE,
                toInteger(PROVIDED_ELSEWHERE));
        cursor.close();
    }

    @Test
    public void delete_tracked_entity_data_value_by_event_and_data_element_uids() {
        TrackedEntityDataValue.Builder trackedEntityDataValueBuilder =
                TrackedEntityDataValue.builder().event(EVENT_1).dataElement(DATA_ELEMENT_1);

        trackedEntityDataValueStore.insert(trackedEntityDataValueBuilder.id(1L).build());
        trackedEntityDataValueStore.insert(trackedEntityDataValueBuilder.id(2L).dataElement(DATA_ELEMENT_2).build());
        trackedEntityDataValueStore.insert(trackedEntityDataValueBuilder.id(3L).event(EVENT_2).build());

        trackedEntityDataValueStore.deleteByEventAndNotInDataElements(EVENT_1, new ArrayList<>());

        List<TrackedEntityDataValue> dataValuesByEvent =
                trackedEntityDataValueStore.queryTrackedEntityDataValuesByEventUid(EVENT_1);

        Map<String, List<TrackedEntityDataValue>> allDataValues =
                trackedEntityDataValueStore.queryTrackerTrackedEntityDataValues();

        Assert.assertThat(dataValuesByEvent.size(), is(0));
        Assert.assertThat(allDataValues.get(EVENT_1), is(nullValue()));
        Assert.assertThat(allDataValues.get(EVENT_2).size(), is(1));
    }


    @Test(expected = RuntimeException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        trackedEntityDataValueStore.insert(trackedEntityDataValue.toBuilder().event(null).build());

    }

    @Test(expected = RuntimeException.class)
    public void throw_illegal_argument_exception_when_insert_null_data_element() {
        trackedEntityDataValueStore.insert(trackedEntityDataValue.toBuilder().dataElement(null).build());
    }
}