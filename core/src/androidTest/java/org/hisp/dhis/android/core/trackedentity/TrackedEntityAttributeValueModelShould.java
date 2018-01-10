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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeValueModelShould {
    //BaseModel:
    private static final long ID = 11L;
    //BaseDataModel:
    private static final State STATE = State.SYNCED;
    //TrackedEntityAttributeValueModel:
    private static final String VALUE = "TestValue";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "TestAttribute";
    private static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";

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
                TrackedEntityAttributeValueModel.Columns.ID,
                TrackedEntityAttributeValueModel.Columns.CREATED,
                TrackedEntityAttributeValueModel.Columns.LAST_UPDATED,
                TrackedEntityAttributeValueModel.Columns.VALUE,
                TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
                TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE
        });

        cursor.addRow(new Object[]{
                ID, dateString, dateString, VALUE, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE
        });
        cursor.moveToFirst();

        TrackedEntityAttributeValueModel model = TrackedEntityAttributeValueModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.value()).isEqualTo(VALUE);
        assertThat(model.trackedEntityAttribute()).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(model.trackedEntityInstance()).isEqualTo(TRACKED_ENTITY_INSTANCE);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        TrackedEntityAttributeValueModel model = TrackedEntityAttributeValueModel.builder()
                .id(ID)
                .created(date)
                .lastUpdated(date)
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE)
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE)
                .value(VALUE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.VALUE)).isEqualTo(VALUE);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_ATTRIBUTE)).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_INSTANCE)).isEqualTo(TRACKED_ENTITY_INSTANCE);
    }
}

