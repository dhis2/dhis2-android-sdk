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
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope

abstract class ReadOnlyObjectRepositoryImpl<M, R : ReadOnlyObjectRepository<M>> internal constructor(
    private val databaseAdapter: DatabaseAdapter,
    private val childrenAppenderGetter: ChildrenAppenderGetter<M>,
    protected val scope: RepositoryScope,
    repositoryFactory: ObjectRepositoryFactory<R>,
) : ReadOnlyObjectRepository<M> {
    @JvmField protected val cf: FilterConnectorFactory<R> = FilterConnectorFactory(scope, repositoryFactory)

    abstract fun blockingGetWithoutChildren(): M?
    protected abstract suspend fun getWithoutChildrenInternal(): M?

    /**
     * Returns the object in an asynchronous way, returning a `Single<M>`.
     * @return A `Single` object with the object
     */
    override fun get(): Single<M?> {
        return Single.fromCallable { blockingGet() }
    }

    /**
     * Returns the object in a synchronous way. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version [.get].
     * @return the object
     */
    override fun blockingGet(): M? {
        return runBlocking { getInternal() }
    }

    protected suspend fun getInternal(): M? {
        return ChildrenAppenderExecutor.appendInObject(
            blockingGetWithoutChildren(),
            databaseAdapter,
            childrenAppenderGetter,
            scope.children(),
        )
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

    internal suspend fun existsInternal(): Boolean {
        return getWithoutChildrenInternal() != null
    }
}
