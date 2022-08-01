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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.internal.DataSetInstanceSQLStatementBuilder;
import org.hisp.dhis.android.core.dataset.internal.DataSetInstanceSummaryStore;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Collections;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataSetInstanceSummaryCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataSetInstanceSummary, DataSetInstanceSummaryCollectionRepository> {

    @Inject
    DataSetInstanceSummaryCollectionRepository(final DataSetInstanceSummaryStore store,
                                               final RepositoryScope scope) {
        super(store, Collections.emptyMap(), scope, new FilterConnectorFactory<>(scope,
                s -> new DataSetInstanceSummaryCollectionRepository(store, s)));
    }

    public StringFilterConnector<DataSetInstanceSummaryCollectionRepository> byDataSetUid() {
        return cf.string(DataSetInstanceSQLStatementBuilder.DATASET_UID_ALIAS);
    }

    public StringFilterConnector<DataSetInstanceSummaryCollectionRepository> byPeriod() {
        return cf.string(DataSetInstanceSQLStatementBuilder.PERIOD_ALIAS);
    }

    public EnumFilterConnector<DataSetInstanceSummaryCollectionRepository, PeriodType> byPeriodType() {
        return cf.enumC(DataSetInstanceSQLStatementBuilder.PERIOD_TYPE_ALIAS);
    }

    public DateFilterConnector<DataSetInstanceSummaryCollectionRepository> byPeriodStartDate() {
        return cf.date(DataSetInstanceSQLStatementBuilder.PERIOD_START_DATE_ALIAS);
    }

    public DateFilterConnector<DataSetInstanceSummaryCollectionRepository> byPeriodEndDate() {
        return cf.date(DataSetInstanceSQLStatementBuilder.PERIOD_END_DATE_ALIAS);
    }

    public StringFilterConnector<DataSetInstanceSummaryCollectionRepository> byOrganisationUnitUid() {
        return cf.string(DataSetInstanceSQLStatementBuilder.ORGANISATION_UNIT_UID_ALIAS);
    }

    public StringFilterConnector<DataSetInstanceSummaryCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(DataSetInstanceSQLStatementBuilder.ATTRIBUTE_OPTION_COMBO_UID_ALIAS);
    }

    public EnumFilterConnector<DataSetInstanceSummaryCollectionRepository, State> byState() {
        return cf.enumC(DataSetInstanceSQLStatementBuilder.STATE_ALIAS);
    }

}