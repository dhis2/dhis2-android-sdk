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
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleVariableModelShould {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String PROGRAM_STAGE = "test_programStage";
    private static final ProgramRuleVariableSourceType PROGRAM_RULE_VARIABLE_SOURCE_TYPE =
            ProgramRuleVariableSourceType.CALCULATED_VALUE;

    private static final Integer USE_CODE_FOR_OPTION_SET = 1; // true
    private static final String PROGRAM = "test_program";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";

    private final Date date;
    private final String dateString;

    public ProgramRuleVariableModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() throws ParseException {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.UID, Columns.CODE, Columns.NAME, Columns.DISPLAY_NAME,
                Columns.CREATED, Columns.LAST_UPDATED, Columns.USE_CODE_FOR_OPTION_SET,
                Columns.PROGRAM, Columns.PROGRAM_STAGE, Columns.TRACKED_ENTITY_ATTRIBUTE,
                Columns.DATA_ELEMENT, Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        });
        cursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, dateString, dateString,
                USE_CODE_FOR_OPTION_SET, PROGRAM, PROGRAM_STAGE, TRACKED_ENTITY_ATTRIBUTE,
                DATA_ELEMENT, PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        });
        cursor.moveToFirst();

        ProgramRuleVariableModel model = ProgramRuleVariableModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(CODE);
        assertThat(model.name()).isEqualTo(NAME);
        assertThat(model.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.useCodeForOptionSet()).isTrue();
        assertThat(model.program()).isEqualTo(PROGRAM);
        assertThat(model.programStage()).isEqualTo(PROGRAM_STAGE);
        assertThat(model.trackedEntityAttribute()).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(model.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(model.programRuleVariableSourceType()).isEqualTo(PROGRAM_RULE_VARIABLE_SOURCE_TYPE);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder() throws ParseException {

        ProgramRuleVariableModel model = ProgramRuleVariableModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .useCodeForOptionSet(Boolean.TRUE)
                .program(PROGRAM)
                .programStage(PROGRAM_STAGE)
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE)
                .dataElement(DATA_ELEMENT)
                .programRuleVariableSourceType(PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsBoolean(Columns.USE_CODE_FOR_OPTION_SET)).isTrue();
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
        assertThat(contentValues.getAsString(Columns.TRACKED_ENTITY_ATTRIBUTE)).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(contentValues.getAsString(Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
        assertThat(contentValues.getAsString(Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE))
                .isEqualTo(PROGRAM_RULE_VARIABLE_SOURCE_TYPE.name());

    }
}
