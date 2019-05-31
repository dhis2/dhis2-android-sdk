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

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUploadCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.DataValueImportSummary;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataValueCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataValue, DataValueCollectionRepository>
        implements ReadOnlyWithUploadCollectionRepository<DataValue> {

    private final DataValueStore store;
    private final DataValuePostCall postCall;

    @Inject
    DataValueCollectionRepository(final DataValueStore store,
                                  final Map<String, ChildrenAppender<DataValue>> childrenAppenders,
                                  final RepositoryScope scope,
                                  final DataValuePostCall postCall) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataValueCollectionRepository(store, childrenAppenders, s, postCall)));
        this.store = store;
        this.postCall = postCall;
    }

    @Override
    public Callable<DataValueImportSummary> upload() {
        return postCall;
    }

    public DataValueObjectRepository value(String period,
                                           String organisationUnit,
                                           String dataElement,
                                           String categoryOptionCombo,
                                           String attributeOptionCombo) {
        RepositoryScope updatedScope = byPeriod().eq(period)
                .byOrganisationUnitUid().eq(organisationUnit)
                .byDataElementUid().eq(dataElement)
                .byCategoryOptionComboUid().eq(categoryOptionCombo)
                .byAttributeOptionComboUid().eq(attributeOptionCombo)
                .scope;
        return new DataValueObjectRepository(store, childrenAppenders, updatedScope, period, organisationUnit,
                dataElement, categoryOptionCombo, attributeOptionCombo);
    }

    public StringFilterConnector<DataValueCollectionRepository> byDataElementUid() {
        return cf.string(DataValueFields.DATA_ELEMENT);
    }

    public StringFilterConnector<DataValueCollectionRepository> byPeriod() {
        return cf.string(DataValueFields.PERIOD);
    }

    public StringFilterConnector<DataValueCollectionRepository> byOrganisationUnitUid() {
        return cf.string(DataValueTableInfo.ORGANISATION_UNIT);
    }

    public StringFilterConnector<DataValueCollectionRepository> byCategoryOptionComboUid() {
        return cf.string(DataValueFields.CATEGORY_OPTION_COMBO);
    }

    public StringFilterConnector<DataValueCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(DataValueFields.ATTRIBUTE_OPTION_COMBO);
    }

    public StringFilterConnector<DataValueCollectionRepository> byValue() {
        return cf.string(DataValueFields.VALUE);
    }

    public StringFilterConnector<DataValueCollectionRepository> byStoredBy() {
        return cf.string(DataValueFields.STORED_BY);
    }

    public DateFilterConnector<DataValueCollectionRepository> byCreated() {
        return cf.date(DataValueFields.CREATED);
    }

    public DateFilterConnector<DataValueCollectionRepository> byLastUpdated() {
        return cf.date(DataValueFields.LAST_UPDATED);
    }

    public StringFilterConnector<DataValueCollectionRepository> byComment() {
        return cf.string(DataValueFields.COMMENT);
    }

    public BooleanFilterConnector<DataValueCollectionRepository> byFollowUp() {
        return cf.bool(DataValueFields.FOLLOW_UP);
    }

    public EnumFilterConnector<DataValueCollectionRepository, State> byState() {
        return cf.enumC(BaseDataModel.Columns.STATE);
    }
}