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
        var rowsDeletedIteration: Int

        do {
            val foreignKeyErrors = getForeignKeyErrors()
            if (foreignKeyErrors.isEmpty()) {
                rowsDeletedIteration = 0
            } else {
                for (error in foreignKeyErrors) {
                    deleteForeignKeyReferencedObject(error)
                }
                rowsDeletedIteration = foreignKeyErrors.size
            }
            totalRows += rowsDeletedIteration
        } while (rowsDeletedIteration > 0)

        return totalRows
    }

    // Represents the data from "PRAGMA foreign_key_check"
    private data class RawForeignKeyError(
        val fromTable: String,
        val rowId: String,
        val toTable: String,
        val foreignKeyId: String, // This is the 'id' of the foreign key constraint from foreign_key_list
    )

    private suspend fun getForeignKeyErrors(): List<RawForeignKeyError> {
        val results = databaseAdapter.rawQueryWithTypedValues("PRAGMA foreign_key_check;")
        return results.mapNotNull { rowMap ->
            val valuesList = rowMap.values.toList()
            if (valuesList.size >= 4) {
                RawForeignKeyError(
                    fromTable = valuesList[0].toString(),
                    rowId = valuesList[1].toString(),
                    toTable = valuesList[2].toString(),
                    foreignKeyId = valuesList[3].toString(),
                )
            } else {
                null
            }
        }
    }

    private suspend fun deleteForeignKeyReferencedObject(error: RawForeignKeyError) {
        val violation = getForeignKeyViolation(
            error.foreignKeyId,
            error.fromTable,
            error.toTable,
            error.rowId,
        )
        violation?.let {
            foreignKeyViolationStore.updateOrInsertWhere(it)

            val rowsAffected = databaseAdapter.delete(error.fromTable, "ROWID = ?", arrayOf(error.rowId))

            if (rowsAffected > 0) {
                val msg =
                    " was not persisted on ${error.fromTable} table to avoid Foreign Key constraint error. " +
                        "Target not found on ${error.toTable} table. $it"
                val warningMsg = it.fromObjectUid()?.let { uid -> "The object $uid$msg" } ?: "An object$msg"
                Log.w(this::class.simpleName, warningMsg)
            }
        }
    }

    private suspend fun getForeignKeyViolation(
        foreignKeyIdString: String,
        fromTable: String,
        toTable: String,
        rowId: String,
    ): ForeignKeyViolation? {
        val foreignKeyEntry = findForeignKeyEntry(fromTable, foreignKeyIdString) ?: return null
        return buildViolation(foreignKeyEntry, fromTable, toTable, rowId)
    }

    private data class ForeignKeyEntry(
        val fromColumn: String,
        val toColumn: String,
    )

    private suspend fun findForeignKeyEntry(
        fromTable: String,
        foreignKeyIdString: String, // This is the 'id' from PRAGMA foreign_key_list
    ): ForeignKeyEntry? {
        // `fromTable` needs to be part of the query string, not a bound argument for PRAGMA
        val results = databaseAdapter.rawQueryWithTypedValues("PRAGMA foreign_key_list(`$fromTable`);")

        for (rowMap in results) {
            val valuesList = rowMap.values.toList()
            if (valuesList.size > 4 && valuesList[0].toString() == foreignKeyIdString) {
                return ForeignKeyEntry(
                    fromColumn = valuesList[3].toString(),
                    toColumn = valuesList[4].toString(),
                )
            }
        }
        return null
    }

    private suspend fun buildViolation(
        foreignKeyEntry: ForeignKeyEntry,
        fromTable: String,
        toTable: String,
        rowId: String,
    ): ForeignKeyViolation? {
        val rowMap =
            databaseAdapter.rawQueryWithTypedValues(
                "SELECT * FROM `$fromTable` WHERE ROWID = ?",
                arrayOf(rowId),
            )
                .firstOrNull()

        return rowMap?.let { map ->
            val uid = map[IdentifiableColumns.UID] as? String

            val columnAndValues = map.entries.map { entry ->
                // entry.value can be String, Long, Double, ByteArray, null
                val valueStr = when (val v = entry.value) {
                    is ByteArray -> "[BLOB]" // Or a more sophisticated representation
                    null -> "NULL"
                    else -> v.toString()
                }
                "${entry.key}: $valueStr"
            }

            ForeignKeyViolation.builder().fromTable(fromTable).toTable(toTable).fromColumn(foreignKeyEntry.fromColumn)
                .toColumn(foreignKeyEntry.toColumn).notFoundValue(map[foreignKeyEntry.fromColumn]?.toString() ?: "NULL")
                .fromObjectRow(commaAndSpaceSeparatedArrayValues(columnAndValues.toTypedArray())).fromObjectUid(uid)
                .created(Date()).build()
        }
    }
}
