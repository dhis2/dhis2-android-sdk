/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.maintenance

import androidx.room.Dao
import androidx.room.RoomDatabase
import androidx.room.Transaction
import org.hisp.dhis.android.persistence.common.daos.ObjectDao

@Dao
internal abstract class ForeignKeyViolationDao(

    // Inject RoomDatabase (your AppDatabase instance) into the DAO
    private val db: RoomDatabase,
) : ObjectDao<ForeignKeyViolationDB> {

//    @RawQuery
//    abstract suspend fun getRawQueryData(query: SupportSQLiteQuery): List<Map<String, Any?>>

//    @RawQuery
//    abstract suspend fun getRowFromTable(query: SupportSQLiteQuery): Map<String, Any?>?

    /**
     * Deletes a row from a dynamically specified table by its ROWID.
     * This operation is performed within a transaction.
     *
     * @param tableName The name of the table from which to delete.
     * @param rowId The ROWID of the row to delete.
     * @return The number of rows affected by the delete operation.
     */
    @Transaction // Ensures atomicity if there were other DB operations here
    open suspend fun deleteRowFromTableByRowId(tableName: String, rowId: String): Int {
        val supportDb = db.openHelper.writableDatabase
        val deleteClause = "ROWID = ?"
        // Use backticks for table name for safety, though it's already a parameter
        return supportDb.delete("`$tableName`", deleteClause, arrayOf(rowId))
    }
}
