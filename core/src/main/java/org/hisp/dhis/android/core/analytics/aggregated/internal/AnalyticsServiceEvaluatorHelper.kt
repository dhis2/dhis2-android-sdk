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
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.AnalyticsLegendStrategy
import org.hisp.dhis.android.core.analytics.LegendEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalValue
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementSQLEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.EventDataItemSQLEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.IndicatorEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.ProgramIndicatorSQLEvaluator

internal class AnalyticsServiceEvaluatorHelper @Inject constructor(
    private val dataElementEvaluator: DataElementSQLEvaluator,
    private val programIndicatorEvaluator: ProgramIndicatorSQLEvaluator,
    private val indicatorEvaluator: IndicatorEvaluator,
    private val eventDataItemEvaluator: EventDataItemSQLEvaluator,
    private val legendEvaluator: LegendEvaluator
) {
    fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>,
        legendStrategy: AnalyticsLegendStrategy,
    ): DimensionalValue {
        val evaluator = getEvaluator(evaluationItem)

        val value = evaluator.evaluate(evaluationItem, metadata)

        val legend = when (legendStrategy) {
            is AnalyticsLegendStrategy.Fixed -> legendEvaluator.getLegendByLegendSet(legendStrategy.legendSetUid, value)
            is AnalyticsLegendStrategy.ByDataItem -> getLegendFromDataDimension(evaluationItem, value)
            is AnalyticsLegendStrategy.None -> null
        }

        return DimensionalValue(
            dimensions = evaluationItem.dimensionItems.map { (it as DimensionItem).id },
            value = value,
            legend = legend
        )
    }

    private fun getEvaluator(evaluationItem: AnalyticsServiceEvaluationItem): AnalyticsEvaluator {
        val dimensionDataItems = evaluationItem.dimensionItems.filterIsInstance<DimensionItem.DataItem>()

        return when (dimensionDataItems.size) {
            0 -> getEvaluatorFromFilters(evaluationItem.filters)
            1 -> getEvaluatorFromDataDimension(dimensionDataItems.first())
            else ->
                throw AnalyticsException.InvalidArguments("Invalid arguments: more than one data item as dimension.")
        }
    }

    private fun getEvaluatorFromFilters(filters: List<DimensionItem>): AnalyticsEvaluator {
        val filterDataItems = filters.filterIsInstance<DimensionItem.DataItem>()

        val allAreDataElements = filterDataItems.all {
            it is DimensionItem.DataItem.DataElementItem || it is DimensionItem.DataItem.DataElementOperandItem
        }

        return when {
            filterDataItems.isEmpty() ->
                throw AnalyticsException.InvalidArguments("Invalid arguments: no data dimension is specified.")
            filterDataItems.size == 1 -> getEvaluatorFromDataDimension(filterDataItems.first())
            allAreDataElements -> dataElementEvaluator
            else ->
                throw AnalyticsException.InvalidArguments(
                    "Invalid arguments: Only a single indicator " +
                        "can be specified as filter."
                )
        }
    }

    private fun getEvaluatorFromDataDimension(item: DimensionItem.DataItem): AnalyticsEvaluator {
        return when (item) {
            is DimensionItem.DataItem.DataElementItem -> dataElementEvaluator
            is DimensionItem.DataItem.DataElementOperandItem -> dataElementEvaluator
            is DimensionItem.DataItem.ProgramIndicatorItem -> programIndicatorEvaluator
            is DimensionItem.DataItem.IndicatorItem -> indicatorEvaluator
            is DimensionItem.DataItem.EventDataItem -> eventDataItemEvaluator
        }
    }

    private fun getLegendFromDataDimension(evaluationItem: AnalyticsServiceEvaluationItem, value: String?): String? {
        val dimensionDataItem = (
            evaluationItem.dimensionItems.filterIsInstance<DimensionItem.DataItem>() +
                evaluationItem.filters.filterIsInstance<DimensionItem.DataItem>()
            ).first()

        return when (dimensionDataItem) {
            is DimensionItem.DataItem.DataElementItem -> legendEvaluator.getLegendByDataElement(
                dimensionDataItem.uid,
                value
            )
            is DimensionItem.DataItem.DataElementOperandItem -> legendEvaluator.getLegendByDataElement(
                dimensionDataItem.dataElement,
                value
            )
            is DimensionItem.DataItem.ProgramIndicatorItem -> legendEvaluator.getLegendByProgramIndicator(
                dimensionDataItem.uid,
                value
            )
            is DimensionItem.DataItem.IndicatorItem -> legendEvaluator.getLegendByIndicator(
                dimensionDataItem.uid,
                value
            )
            is DimensionItem.DataItem.EventDataItem.DataElement -> legendEvaluator.getLegendByDataElement(
                dimensionDataItem.dataElement,
                value
            )
            is DimensionItem.DataItem.EventDataItem.Attribute -> legendEvaluator.getLegendByTrackedEntityAttribute(
                dimensionDataItem.attribute,
                value
            )
        }
    }
}
