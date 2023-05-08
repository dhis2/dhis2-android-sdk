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

package org.hisp.dhis.android.core.datavalue;

import static org.hisp.dhis.android.core.datavalue.DataValueConflictTableInfo.Columns;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.imports.ImportStatus;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataValueConflictCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataValueConflict, DataValueConflictCollectionRepository>
        implements ReadOnlyCollectionRepository<DataValueConflict> {

    ObjectStore<DataValueConflict> store;

    @Inject
    DataValueConflictCollectionRepository(final ObjectStore<DataValueConflict> store,
                                          final Map<String, ChildrenAppender<DataValueConflict>> childrenAppenders,
                                          final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataValueConflictCollectionRepository(store, childrenAppenders, s)));
        this.store = store;
    }

    public DataValueConflictCollectionRepository byDataSet(String dataSetUid) {
        return cf.subQuery(DataValueByDataSetQueryHelper.getDataValueConflictKey())
                .rawSubQuery(DataValueByDataSetQueryHelper.getOperator(),
                        DataValueByDataSetQueryHelper.whereClause(dataSetUid));
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byConflict() {
        return cf.string(Columns.CONFLICT);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byValue() {
        return cf.string(Columns.VALUE);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byAttributeOptionCombo() {
        return cf.string(Columns.ATTRIBUTE_OPTION_COMBO);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byCategoryOptionCombo() {
        return cf.string(Columns.CATEGORY_OPTION_COMBO);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byDataElement() {
        return cf.string(Columns.DATA_ELEMENT);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byPeriod() {
        return cf.string(Columns.PERIOD);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byOrganisationUnitUid() {
        return cf.string(Columns.ORG_UNIT);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byErrorCode() {
        return cf.string(Columns.ERROR_CODE);
    }

    public StringFilterConnector<DataValueConflictCollectionRepository> byDisplayDescription() {
        return cf.string(Columns.DISPLAY_DESCRIPTION);
    }

    public EnumFilterConnector<DataValueConflictCollectionRepository, ImportStatus> byStatus() {
        return cf.enumC(Columns.STATUS);
    }

    public DateFilterConnector<DataValueConflictCollectionRepository> byCreated() {
        return cf.date(Columns.CREATED);
    }
}
