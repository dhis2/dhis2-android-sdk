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

package org.hisp.dhis.android.core.datastore

import dagger.Reusable
import io.reactivex.Completable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteValueObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ObjectRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadWriteWithValueObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.State
import javax.inject.Inject

@Reusable
class DataStoreObjectRepository @Inject internal constructor(
    store: ObjectWithoutUidStore<DataStoreEntry>,
    databaseAdapter: DatabaseAdapter,
    childrenAppenders: ChildrenAppenderGetter<DataStoreEntry>,
    scope: RepositoryScope,
    private val namespace: String,
    private val key: String,
) : ReadWriteWithValueObjectRepositoryImpl<DataStoreEntry, DataStoreObjectRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    ObjectRepositoryFactory { s ->
        DataStoreObjectRepository(store, databaseAdapter, childrenAppenders, s, namespace, key)
    },
),
    ReadWriteValueObjectRepository<DataStoreEntry> {
    override fun set(value: String?): Completable {
        return Completable.fromAction { blockingSet(value) }
    }

    override fun blockingSet(value: String?) {
        val entry = setBuilder().value(value).deleted(false).build()
        setObject(entry)
    }

    override fun delete(): Completable {
        return Completable.fromAction { blockingDelete() }
    }

    override fun blockingDelete() {
        blockingGetWithoutChildren()?.let { entry ->
            if (entry.syncState() == State.TO_POST) {
                super.blockingDelete()
            } else {
                setObject(entry.toBuilder().deleted(true).syncState(State.TO_UPDATE).build())
            }
        }
    }

    private fun setBuilder(): DataStoreEntry.Builder {
        val entry = blockingGetWithoutChildren()

        return if (entry != null) {
            entry.toBuilder()
                .syncState(if (entry.syncState() == State.TO_POST) State.TO_POST else State.TO_UPDATE)
        } else {
            DataStoreEntry.builder()
                .namespace(namespace)
                .key(key)
                .syncState(State.TO_POST)
                .deleted(false)
        }
    }
}
