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

package org.hisp.dhis.android.persistence.datastore

import androidx.room.Query
import org.hisp.dhis.android.persistence.common.daos.ObjectDao
import org.hisp.dhis.android.processor.GenerateDaoQueries

@GenerateDaoQueries
internal interface DataStoreDaoAux : ObjectDao<DataStoreDB> {
    @Query(
        """UPDATE DataStore 
        SET ${DataStoreTableInfo.Columns.SYNC_STATE} = :state 
        WHERE ${DataStoreTableInfo.Columns.NAMESPACE} = :namespace 
        AND `${DataStoreTableInfo.Columns.KEY}` = :key;""",
    )
    fun setSyncState(state: String, namespace: String, key: String)

    @Query(
        """UPDATE DataStore 
        SET ${DataStoreTableInfo.Columns.SYNC_STATE} = :state 
        WHERE ${DataStoreTableInfo.Columns.NAMESPACE} = :namespace 
        AND `${DataStoreTableInfo.Columns.KEY}` = :key 
        AND ${DataStoreTableInfo.Columns.SYNC_STATE} = 'UPLOADING';""",
    )
    fun setStateIfUploading(state: String, namespace: String, key: String)

    @Query(
        """
        DELETE FROM ${DataStoreTableInfo.TABLE_NAME}
        WHERE ${DataStoreTableInfo.Columns.NAMESPACE} = :namespace
          AND ${DataStoreTableInfo.Columns.SYNC_STATE} IN (:syncStates) 
    """,
    )
    fun deleteByNamespaceAndSyncStates(
        namespace: String,
        syncStates: List<String>,
    ): Int

    @Query(
        """
        DELETE FROM ${DataStoreTableInfo.TABLE_NAME}
        WHERE ${DataStoreTableInfo.Columns.NAMESPACE} = :namespace
          AND ${DataStoreTableInfo.Columns.SYNC_STATE} IN (:syncStates)
          AND `${DataStoreTableInfo.Columns.KEY}` NOT IN (:keysToKeep) 
    """,
    )
    fun deleteByNamespaceSyncStatesAndNotInKeys(
        namespace: String,
        syncStates: List<String>,
        keysToKeep: List<String>,
    ): Int
}
