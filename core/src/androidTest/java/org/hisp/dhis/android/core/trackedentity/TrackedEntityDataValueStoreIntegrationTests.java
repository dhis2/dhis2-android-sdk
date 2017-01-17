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
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.event.CreateEventUtils;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.hisp.dhis.android.core.program.CreateProgramStageUtils;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
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

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    private static final long ID = 11L;
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshpTypeUid";


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

    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws ParseException {

        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, TRACKED_ENTITY_UID);

        database().insert(DbOpenHelper.Tables.TRACKED_ENTITY, null, trackedEntity);
        database().insert(DbOpenHelper.Tables.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(DbOpenHelper.Tables.PROGRAM, null, program);

        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANISATION_UNIT);
        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);
        ContentValues event = CreateEventUtils.create(EVENT, PROGRAM, PROGRAM_STAGE, ORGANISATION_UNIT);

        database().insert(DbOpenHelper.Tables.TRACKED_ENTITY, null, trackedEntity);
        database().insert(DbOpenHelper.Tables.RELATIONSHIP_TYPE, null, relationshipType);
        database().insert(DbOpenHelper.Tables.PROGRAM, null, program);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);
        database().insert(DbOpenHelper.Tables.PROGRAM_STAGE, null, programStage);
        database().insert(DbOpenHelper.Tables.EVENT, null, event);

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.TRACKED_ENTITY_DATA_VALUE,
                TRACKED_ENTITY_DATA_VALUE_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        EVENT,
                        DATE,
                        DATE,
                        DATA_ELEMENT,
                        STORED_BY,
                        VALUE,
                        toInteger(PROVIDED_ELSEWHERE))
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertWithoutForeignKey_shouldThrowException() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        trackedEntityDataValueStore.insert(
                EVENT,
                date,
                date,
                DATA_ELEMENT,
                STORED_BY,
                VALUE,
                PROVIDED_ELSEWHERE
        );
    }

    // ToDo: test cascade deletion

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        trackedEntityDataValueStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
