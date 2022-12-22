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
package org.hisp.dhis.android.core.arch.repositories.collection.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.TwoWayTransformer
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyWithTransformerObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface

internal class ReadOnlyWithUidAndTransformerCollectionRepositoryImpl
<M, T, R : ReadOnlyCollectionRepository<T>> internal constructor(
    private val store: IdentifiableObjectStore<M>,
    childrenAppenders: Map<String, ChildrenAppender<M>>,
    scope: RepositoryScope,
    cf: FilterConnectorFactory<R>,
    override val transformer: TwoWayTransformer<M, T>
) : ReadOnlyWithTransformerCollectionRepositoryImpl<M, T, R>(store, childrenAppenders, scope, cf, transformer),
    ReadOnlyWithUidCollectionRepository<T>
        where M : CoreObject, M : ObjectWithUidInterface, T : ObjectWithUidInterface {
    /**
     * Get the list of uids of objects in scope in an asynchronous way, returning a `Single<List<String>>`.
     *
     * @return A `Single` object with the list of uids.
     */
    override fun getUids(): Single<List<String>> {
        return Single.fromCallable { blockingGetUids() }
    }

    /**
     * Get the list of uids of objects in scope in a synchronous way. Important: this is a blocking method and it should
     * not be executed in the main thread. Consider the asynchronous version [.getUids].
     *
     * @return List of uids
     */
    override fun blockingGetUids(): List<String> {
        return store.selectUidsWhere(
            whereClause,
            OrderByClauseBuilder.orderByFromItems(
                scope.orderBy(), scope.pagingKey()
            )
        )
    }

    /**
     * Returns a new [ReadOnlyObjectRepository] whose scope is the one of the current repository plus the
     * equal filter applied to the uid. This method is equivalent to byUid().eq(uid).one().
     * @param uid to compare
     * @return the ReadOnlyObjectRepository
     */
    override fun uid(uid: String): ReadOnlyObjectRepository<T> {
        val updatedScope: RepositoryScope = RepositoryScopeHelper.withUidFilterItem(scope, uid)
        return ReadOnlyWithTransformerObjectRepositoryImpl(store, childrenAppenders, updatedScope, transformer)
    }
}
