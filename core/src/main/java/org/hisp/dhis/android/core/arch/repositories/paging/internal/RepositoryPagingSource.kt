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

package org.hisp.dhis.android.core.arch.repositories.paging.internal

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ReadableStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor.appendInObjectCollection
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.WhereClauseFromScopeBuilder
import org.hisp.dhis.android.core.common.CoreObject
import java.io.IOException

class RepositoryPagingSource<M : CoreObject> internal constructor(
    private val store: ReadableStore<M>,
    private val databaseAdapter: DatabaseAdapter,
    private val scope: RepositoryScope,
    private val childrenAppenders: ChildrenAppenderGetter<M>,
) : PagingSource<M, M>() {

    override fun getRefreshKey(state: PagingState<M, M>): M? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }

    override suspend fun load(params: LoadParams<M>): LoadResult<M, M> {
        try {
            val whereClauseBuilder = WhereClauseBuilder()

            params.key?.let { key ->
                val reverse = when (params) {
                    is LoadParams.Prepend -> true
                    else -> false
                }

                OrderByClauseBuilder.addSortingClauses(
                    whereClauseBuilder,
                    scope.orderBy(),
                    key.toContentValues(),
                    reverse,
                    scope.pagingKey(),
                )
            }

            val whereClause = WhereClauseFromScopeBuilder(whereClauseBuilder).getWhereClause(
                scope,
            )
            val withoutChildren = store.selectWhere(
                whereClause,
                OrderByClauseBuilder.orderByFromItems(scope.orderBy(), scope.pagingKey()),
                params.loadSize,
            )

            val items = appendInObjectCollection(withoutChildren, databaseAdapter, childrenAppenders, scope.children())
            return LoadResult.Page(
                data = items,
                prevKey = items.firstOrNull(),
                nextKey = items.getOrNull(params.loadSize - 1),
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}
