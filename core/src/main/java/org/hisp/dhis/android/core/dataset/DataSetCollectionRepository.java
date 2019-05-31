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
package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataSetCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<DataSet, DataSetCollectionRepository> {

    @Inject
    DataSetCollectionRepository(final IdentifiableObjectStore<DataSet> store,
                                final Map<String, ChildrenAppender<DataSet>> childrenAppenders,
                                final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataSetCollectionRepository(store, childrenAppenders, s)));
    }

    public EnumFilterConnector<DataSetCollectionRepository, PeriodType> byPeriodType() {
        return cf.enumC(DataSetFields.PERIOD_TYPE);
    }

    public StringFilterConnector<DataSetCollectionRepository> byCategoryComboUid() {
        return cf.string(DataSetFields.CATEGORY_COMBO);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byMobile() {
        return cf.bool(DataSetFields.MOBILE);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byVersion() {
        return cf.integer(DataSetFields.VERSION);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byExpiryDays() {
        return cf.integer(DataSetFields.EXPIRY_DAYS);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byTimelyDays() {
        return cf.integer(DataSetFields.TIMELY_DAYS);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byNotifyCompletingUser() {
        return cf.bool(DataSetFields.NOTIFY_COMPLETING_USER);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byOpenFuturePeriods() {
        return cf.integer(DataSetFields.OPEN_FUTURE_PERIODS);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byFieldCombinationRequired() {
        return cf.bool(DataSetFields.FIELD_COMBINATION_REQUIRED);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byValidCompleteOnly() {
        return cf.bool(DataSetFields.VALID_COMPLETE_ONLY);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byNoValueRequiresComment() {
        return cf.bool(DataSetFields.NO_VALUE_REQUIRES_COMMENT);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> bySkipOffline() {
        return cf.bool(DataSetFields.SKIP_OFFLINE);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byDataElementDecoration() {
        return cf.bool(DataSetFields.DATA_ELEMENT_DECORATION);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byRenderAsTabs() {
        return cf.bool(DataSetFields.RENDER_AS_TABS);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byRenderHorizontally() {
        return cf.bool(DataSetFields.RENDER_HORIZONTALLY);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byAccessDataWrite() {
        return cf.bool(DataSetFields.ACCESS_DATA_WRITE);
    }

    public DataSetCollectionRepository withStyle() {
        return cf.withChild(DataSetFields.STYLE);
    }

    public DataSetCollectionRepository withSections() {
        return cf.withChild(DataSetFields.SECTIONS);
    }

    public DataSetCollectionRepository withCompulsoryDataElementOperands() {
        return cf.withChild(DataSetFields.COMPULSORY_DATA_ELEMENT_OPERANDS);
    }

    public DataSetCollectionRepository withDataInputPeriods() {
        return cf.withChild(DataSetFields.DATA_INPUT_PERIODS);
    }

    public DataSetCollectionRepository withDataSetElements() {
        return cf.withChild(DataSetFields.DATA_SET_ELEMENTS);
    }

    public DataSetCollectionRepository withIndicators() {
        return cf.withChild(DataSetFields.INDICATORS);
    }
}
