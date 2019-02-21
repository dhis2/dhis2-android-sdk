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

package org.hisp.dhis.android.core.data.database;

import android.database.Cursor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public final class DatabaseAssert {

    DatabaseAdapter databaseAdapter;

    public static DatabaseAssert assertThatDatabase(DatabaseAdapter databaseAdapter) {
        return new DatabaseAssert(databaseAdapter);
    }

    private DatabaseAssert(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public DatabaseAssert isEmpty() {
        verifyEmptyDatabase(true);

        return this;
    }

    public DatabaseAssert isNotEmpty() {
        verifyEmptyDatabase(false);

        return this;
    }

    public DatabaseAssert isEmptyTable(String tableName) {
        assertThat(tableCount(tableName) == 0, is(true));

        return this;
    }

    public DatabaseAssert isNotEmptyTable(String tableName) {
        assertThat(tableCount(tableName) == 0, is(false));

        return this;
    }

    private void verifyEmptyDatabase(boolean expectedEmpty) {
        boolean isEmpty = true;

        Cursor cursor = databaseAdapter.query(" SELECT name FROM sqlite_master "
                + "WHERE type='table' and name!='android_metadata' and name!='sqlite_sequence'");
        int value = cursor.getColumnIndex("name");
        if (value != -1) {
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(value);

                if (tableCount(tableName) > 0) {
                    isEmpty = false;
                }
            }
        }
        cursor.close();
        assertThat(isEmpty, is(expectedEmpty));
    }

    private int tableCount(String tableName) {
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = databaseAdapter.query("SELECT * from " + tableName, null);
            count = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        cursor.close();
        return count;
    }
}
