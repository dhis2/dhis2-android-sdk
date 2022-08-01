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

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.dataset.internal.DataSetFields;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.dataset.DataSetTableInfo.Columns;

@Reusable
public class DataSetCollectionRepository
            extends ReadOnlyIdentifiableCollectionRepositoryImpl<DataSet, DataSetCollectionRepository> {

    @Inject
    DataSetCollectionRepository(final IdentifiableObjectStore<DataSet> store,
                                final Map<String, ChildrenAppender<DataSet>> childrenAppenders,
                                final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataSetCollectionRepository(store, childrenAppenders, s)));
    }

    public EnumFilterConnector<DataSetCollectionRepository, PeriodType> byPeriodType() {
        return cf.enumC(Columns.PERIOD_TYPE);
    }

    public StringFilterConnector<DataSetCollectionRepository> byCategoryComboUid() {
        return cf.string(Columns.CATEGORY_COMBO);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byMobile() {
        return cf.bool(Columns.MOBILE);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byVersion() {
        return cf.integer(Columns.VERSION);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byExpiryDays() {
        return cf.integer(Columns.EXPIRY_DAYS);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byTimelyDays() {
        return cf.integer(Columns.TIMELY_DAYS);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byNotifyCompletingUser() {
        return cf.bool(Columns.NOTIFY_COMPLETING_USER);
    }

    public IntegerFilterConnector<DataSetCollectionRepository> byOpenFuturePeriods() {
        return cf.integer(Columns.OPEN_FUTURE_PERIODS);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byFieldCombinationRequired() {
        return cf.bool(Columns.FIELD_COMBINATION_REQUIRED);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byValidCompleteOnly() {
        return cf.bool(Columns.VALID_COMPLETE_ONLY);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byNoValueRequiresComment() {
        return cf.bool(Columns.NO_VALUE_REQUIRES_COMMENT);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> bySkipOffline() {
        return cf.bool(Columns.SKIP_OFFLINE);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byDataElementDecoration() {
        return cf.bool(Columns.DATA_ELEMENT_DECORATION);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byRenderAsTabs() {
        return cf.bool(Columns.RENDER_AS_TABS);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byRenderHorizontally() {
        return cf.bool(Columns.RENDER_HORIZONTALLY);
    }

    public BooleanFilterConnector<DataSetCollectionRepository> byAccessDataWrite() {
        return cf.bool(Columns.ACCESS_DATA_WRITE);
    }

    public StringFilterConnector<DataSetCollectionRepository> byColor() {
        return cf.string(Columns.COLOR);
    }

    public StringFilterConnector<DataSetCollectionRepository> byIcon() {
        return cf.string(Columns.ICON);
    }

    public DataSetCollectionRepository byOrganisationUnitUid(String uid) {
        return byOrganisationUnitList(Collections.singletonList(uid));
    }

    public DataSetCollectionRepository byOrganisationUnitList(List<String> uids) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
                DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                uids);
    }

    public DataSetCollectionRepository byOrganisationUnitScope(OrganisationUnit.Scope scope) {
        return cf.subQuery(IdentifiableColumns.UID).inTwoLinkTable(
                DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
                DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                Collections.singletonList(scope.name())
        );
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
