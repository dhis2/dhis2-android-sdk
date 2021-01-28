/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import io.reactivex.Observable
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Reusable
internal class JobQueryCall @Inject internal constructor(
    private val service: TrackerImporterService,
    private val apiCallExecutor: APICallExecutor
) {

    fun storeAndQueryJob(job: ObjectWithUid): Observable<D2Progress> {
        // TODO store
        return queryJob(job.uid())
    }

    fun queryJob(jobId: String): Observable<D2Progress> {
        val progressManager = D2ProgressManager(null)
        @Suppress("MagicNumber")
        return Observable.interval(0, 5, TimeUnit.SECONDS)
            .map {
                apiCallExecutor.executeObjectCall(service.getJob(jobId))
            }
            .map { it.any { ji -> ji.completed } }
            .takeUntil { it }
            .map {
                progressManager.increaseProgress(
                    TrackedEntityInstance::class.java,
                    it
                )
            }.doOnComplete {
                val jobReport = apiCallExecutor.executeObjectCall(service.getJobReport(jobId))
                println(jobReport)
                // TODO manage status
            }
    }
}
