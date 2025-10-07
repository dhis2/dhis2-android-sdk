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
package org.hisp.dhis.android.core.arch.repositories.`object`.internal

import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.arch.db.stores.internal.ReadableStore
import org.hisp.dhis.android.core.arch.handlers.internal.TwoWayTransformer
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.WhereClauseFromScopeBuilder
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder

internal class ReadOnlyWithTransformerObjectRepositoryImpl<M, T, R : ReadOnlyObjectRepository<T>>
internal constructor(
    private val store: ReadableStore<M>,
    private val childrenAppenders: ChildrenAppenderGetter<M>,
    private val scope: RepositoryScope,
    private val transformer: TwoWayTransformer<M, T>,
) : ReadOnlyObjectRepository<T> {

    fun blockingGetWithoutChildren(): M? {
        return runBlocking { getWithoutChildrenInternal() }
    }

    private suspend fun getWithoutChildrenInternal(): M? {
        val whereClauseBuilder = WhereClauseFromScopeBuilder(WhereClauseBuilder())
        return store.selectOneWhere(whereClauseBuilder.getWhereClause(scope))
    }

    /**
     * Returns the object in an asynchronous way, returning a `Single<M>`.
     * @return A `Single` object with the object
     */
    override fun get(): Single<T?> {
        return Single.fromCallable { blockingGet() }
    }

    /**
     * Returns the object in a synchronous way. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version [.get].
     * @return the object
     */
    override fun blockingGet(): T? {
        return runBlocking { getInternal() }
    }

    private suspend fun getInternal(): T? {
        val item = ChildrenAppenderExecutor.appendInObject(
            getWithoutChildrenInternal(),
            childrenAppenders,
            scope.children(),
        )

        return item?.let { transformer.transform(it) }
    }

    /**
     * Returns if the object exists in an asynchronous way, returning a `Single<Boolean>`.
     * @return if the object exists, wrapped in a `Single`
     */
    override fun exists(): Single<Boolean> {
        return rxSingle { existsInternal() }
    }

    /**
     * Returns if the object exists in a synchronous way. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version [.exists].
     * @return if the object exists
     */
    override fun blockingExists(): Boolean {
        return runBlocking { existsInternal() }
    }

    private suspend fun existsInternal(): Boolean {
        return getWithoutChildrenInternal() != null
    }
}
