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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.aggregated.AbsoluteDimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper

internal class AnalyticsServiceDimensionHelper @Inject constructor(
    private val periodGenerator: ParentPeriodGenerator,
    private val periodHelper: PeriodHelper,
    private val analyticsOrganisationUnitHelper: AnalyticsOrganisationUnitHelper
) {

    fun getQueryDimensions(params: AnalyticsRepositoryParams): List<Dimension> {
        return params.dimensions.map { it.dimension }.distinct()
    }

    /**
     * Return the list of AbsoluteDimensionItems by each dimension.
     */
    fun getQueryAbsoluteDimensionItems(
        queryDimensionItems: List<DimensionItem>,
        dimensions: List<Dimension>
    ): Map<Dimension, List<AbsoluteDimensionItem>> {
        return dimensions.associateWith { dimension ->
            queryDimensionItems
                .filter { it.dimension == dimension }
                .flatMap { item -> toAbsoluteDimensionItems(item) }
                .let {
                    when (dimension) {
                        is Dimension.Period -> orderAndDeduplicatePeriods(it)
                        else -> it
                    }
                }
        }
    }

    fun getEvaluationItems(
        params: AnalyticsRepositoryParams,
        queryAbsoluteDimensionItems: Map<Dimension, List<AbsoluteDimensionItem>>
    ): List<AnalyticsServiceEvaluationItem> {
        val dimensionCartesianProductList = queryAbsoluteDimensionItems.entries
            .fold(listOf(listOf<AbsoluteDimensionItem>())) { acc, dimensionEntry ->
                acc.flatMap { list -> dimensionEntry.value.map { element -> list + element } }
            }

        return dimensionCartesianProductList.map { dimensionList ->
            AnalyticsServiceEvaluationItem(
                dimensionItems = dimensionList,
                filters = params.filters,
                aggregationType = params.aggregationType
            )
        }
    }

    private fun toAbsoluteDimensionItems(item: DimensionItem): List<AbsoluteDimensionItem> {
        return when (item) {
            is DimensionItem.DataItem -> listOf(item)
            is DimensionItem.PeriodItem ->
                when (item) {
                    is DimensionItem.PeriodItem.Absolute -> listOf(item)
                    is DimensionItem.PeriodItem.Relative ->
                        periodGenerator.generateRelativePeriods(item.relative).map { period ->
                            DimensionItem.PeriodItem.Absolute(period.periodId()!!)
                        }
                }
            is DimensionItem.OrganisationUnitItem ->
                when (item) {
                    is DimensionItem.OrganisationUnitItem.Absolute -> listOf(item)
                    is DimensionItem.OrganisationUnitItem.Relative ->
                        analyticsOrganisationUnitHelper.getRelativeOrganisationUnitUids(item.relative).map {
                            DimensionItem.OrganisationUnitItem.Absolute(it)
                        }
                    is DimensionItem.OrganisationUnitItem.Level ->
                        analyticsOrganisationUnitHelper.getOrganisationUnitUidsByLevelUid(item.uid).map {
                            DimensionItem.OrganisationUnitItem.Absolute(it)
                        }
                    is DimensionItem.OrganisationUnitItem.Group ->
                        analyticsOrganisationUnitHelper.getOrganisationUnitUidsByGroup(item.uid).map {
                            DimensionItem.OrganisationUnitItem.Absolute(it)
                        }
                }
            is DimensionItem.CategoryItem -> listOf(item)
        }
    }

    private fun orderAndDeduplicatePeriods(periods: List<AbsoluteDimensionItem>): List<AbsoluteDimensionItem> {
        return periods
            .asSequence()
            .map { it as DimensionItem.PeriodItem.Absolute }
            .distinct()
            .map { Pair(it, periodHelper.blockingGetPeriodForPeriodId(it.periodId)) }
            .sortedWith { a, b ->
                val aPeriod = a.second
                val bPeriod = b.second

                if (aPeriod.periodType()!!.sortOrder != bPeriod.periodType()!!.sortOrder) {
                    aPeriod.periodType()!!.sortOrder - bPeriod.periodType()!!.sortOrder
                } else if (aPeriod.startDate()!!.time > bPeriod.startDate()!!.time) {
                    1
                } else {
                    -1
                }
            }
            .map { it.first }
            .toList()
    }
}
