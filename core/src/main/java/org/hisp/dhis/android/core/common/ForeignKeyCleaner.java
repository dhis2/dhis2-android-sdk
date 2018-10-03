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

package org.hisp.dhis.android.core.common;

import android.database.Cursor;
import android.util.Log;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ForeignKeyCleaner {

    private final DatabaseAdapter databaseAdapter;

    public ForeignKeyCleaner(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public Integer cleanForeignKeyErrors() {
        Integer totalRows = 0;
        Integer lastIterationRows;

        do {
            lastIterationRows = cleanForeignKeyErrorsIteration();
            totalRows = totalRows + lastIterationRows;
        } while (lastIterationRows > 0);

        return totalRows;
    }

    private Integer cleanForeignKeyErrorsIteration() {
        Integer rowsCount = 0;
        Cursor errorsCursor = getForeignKeyErrorsCursor();

        if (errorsCursor != null) {
            do {
                deleteForeignKeyReferencedObject(errorsCursor);
                rowsCount++;
            } while (errorsCursor.moveToNext());
            errorsCursor.close();
        }

        return rowsCount;
    }

    private void deleteForeignKeyReferencedObject(Cursor errorsCursor) {
        String tableName = errorsCursor.getString(0);
        String rowId = errorsCursor.getString(1);
        String referencedTableName = errorsCursor.getString(2);

        String selectStatement = "SELECT * FROM " + tableName + " WHERE ROWID = " + rowId + ";";
        Cursor objectCursor = databaseAdapter.query(selectStatement);

        String uid = null;
        if (objectCursor.getCount() > 0) {
            objectCursor.moveToFirst();
            int uidColumnIndex = objectCursor.getColumnIndex(BaseIdentifiableObjectModel.Columns.UID);
            if (uidColumnIndex != -1) {
                uid = objectCursor.getString(uidColumnIndex);
            }
        }
        objectCursor.close();

        List<String> argumentValues = new ArrayList<>();
        argumentValues.add(rowId);
        String[] argumentValuesArray = argumentValues.toArray(new String[argumentValues.size()]);
        String deleteClause = "ROWID = ?;";
        int rowsAffected = databaseAdapter.delete(tableName, deleteClause, argumentValuesArray);
        if (rowsAffected != 0) {
            String msg = " was not persisted on " + tableName +
                    " table to avoid Foreign Key constraint error. Target not found on "
                    + referencedTableName + " table.";
            String warningMsg;
            if (uid == null) {
                warningMsg = "An object" + msg;
            } else {
                warningMsg = "The object " + uid + msg;
            }
            Log.w(this.getClass().getSimpleName(), warningMsg);
        }
    }

    private Cursor getForeignKeyErrorsCursor() {
        Cursor cursor = databaseAdapter.query("PRAGMA foreign_key_check;");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor;
        }

        cursor.close();
        return null;
    }
}