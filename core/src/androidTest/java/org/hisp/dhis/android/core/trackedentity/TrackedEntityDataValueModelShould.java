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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValueModelShould {
    private static final long ID = 11L;
    private static final String EVENT = "test_event";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String STORED_BY = "test_storedBy";
    private static final String VALUE = "test_value";
    private static final Boolean PROVIDED_ELSEWHERE = false;

    private Date date;
    private String dateString;

    @Before
    public void setup() {
        date = new Date();
        dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.EVENT,
                Columns.DATA_ELEMENT,
                Columns.STORED_BY,
                Columns.VALUE,
                Columns.CREATED,
                Columns.LAST_UPDATED,
                Columns.PROVIDED_ELSEWHERE
        });
        cursor.addRow(new Object[]{
                ID, EVENT, DATA_ELEMENT, STORED_BY, VALUE, dateString, dateString, toInteger(PROVIDED_ELSEWHERE)});
        cursor.moveToFirst();

        TrackedEntityDataValueModel model = TrackedEntityDataValueModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.event()).isEqualTo(EVENT);
        assertThat(model.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(model.storedBy()).isEqualTo(STORED_BY);
        assertThat(model.value()).isEqualTo(VALUE);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.providedElsewhere()).isEqualTo(PROVIDED_ELSEWHERE);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        TrackedEntityDataValueModel model = TrackedEntityDataValueModel.builder()
                .id(ID)
                .event(EVENT)
                .dataElement(DATA_ELEMENT)
                .storedBy(STORED_BY)
                .value(VALUE)
                .created(date)
                .lastUpdated(date)
                .providedElsewhere(PROVIDED_ELSEWHERE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.EVENT)).isEqualTo(EVENT);
        assertThat(contentValues.getAsString(Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
        assertThat(contentValues.getAsString(Columns.STORED_BY)).isEqualTo(STORED_BY);
        assertThat(contentValues.getAsString(Columns.VALUE)).isEqualTo(VALUE);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsBoolean(Columns.PROVIDED_ELSEWHERE)).isEqualTo(PROVIDED_ELSEWHERE);
    }
}
