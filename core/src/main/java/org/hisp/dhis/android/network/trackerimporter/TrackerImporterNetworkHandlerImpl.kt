/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.trackerimporter

import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.internal.ObjectWithUidWebResponse
import org.hisp.dhis.android.core.tracker.importer.internal.JobProgress
import org.hisp.dhis.android.core.tracker.importer.internal.JobReport
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterNetworkHandler
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackerImporterNetworkHandlerImpl(
    httpServiceClient: HttpServiceClientKotlinx
) : TrackerImporterNetworkHandler {
    val service = TrackerImporterService(httpServiceClient)
    override suspend fun postTrackerPayload(
        payload: NewTrackerImporterPayload,
        importStrategy: String
    ): ObjectWithUidWebResponse {
        val payloadDTO: NewTrackerImporterPayloadDTO = payload.toDto()
        return service.postTrackerPayload(payloadDTO, ATOMIC_MODE_OBJECT, importStrategy)
    }

    override suspend fun getJobReport(jobId: String): JobReport {
        TODO("Not yet implemented")
    }

    override suspend fun getJob(jobId: String): List<JobProgress> {
        TODO("Not yet implemented")
    }
}
