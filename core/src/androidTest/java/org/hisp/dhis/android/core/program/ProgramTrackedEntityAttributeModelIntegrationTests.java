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
import org.hisp.dhis.android.core.common.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;

@RunWith(AndroidJUnit4.class)
public class ProgramTrackedEntityAttributeModelIntegrationTests {
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
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final Boolean ALLOW_FUTURE_DATES = false;
    private static final Boolean DISPLAY_IN_LIST = true;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramTrackedEntityAttributeModel.Columns.ID,
                ProgramTrackedEntityAttributeModel.Columns.UID,
                ProgramTrackedEntityAttributeModel.Columns.CODE,
                ProgramTrackedEntityAttributeModel.Columns.NAME,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_NAME,
                ProgramTrackedEntityAttributeModel.Columns.CREATED,
                ProgramTrackedEntityAttributeModel.Columns.LAST_UPDATED,
                ProgramTrackedEntityAttributeModel.Columns.SHORT_NAME,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME,
                ProgramTrackedEntityAttributeModel.Columns.DESCRIPTION,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION,
                ProgramTrackedEntityAttributeModel.Columns.MANDATORY,
                ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
                ProgramTrackedEntityAttributeModel.Columns.VALUE_TYPE,
                ProgramTrackedEntityAttributeModel.Columns.ALLOW_FUTURE_DATES,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION,
                toInteger(MANDATORY), TRACKED_ENTITY_ATTRIBUTE, VALUE_TYPE,
                toInteger(ALLOW_FUTURE_DATES), toInteger(DISPLAY_IN_LIST)
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramTrackedEntityAttributeModel trackedEntityAttributeModel = ProgramTrackedEntityAttributeModel.create(matrixCursor);

        assertThat(trackedEntityAttributeModel.id()).isEqualTo(ID);
        assertThat(trackedEntityAttributeModel.uid()).isEqualTo(UID);
        assertThat(trackedEntityAttributeModel.code()).isEqualTo(CODE);
        assertThat(trackedEntityAttributeModel.name()).isEqualTo(NAME);
        assertThat(trackedEntityAttributeModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(trackedEntityAttributeModel.created()).isEqualTo(date);
        assertThat(trackedEntityAttributeModel.lastUpdated()).isEqualTo(date);
        assertThat(trackedEntityAttributeModel.shortName()).isEqualTo(SHORT_NAME);
        assertThat(trackedEntityAttributeModel.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(trackedEntityAttributeModel.description()).isEqualTo(DESCRIPTION);
        assertThat(trackedEntityAttributeModel.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(trackedEntityAttributeModel.mandatory()).isEqualTo(MANDATORY);
        assertThat(trackedEntityAttributeModel.trackedEntityAttribute()).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(trackedEntityAttributeModel.valueType()).isEqualTo(VALUE_TYPE);
        assertThat(trackedEntityAttributeModel.allowFutureDates()).isEqualTo(ALLOW_FUTURE_DATES);
        assertThat(trackedEntityAttributeModel.displayInList()).isEqualTo(DISPLAY_IN_LIST);

        matrixCursor.close();
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramTrackedEntityAttributeModel trackedEntityAttributeModel = ProgramTrackedEntityAttributeModel.builder()
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
                .valueType(VALUE_TYPE)
                .allowFutureDates(ALLOW_FUTURE_DATES)
                .displayInList(DISPLAY_IN_LIST)
                .build();

        ContentValues contentValues = trackedEntityAttributeModel.toContentValues();

        assertThat(contentValues.getAsLong(ProgramTrackedEntityAttributeModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsBoolean(ProgramTrackedEntityAttributeModel.Columns.MANDATORY)).isEqualTo(MANDATORY);
        assertThat(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE)).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(ValueType.valueOf(contentValues.getAsString(ProgramTrackedEntityAttributeModel.Columns.VALUE_TYPE))).isEqualTo(VALUE_TYPE);
        assertThat(contentValues.getAsBoolean(ProgramTrackedEntityAttributeModel.Columns.ALLOW_FUTURE_DATES)).isEqualTo(ALLOW_FUTURE_DATES);
        assertThat(contentValues.getAsBoolean(ProgramTrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST)).isEqualTo(DISPLAY_IN_LIST);
    }
}
