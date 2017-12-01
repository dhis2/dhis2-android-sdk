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

package org.hisp.dhis.android.core.constant;

import android.database.Cursor;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class ConstantStoreShould extends AbsStoreTestCase {

    private ConstantStore store;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Date CREATED = new java.util.Date();
    private static final Date LAST_UPDATED = new java.util.Date();
    private static final String VALUE = "0.18";

    private static final String[] CONSTANT_PROJECTION = {
            ConstantModel.Columns.UID, ConstantModel.Columns.CODE, ConstantModel.Columns.NAME, ConstantModel.Columns.DISPLAY_NAME,
            ConstantModel.Columns.CREATED, ConstantModel.Columns.LAST_UPDATED, ConstantModel.Columns.VALUE
    };

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        store = new ConstantStoreImpl(databaseAdapter());
    }

    @Test
    public void persist_row_in_database_when_insert() {
        long rowId = store.insert(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, VALUE);
        Cursor cursor = database().query(ConstantModel.TABLE, CONSTANT_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isNotEqualTo(-1L); // Checks that the insert was successful (row ID would otherwise be -1)

        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(CREATED), BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED), VALUE);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(null, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, VALUE);
    }
}
