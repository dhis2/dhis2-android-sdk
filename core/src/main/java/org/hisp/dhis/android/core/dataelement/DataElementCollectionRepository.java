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
package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.internal.DataElementFields;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataElementCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<DataElement, DataElementCollectionRepository> {

    @Inject
    DataElementCollectionRepository(final IdentifiableObjectStore<DataElement> store,
                                    final Map<String, ChildrenAppender<DataElement>> childrenAppenders,
                                    final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataElementCollectionRepository(store, childrenAppenders, s)));
    }

    public EnumFilterConnector<DataElementCollectionRepository, ValueType> byValueType() {
        return cf.enumC(DataElementTableInfo.Columns.VALUE_TYPE);
    }

    public BooleanFilterConnector<DataElementCollectionRepository> byZeroIsSignificant() {
        return cf.bool(DataElementTableInfo.Columns.ZERO_IS_SIGNIFICANT);
    }

    public StringFilterConnector<DataElementCollectionRepository> byAggregationType() {
        return cf.string(DataElementTableInfo.Columns.AGGREGATION_TYPE);
    }

    public StringFilterConnector<DataElementCollectionRepository> byFormName() {
        return cf.string(DataElementTableInfo.Columns.FORM_NAME);
    }

    public StringFilterConnector<DataElementCollectionRepository> byDomainType() {
        return cf.string(DataElementTableInfo.Columns.DOMAIN_TYPE);
    }

    public StringFilterConnector<DataElementCollectionRepository> byDisplayFormName() {
        return cf.string(DataElementTableInfo.Columns.DISPLAY_FORM_NAME);
    }

    public StringFilterConnector<DataElementCollectionRepository> byOptionSetUid() {
        return cf.string(DataElementTableInfo.Columns.OPTION_SET);
    }

    public StringFilterConnector<DataElementCollectionRepository> byCategoryComboUid() {
        return cf.string(DataElementTableInfo.Columns.CATEGORY_COMBO);
    }

    public StringFilterConnector<DataElementCollectionRepository> byFieldMask() {
        return cf.string(DataElementTableInfo.Columns.FIELD_MASK);
    }

    public StringFilterConnector<DataElementCollectionRepository> byColor() {
        return cf.string(DataElementTableInfo.Columns.COLOR);
    }

    public StringFilterConnector<DataElementCollectionRepository> byIcon() {
        return cf.string(DataElementTableInfo.Columns.ICON);
    }

    public DataElementCollectionRepository withLegendSets() {
        return cf.withChild(DataElementFields.LEGEND_SETS);
    }
}
