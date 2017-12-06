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
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;

@RunWith(AndroidJUnit4.class)
public class ProgramTrackedEntityAttributeModelShould {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final Boolean MANDATORY = true;
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_tracked_entity_attribute";
    private static final Boolean ALLOW_FUTURE_DATES = false;
    private static final Boolean DISPLAY_IN_LIST = true;
    private static final Integer SORT_ORDER = 99;

    private final Date date;
    private final String dateString;

    public ProgramTrackedEntityAttributeModelShould() {
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
                Columns.MANDATORY,
                Columns.TRACKED_ENTITY_ATTRIBUTE,
                Columns.ALLOW_FUTURE_DATES,
                Columns.DISPLAY_IN_LIST,
                Columns.SORT_ORDER
        });
        cursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, dateString, dateString,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION,
                toInteger(MANDATORY), TRACKED_ENTITY_ATTRIBUTE,
                toInteger(ALLOW_FUTURE_DATES), toInteger(DISPLAY_IN_LIST), SORT_ORDER
        });
        cursor.moveToFirst();

        ProgramTrackedEntityAttributeModel model = ProgramTrackedEntityAttributeModel.create(cursor);
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
        assertThat(model.mandatory()).isEqualTo(MANDATORY);
        assertThat(model.trackedEntityAttribute()).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(model.allowFutureDates()).isEqualTo(ALLOW_FUTURE_DATES);
        assertThat(model.displayInList()).isEqualTo(DISPLAY_IN_LIST);
        assertThat(model.sortOrder()).isEqualTo(SORT_ORDER);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        ProgramTrackedEntityAttributeModel model = ProgramTrackedEntityAttributeModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .mandatory(MANDATORY)
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE)
                .allowFutureDates(ALLOW_FUTURE_DATES)
                .displayInList(DISPLAY_IN_LIST)
                .sortOrder(SORT_ORDER)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsBoolean(Columns.MANDATORY)).isEqualTo(MANDATORY);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_ATTRIBUTE)).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(contentValues.getAsBoolean(Columns.ALLOW_FUTURE_DATES)).isEqualTo(ALLOW_FUTURE_DATES);
        assertThat(contentValues.getAsBoolean(Columns.DISPLAY_IN_LIST)).isEqualTo(DISPLAY_IN_LIST);
        assertThat(contentValues.getAsInteger(Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
    }
}
