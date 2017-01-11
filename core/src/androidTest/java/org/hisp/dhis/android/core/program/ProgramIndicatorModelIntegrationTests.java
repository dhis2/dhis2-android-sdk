/*
 * Copyright (c) 2016, University of Oslo
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
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorModelIntegrationTests {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final Boolean DISPLAY_IN_FORM = true;
    private static final String EXPRESSION = "test_expression";
    private static final String DIMENSION_ITEM = "test_dimension_item";
    private static final String FILTER = "test_filter";
    private static final Integer DECIMALS = 3;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramIndicatorContract.Columns.ID,
                ProgramIndicatorContract.Columns.UID,
                ProgramIndicatorContract.Columns.CODE,
                ProgramIndicatorContract.Columns.NAME,
                ProgramIndicatorContract.Columns.DISPLAY_NAME,
                ProgramIndicatorContract.Columns.CREATED,
                ProgramIndicatorContract.Columns.LAST_UPDATED,
                ProgramIndicatorContract.Columns.SHORT_NAME,
                ProgramIndicatorContract.Columns.DISPLAY_SHORT_NAME,
                ProgramIndicatorContract.Columns.DESCRIPTION,
                ProgramIndicatorContract.Columns.DISPLAY_DESCRIPTION,
                ProgramIndicatorContract.Columns.DISPLAY_IN_FORM,
                ProgramIndicatorContract.Columns.EXPRESSION,
                ProgramIndicatorContract.Columns.DIMENSION_ITEM,
                ProgramIndicatorContract.Columns.FILTER,
                ProgramIndicatorContract.Columns.DECIMALS
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION,
                toInteger(DISPLAY_IN_FORM), EXPRESSION,
                DIMENSION_ITEM, FILTER, DECIMALS
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramIndicatorModel programIndicatorModel = ProgramIndicatorModel.create(matrixCursor);

        assertThat(programIndicatorModel.id()).isEqualTo(ID);
        assertThat(programIndicatorModel.uid()).isEqualTo(UID);
        assertThat(programIndicatorModel.code()).isEqualTo(CODE);
        assertThat(programIndicatorModel.name()).isEqualTo(NAME);
        assertThat(programIndicatorModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programIndicatorModel.created()).isEqualTo(date);
        assertThat(programIndicatorModel.lastUpdated()).isEqualTo(date);
        assertThat(programIndicatorModel.shortName()).isEqualTo(SHORT_NAME);
        assertThat(programIndicatorModel.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(programIndicatorModel.description()).isEqualTo(DESCRIPTION);
        assertThat(programIndicatorModel.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(programIndicatorModel.displayInForm()).isEqualTo(DISPLAY_IN_FORM);
        assertThat(programIndicatorModel.expression()).isEqualTo(EXPRESSION);
        assertThat(programIndicatorModel.dimensionItem()).isEqualTo(DIMENSION_ITEM);
        assertThat(programIndicatorModel.filter()).isEqualTo(FILTER);
        assertThat(programIndicatorModel.decimals()).isEqualTo(DECIMALS);

        matrixCursor.close();
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramIndicatorModel programIndicatorModel = ProgramIndicatorModel.builder()
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
                .displayInForm(DISPLAY_IN_FORM)
                .expression(EXPRESSION)
                .dimensionItem(DIMENSION_ITEM)
                .filter(FILTER)
                .decimals(DECIMALS)
                .build();

        ContentValues contentValues = programIndicatorModel.toContentValues();

        assertThat(contentValues.getAsLong(ProgramIndicatorContract.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsBoolean(ProgramIndicatorContract.Columns.DISPLAY_IN_FORM)).isEqualTo(DISPLAY_IN_FORM);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.EXPRESSION)).isEqualTo(EXPRESSION);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.DIMENSION_ITEM)).isEqualTo(DIMENSION_ITEM);
        assertThat(contentValues.getAsString(ProgramIndicatorContract.Columns.FILTER)).isEqualTo(FILTER);
        assertThat(contentValues.getAsInteger(ProgramIndicatorContract.Columns.DECIMALS)).isEqualTo(DECIMALS);
    }
}
