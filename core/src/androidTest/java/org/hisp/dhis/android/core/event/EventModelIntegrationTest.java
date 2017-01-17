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
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class EventModelIntegrationTest {
    private static final Long ID = 3L;
    private static final String EVENT_UID = "test_uid";
    private static final String ENROLLMENT_UID = "test_enrollment";
    private static final EventStatus STATUS = EventStatus.ACTIVE;
    private static final String LATITUDE = "10.832100";
    private static final String LONGITUDE = "59.345210";
    private static final String PROGRAM = "test_program";
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final State STATE = State.TO_POST;

    // timestamp
    private static final String DATE = "2016-01-12T10:01:00.000";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.UID, Columns.ENROLLMENT_UID,
                Columns.CREATED, Columns.LAST_UPDATED,
                Columns.STATUS, Columns.LATITUDE,
                Columns.LONGITUDE, Columns.PROGRAM, Columns.PROGRAM_STAGE,
                Columns.ORGANISATION_UNIT, Columns.EVENT_DATE, Columns.COMPLETE_DATE,
                Columns.DUE_DATE, Columns.STATE
        });

        matrixCursor.addRow(new Object[]{
                ID, EVENT_UID, ENROLLMENT_UID, DATE, DATE, STATUS, LATITUDE, LONGITUDE,
                PROGRAM, PROGRAM_STAGE, ORGANISATION_UNIT, DATE, DATE, DATE, STATE
        });

        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        EventModel event = EventModel.create(matrixCursor);
        assertThat(event.id()).isEqualTo(ID);
        assertThat(event.uid()).isEqualTo(EVENT_UID);
        assertThat(event.enrollmentUid()).isEqualTo(ENROLLMENT_UID);
        assertThat(event.created()).isEqualTo(timeStamp);
        assertThat(event.lastUpdated()).isEqualTo(timeStamp);
        assertThat(event.status()).isEqualTo(STATUS);
        assertThat(event.latitude()).isEqualTo(LATITUDE);
        assertThat(event.program()).isEqualTo(PROGRAM);
        assertThat(event.programStage()).isEqualTo(PROGRAM_STAGE);
        assertThat(event.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
        assertThat(event.eventDate()).isEqualTo(timeStamp);
        assertThat(event.completedDate()).isEqualTo(timeStamp);
        assertThat(event.dueDate()).isEqualTo(timeStamp);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        EventModel event = EventModel.builder()
                .id(ID)
                .uid(EVENT_UID)
                .enrollmentUid(ENROLLMENT_UID)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .status(STATUS)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .program(PROGRAM)
                .programStage(PROGRAM_STAGE)
                .organisationUnit(ORGANISATION_UNIT)
                .eventDate(timeStamp)
                .completedDate(timeStamp)
                .dueDate(timeStamp)
                .state(STATE)
                .build();

        ContentValues contentValues = event.toContentValues();
        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(EVENT_UID);
        assertThat(contentValues.getAsString(Columns.ENROLLMENT_UID)).isEqualTo(ENROLLMENT_UID);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.STATUS)).isEqualTo(STATUS.name());
        assertThat(contentValues.getAsString(Columns.LATITUDE)).isEqualTo(LATITUDE);
        assertThat(contentValues.getAsString(Columns.LONGITUDE)).isEqualTo(LONGITUDE);
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
        assertThat(contentValues.getAsString(Columns.EVENT_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.COMPLETE_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.DUE_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.STATE)).isEqualTo(STATE.name());
    }
}
