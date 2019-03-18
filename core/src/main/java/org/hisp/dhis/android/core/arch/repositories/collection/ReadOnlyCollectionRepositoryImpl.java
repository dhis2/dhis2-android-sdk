/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.arch.repositories.collection;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppenderExecutor;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyOneObjectRepositoryFinalImpl;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.WhereClauseFromScopeBuilder;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectStore;

import java.util.List;
import java.util.Map;

public class ReadOnlyCollectionRepositoryImpl<M extends Model, R extends ReadOnlyCollectionRepository<M>>
        implements ReadOnlyCollectionRepository<M> {

    private final ObjectStore<M> store;
    protected final Map<String, ChildrenAppender<M>> childrenAppenders;
    protected final RepositoryScope scope;
    protected final FilterConnectorFactory<R> cf;

    public ReadOnlyCollectionRepositoryImpl(ObjectStore<M> store,
                                            Map<String, ChildrenAppender<M>> childrenAppenders,
                                            RepositoryScope scope,
                                            FilterConnectorFactory<R> cf) {
        this.store = store;
        this.childrenAppenders = childrenAppenders;
        this.scope = scope;
        this.cf = cf;
    }

    private List<M> getWithoutChildren() {
        if (scope.filters().isEmpty() && scope.complexFilters().isEmpty()) {
            return store.selectAll();
        } else {
            WhereClauseFromScopeBuilder whereClauseBuilder = new WhereClauseFromScopeBuilder(new WhereClauseBuilder());
            return store.selectWhere(whereClauseBuilder.getWhereClause(scope));
        }
    }

    @Override
    public ReadOnlyOneObjectRepositoryFinalImpl<M> one() {
        return new ReadOnlyOneObjectRepositoryFinalImpl<>(store, childrenAppenders, scope);
    }

    private List<M> getWithAllChildren() {
        return ChildrenAppenderExecutor.appendInObjectCollection(getWithoutChildren(), childrenAppenders.values());
    }

    @Override
    public List<M> get() {
        if (scope.children().areAllChildrenSelected) {
            return getWithAllChildren();
        } else {
            return getWithoutChildren();
        }
    }

    public R withAllChildren() {
        return cf.repositoryFactory.updated(RepositoryScopeHelper.withAllChildren(scope));
    }
}
