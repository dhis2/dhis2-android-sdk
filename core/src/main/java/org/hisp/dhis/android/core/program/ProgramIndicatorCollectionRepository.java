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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.AnalyticsType;
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo.Columns;
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorFields;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramIndicatorCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramIndicator, ProgramIndicatorCollectionRepository> {

    @Inject
    ProgramIndicatorCollectionRepository(
        final IdentifiableObjectStore<ProgramIndicator> store,
        final Map<String, ChildrenAppender<ProgramIndicator>> childrenAppenders,
        final RepositoryScope scope
    ) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new ProgramIndicatorCollectionRepository(store, childrenAppenders, s)));
    }


    public BooleanFilterConnector<ProgramIndicatorCollectionRepository> byDisplayInForm() {
        return cf.bool(Columns.DISPLAY_IN_FORM);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byExpression() {
        return cf.string(Columns.EXPRESSION);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byDimensionItem() {
        return cf.string(Columns.DIMENSION_ITEM);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byFilter() {
        return cf.string(Columns.FILTER);
    }

    public IntegerFilterConnector<ProgramIndicatorCollectionRepository> byDecimals() {
        return cf.integer(Columns.DECIMALS);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byAggregationType() {
        return cf.string(Columns.AGGREGATION_TYPE);
    }

    public EnumFilterConnector<ProgramIndicatorCollectionRepository, AnalyticsType> byAnalyticsType() {
        return cf.enumC(Columns.ANALYTICS_TYPE);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byProgramUid() {
        return cf.string(Columns.PROGRAM);
    }

    public ProgramIndicatorCollectionRepository withLegendSets() {
        return cf.withChild(ProgramIndicatorFields.LEGEND_SETS);
    }

    public ProgramIndicatorCollectionRepository withAnalyticsPeriodBoundaries() {
        return cf.withChild(ProgramIndicatorFields.ANALYTICS_PERIOD_BOUNDARIES);
    }
}