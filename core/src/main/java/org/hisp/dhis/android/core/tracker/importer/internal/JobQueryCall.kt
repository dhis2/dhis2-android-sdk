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
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore

internal const val ATTEMPTS_AFTER_UPLOAD = 3
internal const val ATTEMPTS_WHEN_QUERYING = 1
@Reusable
internal class JobQueryCall @Inject internal constructor(
    private val service: TrackerImporterService,
    private val apiCallExecutor: APICallExecutor,
    private val trackerJobObjectStore: ObjectWithoutUidStore<TrackerJobObject>,
    private val handler: JobReportHandler
) {

    fun queryPendingJobs(): Observable<D2Progress> {
        return Observable.just(true)
            .flatMapIterable {
                val pendingJobs = trackerJobObjectStore.selectAll()
                    .sortedBy { it.lastUpdated() }
                    .groupBy { it.jobUid() }
                    .toList()

                pendingJobs.withIndex().map {
                    Triple(it.value.first, it.value.second, it.index == pendingJobs.size - 1)
                }
            }
            .flatMap { queryJobInternal(it.first, it.second, it.third, ATTEMPTS_WHEN_QUERYING) }
    }

    fun queryJob(jobId: String): Observable<D2Progress> {
        val jobObjects = trackerJobObjectStore.selectWhere(byJobIdClause(jobId))
        return queryJobInternal(jobId, jobObjects, true, ATTEMPTS_AFTER_UPLOAD)
    }

    private fun queryJobInternal(
        jobId: String,
        jobObjects: List<TrackerJobObject>,
        isLastJob: Boolean,
        attempts: Int
    ): Observable<D2Progress> {
        val progressManager = D2ProgressManager(null)
        @Suppress("MagicNumber")
        return Observable.interval(0, 5, TimeUnit.SECONDS)
            .map {
                try {
                    downloadAndHandle(jobId, jobObjects)
                    true
                } catch (_: Throwable) {
                    false
                }
            }
            .takeUntil { it }
            .take(attempts.toLong())
            .map {
                progressManager.increaseProgress(
                    JobReport::class.java,
                    it && isLastJob
                )
            }
    }

    private fun downloadAndHandle(jobId: String, jobObjects: List<TrackerJobObject>) {
        val jobReport = apiCallExecutor.executeObjectCallWithErrorCatcher(
            service.getJobReport(jobId),
            JobQueryErrorCatcher()
        )
        trackerJobObjectStore.deleteWhere(byJobIdClause(jobId))
        handler.handle(jobReport, jobObjects)
    }

    private fun byJobIdClause(jobId: String) = WhereClauseBuilder()
        .appendKeyStringValue(TrackerJobObjectTableInfo.Columns.JOB_UID, jobId)
        .build()
}
