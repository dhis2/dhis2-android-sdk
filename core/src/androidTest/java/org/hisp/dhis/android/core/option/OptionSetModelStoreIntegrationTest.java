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

 package org.hisp.dhis.android.core.option;

import android.database.Cursor;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class OptionSetModelStoreIntegrationTest extends AbsStoreTestCase {

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final int VERSION = 51;

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    private static final String[] OPTION_SET_PROJECTION = {
            OptionSetModel.Columns.UID, OptionSetModel.Columns.CODE, OptionSetModel.Columns.NAME,
            OptionSetModel.Columns.DISPLAY_NAME, OptionSetModel.Columns.CREATED,
            OptionSetModel.Columns.LAST_UPDATED, OptionSetModel.Columns.VERSION, OptionSetModel.Columns.VALUE_TYPE
    };

    private OptionSetStore optionSetStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.optionSetStore = new OptionSetStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistOptionSetInDatabase() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = optionSetStore.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, VERSION, VALUE_TYPE);

        Cursor cursor = database().query(OptionSetModel.OPTION_SET, OPTION_SET_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME,
                DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                VERSION, VALUE_TYPE).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        optionSetStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
