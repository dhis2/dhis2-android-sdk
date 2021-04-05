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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import kotlin.math.min
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine
import org.hisp.dhis.android.core.arch.api.paging.internal.Paging
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.internal.BooleanWrapper
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

@Reusable
internal class TrackedEntityInstanceDownloadInternalCall @Inject constructor(
    private val queryFactory: TrackedEntityInstanceQueryFactory,
    private val persistenceCallFactory: TrackedEntityInstancePersistenceCallFactory,
    private val endpointCallFactory: TrackedEntityInstancesEndpointCallFactory,
    private val apiCallExecutor: RxAPICallExecutor,
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager
) {

    fun downloadTeis(
        progressManager: D2ProgressManager,
        params: ProgramDataDownloadParams,
        relatives: RelationshipItemRelatives
    ): Observable<D2Progress> {
        return Observable.defer {
            val teiQueries = queryFactory.getQueries(params)
            val teiDownloadObservable = Observable.fromIterable(teiQueries)
                .flatMap { getTrackedEntityInstancesWithPaging(it) }
            // TODO .subscribeOn(teiDownloadScheduler);
            val isFullUpdate = params.program() == null
            val overwrite = params.overwrite()
            teiDownloadObservable.flatMapSingle { teiList: List<TrackedEntityInstance> ->
                persistenceCallFactory.persistTEIs(teiList, isFullUpdate, overwrite, relatives)
                    .toSingle {
                        progressManager.increaseProgress(
                            TrackedEntityInstance::class.java, false
                        )
                    }
            }
        }
    }

    private fun getTrackedEntityInstancesWithPaging(
        baseQuery: TeiQuery
    ): Observable<List<TrackedEntityInstance>> {
        val pagingList = ApiPagingEngine.getPaginationList(baseQuery.pageSize(), baseQuery.commonParams().limit)
        val allOkay = BooleanWrapper(true)
        return Observable
            .fromIterable(pagingList)
            .flatMapSingle { paging: Paging ->
                val pageQuery = baseQuery.toBuilder().page(paging.page()).pageSize(paging.pageSize()).build()
                apiCallExecutor.wrapSingle(endpointCallFactory.getCall(pageQuery), true)
                    .map { payload: Payload<TrackedEntityInstance> ->
                        TeiListWithPaging(
                            true,
                            limitTeisForPage(payload.items(), paging),
                            paging
                        )
                    }
                    .onErrorResumeNext {
                        allOkay.set(false)
                        Single.just(TeiListWithPaging(false, emptyList(), paging))
                    }
            }
            .takeUntil { res: TeiListWithPaging ->
                res.isSuccess && (
                    res.paging.isLastPage ||
                        res.teiList.size < res.paging.pageSize()
                    )
            }
            .map { tuple: TeiListWithPaging -> tuple.teiList }
            .doOnComplete {
                if (allOkay.get()) {
                    lastUpdatedManager.update(baseQuery)
                }
            }
    }

    private fun limitTeisForPage(
        pageTrackedEntityInstances: List<TrackedEntityInstance>,
        paging: Paging
    ): List<TrackedEntityInstance> {
        return if (paging.isLastPage &&
            pageTrackedEntityInstances.size > paging.previousItemsToSkipCount()
        ) {
            val toIndex = min(
                pageTrackedEntityInstances.size,
                paging.pageSize() - paging.posteriorItemsToSkipCount()
            )
            pageTrackedEntityInstances.subList(paging.previousItemsToSkipCount(), toIndex)
        } else {
            pageTrackedEntityInstances
        }
    }

    private data class TeiListWithPaging constructor(
        val isSuccess: Boolean,
        val teiList: List<TrackedEntityInstance>,
        val paging: Paging
    )
}
