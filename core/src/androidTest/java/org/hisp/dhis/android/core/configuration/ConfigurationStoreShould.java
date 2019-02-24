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

package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.HttpUrl;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class ConfigurationStoreShould extends AbsStoreTestCase {
    private static final String[] PROJECTION = {ConfigurationTableInfo.Columns.ID,
            ConfigurationTableInfo.Columns.SERVER_URL};

    private ConfigurationStore store;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        store = ConfigurationStoreImpl.create(databaseAdapter());
    }

    @Test
    public void persist_row_in_database_when_save() {
        store.save(Configuration.builder().serverUrl(HttpUrl.parse("http://testserver.org/")).build());

        Cursor cursor = database().query(ConfigurationTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor)
                .hasRow(1L, "http://testserver.org/api/")
                .isExhausted();
    }

    @Test
    public void not_thrown_on_save_conflict() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationTableInfo.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationTableInfo.TABLE_INFO.name(), null, contentValues);

        // trying to configure configuration with server url (which is set to be unique in the table)
        store.save(Configuration.builder().serverUrl(HttpUrl.parse("http://testserver.org/")).build());

        Cursor cursor = database().query(ConfigurationTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor)
                .hasRow(2L, "http://testserver.org/api/")
                .isExhausted();
    }

    @Test
    public void not_persist_more_than_one_url_on_save() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationTableInfo.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationTableInfo.TABLE_INFO.name(), null, contentValues);

        HttpUrl url = HttpUrl.parse("http://othertestserver.org/");
        store.save(Configuration.builder().serverUrl(url).build());

        Cursor cursor = database().query(ConfigurationTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor)
                .hasRow(2L, "http://othertestserver.org/api/")
                .isExhausted();
    }

    @Test
    public void delete_persisted_rows_on_delete() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationTableInfo.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationTableInfo.TABLE_INFO.name(), null, contentValues);

        long deleted = store.delete();

        Cursor cursor = database().query(ConfigurationTableInfo.TABLE_INFO.name(),
                PROJECTION, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void not_fail_if_no_rows_persisted_on_delete() {
        long deleted = store.delete();
        assertThat(deleted).isEqualTo(0);
    }

    @Test
    public void return_persisted_row_when_query() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationTableInfo.Columns.SERVER_URL, "http://testserver.org/");

        database().insert(ConfigurationTableInfo.TABLE_INFO.name(), null, contentValues);

        HttpUrl url = HttpUrl.parse("http://othertestserver.org/");
        store.save(Configuration.builder().serverUrl(url).build());

        Configuration persistedConfiguration = store.selectFirst();
        assertThat(persistedConfiguration.serverUrl().toString()).isEqualTo("http://othertestserver.org/api/");
    }

    @Test
    public void return_null_if_no_rows_are_persisted() {
        Configuration persistedConfiguration = store.selectFirst();
        assertThat(persistedConfiguration).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.save(null);
    }
}