/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx2.asObservable
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostStateManager
import org.koin.core.annotation.Singleton
import kotlin.time.Duration.Companion.seconds

internal const val ATTEMPTS_AFTER_UPLOAD = 90
internal const val ATTEMPTS_WHEN_QUERYING = 1
internal val ATTEMPTS_INITIAL_DELAY = 1.seconds
internal val ATTEMPTS_INTERVAL = 2.seconds

@Singleton
internal class JobQueryCall internal constructor(
    private val networkHandler: TrackerImporterNetworkHandler,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val trackerJobObjectStore: TrackerJobObjectStore,
    private val handler: JobReportHandler,
    private val fileResourceHandler: JobReportFileResourceHandler,
    private val stateManager: NewTrackerImporterTrackedEntityPostStateManager,
) {

    fun queryPendingJobs(): Observable<D2Progress> = flow {
        val pendingJobs = trackerJobObjectStore.selectAll()
            .sortedBy { it.lastUpdated() }
            .groupBy { it.jobUid() }
            .toList()

        pendingJobs.withIndex().map {
            Triple(it.value.first, it.value.second, it.index == pendingJobs.size - 1)
        }.forEach {
            emitAll(queryJobInternal(it.first, it.second, it.third, ATTEMPTS_WHEN_QUERYING))
            updateFileResourceStates(it.second)
        }
    }.asObservable()

    fun queryJob(jobId: String): Flow<D2Progress> = flow {
        val jobObjects = trackerJobObjectStore.selectWhere(byJobIdClause(jobId))
        emitAll(queryJobInternal(jobId, jobObjects, true, ATTEMPTS_AFTER_UPLOAD))
    }

    private suspend fun updateFileResourceStates(jobObjects: List<TrackerJobObject>) {
        return fileResourceHandler.updateFileResourceStates(jobObjects)
    }

    private fun queryJobInternal(
        jobId: String,
        jobObjects: List<TrackerJobObject>,
        isLastJob: Boolean,
        attempts: Int,
    ): Flow<D2Progress> = flow {
        val progressManager = D2ProgressManager(null)

        delay(ATTEMPTS_INITIAL_DELAY)

        @Suppress("TooGenericExceptionCaught", "UnusedPrivateMember")
        for (i in 0..attempts) {
            val isComplete =
                try {
                    if (isReportAvailable(jobId)) {
                        downloadReportAndHandle(jobId, jobObjects)
                        true
                    } else {
                        false
                    }
                } catch (e: Throwable) {
                    handlerError(jobId, jobObjects)
                    true
                }

            emit(progressManager.increaseProgress(JobReport::class.java, isComplete && isLastJob))

            if (isComplete) {
                break
            }

            delay(ATTEMPTS_INTERVAL)
        }

        updateFileResourceStates(jobObjects)
        emit(progressManager.increaseProgress(FileResource::class.java, isLastJob))
    }

    private suspend fun isReportAvailable(jobId: String): Boolean {
        return try {
            val jobProgressLog = coroutineAPICallExecutor.wrap(
                storeError = true,
                errorCatcher = JobQueryErrorCatcher(),
            ) {
                networkHandler.getJob(jobId)
            }.getOrThrow()

            jobProgressLog.any { it.completed }
        } catch (e: D2Error) {
            if (e.errorCode() == D2ErrorCode.JOB_REPORT_NOT_AVAILABLE) {
                false
            } else {
                throw e
            }
        }
    }

    private suspend fun downloadReportAndHandle(jobId: String, jobObjects: List<TrackerJobObject>) {
        val jobReport = coroutineAPICallExecutor.wrap {
            networkHandler.getJobReport(jobId)
        }.getOrThrow()

        trackerJobObjectStore.deleteWhere(byJobIdClause(jobId))
        handler.handle(jobReport, jobObjects)
    }

    private suspend fun handlerError(jobId: String, jobObjects: List<TrackerJobObject>) {
        trackerJobObjectStore.deleteWhere(byJobIdClause(jobId))
        stateManager.restoreStates(jobObjects)
    }

    private fun byJobIdClause(jobId: String) = WhereClauseBuilder()
        .appendKeyStringValue(TrackerJobObjectTableInfo.Columns.JOB_UID, jobId)
        .build()
}
