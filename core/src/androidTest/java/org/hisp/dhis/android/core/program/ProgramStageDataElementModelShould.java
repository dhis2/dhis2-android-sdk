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
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toBoolean;

@RunWith(AndroidJUnit4.class)
public class ProgramStageDataElementModelShould {
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
    private static final String PROGRAM_STAGE = "test_program_stage";

    private final Date date;
    private final String dateString;

    public ProgramStageDataElementModelShould() {
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
                Columns.DISPLAY_IN_REPORTS,
                Columns.COMPULSORY,
                Columns.ALLOW_PROVIDED_ELSEWHERE,
                Columns.SORT_ORDER,
                Columns.ALLOW_FUTURE_DATE,
                Columns.DATA_ELEMENT,
                Columns.PROGRAM_STAGE_SECTION,
                Columns.PROGRAM_STAGE
        });
        cursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, dateString, dateString,
                DISPLAY_IN_REPORTS, COMPULSORY, ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT, PROGRAM_STAGE_SECTION,
                PROGRAM_STAGE
        });
        cursor.moveToFirst();

        ProgramStageDataElementModel model = ProgramStageDataElementModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(CODE);
        assertThat(model.name()).isEqualTo(NAME);
        assertThat(model.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.displayInReports()).isTrue();
        assertThat(model.compulsory()).isFalse();
        assertThat(model.allowProvidedElsewhere()).isFalse();
        assertThat(model.sortOrder()).isEqualTo(SORT_ORDER);
        assertThat(model.allowFutureDate()).isTrue();
        assertThat(model.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(model.programStageSection()).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(model.programStage()).isEqualTo(PROGRAM_STAGE);

    }

    @Test
    public void create_content_values_when_created_from_builder() {
        ProgramStageDataElementModel model = ProgramStageDataElementModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .displayInReports(toBoolean(DISPLAY_IN_REPORTS))
                .compulsory(toBoolean(COMPULSORY))
                .allowProvidedElsewhere(toBoolean(ALLOW_PROVIDED_ELSEWHERE))
                .sortOrder(SORT_ORDER)
                .allowFutureDate(toBoolean(ALLOW_FUTURE_DATE))
                .dataElement(DATA_ELEMENT)
                .programStageSection(PROGRAM_STAGE_SECTION)
                .programStage(PROGRAM_STAGE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsBoolean(Columns.DISPLAY_IN_REPORTS)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.COMPULSORY)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.ALLOW_PROVIDED_ELSEWHERE)).isFalse();
        assertThat(contentValues.getAsInteger(Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsBoolean(Columns.ALLOW_FUTURE_DATE)).isTrue();
        assertThat(contentValues.getAsString(Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE_SECTION)).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
    }

}
