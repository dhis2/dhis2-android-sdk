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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DeletedFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo.Columns;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TrackedEntityDataValueCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<TrackedEntityDataValue, TrackedEntityDataValueCollectionRepository> {

    private final TrackedEntityDataValueStore store;
    private final DataStatePropagator dataStatePropagator;

    @Inject
    TrackedEntityDataValueCollectionRepository(
            final TrackedEntityDataValueStore store,
            final Map<String, ChildrenAppender<TrackedEntityDataValue>> childrenAppenders,
            final RepositoryScope scope,
            final DataStatePropagator dataStatePropagator) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new TrackedEntityDataValueCollectionRepository(store, childrenAppenders, s, dataStatePropagator)));
        this.store = store;
        this.dataStatePropagator = dataStatePropagator;
    }

    public TrackedEntityDataValueObjectRepository value(String event, String dataElement) {
        RepositoryScope updatedScope = byEvent().eq(event).byDataElement().eq(dataElement).scope;
        return new TrackedEntityDataValueObjectRepository(
                store, childrenAppenders, updatedScope, dataStatePropagator, event, dataElement);
    }

    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byEvent() {
        return cf.string(Columns.EVENT);
    }

    public DateFilterConnector<TrackedEntityDataValueCollectionRepository> byCreated() {
        return cf.date(Columns.CREATED);
    }

    public DateFilterConnector<TrackedEntityDataValueCollectionRepository> byLastUpdated() {
        return cf.date(Columns.LAST_UPDATED);
    }

    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byDataElement() {
        return cf.string(Columns.DATA_ELEMENT);
    }

    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byStoredBy() {
        return cf.string(Columns.STORED_BY);
    }

    public StringFilterConnector<TrackedEntityDataValueCollectionRepository> byValue() {
        return cf.string(Columns.VALUE);
    }

    public BooleanFilterConnector<TrackedEntityDataValueCollectionRepository> byProvidedElsewhere() {
        return cf.bool(Columns.PROVIDED_ELSEWHERE);
    }

    public DeletedFilterConnector<TrackedEntityDataValueCollectionRepository> byDeleted() {
        return cf.deleted(Columns.VALUE);
    }
}