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

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.internal.ObjectWithUidWebResponse
import org.koin.core.annotation.Singleton

internal const val TRACKER_URL = "tracker"
internal const val JOBS_URL = "tracker/jobs/"

internal const val ATOMIC_MODE = "atomicMode"
internal const val ATOMIC_MODE_OBJECT = "OBJECT"

internal const val IMPORT_STRATEGY = "importStrategy"
internal const val IMPORT_STRATEGY_CREATE_AND_UPDATE = "CREATE_AND_UPDATE"
internal const val IMPORT_STRATEGY_DELETE = "DELETE"

@Singleton
internal class TrackerImporterService(private val client: HttpServiceClient) {

    suspend fun postTrackerPayload(
        payload: NewTrackerImporterPayload,
        atomicMode: String,
        importStrategy: String,
    ): ObjectWithUidWebResponse {
        return client.post {
            url(TRACKER_URL)
            parameters {
                attribute(ATOMIC_MODE to atomicMode)
                attribute(IMPORT_STRATEGY to importStrategy)
            }
            body(payload)
        }
    }

    suspend fun getJobReport(jobId: String): JobReport {
        return client.get {
            url("$JOBS_URL$jobId/report")
        }
    }

    suspend fun getJob(jobId: String): List<JobProgress> {
        return client.get {
            url("$JOBS_URL$jobId")
        }
    }
}
