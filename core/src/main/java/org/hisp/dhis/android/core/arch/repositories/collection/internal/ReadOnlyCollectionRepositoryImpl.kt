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
package org.hisp.dhis.android.core.arch.repositories.collection.internal

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ReadableStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyOneObjectRepositoryFinalImpl
import org.hisp.dhis.android.core.arch.repositories.paging.internal.RepositoryDataSource
import org.hisp.dhis.android.core.arch.repositories.paging.internal.RepositoryPagingSource
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.WhereClauseFromScopeBuilder
import org.hisp.dhis.android.core.common.CoreObject

@Suppress("TooManyFunctions")
open class ReadOnlyCollectionRepositoryImpl<M : CoreObject, R : ReadOnlyCollectionRepository<M>> internal constructor(
    private val store: ReadableStore<M>,
    internal val databaseAdapter: DatabaseAdapter,
    internal val childrenAppenders: ChildrenAppenderGetter<M>,
    scope: RepositoryScope,
    cf: FilterConnectorFactory<R>,
) : BaseRepositoryImpl<R>(scope, cf), ReadOnlyCollectionRepository<M> {

    protected fun blockingGetWithoutChildren(): List<M> {
        return store.selectWhere(
            whereClause,
            OrderByClauseBuilder.orderByFromItems(
                scope.orderBy(),
                scope.pagingKey(),
            ),
        )
    }

    /**
     * Get a [ReadOnlyObjectRepository] pointing to the first element in the list.
     *
     * @return Object repository
     */
    override fun one(): ReadOnlyOneObjectRepositoryFinalImpl<M> {
        return ReadOnlyOneObjectRepositoryFinalImpl(store, databaseAdapter, childrenAppenders, scope)
    }

    /**
     * Get the list of objects in a synchronous way. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version [.get].
     *
     * @return List of objects
     */
    override fun blockingGet(): List<M> {
        return ChildrenAppenderExecutor.appendInObjectCollection(
            blockingGetWithoutChildren(),
            databaseAdapter,
            childrenAppenders,
            scope.children(),
        )
    }

    /**
     * Get the objects in scope in an asynchronous way, returning a `Single<List>`.
     *
     * @return A `Single` object with the list of objects.
     */
    override fun get(): Single<List<M>> {
        return Single.fromCallable { blockingGet() }
    }

    /**
     * Handy method to use in conjunction with PagedListAdapter to build paged lists.
     *
     * @param pageSize Length of the page
     * @return A LiveData object of PagedList of elements
     */
    @Deprecated("Use {@link #getPagingData()} instead}", replaceWith = ReplaceWith("getPagingData()"))
    override fun getPaged(pageSize: Int): LiveData<PagedList<M>> {
        val factory: DataSource.Factory<M, M> = object : DataSource.Factory<M, M>() {
            override fun create(): DataSource<M, M> {
                return dataSource
            }
        }
        return LivePagedListBuilder(factory, pageSize).build()
    }

    override fun getPagingData(pageSize: Int): Flow<PagingData<M>> {
        return getPager(pageSize).flow
    }

    fun getPager(pageSize: Int): Pager<M, M> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
        ) {
            pagingSource
        }
    }

    @Deprecated("Use {@link #getPagingData()} instead}", replaceWith = ReplaceWith("getPagingData()"))
    val dataSource: DataSource<M, M>
        get() = RepositoryDataSource(store, databaseAdapter, scope, childrenAppenders)

    private val pagingSource: PagingSource<M, M>
        get() = RepositoryPagingSource(store, databaseAdapter, scope, childrenAppenders)

    /**
     * Get the count of elements in an asynchronous way, returning a `Single`.
     * @return A `Single` object with the element count
     */
    override fun count(): Single<Int> {
        return Single.fromCallable { blockingCount() }
    }

    /**
     * Get the count of elements. Important: this is a blocking method and it should not be
     * executed in the main thread. Consider the asynchronous version [.count].
     *
     * @return Element count
     */
    override fun blockingCount(): Int {
        return store.countWhere(whereClause)
    }

    /**
     * Check if selection of objects in current scope with applied filters is empty in an asynchronous way,
     * returning a `Single`.
     * @return If selection is empty
     */
    override fun isEmpty(): Single<Boolean> {
        return Single.fromCallable { blockingIsEmpty() }
    }

    /**
     * Check if selection of objects with applied filters is empty in a synchronous way.
     * Important: this is a blocking method and it should not be executed in the main thread.
     * Consider the asynchronous version [.isEmpty].
     *
     * @return If selection is empty
     */
    override fun blockingIsEmpty(): Boolean {
        return !one().blockingExists()
    }

    protected val whereClause: String
        get() = WhereClauseFromScopeBuilder(WhereClauseBuilder()).getWhereClause(scope)
}
