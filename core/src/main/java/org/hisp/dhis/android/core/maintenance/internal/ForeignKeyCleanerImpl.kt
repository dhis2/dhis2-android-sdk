/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.maintenance.internal

import android.database.Cursor
import android.util.Log
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.commaAndSpaceSeparatedArrayValues
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation
import org.koin.core.annotation.Singleton
import java.util.Date

@Suppress("MagicNumber")
@Singleton
internal class ForeignKeyCleanerImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val foreignKeyViolationStore: ForeignKeyViolationStore,
) : ForeignKeyCleaner {
    override suspend fun cleanForeignKeyErrors(): Int {
        var totalRows = 0
        var rowsDeleted: Int

        do {
            rowsDeleted = cleanForeignKeyErrorsIteration()
            totalRows += rowsDeleted
        } while (rowsDeleted > 0)

        return totalRows
    }

    private suspend fun cleanForeignKeyErrorsIteration(): Int {
        return foreignKeyErrorsCursor?.use { cursor ->
            var count = 0
            do {
                deleteForeignKeyReferencedObject(cursor)
                count++
            } while (cursor.moveToNext())
            count
        } ?: 0
    }

    private suspend fun deleteForeignKeyReferencedObject(cursor: Cursor) {
        val fromTable = cursor.getString(0)
        val rowId = cursor.getString(1)
        val toTable = cursor.getString(2)
        val foreignKeyId = cursor.getString(3)

        val violation = getForeignKeyViolation(foreignKeyId, fromTable, toTable, rowId)
        violation?.let {
            foreignKeyViolationStore.updateOrInsertWhere(it)

            val deleteClause = "ROWID = ?"
            val rowsAffected = databaseAdapter.delete(fromTable, deleteClause, arrayOf(rowId))
            if (rowsAffected > 0) {
                val msg =
                    " was not persisted on $fromTable table to avoid Foreign Key constraint error. " +
                        "Target not found on $toTable table. $it"
                val warningMsg = it.fromObjectUid()
                    ?.let { uid -> "The object $uid$msg" }
                    ?: "An object$msg"
                Log.w(this::class.simpleName, warningMsg)
            }
        }
    }

    private fun getForeignKeyViolation(
        foreignKeyId: String,
        fromTable: String,
        toTable: String,
        rowId: String,
    ): ForeignKeyViolation? {
        val foreignKeyEntry = findForeignKeyEntry(fromTable, foreignKeyId) ?: return null
        return buildViolation(foreignKeyEntry, fromTable, toTable, rowId)
    }

    private data class ForeignKeyEntry(
        val fromColumn: String,
        val toColumn: String,
    )

    private fun findForeignKeyEntry(
        fromTable: String,
        foreignKeyId: String,
    ): ForeignKeyEntry? {
        val sql = "PRAGMA foreign_key_list($fromTable);"
        databaseAdapter.rawQuery(sql).use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getInt(0).toString() == foreignKeyId) {
                    return ForeignKeyEntry(
                        fromColumn = cursor.getString(3),
                        toColumn = cursor.getString(4),
                    )
                }
            }
        }
        return null
    }

    private fun buildViolation(
        foreignKeyEntry: ForeignKeyEntry,
        fromTable: String,
        toTable: String,
        rowId: String,
    ): ForeignKeyViolation? {
        val selectStmt = "SELECT * FROM $fromTable WHERE ROWID = $rowId;"
        databaseAdapter.rawQuery(selectStmt).use { objectCursor ->
            if (objectCursor.moveToFirst()) {
                val uid = objectCursor.getColumnIndex(IdentifiableColumns.UID)
                    .takeIf { it != -1 }
                    ?.let { objectCursor.getString(it) }

                val columnAndValues = objectCursor.columnNames.map { columnName ->
                    "$columnName: ${getColumnValueAsString(objectCursor, columnName)}"
                }

                return ForeignKeyViolation.builder()
                    .fromTable(fromTable)
                    .toTable(toTable)
                    .fromColumn(foreignKeyEntry.fromColumn)
                    .toColumn(foreignKeyEntry.toColumn)
                    .notFoundValue(getColumnValueAsString(objectCursor, foreignKeyEntry.fromColumn))
                    .fromObjectRow(commaAndSpaceSeparatedArrayValues(columnAndValues.toTypedArray()))
                    .fromObjectUid(uid)
                    .created(Date())
                    .build()
            }
        }
        return null
    }

    private fun getColumnValueAsString(cursor: Cursor, columnName: String): String? {
        val index = cursor.getColumnIndex(columnName)
        return if (index != -1) {
            when (cursor.getType(index)) {
                Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(index).toString()
                Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(index).toString()
                Cursor.FIELD_TYPE_STRING -> cursor.getString(index)
                else -> null
            }
        } else {
            null
        }
    }

    private val foreignKeyErrorsCursor: Cursor?
        get() {
            val cursor = databaseAdapter.rawQuery("PRAGMA foreign_key_check;")
            return if (cursor.count > 0) {
                cursor.apply { moveToFirst() }
            } else {
                cursor.close()
                null
            }
        }
}
