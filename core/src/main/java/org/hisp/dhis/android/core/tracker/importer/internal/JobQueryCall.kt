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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.common.StorableObjectWithUid

@Reusable
internal class JobQueryCall @Inject internal constructor(
    private val service: TrackerImporterService,
    private val apiCallExecutor: APICallExecutor,
    private val trackerJobStore: IdentifiableObjectStore<StorableObjectWithUid>
) {

    fun storeJob(jobId: String) {
        trackerJobStore.insert(StorableObjectWithUid.create(jobId))
    }

    fun queryPendingJobs(): Observable<D2Progress> {
        return Observable.just(true)
            .flatMapIterable {
                val pendingJobs = trackerJobStore.selectAll()
                pendingJobs.withIndex().map { ij -> Pair(ij.value, ij.index == pendingJobs.size - 1) }
            }
            .flatMap { jobWithIsLast -> queryJob(jobWithIsLast.first.uid(), jobWithIsLast.second) }
    }

    fun queryJob(jobId: String): Observable<D2Progress> {
        return queryJob(jobId, true)
    }

    private fun queryJob(jobId: String, isLastJob: Boolean): Observable<D2Progress> {
        val progressManager = D2ProgressManager(null)
        @Suppress("MagicNumber")
        return Observable.interval(0, 5, TimeUnit.SECONDS)
            .map {
                apiCallExecutor.executeObjectCall(service.getJob(jobId))
            }
            .map { it.any { ji -> ji.completed } }
            .takeUntil { it }
            .doOnNext {
                if (it) {
                    val jobReport = apiCallExecutor.executeObjectCall(service.getJobReport(jobId))
                    trackerJobStore.delete(jobId)
                    println(jobReport)
                    // TODO manage status
                }
            }
            .take(3)
            .map {
                progressManager.increaseProgress(
                    JobReport::class.java,
                    it && isLastJob
                )
            }
            .onErrorResumeNext { _: Throwable ->
                return@onErrorResumeNext Observable.just(
                    progressManager.increaseProgress(
                        JobReport::class.java,
                        false
                    )
                )
            }
    }
}
