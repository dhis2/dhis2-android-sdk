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
package org.hisp.dhis.android.core.indicator;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.dataset.SectionIndicatorLinkTableInfo;
import org.hisp.dhis.android.core.indicator.IndicatorTableInfo.Columns;
import org.hisp.dhis.android.core.indicator.internal.IndicatorFields;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class IndicatorCollectionRepository
        extends ReadOnlyNameableCollectionRepositoryImpl<Indicator, IndicatorCollectionRepository> {

    @Inject
    IndicatorCollectionRepository(final IdentifiableObjectStore<Indicator> store,
                                  final Map<String, ChildrenAppender<Indicator>> childrenAppenders,
                                  final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new IndicatorCollectionRepository(store, childrenAppenders, s)));
    }

    public BooleanFilterConnector<IndicatorCollectionRepository> byAnnualized() {
        return cf.bool(Columns.ANNUALIZED);
    }

    public StringFilterConnector<IndicatorCollectionRepository> byIndicatorTypeUid() {
        return cf.string(Columns.INDICATOR_TYPE);
    }

    public StringFilterConnector<IndicatorCollectionRepository> byNumerator() {
        return cf.string(Columns.NUMERATOR);
    }

    public StringFilterConnector<IndicatorCollectionRepository> byNumeratorDescription() {
        return cf.string(Columns.NUMERATOR_DESCRIPTION);
    }

    public StringFilterConnector<IndicatorCollectionRepository> byDenominator() {
        return cf.string(Columns.DENOMINATOR);
    }

    public StringFilterConnector<IndicatorCollectionRepository> byDenominatorDescription() {
        return cf.string(Columns.DENOMINATOR_DESCRIPTION);
    }

    public StringFilterConnector<IndicatorCollectionRepository> byUrl() {
        return cf.string(Columns.URL);
    }

    public IndicatorCollectionRepository withLegendSets() {
        return cf.withChild(IndicatorFields.LEGEND_SETS);
    }

    public IndicatorCollectionRepository byDataSetUid(String dataSetUid) {
        return cf.subQuery(Columns.UID).inLinkTable(
                DataSetIndicatorLinkTableInfo.TABLE_INFO.name(),
                DataSetIndicatorLinkTableInfo.Columns.INDICATOR,
                DataSetIndicatorLinkTableInfo.Columns.DATA_SET,
                Collections.singletonList(dataSetUid)
        );
    }

    public IndicatorCollectionRepository bySectionUid(String dataSetUid) {
        return cf.subQuery(Columns.UID).inLinkTable(
                SectionIndicatorLinkTableInfo.TABLE_INFO.name(),
                SectionIndicatorLinkTableInfo.Columns.INDICATOR,
                SectionIndicatorLinkTableInfo.Columns.SECTION,
                Collections.singletonList(dataSetUid)
        );
    }
}
