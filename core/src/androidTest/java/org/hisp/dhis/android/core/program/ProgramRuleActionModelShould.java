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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleActionModelShould {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String DATA = "test_data";
    private static final String CONTENT = "test_content";
    private static final String LOCATION = "test_location";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";
    private static final String PROGRAM_INDICATOR = "test_programIndicator";
    private static final String PROGRAM_STAGE_SECTION = "test_programStageSection";
    private static final ProgramRuleActionType PROGRAM_RULE_ACTION_TYPE = ProgramRuleActionType.ASSIGN;
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String DATA_ELEMENT = "test_dataElement";

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramRuleActionModel.Columns.ID,
                ProgramRuleActionModel.Columns.UID,
                ProgramRuleActionModel.Columns.CODE,
                ProgramRuleActionModel.Columns.NAME,
                ProgramRuleActionModel.Columns.DISPLAY_NAME,
                ProgramRuleActionModel.Columns.CREATED,
                ProgramRuleActionModel.Columns.LAST_UPDATED,
                ProgramRuleActionModel.Columns.DATA,
                ProgramRuleActionModel.Columns.CONTENT,
                ProgramRuleActionModel.Columns.LOCATION,
                ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
                ProgramRuleActionModel.Columns.PROGRAM_INDICATOR,
                ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION,
                ProgramRuleActionModel.Columns.PROGRAM_RULE_ACTION_TYPE,
                ProgramRuleActionModel.Columns.PROGRAM_STAGE,
                ProgramRuleActionModel.Columns.DATA_ELEMENT
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                DATA, CONTENT, LOCATION, TRACKED_ENTITY_ATTRIBUTE, PROGRAM_INDICATOR,
                PROGRAM_STAGE_SECTION, PROGRAM_RULE_ACTION_TYPE, PROGRAM_STAGE, DATA_ELEMENT
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramRuleActionModel programRuleActionModel = ProgramRuleActionModel.create(matrixCursor);

        assertThat(programRuleActionModel.id()).isEqualTo(ID);
        assertThat(programRuleActionModel.uid()).isEqualTo(UID);
        assertThat(programRuleActionModel.code()).isEqualTo(CODE);
        assertThat(programRuleActionModel.name()).isEqualTo(NAME);
        assertThat(programRuleActionModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programRuleActionModel.created()).isEqualTo(date);
        assertThat(programRuleActionModel.lastUpdated()).isEqualTo(date);
        assertThat(programRuleActionModel.data()).isEqualTo(DATA);
        assertThat(programRuleActionModel.content()).isEqualTo(CONTENT);
        assertThat(programRuleActionModel.location()).isEqualTo(LOCATION);
        assertThat(programRuleActionModel.trackedEntityAttribute()).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(programRuleActionModel.programIndicator()).isEqualTo(PROGRAM_INDICATOR);
        assertThat(programRuleActionModel.programStageSection()).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(programRuleActionModel.programRuleActionType()).isEqualTo(PROGRAM_RULE_ACTION_TYPE);
        assertThat(programRuleActionModel.programStage()).isEqualTo(PROGRAM_STAGE);
        assertThat(programRuleActionModel.dataElement()).isEqualTo(DATA_ELEMENT);
        matrixCursor.close();
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramRuleActionModel programRuleActionModel = ProgramRuleActionModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .data(DATA)
                .content(CONTENT)
                .location(LOCATION)
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE)
                .programIndicator(PROGRAM_INDICATOR)
                .programStageSection(PROGRAM_STAGE_SECTION)
                .programRuleActionType(PROGRAM_RULE_ACTION_TYPE)
                .programStage(PROGRAM_STAGE)
                .dataElement(DATA_ELEMENT)
                .build();

        ContentValues contentValues = programRuleActionModel.toContentValues();

        assertThat(contentValues.getAsLong(ProgramRuleActionModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.DATA)).isEqualTo(DATA);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.CONTENT)).isEqualTo(CONTENT);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.LOCATION)).isEqualTo(LOCATION);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE)).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.PROGRAM_INDICATOR)).isEqualTo(PROGRAM_INDICATOR);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION)).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.PROGRAM_RULE_ACTION_TYPE)).isEqualTo(PROGRAM_RULE_ACTION_TYPE.name());
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
        assertThat(contentValues.getAsString(ProgramRuleActionModel.Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);

    }
}
