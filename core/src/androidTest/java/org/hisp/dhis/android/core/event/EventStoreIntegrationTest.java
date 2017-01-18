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
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.event.EventModel.Columns;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.CreateProgramStageUtils;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class EventStoreIntegrationTest extends AbsStoreTestCase {
    private static final String[] EVENT_PROJECTION = {
            Columns.UID,
            Columns.ENROLLMENT_UID,
            Columns.CREATED, // created
            Columns.LAST_UPDATED, // lastUpdated
            Columns.STATUS,
            Columns.LATITUDE,
            Columns.LONGITUDE,
            Columns.PROGRAM,
            Columns.PROGRAM_STAGE,
            Columns.ORGANISATION_UNIT,
            Columns.EVENT_DATE, // eventDate
            Columns.COMPLETE_DATE, // completedDate
            Columns.DUE_DATE, // dueDate
            Columns.STATE
    };
    private EventStore eventStore;

    private static final String EVENT_UID = "test_uid";
    private static final String ENROLLMENT_UID = "test_enrollment";
    private static final EventStatus STATUS = EventStatus.ACTIVE;
    private static final String LATITUDE = "10.832152";
    private static final String LONGITUDE = "59.345231";
    private static final String PROGRAM = "test_program";
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final State STATE = State.TO_POST;



    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    // timestamp
    private final Date date;
    private final String dateString;

    private final String WRONG_UID = "wrong";

    public EventStoreIntegrationTest() throws ParseException {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.eventStore = new EventStoreImpl(database());

        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TRACKED_ENTITY, null, trackedEntity);
        database().insert(RelationshipTypeModel.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(ProgramModel.PROGRAM, null, program);

        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANISATION_UNIT);
        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);

        database().insert(OrganisationUnitModel.ORGANISATION_UNIT, null, organisationUnit);
        database().insert(ProgramStageModel.PROGRAM_STAGE, null, programStage);
    }

    @Test
    public void insert_shouldPersistEventInDatabase() {

        long rowId = eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date, // created
                date, // lastUpdated
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date, // eventDate
                date, // completedDate
                date, // dueDate
                STATE
        );
        Cursor cursor = database().query(EventModel.EVENT, EVENT_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                EVENT_UID,
                ENROLLMENT_UID,
                dateString, // created
                dateString, // lastUpdated
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                dateString, // eventDate
                dateString, // completedDate
                dateString, // dueDate
                STATE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistEventNullableInDatabase() {

        long rowId = eventStore.insert(EVENT_UID, ENROLLMENT_UID, null, null, null, null, null, PROGRAM,
                PROGRAM_STAGE, ORGANISATION_UNIT, null, null, null, null);
        Cursor cursor = database().query(EventModel.EVENT, EVENT_PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(EVENT_UID, ENROLLMENT_UID, null, null, null, null, null, PROGRAM,
                PROGRAM_STAGE, ORGANISATION_UNIT, null, null, null, null).isExhausted();
    }

    @Test
    public void delete_shouldDeleteEventWhenDeletingProgramForeignKey() {

        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE
        );

        database().delete(ProgramModel.PROGRAM, ProgramModel.Columns.UID + "=?", new String[]{PROGRAM});
        Cursor cursor = database().query(EventModel.EVENT, EVENT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteEventWhenDeletingProgramStageForeignKey() {

        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE
        );

        database().delete(ProgramStageModel.PROGRAM_STAGE, ProgramStageModel.Columns.UID + "=?", new String[]{PROGRAM_STAGE});
        Cursor cursor = database().query(EventModel.EVENT, EVENT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldDeleteEventWhenDeletingOrganisationUnitForeignKey() {

        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE
        );

        database().delete(OrganisationUnitModel.ORGANISATION_UNIT,
                OrganisationUnitModel.Columns.UID + "=?", new String[]{ORGANISATION_UNIT});

        Cursor cursor = database().query(EventModel.EVENT, EVENT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistEventWithInvalidProgramForeignKey() {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                STATUS,
                LATITUDE,
                LONGITUDE,
                WRONG_UID, //supply wrong uid
                PROGRAM_STAGE,
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistEventWithInvalidProgramStageForeignKey() throws ParseException {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                WRONG_UID, //supply wrong uid
                ORGANISATION_UNIT,
                date,
                date,
                date,
                STATE
        );
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistEventWithInvalidOrganisationUnitForeignKey() {
        eventStore.insert(
                EVENT_UID,
                ENROLLMENT_UID,
                date,
                date,
                STATUS,
                LATITUDE,
                LONGITUDE,
                PROGRAM,
                PROGRAM_STAGE,
                WRONG_UID, //supply wrong uid
                date,
                date,
                date,
                STATE
        );
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        eventStore.close();
        assertThat(database().isOpen()).isTrue();
    }
}
