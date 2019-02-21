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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeItem;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramIndicatorCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramIndicator, ProgramIndicatorCollectionRepository> {

    @Inject
    ProgramIndicatorCollectionRepository(final IdentifiableObjectStore<ProgramIndicator> store,
                                         final Collection<ChildrenAppender<ProgramIndicator>> childrenAppenders,
                                         List<RepositoryScopeItem> scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                updatedScope -> new ProgramIndicatorCollectionRepository(store, childrenAppenders, updatedScope)));
    }


    public BooleanFilterConnector<ProgramIndicatorCollectionRepository> byDisplayInForm() {
        return cf.bool(ProgramIndicatorFields.DISPLAY_IN_FORM);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byExpression() {
        return cf.string(ProgramIndicatorFields.EXPRESSION);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byDimensionItem() {
        return cf.string(ProgramIndicatorFields.DIMENSION_ITEM);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byFilter() {
        return cf.string(ProgramIndicatorFields.FILTER);
    }

    public IntegerFilterConnector<ProgramIndicatorCollectionRepository> byDecimals() {
        return cf.integer(ProgramIndicatorFields.DECIMALS);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byAggregationType() {
        return cf.string(ProgramIndicatorFields.AGGREGATION_TYPE);
    }

    public StringFilterConnector<ProgramIndicatorCollectionRepository> byProgramUid() {
        return cf.string(ProgramIndicatorFields.PROGRAM);
    }

}