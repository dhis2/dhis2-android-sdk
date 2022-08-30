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

package org.hisp.dhis.android.core.maintenance;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ForeignKeyViolationCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<ForeignKeyViolation, ForeignKeyViolationCollectionRepository> {

    @Inject
    ForeignKeyViolationCollectionRepository(final ObjectStore<ForeignKeyViolation> store,
                                            final Map<String, ChildrenAppender<ForeignKeyViolation>> childrenAppenders,
                                            final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new ForeignKeyViolationCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<ForeignKeyViolationCollectionRepository> byFromTable() {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_TABLE);
    }

    public StringFilterConnector<ForeignKeyViolationCollectionRepository> byFromColumn() {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_COLUMN);
    }

    public StringFilterConnector<ForeignKeyViolationCollectionRepository> byToTable() {
        return cf.string(ForeignKeyViolationTableInfo.Columns.TO_TABLE);
    }

    public StringFilterConnector<ForeignKeyViolationCollectionRepository> byToColumn() {
        return cf.string(ForeignKeyViolationTableInfo.Columns.TO_COLUMN);
    }

    public StringFilterConnector<ForeignKeyViolationCollectionRepository> byNotFoundValue() {
        return cf.string(ForeignKeyViolationTableInfo.Columns.NOT_FOUND_VALUE);
    }

    public StringFilterConnector<ForeignKeyViolationCollectionRepository> byFromObjectUid() {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_OBJECT_UID);
    }

    public StringFilterConnector<ForeignKeyViolationCollectionRepository> byFromObjectRow() {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_OBJECT_ROW);
    }

    public DateFilterConnector<ForeignKeyViolationCollectionRepository> byCreated() {
        return cf.date(ForeignKeyViolationTableInfo.Columns.CREATED);
    }
}
