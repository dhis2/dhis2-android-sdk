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

package org.hisp.dhis.android.core.systeminfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class SystemInfoStoreShould extends AbsStoreTestCase {

    private static final long ID = 1L;
    private static final String DATE_FORMAT = "testDateFormat";
    private static final String VERSION = "test.version-snapshot";
    private static final String CONTEXT_PATH = "https://test.context.com/path";

    private static final String[] SYSTEM_INFO_PROJECTION = {
            Columns.SERVER_DATE,
            Columns.DATE_FORMAT,
            Columns.VERSION,
            Columns.CONTEXT_PATH
    };

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";
    private final Date date;

    private SystemInfoStore store;

    public SystemInfoStoreShould() throws ParseException {
        this.date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.store = new SystemInfoStoreImpl(databaseAdapter());
    }

    @Test
    public void insert_system_info_in_data_base_when_insert() throws ParseException {

        long rowId = store.insert(date, DATE_FORMAT, VERSION, CONTEXT_PATH);
        Cursor cursor = database().query(
                SystemInfoModel.TABLE,
                SYSTEM_INFO_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                DATE_FORMAT,
                VERSION,
                CONTEXT_PATH
        ).isExhausted();
    }

    @Test
    public void update_system_info_in_data_base_when_update() throws Exception {
        ContentValues systemInfo = new ContentValues();
        systemInfo.put(Columns.SERVER_DATE, DATE);
        systemInfo.put(Columns.DATE_FORMAT, DATE_FORMAT);
        systemInfo.put(Columns.VERSION, VERSION);
        systemInfo.put(Columns.CONTEXT_PATH, CONTEXT_PATH);

        database().insert(SystemInfoModel.TABLE, null, systemInfo);

        Cursor cursor = database().query(SystemInfoModel.TABLE, SYSTEM_INFO_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).hasRow(DATE, DATE_FORMAT, VERSION, CONTEXT_PATH);

        Date newDate = BaseIdentifiableObject.DATE_FORMAT.parse("2017-02-24T13:37:00.007");
        int update = store.update(newDate, DATE_FORMAT, VERSION, CONTEXT_PATH, CONTEXT_PATH);

        assertThat(update).isEqualTo(1);
        cursor = database().query(SystemInfoModel.TABLE, SYSTEM_INFO_PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(newDate),
                DATE_FORMAT,
                VERSION,
                CONTEXT_PATH
        );
    }

    @Test
    public void delete_system_info_in_data_base_when_delete() throws Exception {
        ContentValues systemInfo = new ContentValues();
        systemInfo.put(Columns.SERVER_DATE, DATE);
        systemInfo.put(Columns.DATE_FORMAT, DATE_FORMAT);
        systemInfo.put(Columns.VERSION, VERSION);
        systemInfo.put(Columns.CONTEXT_PATH, CONTEXT_PATH);

        database().insert(SystemInfoModel.TABLE, null, systemInfo);

        Cursor cursor = database().query(SystemInfoModel.TABLE, SYSTEM_INFO_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).hasRow(DATE, DATE_FORMAT, VERSION, CONTEXT_PATH);

        int delete = store.delete(CONTEXT_PATH);
        assertThat(delete).isEqualTo(1);

        cursor = database().query(SystemInfoModel.TABLE, SYSTEM_INFO_PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_date() {
        store.insert(null, DATE_FORMAT, VERSION, CONTEXT_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_dateFormat() {
        store.insert(date, null, VERSION, CONTEXT_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_version() {
        store.insert(date, DATE_FORMAT, null, CONTEXT_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_contextPath() {
        store.insert(date, DATE_FORMAT, VERSION, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_date() {
        store.update(null, DATE_FORMAT, VERSION, CONTEXT_PATH, CONTEXT_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_dateFormat() {
        store.update(date, null, VERSION, CONTEXT_PATH, CONTEXT_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_version() {
        store.update(date, DATE_FORMAT, null, CONTEXT_PATH, CONTEXT_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_contextPath() {
        store.update(date, DATE_FORMAT, VERSION, null, CONTEXT_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_null_whereContextPath() {
        store.update(date, DATE_FORMAT, VERSION, CONTEXT_PATH, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_null_ContextPath() {
        store.delete(null);
    }

}
