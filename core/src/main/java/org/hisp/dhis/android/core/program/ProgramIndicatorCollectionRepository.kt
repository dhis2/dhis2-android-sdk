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
package org.hisp.dhis.android.core.program

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorAnalyticsPeriodBoundaryChildrenAppender
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorLegendSetChildrenAppender
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorStore
import org.koin.core.annotation.Singleton

@Singleton
class ProgramIndicatorCollectionRepository internal constructor(
    store: ProgramIndicatorStore,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramIndicator, ProgramIndicatorCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramIndicatorCollectionRepository(
            store,
            s,
        )
    },
) {
    fun byDisplayInForm(): BooleanFilterConnector<ProgramIndicatorCollectionRepository> {
        return cf.bool(ProgramIndicatorTableInfo.Columns.DISPLAY_IN_FORM)
    }

    fun byExpression(): StringFilterConnector<ProgramIndicatorCollectionRepository> {
        return cf.string(ProgramIndicatorTableInfo.Columns.EXPRESSION)
    }

    fun byDimensionItem(): StringFilterConnector<ProgramIndicatorCollectionRepository> {
        return cf.string(ProgramIndicatorTableInfo.Columns.DIMENSION_ITEM)
    }

    fun byFilter(): StringFilterConnector<ProgramIndicatorCollectionRepository> {
        return cf.string(ProgramIndicatorTableInfo.Columns.FILTER)
    }

    fun byDecimals(): IntegerFilterConnector<ProgramIndicatorCollectionRepository> {
        return cf.integer(ProgramIndicatorTableInfo.Columns.DECIMALS)
    }

    fun byAggregationType(): StringFilterConnector<ProgramIndicatorCollectionRepository> {
        return cf.string(ProgramIndicatorTableInfo.Columns.AGGREGATION_TYPE)
    }

    fun byAnalyticsType(): EnumFilterConnector<ProgramIndicatorCollectionRepository, AnalyticsType> {
        return cf.enumC(ProgramIndicatorTableInfo.Columns.ANALYTICS_TYPE)
    }

    fun byProgramUid(): StringFilterConnector<ProgramIndicatorCollectionRepository> {
        return cf.string(ProgramIndicatorTableInfo.Columns.PROGRAM)
    }

    fun withLegendSets(): ProgramIndicatorCollectionRepository {
        return cf.withChild(LEGEND_SETS)
    }

    fun withAnalyticsPeriodBoundaries(): ProgramIndicatorCollectionRepository {
        return cf.withChild(ANALYTICS_PERIOD_BOUNDARIES)
    }

    internal companion object {
        private const val ANALYTICS_PERIOD_BOUNDARIES = "analyticsPeriodBoundaries"
        private const val LEGEND_SETS = "legendSets"

        val childrenAppenders: ChildrenAppenderGetter<ProgramIndicator> = mapOf(
            LEGEND_SETS to ProgramIndicatorLegendSetChildrenAppender::create,
            ANALYTICS_PERIOD_BOUNDARIES to
                ProgramIndicatorAnalyticsPeriodBoundaryChildrenAppender::create,
        )
    }
}
