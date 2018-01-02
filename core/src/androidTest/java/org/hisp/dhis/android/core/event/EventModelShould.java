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
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class EventModelShould {
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

    private final Date date;
    private final String dateString;

    public EventModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.UID, Columns.ENROLLMENT_UID,
                Columns.CREATED, Columns.LAST_UPDATED,
                Columns.STATUS, Columns.LATITUDE,
                Columns.LONGITUDE, Columns.PROGRAM, Columns.PROGRAM_STAGE,
                Columns.ORGANISATION_UNIT, Columns.EVENT_DATE, Columns.COMPLETE_DATE,
                Columns.DUE_DATE, Columns.STATE
        });

        cursor.addRow(new Object[]{
                ID, EVENT_UID, ENROLLMENT_UID, dateString, dateString, STATUS, LATITUDE, LONGITUDE,
                PROGRAM, PROGRAM_STAGE, ORGANISATION_UNIT, dateString, dateString, dateString, STATE
        });
        cursor.moveToFirst();
        EventModel model = EventModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(EVENT_UID);
        assertThat(model.enrollmentUid()).isEqualTo(ENROLLMENT_UID);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.status()).isEqualTo(STATUS);
        assertThat(model.latitude()).isEqualTo(LATITUDE);
        assertThat(model.program()).isEqualTo(PROGRAM);
        assertThat(model.programStage()).isEqualTo(PROGRAM_STAGE);
        assertThat(model.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
        assertThat(model.eventDate()).isEqualTo(date);
        assertThat(model.completedDate()).isEqualTo(date);
        assertThat(model.dueDate()).isEqualTo(date);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder(){
        EventModel model = EventModel.builder()
                .id(ID)
                .uid(EVENT_UID)
                .enrollmentUid(ENROLLMENT_UID)
                .created(date)
                .lastUpdated(date)
                .status(STATUS)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .program(PROGRAM)
                .programStage(PROGRAM_STAGE)
                .organisationUnit(ORGANISATION_UNIT)
                .eventDate(date)
                .completedDate(date)
                .dueDate(date)
                .state(STATE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(EVENT_UID);
        assertThat(contentValues.getAsString(Columns.ENROLLMENT_UID)).isEqualTo(ENROLLMENT_UID);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.STATUS)).isEqualTo(STATUS.name());
        assertThat(contentValues.getAsString(Columns.LATITUDE)).isEqualTo(LATITUDE);
        assertThat(contentValues.getAsString(Columns.LONGITUDE)).isEqualTo(LONGITUDE);
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
        assertThat(contentValues.getAsString(Columns.EVENT_DATE)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.COMPLETE_DATE)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.DUE_DATE)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.STATE)).isEqualTo(STATE.name());
    }
}
