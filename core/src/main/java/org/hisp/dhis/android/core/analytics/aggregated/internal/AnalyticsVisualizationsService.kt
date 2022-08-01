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
import org.hisp.dhis.android.core.analytics.aggregated.*
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.visualization.LegendStrategy
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationCollectionRepository

internal class AnalyticsVisualizationsService @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val visualizationCollectionRepository: VisualizationCollectionRepository,
    private val dimensionHelper: AnalyticsVisualizationsServiceDimensionHelper
) {

    fun evaluate(params: AnalyticsVisualizationsRepositoryParams): Result<GridAnalyticsResponse, AnalyticsException> {
        return if (params.visualization == null) {
            Result.Failure(AnalyticsException.InvalidArguments("Null visualization id"))
        } else {
            val visualization = getVisualization(params.visualization)

            if (visualization == null) {
                Result.Failure(AnalyticsException.InvalidVisualization(params.visualization))
            } else {
                when (val response = getDimensionalResponse(visualization, params)) {
                    is Result.Success -> {
                        val gridResponse = buildGridResponse(visualization, response.value)
                        Result.Success(gridResponse)
                    }
                    is Result.Failure -> Result.Failure(response.failure)
                }
            }
        }
    }

    private fun getVisualization(visualizationId: String): Visualization? {
        return visualizationCollectionRepository
            .withCategoryDimensions()
            .withDataDimensionItems()
            .uid(visualizationId)
            .blockingGet()
    }

    @Suppress("ComplexMethod")
    private fun getDimensionalResponse(
        visualization: Visualization,
        params: AnalyticsVisualizationsRepositoryParams
    ): Result<DimensionalResponse, AnalyticsException> {

        var queryAnalyticsRepository = analyticsRepository

        val queryDimensions =
            (visualization.rowDimensions() ?: emptyList()) +
                (visualization.columnDimensions() ?: emptyList())

        var queryItems = dimensionHelper.getDimensionItems(visualization, queryDimensions)
        var filterItems = dimensionHelper.getDimensionItems(visualization, visualization.filterDimensions())

        // Overwrite periods
        if (!params.periods.isNullOrEmpty()) {
            if (queryItems.any { it.dimension == Dimension.Period }) {
                queryItems = queryItems.filterNot { it.dimension == Dimension.Period } + params.periods
            } else if (filterItems.any { it.dimension == Dimension.Period }) {
                filterItems = filterItems.filterNot { it.dimension == Dimension.Period } + params.periods
            } else {
                filterItems = filterItems + params.periods
            }
        }

        // Overwrite organisationUnits
        if (!params.organisationUnits.isNullOrEmpty()) {
            if (queryItems.any { it.dimension == Dimension.OrganisationUnit }) {
                queryItems =
                    queryItems.filterNot { it.dimension == Dimension.OrganisationUnit } + params.organisationUnits
            } else if (filterItems.any { it.dimension == Dimension.OrganisationUnit }) {
                filterItems =
                    filterItems.filterNot { it.dimension == Dimension.OrganisationUnit } + params.organisationUnits
            } else {
                filterItems = filterItems + params.organisationUnits
            }
        }

        val visualizationLegendStrategy = visualization.legend()?.strategy()
        val legendSetUId = visualization.legend()?.set()?.uid()

        val legendStrategy = when (visualizationLegendStrategy) {
            LegendStrategy.FIXED ->
                if (legendSetUId != null) AnalyticsLegendStrategy.Fixed(legendSetUId)
                else AnalyticsLegendStrategy.None
            LegendStrategy.BY_DATA_ITEM, null -> AnalyticsLegendStrategy.ByDataItem
        }

        queryAnalyticsRepository = queryAnalyticsRepository.withLegendStrategy(legendStrategy)

        queryItems.forEach { queryAnalyticsRepository = queryAnalyticsRepository.withDimension(it) }
        filterItems.forEach { queryAnalyticsRepository = queryAnalyticsRepository.withFilter(it) }

        visualization.aggregationType()?.let {
            queryAnalyticsRepository = queryAnalyticsRepository.withAggregationType(it)
        }

        return queryAnalyticsRepository.blockingEvaluate()
    }

    private fun buildGridResponse(
        visualization: Visualization,
        dimensionalResponse: DimensionalResponse
    ): GridAnalyticsResponse {

        val gridDimension = dimensionHelper.getGridDimensions(visualization)

        val rowIndexes = gridDimension.rows.map { dimensionalResponse.dimensions.indexOf(it) }

        val groupedByRow = dimensionalResponse.values.groupBy { value ->
            rowIndexes.map { rowIndex -> value.dimensions[rowIndex] }
        }

        val gridValues = groupedByRow.map { (rows, valueList) ->
            valueList.map { value ->
                GridResponseValue(
                    columns = value.dimensions.filterNot { rows.contains(it) },
                    rows = rows,
                    value = value.value,
                    legend = value.legend
                )
            }
        }

        val gridHeader = buildGridHeader(gridValues)

        return GridAnalyticsResponse(
            metadata = dimensionalResponse.metadata,
            headers = gridHeader,
            dimensions = gridDimension,
            dimensionItems = dimensionalResponse.dimensionItems,
            filters = dimensionalResponse.filters,
            values = gridValues
        )
    }

    private fun buildGridHeader(gridValues: List<List<GridResponseValue>>): GridHeader {
        if (gridValues.isEmpty() || gridValues.first().isEmpty()) {
            return GridHeader(emptyList(), emptyList())
        }

        val sampleRow = gridValues.first()

        val columnCount = sampleRow.first().columns.size
        val columns = (0 until columnCount).map { columnIdx ->
            val columnValues = sampleRow.map { it.columns[columnIdx] }
            getGridHeaderItems(columnValues)
        }

        val sampleColumn = gridValues.map { it.first() }

        val rowCount = sampleColumn.first().rows.size
        val rows = (0 until rowCount).map { rowIdx ->
            val rowValues = sampleColumn.map { it.rows[rowIdx] }
            getGridHeaderItems(rowValues)
        }

        return GridHeader(columns, rows)
    }

    private fun getGridHeaderItems(values: List<String>): List<GridHeaderItem> {
        val groups = mutableListOf<GridHeaderItem>()
        values.forEach {
            val last = groups.lastOrNull()
            if (last?.id == it) {
                groups[groups.lastIndex] = last.copy(weight = last.weight + 1)
            } else {
                groups.add(GridHeaderItem(it, 1))
            }
        }
        return groups
    }
}
