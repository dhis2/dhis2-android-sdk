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

package org.hisp.dhis.android.core.arch.repositories.filters;

import org.hisp.dhis.android.core.arch.repositories.collection.CollectionRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeItem;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

abstract class BaseFilterConnector<R extends ReadOnlyCollectionRepository<?>, V> {

    private final CollectionRepositoryFactory<R> repositoryFactory;
    private final List<RepositoryScopeItem> scope;
    private final String key;

    BaseFilterConnector(CollectionRepositoryFactory<R> repositoryFactory,
                        List<RepositoryScopeItem> scope,
                        String key) {
        this.repositoryFactory = repositoryFactory;
        this.scope = scope;
        this.key = key;
    }

    abstract String wrapValue(V value);

    private List<RepositoryScopeItem> updatedWrappedScope(String operator, V value) {
        List<RepositoryScopeItem> copiedScope = new ArrayList<>(scope);
        copiedScope.add(RepositoryScopeItem.builder().key(key).operator(operator).value(wrapValue(value)).build());
        return copiedScope;
    }

    private List<RepositoryScopeItem> updatedUnwrappedScope(String operator, String valueStr) {
        List<RepositoryScopeItem> copiedScope = new ArrayList<>(scope);
        copiedScope.add(RepositoryScopeItem.builder().key(key).operator(operator).value(valueStr).build());
        return copiedScope;
    }

    R newWithWrappedScope(String operator, V value) {
        return repositoryFactory.newWithScope(updatedWrappedScope(operator, value));
    }

    private String getCommaSeparatedValues(Collection<V> values) {
        List<String> wrappedValues = new ArrayList<>();
        for (V v: values) {
            wrappedValues.add(wrapValue(v));
        }
        return Utils.commaAndSpaceSeparatedCollectionValues(wrappedValues);
    }

    private R newWithUnwrappedScope(String operator, String value) {
        return repositoryFactory.newWithScope(updatedUnwrappedScope(operator, value));

    }

    public R eq(V value) {
        return newWithWrappedScope("=", value);
    }

    public R neq(V value) {
        return newWithWrappedScope("!=", value);
    }

    public R in(Collection<V> values) {
        return newWithUnwrappedScope("IN", "(" + getCommaSeparatedValues(values) + ")");
    }

    @SafeVarargs
    public final R in(V... values) {
        return in(Arrays.asList(values));
    }

    public R notIn(Collection<V> values) {
        return newWithUnwrappedScope("NOT IN", "(" + getCommaSeparatedValues(values) + ")");
    }

    @SafeVarargs
    public final R notIn(V... values) {
        return notIn(Arrays.asList(values));
    }
}
