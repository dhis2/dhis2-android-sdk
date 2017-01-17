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
public class ProgramStageDataElementModelIntegrationTest {
    private static final long ID = 3L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Integer DISPLAY_IN_REPORTS = 1;
    private static final Integer COMPULSORY = 0;
    private static final Integer ALLOW_PROVIDED_ELSEWHERE = 0;
    private static final Integer SORT_ORDER = 7;
    private static final Integer ALLOW_FUTURE_DATE = 1;
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String PROGRAM_STAGE_SECTION = "test_program_stage_section";

    // timestamp
    private static final String DATE = "2017-01-04T16:40:02.007";

    public static ContentValues create(long id, String uid) {
        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(ProgramStageDataElementModel.Columns.ID, id);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.UID, uid);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.CODE, CODE);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.NAME, NAME);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.CREATED, DATE);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.LAST_UPDATED, DATE);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS, DISPLAY_IN_REPORTS);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.COMPULSORY, COMPULSORY);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.ALLOW_PROVIDED_ELSEWHERE, ALLOW_PROVIDED_ELSEWHERE);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.SORT_ORDER, SORT_ORDER);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.ALLOW_FUTURE_DATE, ALLOW_FUTURE_DATE);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.DATA_ELEMENT, DATA_ELEMENT);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION, PROGRAM_STAGE_SECTION);

        return programStageDataElement;
    }

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramStageDataElementModel.Columns.ID,
                ProgramStageDataElementModel.Columns.UID,
                ProgramStageDataElementModel.Columns.CODE,
                ProgramStageDataElementModel.Columns.NAME,
                ProgramStageDataElementModel.Columns.DISPLAY_NAME,
                ProgramStageDataElementModel.Columns.CREATED,
                ProgramStageDataElementModel.Columns.LAST_UPDATED,
                ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS,
                ProgramStageDataElementModel.Columns.COMPULSORY,
                ProgramStageDataElementModel.Columns.ALLOW_PROVIDED_ELSEWHERE,
                ProgramStageDataElementModel.Columns.SORT_ORDER,
                ProgramStageDataElementModel.Columns.ALLOW_FUTURE_DATE,
                ProgramStageDataElementModel.Columns.DATA_ELEMENT,
                ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                DISPLAY_IN_REPORTS, COMPULSORY, ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT, PROGRAM_STAGE_SECTION
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageDataElementModel programStageDataElement = ProgramStageDataElementModel.create(matrixCursor);
        assertThat(programStageDataElement.id()).isEqualTo(ID);
        assertThat(programStageDataElement.uid()).isEqualTo(UID);
        assertThat(programStageDataElement.code()).isEqualTo(CODE);
        assertThat(programStageDataElement.name()).isEqualTo(NAME);
        assertThat(programStageDataElement.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programStageDataElement.created()).isEqualTo(timeStamp);
        assertThat(programStageDataElement.lastUpdated()).isEqualTo(timeStamp);
        assertThat(programStageDataElement.displayInReports()).isTrue();
        assertThat(programStageDataElement.compulsory()).isFalse();
        assertThat(programStageDataElement.allowProvidedElsewhere()).isFalse();
        assertThat(programStageDataElement.sortOrder()).isEqualTo(SORT_ORDER);
        assertThat(programStageDataElement.allowFutureDate()).isTrue();
        assertThat(programStageDataElement.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(programStageDataElement.programStageSection()).isEqualTo(PROGRAM_STAGE_SECTION);
    }

    @Test
    public void create_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageDataElementModel programStageDataElementModel = ProgramStageDataElementModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .displayInReports(DISPLAY_IN_REPORTS != 0 ? Boolean.TRUE : Boolean.FALSE)
                .compulsory(COMPULSORY != 0 ? Boolean.TRUE : Boolean.FALSE)
                .allowProvidedElsewhere(ALLOW_PROVIDED_ELSEWHERE != 0 ? Boolean.TRUE : Boolean.FALSE)
                .sortOrder(SORT_ORDER)
                .allowFutureDate(ALLOW_FUTURE_DATE != 0 ? Boolean.TRUE : Boolean.FALSE)
                .dataElement(DATA_ELEMENT)
                .programStageSection(PROGRAM_STAGE_SECTION)
                .build();

        ContentValues contentValues = programStageDataElementModel.toContentValues();

        assertThat(contentValues.getAsLong(ProgramStageDataElementModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsBoolean(ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS)).isTrue();
        assertThat(contentValues.getAsBoolean(ProgramStageDataElementModel.Columns.COMPULSORY)).isFalse();
        assertThat(contentValues.getAsBoolean(ProgramStageDataElementModel.Columns.ALLOW_PROVIDED_ELSEWHERE)).isFalse();
        assertThat(contentValues.getAsInteger(ProgramStageDataElementModel.Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsBoolean(ProgramStageDataElementModel.Columns.ALLOW_FUTURE_DATE)).isTrue();
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
        assertThat(contentValues.getAsString(ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION)).isEqualTo(PROGRAM_STAGE_SECTION);
    }

}
