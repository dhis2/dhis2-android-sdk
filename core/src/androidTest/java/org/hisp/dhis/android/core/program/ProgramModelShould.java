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
import org.hisp.dhis.android.core.program.ProgramModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toBoolean;

@RunWith(AndroidJUnit4.class)
public class ProgramModelShould {
    //BaseIdentifiableModel attributes:
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

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

    private final Date date;
    private final String dateString;

    public ProgramModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.UID,
                Columns.CODE,
                Columns.NAME,
                Columns.DISPLAY_NAME,
                Columns.CREATED,
                Columns.LAST_UPDATED,
                Columns.SHORT_NAME,
                Columns.DISPLAY_SHORT_NAME,
                Columns.DESCRIPTION,
                Columns.DISPLAY_DESCRIPTION,
                Columns.VERSION,
                Columns.ONLY_ENROLL_ONCE,
                Columns.ENROLLMENT_DATE_LABEL,
                Columns.DISPLAY_INCIDENT_DATE,
                Columns.INCIDENT_DATE_LABEL,
                Columns.REGISTRATION,
                Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE,
                Columns.DATA_ENTRY_METHOD,
                Columns.IGNORE_OVERDUE_EVENTS,
                Columns.RELATIONSHIP_FROM_A,
                Columns.SELECT_INCIDENT_DATES_IN_FUTURE,
                Columns.CAPTURE_COORDINATES,
                Columns.USE_FIRST_STAGE_DURING_REGISTRATION,
                Columns.DISPLAY_FRONT_PAGE_LIST,
                Columns.PROGRAM_TYPE,
                Columns.RELATIONSHIP_TYPE,
                Columns.RELATIONSHIP_TEXT,
                Columns.RELATED_PROGRAM,
                Columns.TRACKED_ENTITY
        });
        cursor.addRow(new Object[]{ID, UID, CODE, NAME, DISPLAY_NAME, dateString, dateString,
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
        cursor.moveToFirst();

        ProgramModel model = ProgramModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(CODE);
        assertThat(model.name()).isEqualTo(NAME);
        assertThat(model.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.shortName()).isEqualTo(SHORT_NAME);
        assertThat(model.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(model.description()).isEqualTo(DESCRIPTION);
        assertThat(model.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(model.version()).isEqualTo(VERSION);
        assertThat(model.onlyEnrollOnce()).isEqualTo(toBoolean(ONLY_ENROLL_ONCE));
        assertThat(model.enrollmentDateLabel()).isEqualTo(ENROLLMENT_DATE_LABEL);
        assertThat(model.displayIncidentDate()).isEqualTo(toBoolean(DISPLAY_INCIDENT_DATE));
        assertThat(model.incidentDateLabel()).isEqualTo(INCIDENT_DATE_LABEL);
        assertThat(model.registration()).isEqualTo(toBoolean(REGISTRATION));
        assertThat(model.selectEnrollmentDatesInFuture()).isEqualTo(toBoolean(SELECT_ENROLLMENT_DATES_IN_FUTURE));
        assertThat(model.dataEntryMethod()).isEqualTo(toBoolean(DATA_ENTRY_METHOD));
        assertThat(model.ignoreOverdueEvents()).isEqualTo(toBoolean(IGNORE_OVERDUE_EVENTS));
        assertThat(model.relationshipFromA()).isEqualTo(toBoolean(RELATIONSHIP_FROM_A));
        assertThat(model.selectIncidentDatesInFuture()).isEqualTo(toBoolean(SELECT_INCIDENT_DATES_IN_FUTURE));
        assertThat(model.captureCoordinates()).isEqualTo(toBoolean(CAPTURE_COORDINATES));
        assertThat(model.useFirstStageDuringRegistration()).isEqualTo(toBoolean(USE_FIRST_STAGE_DURING_REGISTRATION));
        assertThat(model.displayFrontPageList()).isEqualTo(toBoolean(DISPLAY_FRONT_PAGE_LIST));
        assertThat(model.programType()).isEqualTo(PROGRAM_TYPE);
        assertThat(model.relationshipType()).isEqualTo(RELATIONSHIP_TYPE);
        assertThat(model.relationshipText()).isEqualTo(RELATIONSHIP_TEXT);
        assertThat(model.relatedProgram()).isEqualTo(RELATED_PROGRAM);
        assertThat(model.trackedEntity()).isEqualTo(TRACKED_ENTITY);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        ProgramModel model = ProgramModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME).displayName(DISPLAY_NAME).created(date).lastUpdated(date)
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
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(ProgramModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(ProgramModel.Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(ProgramModel.Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(ProgramModel.Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsInteger(ProgramModel.Columns.VERSION)).isEqualTo(VERSION);
        assertThat(contentValues.getAsBoolean(
                ProgramModel.Columns.ONLY_ENROLL_ONCE)).isEqualTo(toBoolean(ONLY_ENROLL_ONCE));
        assertThat(contentValues.getAsString(
                ProgramModel.Columns.ENROLLMENT_DATE_LABEL)).isEqualTo(ENROLLMENT_DATE_LABEL);
        assertThat(contentValues.getAsBoolean(
                ProgramModel.Columns.DISPLAY_INCIDENT_DATE)).isEqualTo(toBoolean(DISPLAY_INCIDENT_DATE));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.REGISTRATION)).isEqualTo(toBoolean(REGISTRATION));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE))
                .isEqualTo(toBoolean(SELECT_ENROLLMENT_DATES_IN_FUTURE));
        assertThat(contentValues.getAsBoolean(
                ProgramModel.Columns.DATA_ENTRY_METHOD)).isEqualTo(toBoolean(DATA_ENTRY_METHOD));
        assertThat(contentValues.getAsBoolean(
                ProgramModel.Columns.IGNORE_OVERDUE_EVENTS)).isEqualTo(toBoolean(IGNORE_OVERDUE_EVENTS));
        assertThat(contentValues.getAsBoolean(
                ProgramModel.Columns.RELATIONSHIP_FROM_A)).isEqualTo(toBoolean(RELATIONSHIP_FROM_A));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE))
                .isEqualTo(toBoolean(SELECT_INCIDENT_DATES_IN_FUTURE));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.CAPTURE_COORDINATES))
                .isEqualTo(toBoolean(CAPTURE_COORDINATES));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION))
                .isEqualTo(toBoolean(USE_FIRST_STAGE_DURING_REGISTRATION));
        assertThat(contentValues.getAsBoolean(ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST))
                .isEqualTo(toBoolean(DISPLAY_FRONT_PAGE_LIST));
        assertThat(contentValues.getAsString(ProgramModel.Columns.PROGRAM_TYPE)).isEqualTo(PROGRAM_TYPE.toString());
        assertThat(contentValues.getAsString(ProgramModel.Columns.RELATIONSHIP_TYPE)).isEqualTo(RELATIONSHIP_TYPE);
        assertThat(contentValues.getAsString(ProgramModel.Columns.RELATIONSHIP_TEXT)).isEqualTo(RELATIONSHIP_TEXT);
        assertThat(contentValues.getAsString(ProgramModel.Columns.RELATED_PROGRAM)).isEqualTo(RELATED_PROGRAM);
        assertThat(contentValues.getAsString(ProgramModel.Columns.TRACKED_ENTITY)).isEqualTo(TRACKED_ENTITY);
    }
}
