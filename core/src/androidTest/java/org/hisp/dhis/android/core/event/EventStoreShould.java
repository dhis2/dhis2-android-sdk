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

package org.hisp.dhis.android.core.event;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOptionComboModel;
import org.hisp.dhis.android.core.category.CategoryOptionModel;
import org.hisp.dhis.android.core.category.CreateCategoryOptionComboUtils;
import org.hisp.dhis.android.core.category.CreateCategoryOptionUtils;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.enrollment.CreateEnrollmentUtils;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel.Columns;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.CreateProgramStageUtils;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityInstanceUtils;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventStoreShould extends AbsStoreTestCase {
    private static final String[] EVENT_PROJECTION = {
            Columns.UID,
            Columns.ENROLLMENT_UID,
            Columns.CREATED, // created
            Columns.LAST_UPDATED, // lastUpdated
            Columns.CREATED_AT_CLIENT,
            Columns.LAST_UPDATED_AT_CLIENT,
            Columns.STATUS,
            Columns.LATITUDE,
            Columns.LONGITUDE,
            Columns.PROGRAM,
            Columns.PROGRAM_STAGE,
            Columns.ORGANISATION_UNIT,
            Columns.EVENT_DATE, // eventDate
            Columns.COMPLETE_DATE, // completedDate
            Columns.DUE_DATE, // dueDate
            Columns.STATE,
            Columns.ATTRIBUTE_CATEGORY_OPTIONS,
            Columns.ATTRIBUTE_OPTION_COMBO,
            Columns.TRACKED_ENTITY_INSTANCE
    };
    private static final String EVENT_UID = "test_uid";
    private static final String ENROLLMENT_UID = "test_enrollment";
    private static final String TRACKED_ENTITY_INSTANCE = "test_tracked_entity_instance";
    private static final EventStatus STATUS = EventStatus.ACTIVE;
    private static final String LATITUDE = "10.832152";
    private static final String LONGITUDE = "59.345231";
    private static final String PROGRAM = "test_program";
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final State STATE = State.TO_POST;
    private static final String CREATED_AT_CLIENT = "2016-04-28T23:44:28.126";
    private static final String LAST_UPDATED_AT_CLIENT = "2016-04-28T23:44:28.126";

    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";
    private static final String ATTRIBUTE_CATEGORY_OPTION_UID = "attributeCategoryOptionUid";
    private static final String ATTRIBUTE_OPTION_COMBO_UID = "attributeOptionComboUid";
    private final Date date;

    private final String dateString;
    private final String WRONG_UID = "wrong";

    private EventStore eventStore;

    public EventStoreShould() throws ParseException {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.eventStore = new EventStoreImpl(databaseAdapter());
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
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.create(TRACKED_ENTITY_INSTANCE,
                ORGANISATION_UNIT, TRACKED_ENTITY_UID);
        ContentValues enrollment = CreateEnrollmentUtils.create(
                ENROLLMENT_UID, PROGRAM, ORGANISATION_UNIT, TRACKED_ENTITY_INSTANCE
        );

        ContentValues categoryOptionCombo = CreateCategoryOptionComboUtils.create(
                1L, ATTRIBUTE_OPTION_COMBO_UID
        );
        database().insert(CategoryOptionComboModel.TABLE, null, categoryOptionCombo);
        ContentValues categoryOption = CreateCategoryOptionUtils.create(
                1l, ATTRIBUTE_CATEGORY_OPTION_UID
        );
        database().insert(CategoryOptionModel.TABLE, null, categoryOption);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(ProgramStageModel.TABLE, null, programStage);
        database().insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);
        database().insert(EnrollmentModel.TABLE, null, enrollment);
    }

    @Test
    @MediumTest
    public void persist_event_in_data_base_after_insert() {
        long rowId = eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date, // created
                date, // lastUpdated
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date, // eventDate
                date, // completedDate
                date, // dueDate
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );
        Cursor cursor = database().query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                EVENT_UID,
                ENROLLMENT_UID,
                dateString, // created
                dateString, // lastUpdated
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                dateString, // eventDate
                dateString, // completedDate
                dateString, // dueDate
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void persist_deferrable_event_in_data_base_after_insert() {
        final String deferredProgram = "deferredProgram";
        final String deferredProgramStage = "deferredProgramStage";
        final String deferredOrganisationUnit = "deferredOrganisationUnit";

        ContentValues program = CreateProgramUtils.create(11L, deferredProgram,
                RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(11L, deferredOrganisationUnit);
        ContentValues programStage = CreateProgramStageUtils.create(11L, deferredProgramStage, PROGRAM);
        database().beginTransaction();
        long rowId = eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date, // created
                date, // lastUpdated
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                deferredProgram,
                deferredProgramStage,
                deferredOrganisationUnit,
                date, // eventDate
                date, // completedDate
                date, // dueDate
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );


        database().insert(ProgramModel.TABLE, null, program);
        database().insert(OrganisationUnitModel.TABLE, null, organisationUnit);
        database().insert(ProgramStageModel.TABLE, null, programStage);
        database().setTransactionSuccessful();
        database().endTransaction();

        Cursor cursor = database().query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                EVENT_UID,
                ENROLLMENT_UID,
                dateString, // created
                dateString, // lastUpdated
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                deferredProgram,
                deferredProgramStage,
                deferredOrganisationUnit,
                dateString, // eventDate
                dateString, // completedDate
                dateString, // dueDate
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void persist_event_nullable_in_data_base_after_insert() {

        long rowId = eventStore.insert(EVENT_UID, ENROLLMENT_UID, null, null, null, null, null, null, null, PROGRAM,
                PROGRAM_STAGE, ORGANISATION_UNIT, null, null, null, null,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE);
        Cursor cursor = database().query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(EVENT_UID, ENROLLMENT_UID, null, null, null, null, null, null, null, PROGRAM,
                PROGRAM_STAGE, ORGANISATION_UNIT, null, null, null, null,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_event_in_data_base_after_delete_program_foreign_key() {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );

        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + "=?", new String[]{PROGRAM});
        Cursor cursor = database().query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_event_in_data_base_after_delete_program_stage_foreign_key() {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );

        database().delete(ProgramStageModel.TABLE, ProgramStageModel.Columns.UID + "=?", new String[]{PROGRAM_STAGE});
        Cursor cursor = database().query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_event_in_data_base_after_delete_organisation_unit_foreign_key() {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );

        database().delete(OrganisationUnitModel.TABLE,
                OrganisationUnitModel.Columns.UID + "=?", new String[]{ORGANISATION_UNIT});

        Cursor cursor = database().query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void update_event_in_data_base_after_update() throws Exception {
        ContentValues event = new ContentValues();
        event.put(Columns.UID, EVENT_UID);
        event.put(Columns.EVENT_DATE, dateString);
        event.put(Columns.PROGRAM, PROGRAM);
        event.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        event.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        database().insert(EventModel.TABLE, null, event);

        String[] projection = {Columns.UID, Columns.EVENT_DATE};
        Cursor cursor = database().query(EventModel.TABLE, projection, null, null, null, null, null);

        // check that event was successfully inserted into database
        assertThatCursor(cursor).hasRow(EVENT_UID, dateString).isExhausted();

        Date updatedDate = new Date();

        int updated = eventStore.update(EVENT_UID, null, null, null, null, null, null, null, null,
                PROGRAM, PROGRAM_STAGE, ORGANISATION_UNIT, updatedDate, null, null, null,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID, TRACKED_ENTITY_INSTANCE,
                EVENT_UID);

        assertTrue(updated==1);

        cursor = database().query(EventModel.TABLE, projection, null, null, null, null, null);

        String updatedDateString = BaseIdentifiableObject.DATE_FORMAT.format(updatedDate);

        // check that event was updated with updatedDateString
        assertThatCursor(cursor).hasRow(EVENT_UID, updatedDateString).isExhausted();
    }

    @Test
    @MediumTest
    public void delete_event_in_data_base_after_delete() throws Exception {
        ContentValues event = new ContentValues();
        event.put(Columns.UID, EVENT_UID);
        event.put(Columns.PROGRAM, PROGRAM);
        event.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        event.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        database().insert(EventModel.TABLE, null, event);

        String[] projection = {Columns.UID, Columns.ORGANISATION_UNIT};
        Cursor cursor = database().query(EventModel.TABLE, projection, null, null, null, null, null);

        // check that event was successfully inserted into database
        assertThatCursor(cursor).hasRow(EVENT_UID, ORGANISATION_UNIT).isExhausted();

        eventStore.delete(EVENT_UID);

        cursor = database().query(EventModel.TABLE, projection, null, null, null, null, null);

        // check that event is deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    @MediumTest
    public void update_event_state_in_database_after_set_state() throws Exception {
        ContentValues event = new ContentValues();
        event.put(Columns.UID, EVENT_UID);
        event.put(Columns.PROGRAM, PROGRAM);
        event.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        event.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        event.put(Columns.STATE, STATE.name());
        database().insert(EventModel.TABLE, null, event);

        String[] projection = {Columns.UID, Columns.STATE};
        Cursor cursor = database().query(EventModel.TABLE, projection, null, null, null, null, null);

        // check that event was successfully inserted into database
        assertThatCursor(cursor).hasRow(EVENT_UID, STATE).isExhausted();
        State updatedState = State.ERROR;
        eventStore.setState(EVENT_UID, updatedState);

        cursor = database().query(EventModel.TABLE, projection, null, null, null, null, null);

        // check that state was updated
        assertThatCursor(cursor).hasRow(EVENT_UID, updatedState);

    }

    @Test
    @MediumTest
    public void return_list_of_events_after_query() throws Exception {
        ContentValues eventContentValues = new ContentValues();
        eventContentValues.put(Columns.UID, EVENT_UID);
        eventContentValues.put(Columns.PROGRAM, PROGRAM);
        eventContentValues.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        eventContentValues.put(Columns.ORGANISATION_UNIT, ORGANISATION_UNIT);
        eventContentValues.put(Columns.ENROLLMENT_UID, ENROLLMENT_UID);
        eventContentValues.put(Columns.STATUS, STATUS.name());
        eventContentValues.put(Columns.EVENT_DATE, dateString);
        eventContentValues.put(Columns.COMPLETE_DATE, dateString);
        eventContentValues.put(Columns.LATITUDE, dateString);
        eventContentValues.put(Columns.STATE, STATE.name());
        database().insert(EventModel.TABLE, null, eventContentValues);

        String dataElementUid = "de_uid";
        ContentValues dataElement = new ContentValues();
        dataElement.put(DataElementModel.Columns.UID, dataElementUid);
        database().insert(DataElementModel.TABLE, null, dataElement);

        ContentValues dataValue = new ContentValues();
        dataValue.put(TrackedEntityDataValueModel.Columns.EVENT, EVENT_UID);
        dataValue.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, dataElementUid);
        dataValue.put(TrackedEntityDataValueModel.Columns.VALUE, "some_value");
        database().insert(TrackedEntityDataValueModel.TABLE, null, dataValue);

        String[] dataValueProjection = {TrackedEntityDataValueModel.Columns.EVENT};
        Cursor dataValueCursor = database().query(TrackedEntityDataValueModel.TABLE, dataValueProjection,
                null, null, null, null, null);
        assertThatCursor(dataValueCursor).hasRow(EVENT_UID).isExhausted();

        String[] projection = {Columns.UID};
        Cursor cursor = database().query(EventModel.TABLE, projection, Columns.UID + " =?",
                new String[]{EVENT_UID}, null, null, null);
        // verify that eventContentValues was successfully inserted
        assertThatCursor(cursor).hasRow(EVENT_UID).isExhausted();


        // query for events
        Map<String, List<Event>> eventMap = eventStore.queryEventsAttachedToEnrollmentToPost();
        assertThat(eventMap.size()).isEqualTo(1);

        List<Event> events = eventMap.get(ENROLLMENT_UID);
        assertThat(events.size()).isEqualTo(1);

        Event event = events.get(0);
        // check that uid and data values is included
        assertThat(event.uid()).isEqualTo(EVENT_UID);
    }

    @Test
    @MediumTest
    public void return_empty_list_with_no_events_after_query() throws Exception {
        Map<String, List<Event>> events = eventStore.queryEventsAttachedToEnrollmentToPost();

        assertThat(events.size()).isEqualTo(0);
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_exception_after_persist_event_with_invalid_program_foreign_key() {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                WRONG_UID, //supply wrong uid
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_exception_after_persist_event_with_invalid_program_stage_foreign_key() throws ParseException {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                WRONG_UID, //supply wrong uid
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_exception_after_persist_event_with_invalid_organisation_unit_foreign_key() {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                WRONG_UID, //supply wrong uid
                date,
                date,
                date,
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );
    }


    @Test(expected = SQLiteConstraintException.class)
    @MediumTest
    public void throw_exception_after_persist_event_with_invalid_enrollment_foreign_key() {
        eventStore.insert(
                EVENT_UID,
                WRONG_UID, // supply wrong uid
                date,
                date,
                CREATED_AT_CLIENT,
                LAST_UPDATED_AT_CLIENT,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE,
                ATTRIBUTE_CATEGORY_OPTION_UID,
                ATTRIBUTE_OPTION_COMBO_UID,
                TRACKED_ENTITY_INSTANCE
        );
    }


}
