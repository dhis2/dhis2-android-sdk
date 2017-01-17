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

import org.hisp.dhis.android.core.AndroidTestUtils;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValueModelIntegrationTests {

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    private static final long ID = 11L;
    private static final String EVENT = "test_event";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String STORED_BY = "test_storedBy";
    private static final String VALUE = "test_value";
    private static final Boolean PROVIDED_ELSEWHERE = false;

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                TrackedEntityDataValueModel.Columns.ID,
                TrackedEntityDataValueModel.Columns.EVENT,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT,
                TrackedEntityDataValueModel.Columns.STORED_BY,
                TrackedEntityDataValueModel.Columns.VALUE,
                TrackedEntityDataValueModel.Columns.CREATED,
                TrackedEntityDataValueModel.Columns.LAST_UPDATED,
                TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE
        });

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        matrixCursor.addRow(new Object[]{
                ID, EVENT, DATA_ELEMENT, STORED_BY, VALUE, DATE, DATE, AndroidTestUtils.toInteger(PROVIDED_ELSEWHERE)
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        TrackedEntityDataValueModel trackedEntityDataValueModel = TrackedEntityDataValueModel.create(matrixCursor);

        assertThat(trackedEntityDataValueModel.id()).isEqualTo(ID);
        assertThat(trackedEntityDataValueModel.event()).isEqualTo(EVENT);
        assertThat(trackedEntityDataValueModel.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(trackedEntityDataValueModel.storedBy()).isEqualTo(STORED_BY);
        assertThat(trackedEntityDataValueModel.value()).isEqualTo(VALUE);
        assertThat(trackedEntityDataValueModel.created()).isEqualTo(date);
        assertThat(trackedEntityDataValueModel.lastUpdated()).isEqualTo(date);
        assertThat(trackedEntityDataValueModel.providedElsewhere()).isEqualTo(PROVIDED_ELSEWHERE);

        matrixCursor.close();
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {

        ContentValues contentValues =
                CreateTrackedEntityDataValueUtils.create(ID);

        assertThat(contentValues.getAsLong(TrackedEntityDataValueModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(TrackedEntityDataValueModel.Columns.EVENT)).isEqualTo(EVENT);
        assertThat(contentValues.getAsString(TrackedEntityDataValueModel.Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
        assertThat(contentValues.getAsString(TrackedEntityDataValueModel.Columns.STORED_BY)).isEqualTo(STORED_BY);
        assertThat(contentValues.getAsString(TrackedEntityDataValueModel.Columns.VALUE)).isEqualTo(VALUE);
        assertThat(contentValues.getAsString(TrackedEntityDataValueModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(TrackedEntityDataValueModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsBoolean(TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE)).isEqualTo(PROVIDED_ELSEWHERE);
    }
}
