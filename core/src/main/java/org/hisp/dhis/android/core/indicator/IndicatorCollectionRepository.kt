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
package org.hisp.dhis.android.core.indicator

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.NameableWithStyleColumns
import org.hisp.dhis.android.core.dataset.SectionIndicatorLinkTableInfo
import org.hisp.dhis.android.core.indicator.internal.IndicatorLegendSetChildrenAppender
import org.hisp.dhis.android.core.indicator.internal.IndicatorStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class IndicatorCollectionRepository internal constructor(
    store: IndicatorStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyNameableCollectionRepositoryImpl<Indicator, IndicatorCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        IndicatorCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byAnnualized(): BooleanFilterConnector<IndicatorCollectionRepository> {
        return cf.bool(IndicatorTableInfo.Columns.ANNUALIZED)
    }

    fun byIndicatorTypeUid(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(IndicatorTableInfo.Columns.INDICATOR_TYPE)
    }

    fun byNumerator(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(IndicatorTableInfo.Columns.NUMERATOR)
    }

    fun byNumeratorDescription(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(IndicatorTableInfo.Columns.NUMERATOR_DESCRIPTION)
    }

    fun byDenominator(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(IndicatorTableInfo.Columns.DENOMINATOR)
    }

    fun byDenominatorDescription(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(IndicatorTableInfo.Columns.DENOMINATOR_DESCRIPTION)
    }

    fun byUrl(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(IndicatorTableInfo.Columns.URL)
    }

    fun byColor(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(NameableWithStyleColumns.COLOR)
    }

    fun byIcon(): StringFilterConnector<IndicatorCollectionRepository> {
        return cf.string(NameableWithStyleColumns.ICON)
    }

    fun withLegendSets(): IndicatorCollectionRepository {
        return cf.withChild(LEGEND_SETS)
    }

    fun byDataSetUid(dataSetUid: String): IndicatorCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            DataSetIndicatorLinkTableInfo.TABLE_INFO.name(),
            DataSetIndicatorLinkTableInfo.Columns.INDICATOR,
            DataSetIndicatorLinkTableInfo.Columns.DATA_SET,
            listOf(dataSetUid),
        )
    }

    fun bySectionUid(dataSetUid: String): IndicatorCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            SectionIndicatorLinkTableInfo.TABLE_INFO.name(),
            SectionIndicatorLinkTableInfo.Columns.INDICATOR,
            SectionIndicatorLinkTableInfo.Columns.SECTION,
            listOf(dataSetUid),
        )
    }

    internal companion object {
        private const val LEGEND_SETS = "legendSets"

        val childrenAppenders: ChildrenAppenderGetter<Indicator> = mapOf(
            LEGEND_SETS to IndicatorLegendSetChildrenAppender::create,
        )
    }
}
