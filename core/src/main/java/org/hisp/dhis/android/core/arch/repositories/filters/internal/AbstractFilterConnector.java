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

package org.hisp.dhis.android.core.arch.repositories.filters.internal;

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeComplexFilterItem;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class AbstractFilterConnector<R extends BaseRepository, V> {

    private final BaseRepositoryFactory<R> repositoryFactory;
    private final RepositoryScope scope;
    final String key;

    AbstractFilterConnector(BaseRepositoryFactory<R> repositoryFactory,
                            RepositoryScope scope,
                            String key) {
        this.repositoryFactory = repositoryFactory;
        this.scope = scope;
        this.key = key;
    }

    abstract String wrapValue(V value);

    RepositoryScope updatedUnwrappedScope(FilterItemOperator operator, String valueStr) {
        return RepositoryScopeHelper.withFilterItem(scope,
                RepositoryScopeFilterItem.builder().key(key).operator(operator).value(valueStr).build());
    }

    R newWithWrappedScope(FilterItemOperator operator, V value) {
        return repositoryFactory.updated(updatedUnwrappedScope(operator, wrapValue(value)));
    }

    RepositoryScope updatePassedScope(FilterItemOperator operator, String valueStr, RepositoryScope scope) {
        return RepositoryScopeHelper.withFilterItem(scope,
                RepositoryScopeFilterItem.builder().key(key).operator(operator).value(valueStr).build());
    }

    R newWithPassedScope(FilterItemOperator operator, V value, RepositoryScope scope) {
        return repositoryFactory.updated(updatePassedScope(operator, wrapValue(value), scope));
    }

    private RepositoryScope updatedUnwrappedScope(String whereClause) {
        return RepositoryScopeHelper.withComplexFilterItem(scope,
                RepositoryScopeComplexFilterItem.builder().whereQuery(whereClause).build());
    }

    R newWithWrappedScope(String whereClause) {
        return repositoryFactory.updated(updatedUnwrappedScope(whereClause));
    }

    String getCommaSeparatedValues(Collection<V> values) {
        List<String> wrappedValues = new ArrayList<>();
        for (V v: values) {
            wrappedValues.add(wrapValue(v));
        }
        return CollectionsHelper.commaAndSpaceSeparatedCollectionValues(wrappedValues);
    }

    R newWithUnwrappedScope(FilterItemOperator operator, String value) {
        return repositoryFactory.updated(updatedUnwrappedScope(operator, value));

    }
}
