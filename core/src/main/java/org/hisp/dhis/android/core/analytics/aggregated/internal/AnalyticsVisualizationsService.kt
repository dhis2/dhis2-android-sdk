/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import org.hisp.dhis.android.core.analytics.aggregated.*
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationCollectionRepository
import javax.inject.Inject

internal class AnalyticsVisualizationsService @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val visualizationCollectionRepository: VisualizationCollectionRepository,
    private val dimensionHelper: AnalyticsVisualizationsServiceDimensionHelper
) {

    fun evaluate(params: AnalyticsVisualizationsRepositoryParams): GridAnalyticsResponse {
        if (params.visualization == null) {
            throw AnalyticsException.InvalidArguments("Null visualization id")
        }

        val visualization = getVisualization(params.visualization)
        val dimensionalResponse = getDimensionalResponse(visualization)

        return GridAnalyticsResponse(
            metadata = dimensionalResponse.metadata,
            headers = GridHeader(
                columns = listOf(),
                rows = listOf()
            ),
            dimensions = GridDimension(
                columns = dimensionalResponse.dimensions.toList(),
                rows = listOf()
            ),
            filters = dimensionalResponse.filters,
            values = listOf(
                dimensionalResponse.values.map {
                    GridResponseValue(
                        columns = it.dimensions,
                        rows = listOf(),
                        value = it.value
                    )
                }
            )
        )
    }

    private fun getVisualization(visualizationId: String): Visualization {
        return visualizationCollectionRepository
            .withCategoryDimensions()
            .withDataDimensionItems()
            .uid(visualizationId)
            .blockingGet()
            ?: throw AnalyticsException.InvalidArguments("Visualization $visualizationId does not exist")
    }

    private fun getDimensionalResponse(visualization: Visualization): DimensionalResponse {
        var analyticsRepository = analyticsRepository

        val queryDimensions =
            (visualization.rowDimensions() ?: emptyList()) +
                    (visualization.columnDimensions() ?: emptyList())

        dimensionHelper.getDimensionItems(
            visualization,
            queryDimensions
        ).forEach { item ->
            analyticsRepository = analyticsRepository.withDimension(item)
        }

        dimensionHelper.getDimensionItems(
            visualization,
            visualization.filterDimensions()
        ).forEach { item ->
            analyticsRepository = analyticsRepository.withFilter(item)
        }

        return analyticsRepository.blockingEvaluate()
    }
}
