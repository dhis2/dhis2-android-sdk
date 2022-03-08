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

import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.LegendStrategy
import org.hisp.dhis.android.core.analytics.aggregated.AnalyticsRepository
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse
import org.hisp.dhis.android.core.analytics.linelist.EventLineListRepository
import org.hisp.dhis.android.core.arch.helpers.Result

internal class AnalyticsRepositoryImpl @Inject constructor(
    private val params: AnalyticsRepositoryParams,
    private val analyticsService: AnalyticsService
) : AnalyticsRepository {

    override fun withDimension(dimensionItem: DimensionItem): AnalyticsRepositoryImpl {
        return updateParams { params -> params.copy(dimensions = params.dimensions + dimensionItem) }
    }

    override fun withFilter(dimensionItem: DimensionItem): AnalyticsRepositoryImpl {
        return updateParams { params -> params.copy(filters = params.filters + dimensionItem) }
    }

    override fun withLegendStrategy(legendStrategy: LegendStrategy): AnalyticsRepositoryImpl {
        return updateParams { params -> params.copy(legendStrategy = legendStrategy) }
    }

    override fun evaluate(): Single<Result<DimensionalResponse, AnalyticsException>> {
        return Single.fromCallable { blockingEvaluate() }
    }

    override fun blockingEvaluate(): Result<DimensionalResponse, AnalyticsException> {
        return analyticsService.evaluate(params)
    }

    private fun updateParams(
        func: (params: AnalyticsRepositoryParams) -> AnalyticsRepositoryParams
    ): AnalyticsRepositoryImpl {
        return AnalyticsRepositoryImpl(func(params), analyticsService)
    }
}
