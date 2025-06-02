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

import io.reactivex.Completable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxCompletable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteValueObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ObjectRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadWriteWithValueObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.datastore.internal.LocalDataStoreStore
import org.hisp.dhis.android.core.maintenance.D2Error

class LocalDataStoreObjectRepository internal constructor(
    store: LocalDataStoreStore,
    databaseAdapter: DatabaseAdapter,
    childrenAppenders: ChildrenAppenderGetter<KeyValuePair>,
    scope: RepositoryScope,
    private val key: String,
) : ReadWriteWithValueObjectRepositoryImpl<KeyValuePair, LocalDataStoreObjectRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    ObjectRepositoryFactory { s: RepositoryScope ->
        LocalDataStoreObjectRepository(
            store,
            databaseAdapter,
            childrenAppenders,
            s,
            key,
        )
    },
),
    ReadWriteValueObjectRepository<KeyValuePair> {
    override fun set(value: String?): Completable {
        return rxCompletable { setInternal(value) }
    }

    @Throws(D2Error::class)
    override fun blockingSet(value: String?) {
        runBlocking { setInternal(value) }
    }

    @Throws(D2Error::class)
    private suspend fun setInternal(value: String?) {
        val pair = KeyValuePair.builder()
            .key(key)
            .value(value)
            .build()
        setObject(pair)
    }
}
