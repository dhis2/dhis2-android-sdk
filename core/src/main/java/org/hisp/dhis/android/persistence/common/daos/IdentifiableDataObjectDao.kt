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

package org.hisp.dhis.android.persistence.common.daos

import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilder

internal abstract class IdentifiableDataObjectDao<P : EntityDB<*>>(
    tableName: String,
    override val builder: SQLStatementBuilder,
) : IdentifiableObjectDao<P>(tableName, builder) {

    suspend fun setSyncState(uid: String, state: State): Int {
        val query =
            RoomRawQuery("UPDATE $tableName SET ${DataColumns.SYNC_STATE} = '$state' WHERE ${IdentifiableColumns.UID} = '$uid'")
        return intRawQuery(query)
    }

    suspend fun setSyncState(uids: List<String>, state: State): Int {
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(IdentifiableColumns.UID, uids)
            .build()
        val query = RoomRawQuery("UPDATE $tableName SET ${DataColumns.SYNC_STATE} = '$state' WHERE $whereClause")
        return intRawQuery(query)
    }

    suspend fun setSyncStateIfUploading(uid: String, state: State): Int {
        val query = RoomRawQuery(
            "UPDATE $tableName SET ${DataColumns.SYNC_STATE} = '$state' " +
                "WHERE ${IdentifiableColumns.UID} = '$uid' AND ${DataColumns.SYNC_STATE} = '${State.UPLOADING}'"
        )
        return intRawQuery(query)
    }

    suspend fun getSyncState(uid: String): State? {
        val query =
            RoomRawQuery("SELECT ${DataColumns.SYNC_STATE} FROM $tableName WHERE ${IdentifiableColumns.UID} = '$uid'")
        return stateRawQuery(query)
    }

    suspend fun exists(uid: String): Boolean {
        val query = RoomRawQuery("SELECT 1 FROM $tableName WHERE ${IdentifiableColumns.UID} = '$uid'")
        return intRawQuery(query) > 0
    }

    suspend fun getUploadableSyncStatesIncludingError(): List<P> {
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.SYNC_STATE,
                EnumHelper.asStringList(State.uploadableStatesIncludingError().toList())
            ).build()
        val query = RoomRawQuery("SELECT * FROM $tableName WHERE $whereClause")
        return objectListRawQuery(query)
    }

    @RawQuery
    protected abstract suspend fun stateRawQuery(query: RoomRawQuery): State?
}
