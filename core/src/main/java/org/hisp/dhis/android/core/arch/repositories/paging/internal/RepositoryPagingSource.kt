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
import org.hisp.dhis.android.core.arch.db.stores.internal.ReadableStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor.appendInObjectCollection
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.WhereClauseFromScopeBuilder
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.persistence.common.querybuilders.OrderByClauseBuilder
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import java.io.IOException

class RepositoryPagingSource<M : CoreObject> internal constructor(
    private val store: ReadableStore<M>,
    private val scope: RepositoryScope,
    private val childrenAppenders: ChildrenAppenderGetter<M>,
) : PagingSource<Int, M>() {

    override fun getRefreshKey(state: PagingState<Int, M>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, M> {
        try {
            val offset = params.key

            val whereClause = WhereClauseFromScopeBuilder(WhereClauseBuilder()).getWhereClause(scope)
            val withoutChildren = store.selectWhere(
                whereClause,
                OrderByClauseBuilder.orderByFromItems(scope.orderBy()),
                params.loadSize,
                offset,
            )

            val items = appendInObjectCollection(withoutChildren, childrenAppenders, scope.children())

            val prevKey = if (offset == null) null else offset - params.loadSize
            val nextKey = if (items.size < params.loadSize) null else (offset ?: 0) + params.loadSize

            return LoadResult.Page(
                data = items,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}
