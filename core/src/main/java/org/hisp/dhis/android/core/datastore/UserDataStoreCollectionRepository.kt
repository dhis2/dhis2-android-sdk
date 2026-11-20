/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUploadCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.DeletableDataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datastore.internal.UserDataStoreEntryStore
import org.hisp.dhis.android.core.datastore.internal.UserDataStorePostCall
import org.hisp.dhis.android.persistence.datastore.UserDataStoreTableInfo
import org.koin.core.annotation.Singleton

/**
 * Entries of the server /api/userDataStore: values that belong to the logged-in user and are kept in the server,
 * so they are available on any device the user logs in. Use [UserDataStoreDownloader] to download them.
 */
@Singleton
class UserDataStoreCollectionRepository internal constructor(
    private val store: UserDataStoreEntryStore,
    private val call: UserDataStorePostCall,
    scope: RepositoryScope,
    private val databaseAdapter: DatabaseAdapter,
) : ReadOnlyCollectionRepositoryImpl<DataStoreEntry, UserDataStoreCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(scope) { s -> UserDataStoreCollectionRepository(store, call, s, databaseAdapter) },
),
    ReadOnlyWithUploadCollectionRepository<DataStoreEntry> {

    override fun flowUpload(): Flow<D2Progress> = flow {
        val entries = bySyncState().`in`(State.uploadableStatesIncludingError().toList())
            .getWithoutChildrenInternal()
        emitAll(call.uploadDataStoreEntries(entries))
    }

    fun value(namespace: String, key: String): DataStoreObjectRepository {
        val valueScope = byNamespace().eq(namespace)
            .byKey().eq(key)
            .scope

        return DataStoreObjectRepository(store, databaseAdapter, childrenAppenders, valueScope, namespace, key)
    }

    fun byNamespace(): StringFilterConnector<UserDataStoreCollectionRepository> {
        return cf.string(UserDataStoreTableInfo.Columns.NAMESPACE)
    }

    fun byKey(): StringFilterConnector<UserDataStoreCollectionRepository> {
        return cf.string(UserDataStoreTableInfo.Columns.KEY)
    }

    fun byValue(): StringFilterConnector<UserDataStoreCollectionRepository> {
        return cf.string(UserDataStoreTableInfo.Columns.VALUE)
    }

    fun bySyncState(): EnumFilterConnector<UserDataStoreCollectionRepository, State> {
        return cf.enumC(DataColumns.SYNC_STATE)
    }

    fun byDeleted(): BooleanFilterConnector<UserDataStoreCollectionRepository> {
        return cf.bool(DeletableDataColumns.DELETED)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<DataStoreEntry> = emptyMap()
    }
}
