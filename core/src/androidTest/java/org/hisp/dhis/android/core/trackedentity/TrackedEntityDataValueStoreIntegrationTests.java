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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValueStoreIntegrationTests extends AbsStoreTestCase {

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

    public static final String[] TRACKED_ENTITY_DATA_VALUE_PROJECTION = {
            TrackedEntityDataValueModel.Columns.EVENT,
            TrackedEntityDataValueModel.Columns.CREATED,
            TrackedEntityDataValueModel.Columns.LAST_UPDATED,
            TrackedEntityDataValueModel.Columns.DATA_ELEMENT,
            TrackedEntityDataValueModel.Columns.STORED_BY,
            TrackedEntityDataValueModel.Columns.VALUE,
            TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE
    };

    private final Date date;
    private final String dateString;

    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    public TrackedEntityDataValueStoreIntegrationTests() throws ParseException {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(database());

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
        ContentValues event = CreateEventUtils.create(EVENT, PROGRAM, PROGRAM_STAGE, ORGANISATION_UNIT);

        database().insert(TrackedEntityModel.TRACKED_ENTITY, null, trackedEntity);
        database().insert(RelationshipTypeModel.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(ProgramModel.PROGRAM, null, program);
        database().insert(OrganisationUnitModel.ORGANISATION_UNIT, null, organisationUnit);
        database().insert(ProgramStageModel.PROGRAM_STAGE, null, programStage);
        database().insert(EventModel.EVENT, null, event);
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws ParseException {
        long rowId = trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        Cursor cursor = database().query(TrackedEntityDataValueModel.TRACKED_ENTITY_DATA_VALUE,
                TRACKED_ENTITY_DATA_VALUE_PROJECTION, null, null, null, null, null);
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
    public void insert_shouldPersistNullableRowInDatabase() throws ParseException {
        long rowId = trackedEntityDataValueStore.insert(EVENT, null, null, null, null, null, null);

        Cursor cursor = database().query(TrackedEntityDataValueModel.TRACKED_ENTITY_DATA_VALUE,
                TRACKED_ENTITY_DATA_VALUE_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(EVENT, null, null, null, null, null, null).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutForeignKey_shouldThrowException() {
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

    @Test
    public void delete_shouldDeleteTrackedEntityDataValueWhenDeletingEventForeignKey() throws ParseException {
        trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
        database().delete(EventModel.EVENT, EventModel.Columns.UID + "=?", new String[]{EVENT});

        Cursor cursor = database().query(TrackedEntityDataValueModel.TRACKED_ENTITY_DATA_VALUE,
                TRACKED_ENTITY_DATA_VALUE_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistTrackedEntityDataValueWithInvalidEventForeignKey() throws ParseException {
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

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        trackedEntityDataValueStore.close();
        assertThat(database().isOpen()).isTrue();
    }
}
