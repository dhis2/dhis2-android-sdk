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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithValueCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.DataStatePropagator;
import org.hisp.dhis.android.core.common.Transformer;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TrackedEntityDataValueCollectionRepository extends ReadWriteWithValueCollectionRepositoryImpl
        <TrackedEntityDataValue, TrackedEntityDataValueCreateProjection, TrackedEntityDataValueCollectionRepository> {

    private final TrackedEntityDataValueStore store;
    private final DataStatePropagator dataStatePropagator;

    @Inject
    TrackedEntityDataValueCollectionRepository(
            final TrackedEntityDataValueStore store,
            final Map<String, ChildrenAppender<TrackedEntityDataValue>> childrenAppenders,
            final RepositoryScope scope,
            final Transformer<TrackedEntityDataValueCreateProjection, TrackedEntityDataValue> transformer,
            final DataStatePropagator dataStatePropagator) {
        super(store, childrenAppenders, scope, transformer, new FilterConnectorFactory<>(scope,
                s -> new TrackedEntityDataValueCollectionRepository(store, childrenAppenders, s, transformer,
                        dataStatePropagator)));
        this.store = store;
        this.dataStatePropagator = dataStatePropagator;
    }

    public TrackedEntityDataValueObjectRepository value(String event, String dataElement) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withFilterItem(scope,
                RepositoryScopeFilterItem.builder().key(TrackedEntityDataValueTableInfo.Columns.EVENT)
                        .operator("=").value("'" + event + "'").build());
        updatedScope = RepositoryScopeHelper.withFilterItem(updatedScope,
                RepositoryScopeFilterItem.builder().key(TrackedEntityDataValueFields.DATA_ELEMENT)
                        .operator("=").value("'" + dataElement + "'").build());

        return new TrackedEntityDataValueObjectRepository(store, childrenAppenders, updatedScope, dataStatePropagator);
    }


    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byEvent() {
        return cf.string(TrackedEntityDataValueTableInfo.Columns.EVENT);
    }

    public DateFilterConnector<TrackedEntityDataValueCollectionRepository> byCreated() {
        return cf.date(TrackedEntityDataValueFields.CREATED);
    }

    public DateFilterConnector<TrackedEntityDataValueCollectionRepository> byLastUpdated() {
        return cf.date(TrackedEntityDataValueFields.LAST_UPDATED);
    }

    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byDataElement() {
        return cf.string(TrackedEntityDataValueFields.DATA_ELEMENT);
    }

    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byStoredBy() {
        return cf.string(TrackedEntityDataValueFields.STORED_BY);
    }

    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byValue() {
        return cf.string(TrackedEntityDataValueFields.VALUE);
    }

    public BooleanFilterConnector<TrackedEntityDataValueCollectionRepository> byProvidedElsewhere() {
        return cf.bool(TrackedEntityDataValueFields.PROVIDED_ELSEWHERE);
    }
}