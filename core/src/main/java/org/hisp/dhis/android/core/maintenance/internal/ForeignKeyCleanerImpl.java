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

package org.hisp.dhis.android.core.maintenance.internal;

import android.database.Cursor;
import android.util.Log;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ForeignKeyCleanerImpl implements ForeignKeyCleaner {

    private final DatabaseAdapter databaseAdapter;
    private final ObjectStore<ForeignKeyViolation> foreignKeyViolationStore;

    ForeignKeyCleanerImpl(DatabaseAdapter databaseAdapter,
                          ObjectStore<ForeignKeyViolation> foreignKeyViolationStore) {
        this.databaseAdapter = databaseAdapter;
        this.foreignKeyViolationStore = foreignKeyViolationStore;
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
        String fromTable = errorsCursor.getString(0);
        String rowId = errorsCursor.getString(1);
        String toTable = errorsCursor.getString(2);
        String foreignKeyIdNumber = errorsCursor.getString(3);

        ForeignKeyViolation foreignKeyViolation =
                getForeignKeyViolation(foreignKeyIdNumber, fromTable, toTable, rowId);

        if (foreignKeyViolation != null) {
            foreignKeyViolationStore.insert(foreignKeyViolation);

            List<String> argumentValues = new ArrayList<>();
            argumentValues.add(rowId);
            String[] argumentValuesArray = argumentValues.toArray(new String[argumentValues.size()]);
            String deleteClause = "ROWID = ?;";
            int rowsAffected = databaseAdapter.delete(fromTable, deleteClause, argumentValuesArray);
            if (rowsAffected != 0) {
                String msg = " was not persisted on " + fromTable +
                        " table to avoid Foreign Key constraint error. Target not found on "
                        + toTable + " table. " + foreignKeyViolation.toString();
                String warningMsg;
                if (foreignKeyViolation.fromObjectUid() == null) {
                    warningMsg = "An object" + msg;
                } else {
                    warningMsg = "The object " + foreignKeyViolation.fromObjectUid() + msg;
                }
                Log.w(this.getClass().getSimpleName(), warningMsg);
            }
        }
    }

    private ForeignKeyViolation getForeignKeyViolation(String foreignKeyIdNumber, String fromTable, String toTable,
                                                       String rowId) {
        Cursor listCursor = databaseAdapter.rawQuery("PRAGMA foreign_key_list(" + fromTable + ");");

        ForeignKeyViolation foreignKeyViolation = null;

        try {
            if (listCursor.getCount() > 0) {
                listCursor.moveToFirst();
                do {
                    if (foreignKeyIdNumber.equals(String.valueOf(listCursor.getInt(0)))) {
                        foreignKeyViolation = buildViolation(listCursor, fromTable, toTable, rowId);
                    }

                } while (listCursor.moveToNext());
            }

        } finally {
            listCursor.close();
        }

        return foreignKeyViolation;
    }

    private ForeignKeyViolation buildViolation(Cursor listCursor, String fromTable, String toTable, String rowId) {

        ForeignKeyViolation foreignKeyViolation = null;

        String fromColumn = listCursor.getString(3);
        String selectStatement = "SELECT * FROM " + fromTable + " WHERE ROWID = " + rowId + ";";

        try (Cursor objectCursor = databaseAdapter.rawQuery(selectStatement)) {
            if (objectCursor.getCount() > 0) {
                objectCursor.moveToFirst();

                String uid = null;
                int uidColumnIndex = objectCursor.getColumnIndex(
                        IdentifiableColumns.UID);
                if (uidColumnIndex != -1) {
                    uid = objectCursor.getString(uidColumnIndex);
                }

                List<String> columnAndValues = new ArrayList<>();
                for (String columnName : objectCursor.getColumnNames()) {
                    columnAndValues.add(columnName + ": " +
                            getColumnValueAsString(objectCursor, columnName));
                }

                foreignKeyViolation = ForeignKeyViolation.builder()
                        .fromTable(fromTable)
                        .toTable(toTable)
                        .fromColumn(fromColumn)
                        .toColumn(listCursor.getString(4))
                        .notFoundValue(getColumnValueAsString(objectCursor, fromColumn))
                        .fromObjectRow(CollectionsHelper.commaAndSpaceSeparatedArrayValues(
                                columnAndValues.toArray(new String[objectCursor.getColumnCount()])))
                        .fromObjectUid(uid)
                        .created(new Date())
                        .build();
            }
        }

        return foreignKeyViolation;
    }

    private String getColumnValueAsString(Cursor cursor, String columnName) {

        int fromColumnIndex = cursor.getColumnIndex(columnName);
        int fromColumnType = cursor.getType(fromColumnIndex);

        String columnValue;
        switch (fromColumnType) {
            case 1:
                columnValue = String.valueOf(cursor.getInt(fromColumnIndex));
                break;
            case 2:
                columnValue = String.valueOf(cursor.getFloat(fromColumnIndex));
                break;
            case 3:
                columnValue = cursor.getString(fromColumnIndex);
                break;
            default:
                columnValue = null;
                break;
        }

        return columnValue;
    }

    private Cursor getForeignKeyErrorsCursor() {
        Cursor cursor = databaseAdapter.rawQuery("PRAGMA foreign_key_check;");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor;
        }

        cursor.close();
        return null;
    }

    public static ForeignKeyCleaner create(DatabaseAdapter databaseAdapter) {
        return new ForeignKeyCleanerImpl(
                databaseAdapter,
                ForeignKeyViolationStore.create(databaseAdapter)
        );
    }
}