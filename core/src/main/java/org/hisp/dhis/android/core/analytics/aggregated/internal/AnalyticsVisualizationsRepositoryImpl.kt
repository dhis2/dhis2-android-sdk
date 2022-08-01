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

import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.AnalyticsVisualizationsRepository
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.GridAnalyticsResponse
import org.hisp.dhis.android.core.arch.helpers.Result

internal class AnalyticsVisualizationsRepositoryImpl @Inject constructor(
    private val params: AnalyticsVisualizationsRepositoryParams,
    private val service: AnalyticsVisualizationsService
) : AnalyticsVisualizationsRepository {

    override fun withVisualization(visualization: String): AnalyticsVisualizationsRepositoryImpl {
        return updateParams { params -> params.copy(visualization = visualization) }
    }

    override fun withPeriods(periods: List<DimensionItem.PeriodItem>): AnalyticsVisualizationsRepository {
        return updateParams { params -> params.copy(periods = periods) }
    }

    override fun withOrganisationUnits(
        orgUnits: List<DimensionItem.OrganisationUnitItem>
    ): AnalyticsVisualizationsRepository {
        return updateParams { params -> params.copy(organisationUnits = orgUnits) }
    }

    override fun evaluate(): Single<Result<GridAnalyticsResponse, AnalyticsException>> {
        return Single.fromCallable { blockingEvaluate() }
    }

    override fun blockingEvaluate(): Result<GridAnalyticsResponse, AnalyticsException> {
        return service.evaluate(params)
    }

    private fun updateParams(
        func: (params: AnalyticsVisualizationsRepositoryParams) -> AnalyticsVisualizationsRepositoryParams
    ): AnalyticsVisualizationsRepositoryImpl {
        return AnalyticsVisualizationsRepositoryImpl(func(params), service)
    }
}
