/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListRepository
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListResponse
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListSortingItem
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.repositories.paging.PageConfig
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class TrackerLineListRepositoryImpl(
    private val params: TrackerLineListParams,
    private val service: TrackerLineListService,
) : TrackerLineListRepository {

    override fun withEventOutput(programStageId: String): TrackerLineListRepositoryImpl {
        return updateParams {
            params.copy(
                outputType = TrackerLineListOutputType.EVENT,
                programStageId = programStageId,
            )
        }
    }

    override fun withEnrollmentOutput(programId: String): TrackerLineListRepositoryImpl {
        return updateParams {
            params.copy(
                outputType = TrackerLineListOutputType.ENROLLMENT,
                programId = programId,
            )
        }
    }

    override fun withTrackedEntityInstanceOutput(trackedEntityTypeId: String): TrackerLineListRepositoryImpl {
        return updateParams {
            params.copy(
                outputType = TrackerLineListOutputType.TRACKED_ENTITY_INSTANCE,
                trackedEntityTypeId = trackedEntityTypeId,
            )
        }
    }

    override fun withColumn(column: TrackerLineListItem): TrackerLineListRepositoryImpl {
        return updateParams { params.updateInColumns(column) }
    }

    override fun withFilter(filter: TrackerLineListItem): TrackerLineListRepositoryImpl {
        return updateParams { params.updateInFilters(filter) }
    }

    override fun withTrackerVisualization(trackerVisualization: String): TrackerLineListRepositoryImpl {
        return updateParams { params.copy(trackerVisualization = trackerVisualization) }
    }

    override fun withPageConfig(pageConfig: PageConfig): TrackerLineListRepository {
        return updateParams { params.copy(pageConfig = pageConfig) }
    }

    override fun withSorting(sorting: TrackerLineListSortingItem): TrackerLineListRepository {
        return updateParams { params.copy(sorting = listOf(sorting)) }
    }

    override fun evaluate(): Single<Result<TrackerLineListResponse, AnalyticsException>> {
        return Single.fromCallable { blockingEvaluate() }
    }

    override fun blockingEvaluate(): Result<TrackerLineListResponse, AnalyticsException> {
        return service.evaluate(params)
    }

    private fun updateParams(
        func: (params: TrackerLineListParams) -> TrackerLineListParams,
    ): TrackerLineListRepositoryImpl {
        return TrackerLineListRepositoryImpl(func(params), service)
    }
}
