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

package org.hisp.dhis.android.core.arch.repositories.collection.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.common.IdentifiableObject;

import java.util.Map;

public class ReadOnlyIdentifiableCollectionRepositoryImpl<M extends CoreObject & IdentifiableObject,
        R extends ReadOnlyCollectionRepository<M>>
        extends ReadOnlyWithUidCollectionRepositoryImpl<M, R>
        implements ReadOnlyIdentifiableCollectionRepository<M, R> {

    public ReadOnlyIdentifiableCollectionRepositoryImpl(final IdentifiableObjectStore<M> store,
                                                        final Map<String, ChildrenAppender<M>> childrenAppenders,
                                                        final RepositoryScope scope,
                                                        final FilterConnectorFactory<R> cf) {
        super(store, childrenAppenders, scope, cf);
    }

    @Override
    public StringFilterConnector<R> byUid() {
        return cf.string(IdentifiableColumns.UID);
    }

    @Override
    public StringFilterConnector<R> byCode() {
        return cf.string(IdentifiableColumns.CODE);
    }

    @Override
    public StringFilterConnector<R> byName() {
        return cf.string(IdentifiableColumns.NAME);
    }

    @Override
    public StringFilterConnector<R> byDisplayName() {
        return cf.string(IdentifiableColumns.DISPLAY_NAME);
    }

    @Override
    public DateFilterConnector<R> byCreated() {
        return cf.date(IdentifiableColumns.CREATED);
    }

    @Override
    public DateFilterConnector<R> byLastUpdated() {
        return cf.date(IdentifiableColumns.LAST_UPDATED);
    }

    public R orderByUid(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(IdentifiableColumns.UID, direction);
    }

    public R orderByCode(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(IdentifiableColumns.CODE, direction);
    }

    public R orderByName(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(IdentifiableColumns.NAME, direction);
    }

    public R orderByDisplayName(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(IdentifiableColumns.DISPLAY_NAME, direction);
    }

    public R orderByCreated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(IdentifiableColumns.CREATED, direction);
    }

    public R orderByLastUpdated(RepositoryScope.OrderByDirection direction) {
        return cf.withOrderBy(IdentifiableColumns.LAST_UPDATED, direction);
    }
}