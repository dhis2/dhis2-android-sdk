/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.db.access;

import android.database.Cursor;

public final class SqliteCheckerUtility {

    private SqliteCheckerUtility() {
    }

    public static boolean isTableEmpty(DatabaseAdapter databaseAdapter, String table) {
        boolean isTableEmpty = true;
        Cursor cursor = databaseAdapter.rawQuery(" SELECT * FROM "+table);
        int value = cursor.getCount();
        if (value > 0) {
            isTableEmpty = false;
        }
        cursor.close();
        return isTableEmpty;
    }

    public static boolean isDatabaseEmpty(DatabaseAdapter databaseAdapter) {
        boolean isDatabaseEmpty = true;
        Cursor cursor = databaseAdapter.rawQuery(" SELECT name FROM sqlite_master WHERE "
                + "type='table' and name!='android_metadata' and name!='sqlite_sequence'");
        int value = cursor.getColumnIndex("name");
        if (value != -1) {
            while (cursor.moveToNext()){
                String tableName = cursor.getString(value);
                Cursor resTable = databaseAdapter.rawQuery("SELECT * from " + tableName);
                if (resTable.getCount() > 0) {
                    isDatabaseEmpty = false;
                    break;
                }
            }
        }
        cursor.close();
        return isDatabaseEmpty;
    }

    public static boolean ifTableExist(String table, DatabaseAdapter databaseAdapter) {
        boolean isExist = false;
        Cursor cursor = databaseAdapter.rawQuery("PRAGMA table_info(" + table + ")");
        int itemsCount = cursor.getCount();
        if (itemsCount > 0) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }
}