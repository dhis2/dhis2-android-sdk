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
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstanceModelIntegrationTests {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String ORGANISATION_UNIT = "test_organisationUnit";
    private static final State STATE = State.ERROR;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                TrackedEntityInstanceModel.Columns.ID,
                TrackedEntityInstanceModel.Columns.UID,
                TrackedEntityInstanceModel.Columns.CREATED,
                TrackedEntityInstanceModel.Columns.LAST_UPDATED,
                TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT,
                TrackedEntityInstanceModel.Columns.STATE
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, DATE, DATE, ORGANISATION_UNIT, STATE
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        TrackedEntityInstanceModel trackedEntityInstanceModel = TrackedEntityInstanceModel.create(matrixCursor);

        assertThat(trackedEntityInstanceModel.id()).isEqualTo(ID);
        assertThat(trackedEntityInstanceModel.uid()).isEqualTo(UID);
        assertThat(trackedEntityInstanceModel.created()).isEqualTo(date);
        assertThat(trackedEntityInstanceModel.lastUpdated()).isEqualTo(date);
        assertThat(trackedEntityInstanceModel.organisationUnit()).isEqualTo(ORGANISATION_UNIT);

        matrixCursor.close();
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        TrackedEntityInstanceModel trackedEntityInstanceModel = TrackedEntityInstanceModel.builder()
                .id(ID)
                .uid(UID)
                .created(date)
                .lastUpdated(date)
                .organisationUnit(ORGANISATION_UNIT)
                .state(STATE)
                .build();

        ContentValues contentValues = trackedEntityInstanceModel.toContentValues();

        assertThat(contentValues.getAsLong(TrackedEntityInstanceModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(TrackedEntityInstanceModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(TrackedEntityInstanceModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(TrackedEntityInstanceModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT))
                .isEqualTo(ORGANISATION_UNIT);
        assertThat(contentValues.getAsString(TrackedEntityInstanceModel.Columns.STATE)).isEqualTo(STATE.name());
    }
}
