/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLinkModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramStageSectionDataElementLinkModelShould {
    private static final long ID = 3L;
    private static final String PROGRAM_STAGE_SECTION = "test_program_stage_section";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final Integer SORT_ORDER = 1;

    @Test
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.PROGRAM_STAGE_SECTION,
                Columns.DATA_ELEMENT,
                Columns.SORT_ORDER
        });
        cursor.addRow(new Object[]{ ID, PROGRAM_STAGE_SECTION, DATA_ELEMENT, SORT_ORDER });
        cursor.moveToFirst();

        ProgramStageSectionDataElementLinkModel model = ProgramStageSectionDataElementLinkModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.programStageSection()).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(model.dataElement()).isEqualTo(DATA_ELEMENT);
        assertThat(model.sortOrder()).isEqualTo(SORT_ORDER);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        ProgramStageSectionDataElementLinkModel model = ProgramStageSectionDataElementLinkModel.builder()
                .id(ID)
                .programStageSection(PROGRAM_STAGE_SECTION)
                .dataElement(DATA_ELEMENT)
                .sortOrder(SORT_ORDER)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE_SECTION)).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(contentValues.getAsString(Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
        assertThat(contentValues.getAsInteger(Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
    }

}
