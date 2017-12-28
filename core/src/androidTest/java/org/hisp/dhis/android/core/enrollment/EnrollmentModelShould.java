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

package org.hisp.dhis.android.core.enrollment;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.AndroidTestUtils;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class EnrollmentModelShould {
    private static final Long ID = 1L;
    private static final String UID = "test_enrollment";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final String PROGRAM = "test_program";
    private static final Boolean FOLLOW_UP = true;
    private static final EnrollmentStatus ENROLLMENT_STATUS = EnrollmentStatus.ACTIVE;
    private static final String TRACKED_ENTITY_INSTANCE = "test_trackedEntityInstance";
    private static final String LATITUDE = "10.1337";
    private static final String LONGITUDE = "59.140";
    private static final State STATE = State.TO_UPDATE;

    private final Date date;
    private final String dateString;

    public EnrollmentModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.UID, Columns.CREATED, Columns.LAST_UPDATED, Columns.ORGANISATION_UNIT,
                Columns.PROGRAM, Columns.DATE_OF_ENROLLMENT, Columns.DATE_OF_INCIDENT, Columns.FOLLOW_UP,
                Columns.ENROLLMENT_STATUS, Columns.TRACKED_ENTITY_INSTANCE, Columns.LATITUDE, Columns.LONGITUDE,
                Columns.STATE
        });
        cursor.addRow(new Object[]{
                ID, UID, dateString, dateString, ORGANISATION_UNIT, PROGRAM, dateString, dateString,
                AndroidTestUtils.toInteger(FOLLOW_UP), ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE, LATITUDE, LONGITUDE, STATE
        });
        cursor.moveToFirst();

        EnrollmentModel model = EnrollmentModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
        assertThat(model.program()).isEqualTo(PROGRAM);
        assertThat(model.dateOfEnrollment()).isEqualTo(date);
        assertThat(model.dateOfIncident()).isEqualTo(date);
        assertThat(model.followUp()).isEqualTo(FOLLOW_UP);
        assertThat(model.enrollmentStatus()).isEqualTo(ENROLLMENT_STATUS);
        assertThat(model.trackedEntityInstance()).isEqualTo(TRACKED_ENTITY_INSTANCE);
        assertThat(model.latitude()).isEqualTo(LATITUDE);
        assertThat(model.longitude()).isEqualTo(LONGITUDE);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder(){
        EnrollmentModel model = EnrollmentModel.builder()
                .id(ID)
                .uid(UID)
                .created(date)
                .lastUpdated(date)
                .organisationUnit(ORGANISATION_UNIT)
                .program(PROGRAM)
                .dateOfEnrollment(date)
                .dateOfIncident(date)
                .followUp(FOLLOW_UP)
                .enrollmentStatus(ENROLLMENT_STATUS)
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .state(STATE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.ORGANISATION_UNIT)).isEqualTo(ORGANISATION_UNIT);
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(Columns.DATE_OF_ENROLLMENT)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.DATE_OF_INCIDENT)).isEqualTo(dateString);
        assertThat(contentValues.getAsBoolean(Columns.FOLLOW_UP)).isEqualTo(FOLLOW_UP);
        assertThat(contentValues.getAsString(Columns.ENROLLMENT_STATUS)).isEqualTo(ENROLLMENT_STATUS.name());
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_INSTANCE)).isEqualTo(TRACKED_ENTITY_INSTANCE);
        assertThat(contentValues.getAsString(Columns.LATITUDE)).isEqualTo(LATITUDE);
        assertThat(contentValues.getAsString(Columns.LONGITUDE)).isEqualTo(LONGITUDE);
        assertThat(contentValues.getAsString(Columns.STATE)).isEqualTo(STATE.name());

    }
}
