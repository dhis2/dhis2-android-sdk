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
package org.hisp.dhis.android.core.arch.repositories.paging.internal;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ReadableStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.WhereClauseFromScopeBuilder;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.List;
import java.util.Map;

public final class RepositoryDataSource<M extends CoreObject> extends ItemKeyedDataSource<M, M> {

    private final ReadableStore<M> store;
    private final RepositoryScope scope;
    private final Map<String, ChildrenAppender<M>> childrenAppenders;

    public RepositoryDataSource(ReadableStore<M> store,
                                RepositoryScope scope,
                                Map<String, ChildrenAppender<M>> childrenAppenders) {
        this.store = store;
        this.scope = scope;
        this.childrenAppenders = childrenAppenders;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<M> params, @NonNull LoadInitialCallback<M> callback) {
        String whereClause = new WhereClauseFromScopeBuilder(new WhereClauseBuilder()).getWhereClause(scope);
        List<M> withoutChildren = store.selectWhere(whereClause,
                OrderByClauseBuilder.orderByFromItems(scope.orderBy(), scope.pagingKey()), params.requestedLoadSize);
        callback.onResult(appendChildren(withoutChildren));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<M> params, @NonNull LoadCallback<M> callback) {
        loadPages(params, callback, false);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<M> params, @NonNull LoadCallback<M> callback) {
        loadPages(params, callback, true);
    }

    private void loadPages(@NonNull LoadParams<M> params, @NonNull LoadCallback<M> callback, boolean reversed) {
        WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
        OrderByClauseBuilder.addSortingClauses(whereClauseBuilder, scope.orderBy(),
                params.key.toContentValues(), reversed, scope.pagingKey());
        String whereClause = new WhereClauseFromScopeBuilder(whereClauseBuilder).getWhereClause(scope);
        List<M> withoutChildren = store.selectWhere(whereClause,
                OrderByClauseBuilder.orderByFromItems(scope.orderBy(), scope.pagingKey()),
                params.requestedLoadSize);
        callback.onResult(appendChildren(withoutChildren));
    }

    @NonNull
    @Override
    public M getKey(@NonNull M item) {
        return item;
    }

    private List<M> appendChildren(List<M> withoutChildren) {
        return ChildrenAppenderExecutor.appendInObjectCollection(withoutChildren, childrenAppenders, scope.children());
    }
}