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
package org.hisp.dhis.android.core.dataset

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.dataset.internal.DataInputPeriodChildrenAppender
import org.hisp.dhis.android.core.dataset.internal.DataSetCompulsoryDataElementOperandChildrenAppender
import org.hisp.dhis.android.core.dataset.internal.DataSetElementChildrenAppender
import org.hisp.dhis.android.core.dataset.internal.DataSetStore
import org.hisp.dhis.android.core.indicator.internal.DataSetIndicatorChildrenAppender
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.network.dataset.DataSetFields
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class DataSetCollectionRepository internal constructor(
    store: DataSetStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<DataSet, DataSetCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        DataSetCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byPeriodType(): EnumFilterConnector<DataSetCollectionRepository, PeriodType> {
        return cf.enumC(DataSetTableInfo.Columns.PERIOD_TYPE)
    }

    fun byCategoryComboUid(): StringFilterConnector<DataSetCollectionRepository> {
        return cf.string(DataSetTableInfo.Columns.CATEGORY_COMBO)
    }

    fun byMobile(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.MOBILE)
    }

    fun byVersion(): IntegerFilterConnector<DataSetCollectionRepository> {
        return cf.integer(DataSetTableInfo.Columns.VERSION)
    }

    fun byExpiryDays(): IntegerFilterConnector<DataSetCollectionRepository> {
        return cf.integer(DataSetTableInfo.Columns.EXPIRY_DAYS)
    }

    fun byTimelyDays(): IntegerFilterConnector<DataSetCollectionRepository> {
        return cf.integer(DataSetTableInfo.Columns.TIMELY_DAYS)
    }

    fun byNotifyCompletingUser(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.NOTIFY_COMPLETING_USER)
    }

    fun byOpenFuturePeriods(): IntegerFilterConnector<DataSetCollectionRepository> {
        return cf.integer(DataSetTableInfo.Columns.OPEN_FUTURE_PERIODS)
    }

    fun byFieldCombinationRequired(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.FIELD_COMBINATION_REQUIRED)
    }

    fun byValidCompleteOnly(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.VALID_COMPLETE_ONLY)
    }

    fun byNoValueRequiresComment(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.NO_VALUE_REQUIRES_COMMENT)
    }

    fun bySkipOffline(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.SKIP_OFFLINE)
    }

    fun byDataElementDecoration(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.DATA_ELEMENT_DECORATION)
    }

    fun byRenderAsTabs(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.RENDER_AS_TABS)
    }

    fun byRenderHorizontally(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.RENDER_HORIZONTALLY)
    }

    fun byAccessDataWrite(): BooleanFilterConnector<DataSetCollectionRepository> {
        return cf.bool(DataSetTableInfo.Columns.ACCESS_DATA_WRITE)
    }

    fun byColor(): StringFilterConnector<DataSetCollectionRepository> {
        return cf.string(DataSetTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<DataSetCollectionRepository> {
        return cf.string(DataSetTableInfo.Columns.ICON)
    }

    fun byOrganisationUnitUid(uid: String): DataSetCollectionRepository {
        return byOrganisationUnitList(listOf(uid))
    }

    fun byOrganisationUnitList(uids: List<String>): DataSetCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
            DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
            uids,
        )
    }

    fun byOrganisationUnitScope(scope: OrganisationUnit.Scope): DataSetCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inTwoLinkTable(
            DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
            DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
            listOf(scope.name),
        )
    }

    fun withCompulsoryDataElementOperands(): DataSetCollectionRepository {
        return cf.withChild(DataSetFields.COMPULSORY_DATA_ELEMENT_OPERANDS)
    }

    fun withDataInputPeriods(): DataSetCollectionRepository {
        return cf.withChild(DataSetFields.DATA_INPUT_PERIODS)
    }

    fun withDataSetElements(): DataSetCollectionRepository {
        return cf.withChild(DataSetFields.DATA_SET_ELEMENTS)
    }

    fun withIndicators(): DataSetCollectionRepository {
        return cf.withChild(DataSetFields.INDICATORS)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<DataSet> = mapOf(
            DataSetFields.COMPULSORY_DATA_ELEMENT_OPERANDS to
                DataSetCompulsoryDataElementOperandChildrenAppender::create,
            DataSetFields.DATA_INPUT_PERIODS to DataInputPeriodChildrenAppender::create,
            DataSetFields.DATA_SET_ELEMENTS to DataSetElementChildrenAppender::create,
            DataSetFields.INDICATORS to DataSetIndicatorChildrenAppender::create,
        )
    }
}
