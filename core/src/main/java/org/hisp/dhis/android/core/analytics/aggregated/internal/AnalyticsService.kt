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

import android.database.sqlite.SQLiteException
import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.antlr.ParserExceptionWithoutContext

internal class AnalyticsService @Inject constructor(
    private val analyticsServiceDimensionHelper: AnalyticsServiceDimensionHelper,
    private val analyticsServiceMetadataHelper: AnalyticsServiceMetadataHelper,
    private val analyticsServiceEvaluatorHelper: AnalyticsServiceEvaluatorHelper
) {

    fun evaluate(params: AnalyticsRepositoryParams): Result<DimensionalResponse, AnalyticsException> {
        return try {
            if (params.dimensions.isEmpty()) {
                throw AnalyticsException.InvalidArguments("At least one dimension must be specified")
            }

            val dimensionItems = params.dimensions + params.filters

            if (dimensionItems.none { it.dimension == Dimension.Data }) {
                throw AnalyticsException.InvalidArguments("At least one data dimension must be specified")
            }

            val queryDimensions = analyticsServiceDimensionHelper.getQueryDimensions(params)
            val queryAbsoluteDimensions =
                analyticsServiceDimensionHelper.getQueryAbsoluteDimensionItems(params.dimensions, queryDimensions)

            val evaluationItems = analyticsServiceDimensionHelper.getEvaluationItems(params, queryAbsoluteDimensions)

            val metadata = analyticsServiceMetadataHelper.getMetadata(evaluationItems)

            val values = evaluationItems.map {
                analyticsServiceEvaluatorHelper.evaluate(it, metadata, params.analyticsLegendStrategy)
            }

            val legends = values.filter { it.legend != null }.map { it.legend!! }
            val finalMetadata = analyticsServiceMetadataHelper.includeLegendsToMetadata(metadata, legends)

            val dimensionItemsMap =
                queryAbsoluteDimensions.mapValues { v -> v.value.map { it as DimensionItem } } +
                    params.filters.groupBy { it.dimension }

            Result.Success(
                DimensionalResponse(
                    metadata = finalMetadata,
                    dimensions = queryDimensions,
                    dimensionItems = dimensionItemsMap,
                    filters = params.filters.map { it.id },
                    values = values
                )
            )
        } catch (e: AnalyticsException) {
            Result.Failure(e)
        } catch (e: ParserExceptionWithoutContext) {
            Result.Failure(AnalyticsException.ParserException(e.message ?: "Unknown"))
        } catch (e: IllegalArgumentException) {
            Result.Failure(AnalyticsException.InvalidArguments(e.message ?: "Unknown"))
        } catch (e: SQLiteException) {
            Result.Failure(AnalyticsException.SQLException(e.message ?: "Unknown"))
        }
    }
}
