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
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.program.ProgramIndicatorModel.Columns;

import java.util.Date;

public class CreateProgramIndicatorUtils {
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Date DATE = new Date();
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final Boolean DISPLAY_IN_FORM = true;
    private static final String EXPRESSION = "test_expression";
    private static final String DIMENSION_ITEM = "test_dimension_item";
    private static final String FILTER = "test_filter";
    private static final Integer DECIMALS = 3;

    public static ContentValues create(long id, @NonNull String uid, @NonNull String program) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID, id);
        contentValues.put(Columns.UID, uid);
        contentValues.put(Columns.CODE, CODE);
        contentValues.put(Columns.NAME, NAME);
        contentValues.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        contentValues.put(Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(DATE));
        contentValues.put(Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(DATE));
        contentValues.put(Columns.SHORT_NAME, SHORT_NAME);
        contentValues.put(Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        contentValues.put(Columns.DESCRIPTION, DESCRIPTION);
        contentValues.put(Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        contentValues.put(Columns.DISPLAY_IN_FORM, DISPLAY_IN_FORM);
        contentValues.put(Columns.EXPRESSION, EXPRESSION);
        contentValues.put(Columns.DIMENSION_ITEM, DIMENSION_ITEM);
        contentValues.put(Columns.FILTER, FILTER);
        contentValues.put(Columns.DECIMALS, DECIMALS);
        contentValues.put(Columns.PROGRAM, program);

        return contentValues;
    }
}
