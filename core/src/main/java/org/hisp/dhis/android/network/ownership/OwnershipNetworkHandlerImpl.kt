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

package org.hisp.dhis.android.network.ownership

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipNetworkHandler
import org.koin.core.annotation.Singleton

@Singleton
internal class OwnershipNetworkHandlerImpl(
    val httpServiceClient: HttpServiceClient,
    val parameterManager: OwnsershipParameterManager,
) : OwnershipNetworkHandler {
    private val service = OwnershipService(httpServiceClient)
    override suspend fun breakGlass(
        trackedEntityInstance: String,
        program: String,
        reason: String,
    ): HttpMessageResponse {
        val trackedEntity = parameterManager.getTrackedEntityForOwnershipParameter(trackedEntityInstance)
        val apiPayload = service.breakGlass(trackedEntity, program, reason)
        return apiPayload.toDomain()
    }

    override suspend fun transfer(
        trackedEntityInstance: String,
        program: String,
        ou: String,
    ): HttpMessageResponse {
        val trackedEntity = parameterManager.getTrackedEntityForOwnershipParameter(trackedEntityInstance)
        val apiPayload = service.transfer(trackedEntity, program, ou)
        return apiPayload.toDomain()
    }
}
