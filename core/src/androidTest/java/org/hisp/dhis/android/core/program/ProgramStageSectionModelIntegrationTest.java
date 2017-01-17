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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramStageSectionModelIntegrationTest {
    private static final long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Integer SORT_ORDER = 7;
    private static final String PROGRAM_STAGE = "test_program_stage";

    // timestamp
    private static final String DATE = "2017-01-05T10:26:00.000";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramStageSectionModel.Columns.ID, ProgramStageSectionModel.Columns.UID, ProgramStageSectionModel.Columns.CODE, ProgramStageSectionModel.Columns.NAME,
                ProgramStageSectionModel.Columns.DISPLAY_NAME, ProgramStageSectionModel.Columns.CREATED, ProgramStageSectionModel.Columns.LAST_UPDATED,
                ProgramStageSectionModel.Columns.SORT_ORDER, ProgramStageSectionModel.Columns.PROGRAM_STAGE
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                SORT_ORDER, PROGRAM_STAGE
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageSectionModel programStageSection = ProgramStageSectionModel.create(matrixCursor);
        assertThat(programStageSection.id()).isEqualTo(ID);
        assertThat(programStageSection.uid()).isEqualTo(UID);
        assertThat(programStageSection.code()).isEqualTo(CODE);
        assertThat(programStageSection.name()).isEqualTo(NAME);
        assertThat(programStageSection.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programStageSection.created()).isEqualTo(timeStamp);
        assertThat(programStageSection.lastUpdated()).isEqualTo(timeStamp);
        assertThat(programStageSection.sortOrder()).isEqualTo(SORT_ORDER);
        assertThat(programStageSection.programStage()).isEqualTo(PROGRAM_STAGE);
    }

    @Test
    public void create_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageSectionModel programStageSection = ProgramStageSectionModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .sortOrder(SORT_ORDER)
                .programStage(PROGRAM_STAGE)
                .build();

        ContentValues contentValues = programStageSection.toContentValues();

        assertThat(contentValues.getAsLong(ProgramStageSectionModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsInteger(ProgramStageSectionModel.Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
    }
}
