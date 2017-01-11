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

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;


@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorStoreIntegrationTests extends AbsStoreTestCase {
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Date CREATED = new Date();
    private static final Date LAST_UPDATED = CREATED;
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final Boolean DISPLAY_IN_FORM = true;
    private static final String EXPRESSION = "test_expression";
    private static final String DIMENSION_ITEM = "test_dimension_item";
    private static final String FILTER = "test_filter";
    private static final Integer DECIMALS = 3;


    public static final String[] PROGRAM_INDICATOR_PROJECTION = {
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
    };

    private ProgramIndicatorStore programIndicatorStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        programIndicatorStore = new ProgramIndicatorStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() throws ParseException {

        long rowId = programIndicatorStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                CREATED,
                LAST_UPDATED,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                DISPLAY_IN_FORM,
                EXPRESSION,
                DIMENSION_ITEM,
                FILTER,
                DECIMALS
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.PROGRAM_INDICATOR,
                PROGRAM_INDICATOR_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        UID,
                        CODE,
                        NAME,
                        DISPLAY_NAME,
                        BaseIdentifiableObject.DATE_FORMAT.format(CREATED),
                        BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED),
                        SHORT_NAME,
                        DISPLAY_SHORT_NAME,
                        DESCRIPTION,
                        DISPLAY_DESCRIPTION,
                        toInteger(DISPLAY_IN_FORM),
                        EXPRESSION,
                        DIMENSION_ITEM,
                        FILTER,
                        DECIMALS)
                .isExhausted();
    }

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        programIndicatorStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
