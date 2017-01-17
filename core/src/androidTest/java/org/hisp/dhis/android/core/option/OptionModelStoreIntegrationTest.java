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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.option.OptionModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class OptionModelStoreIntegrationTest extends AbsStoreTestCase {

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final long OPTION_SET_ID = 53L;
    private static final String OPTION_SET_UID = "test_option_set_uid";

    private static final String[] OPTION_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME,
            Columns.DISPLAY_NAME, Columns.CREATED,
            Columns.LAST_UPDATED, Columns.OPTION_SET
    };

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    private OptionStore optionStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.optionStore = new OptionStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistOptionInDatabase() throws ParseException {
        // INSERT OPTION SETS
        ContentValues optionSet =
                CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);

        database().insert(Tables.OPTION_SET, null, optionSet);

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = optionStore.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID
        );

        Cursor cursor = database().query(Tables.OPTION, OPTION_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME,
                DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date), OPTION_SET_UID)
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_shouldNotPersistOptionWithoutForeignKey() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        optionStore.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID
        );
    }

    @Test
    public void delete_shouldDeleteOptionsWhenDeletingOptionSet() throws Exception {
        ContentValues optionSet = CreateOptionSetUtils.create(OPTION_SET_ID, OPTION_SET_UID);
        database().insert(Tables.OPTION_SET, null, optionSet);

        ContentValues option = new ContentValues();
        option.put(Columns.ID, 1L);
        option.put(Columns.UID, UID);
        option.put(Columns.OPTION_SET, OPTION_SET_UID);

        String option1Uid = "test_option1_uid";

        ContentValues option1 = new ContentValues();
        option1.put(Columns.ID, 2L);
        option1.put(Columns.UID, option1Uid);
        option1.put(Columns.OPTION_SET, OPTION_SET_UID);

        database().insert(Tables.OPTION, null, option);
        database().insert(Tables.OPTION, null, option1);

        String[] projection = {Columns.ID, Columns.UID, Columns.OPTION_SET};

        Cursor cursor = database().query(Tables.OPTION, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(1L, UID, OPTION_SET_UID);
        assertThatCursor(cursor).hasRow(2L, option1Uid, OPTION_SET_UID).isExhausted();

        database().delete(Tables.OPTION_SET, OptionSetModel.Columns.UID + " =?", new String[]{OPTION_SET_UID});

        cursor = database().query(Tables.OPTION, projection, null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();

    }

    @Test
    public void close_shouldNotCloseDatabase() {
        optionStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
