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

 package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toBoolean;

@RunWith(AndroidJUnit4.class)
public class ProgramModelIntegrationTest {
    //BaseIdentifiableModel attributes:
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    //BaseNameableModel attributes:
    private static final String SHORT_NAME = "test_program";
    private static final String DISPLAY_SHORT_NAME = "test_prog";
    private static final String DESCRIPTION = "A test program for the integration tests.";
    private static final String DISPLAY_DESCRIPTION = "A test program for the integration tests.";

    //ProgramModel attributes:
    private static final Integer VERSION = 1;
    private static final Integer ONLY_ENROLL_ONCE = 1;
    private static final String ENROLLMENT_DATE_LABEL = "enrollment date";
    private static final Integer DISPLAY_INCIDENT_DATE = 1;
    private static final String INCIDENT_DATE_LABEL = "incident date label";
    private static final Integer REGISTRATION = 1;
    private static final Integer SELECT_ENROLLMENT_DATES_IN_FUTURE = 1;
    private static final Integer DATA_ENTRY_METHOD = 1;
    private static final Integer IGNORE_OVERDUE_EVENTS = 0;
    private static final Integer RELATIONSHIP_FROM_A = 1;
    private static final Integer SELECT_INCIDENT_DATES_IN_FUTURE = 1;
    private static final Integer CAPTURE_COORDINATES = 1;
    private static final Integer USE_FIRST_STAGE_DURING_REGISTRATION = 1;
    private static final Integer DISPLAY_FRONT_PAGE_LIST = 1;

    private static final ProgramType PROGRAM_TYPE = ProgramType.WITH_REGISTRATION;
    private static final String RELATIONSHIP_TYPE = "relationshipUid";
    private static final String RELATIONSHIP_TEXT = "test relationship";
    private static final String RELATED_PROGRAM = "ProgramUid";
    private static final String TRACKED_ENTITY = "TrackedEntityUid";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramModel.Columns.ID,
                ProgramModel.Columns.UID,
                ProgramModel.Columns.CODE,
                ProgramModel.Columns.NAME,
                ProgramModel.Columns.DISPLAY_NAME,
                ProgramModel.Columns.CREATED,
                ProgramModel.Columns.LAST_UPDATED,
                ProgramModel.Columns.SHORT_NAME,
                ProgramModel.Columns.DISPLAY_SHORT_NAME,
                ProgramModel.Columns.DESCRIPTION,
                ProgramModel.Columns.DISPLAY_DESCRIPTION,
                ProgramModel.Columns.VERSION,
                ProgramModel.Columns.ONLY_ENROLL_ONCE,
                ProgramModel.Columns.ENROLLMENT_DATE_LABEL,
                ProgramModel.Columns.DISPLAY_INCIDENT_DATE,
                ProgramModel.Columns.INCIDENT_DATE_LABEL,
                ProgramModel.Columns.REGISTRATION,
                ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE,
                ProgramModel.Columns.DATA_ENTRY_METHOD,
                ProgramModel.Columns.IGNORE_OVERDUE_EVENTS,
                ProgramModel.Columns.RELATIONSHIP_FROM_A,
                ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE,
                ProgramModel.Columns.CAPTURE_COORDINATES,
                ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION,
                ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST,
                ProgramModel.Columns.PROGRAM_TYPE,
                ProgramModel.Columns.RELATIONSHIP_TYPE,
                ProgramModel.Columns.RELATIONSHIP_TEXT,
                ProgramModel.Columns.RELATED_PROGRAM,
                ProgramModel.Columns.TRACKED_ENTITY
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VERSION,
                ONLY_ENROLL_ONCE,
                ENROLLMENT_DATE_LABEL,
                DISPLAY_INCIDENT_DATE,
                INCIDENT_DATE_LABEL,
                REGISTRATION,
                SELECT_ENROLLMENT_DATES_IN_FUTURE,
                DATA_ENTRY_METHOD,
                IGNORE_OVERDUE_EVENTS,
                RELATIONSHIP_FROM_A,
                SELECT_INCIDENT_DATES_IN_FUTURE,
                CAPTURE_COORDINATES,
                USE_FIRST_STAGE_DURING_REGISTRATION,
                DISPLAY_FRONT_PAGE_LIST,
                PROGRAM_TYPE,
                RELATIONSHIP_TYPE,
                RELATIONSHIP_TEXT,
                RELATED_PROGRAM,
                TRACKED_ENTITY
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        ProgramModel program = ProgramModel.create(matrixCursor);

        assertThat(program.id()).isEqualTo(ID);
        assertThat(program.uid()).isEqualTo(UID);
        assertThat(program.code()).isEqualTo(CODE);
        assertThat(program.name()).isEqualTo(NAME);
        assertThat(program.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(program.created()).isEqualTo(timeStamp);
        assertThat(program.lastUpdated()).isEqualTo(timeStamp);
        assertThat(program.shortName()).isEqualTo(SHORT_NAME);
        assertThat(program.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(program.description()).isEqualTo(DESCRIPTION);
        assertThat(program.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(program.version()).isEqualTo(VERSION);
        assertThat(program.onlyEnrollOnce()).isEqualTo(toBoolean(ONLY_ENROLL_ONCE));
        assertThat(program.enrollmentDateLabel()).isEqualTo(ENROLLMENT_DATE_LABEL);
        assertThat(program.displayIncidentDate()).isEqualTo(toBoolean(DISPLAY_INCIDENT_DATE));
        assertThat(program.incidentDateLabel()).isEqualTo(INCIDENT_DATE_LABEL);
        assertThat(program.registration()).isEqualTo(toBoolean(REGISTRATION));
        assertThat(program.selectEnrollmentDatesInFuture()).isEqualTo(toBoolean(SELECT_ENROLLMENT_DATES_IN_FUTURE));
        assertThat(program.dataEntryMethod()).isEqualTo(toBoolean(DATA_ENTRY_METHOD));
        assertThat(program.ignoreOverdueEvents()).isEqualTo(toBoolean(IGNORE_OVERDUE_EVENTS));
        assertThat(program.relationshipFromA()).isEqualTo(toBoolean(RELATIONSHIP_FROM_A));
        assertThat(program.selectIncidentDatesInFuture()).isEqualTo(toBoolean(SELECT_INCIDENT_DATES_IN_FUTURE));
        assertThat(program.captureCoordinates()).isEqualTo(toBoolean(CAPTURE_COORDINATES));
        assertThat(program.useFirstStageDuringRegistration()).isEqualTo(toBoolean(USE_FIRST_STAGE_DURING_REGISTRATION));
        assertThat(program.displayFrontPageList()).isEqualTo(toBoolean(DISPLAY_FRONT_PAGE_LIST));
        assertThat(program.programType()).isEqualTo(PROGRAM_TYPE);
        assertThat(program.relationshipType()).isEqualTo(RELATIONSHIP_TYPE);
        assertThat(program.relationshipText()).isEqualTo(RELATIONSHIP_TEXT);
        assertThat(program.relatedProgram()).isEqualTo(RELATED_PROGRAM);
        assertThat(program.trackedEntity()).isEqualTo(TRACKED_ENTITY);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramModel program = ProgramModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .version(VERSION)
                .onlyEnrollOnce(toBoolean(ONLY_ENROLL_ONCE))
                .enrollmentDateLabel(ENROLLMENT_DATE_LABEL)
                .displayIncidentDate(toBoolean(DISPLAY_INCIDENT_DATE))
                .registration(toBoolean(REGISTRATION))
                .selectEnrollmentDatesInFuture(toBoolean(SELECT_ENROLLMENT_DATES_IN_FUTURE))
                .dataEntryMethod(toBoolean(DATA_ENTRY_METHOD))
                .ignoreOverdueEvents(toBoolean(IGNORE_OVERDUE_EVENTS))
                .relationshipFromA(toBoolean(RELATIONSHIP_FROM_A))
                .selectIncidentDatesInFuture(toBoolean(SELECT_INCIDENT_DATES_IN_FUTURE))
                .captureCoordinates(toBoolean(CAPTURE_COORDINATES))
                .useFirstStageDuringRegistration(toBoolean(USE_FIRST_STAGE_DURING_REGISTRATION))
                .displayFrontPageList(toBoolean(DISPLAY_FRONT_PAGE_LIST))
                .programType(PROGRAM_TYPE)
                .relationshipType(RELATIONSHIP_TYPE)
                .relationshipText(RELATIONSHIP_TEXT)
                .relatedProgram(RELATED_PROGRAM)
                .trackedEntity(TRACKED_ENTITY)
                .build();

        ContentValues contentValues = program.toContentValues();
        assertThat(contentValues.getAsLong(ProgramModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramModel.Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsInteger(ProgramModel.Columns.VERSION)).isEqualTo(VERSION);
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.ONLY_ENROLL_ONCE)).isEqualTo(toBoolean(ONLY_ENROLL_ONCE));
        assertThat(contentValues.getAsString(ProgramModel.Columns.ENROLLMENT_DATE_LABEL)).isEqualTo(ENROLLMENT_DATE_LABEL);
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.DISPLAY_INCIDENT_DATE)).isEqualTo(toBoolean(DISPLAY_INCIDENT_DATE));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.REGISTRATION)).isEqualTo(toBoolean(REGISTRATION));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE)).isEqualTo(toBoolean(SELECT_ENROLLMENT_DATES_IN_FUTURE));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.DATA_ENTRY_METHOD)).isEqualTo(toBoolean(DATA_ENTRY_METHOD));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.IGNORE_OVERDUE_EVENTS)).isEqualTo(toBoolean(IGNORE_OVERDUE_EVENTS));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.RELATIONSHIP_FROM_A)).isEqualTo(toBoolean(RELATIONSHIP_FROM_A));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE)).isEqualTo(toBoolean(SELECT_INCIDENT_DATES_IN_FUTURE));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.CAPTURE_COORDINATES)).isEqualTo(toBoolean(CAPTURE_COORDINATES));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION)).isEqualTo(toBoolean(USE_FIRST_STAGE_DURING_REGISTRATION));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST)).isEqualTo(toBoolean(DISPLAY_FRONT_PAGE_LIST));
        assertThat(contentValues.getAsString(ProgramModel.Columns.PROGRAM_TYPE)).isEqualTo(PROGRAM_TYPE.toString());
        assertThat(contentValues.getAsString(ProgramModel.Columns.RELATIONSHIP_TYPE)).isEqualTo(RELATIONSHIP_TYPE);
        assertThat(contentValues.getAsString(ProgramModel.Columns.RELATIONSHIP_TEXT)).isEqualTo(RELATIONSHIP_TEXT);
        assertThat(contentValues.getAsString(ProgramModel.Columns.RELATED_PROGRAM)).isEqualTo(RELATED_PROGRAM);
        assertThat(contentValues.getAsString(ProgramModel.Columns.TRACKED_ENTITY)).isEqualTo(TRACKED_ENTITY);
    }
}
