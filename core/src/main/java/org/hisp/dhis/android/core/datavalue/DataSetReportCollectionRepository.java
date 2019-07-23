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

import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.datavalue.internal.DataSetReportSQLStatementBuilder;
import org.hisp.dhis.android.core.datavalue.internal.DataSetReportStore;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Collections;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataSetReportCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataSetReport, DataSetReportCollectionRepository> {

    @Inject
    public DataSetReportCollectionRepository(final DataSetReportStore store,
                                             final RepositoryScope scope) {
        super(store, Collections.emptyMap(), scope, new FilterConnectorFactory<>(scope,
                s -> new DataSetReportCollectionRepository(store, s)));
    }

    public StringFilterConnector<DataSetReportCollectionRepository> byDataSetUid() {
        return cf.string(DataSetReportSQLStatementBuilder.DATASET_UID_ALIAS);
    }

    public StringFilterConnector<DataSetReportCollectionRepository> byPeriod() {
        return cf.string(DataSetReportSQLStatementBuilder.PERIOD_ALIAS);
    }

    public EnumFilterConnector<DataSetReportCollectionRepository, PeriodType> byPeriodType() {
        return cf.enumC(DataSetReportSQLStatementBuilder.PERIOD_TYPE_ALIAS);
    }

    public DateFilterConnector<DataSetReportCollectionRepository> byPeriodStartDate() {
        return cf.date(DataSetReportSQLStatementBuilder.PERIOD_START_DATE_ALIAS);
    }

    public DateFilterConnector<DataSetReportCollectionRepository> byPeriodEndDate() {
        return cf.date(DataSetReportSQLStatementBuilder.PERIOD_END_DATE_ALIAS);
    }

    public StringFilterConnector<DataSetReportCollectionRepository> byOrganisationUnitUid() {
        return cf.string(DataSetReportSQLStatementBuilder.ORGANISATION_UNIT_UID_ALIAS);
    }

    public StringFilterConnector<DataSetReportCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(DataSetReportSQLStatementBuilder.ATTRIBUTE_OPTION_COMBO_UID_ALIAS);
    }

}