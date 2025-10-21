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

package org.hisp.dhis.android.network.trackedentityinstancefilter

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterNetworkHandler
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.fields.DataAccessFields
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityInstanceFilterNetworkHandlerImpl(
    httpClient: HttpServiceClient,
    private val dhis2VersionManager: DHISVersionManagerImpl,
) : TrackedEntityInstanceFilterNetworkHandler {
    private val service: TrackedEntityInstanceFilterService = TrackedEntityInstanceFilterService(httpClient)
    val accessDataReadFilter = "access." + DataAccessFields.read.eq(true).generateString()

    override suspend fun getTrackedEntityInstanceFilters(
        partitionUids: Set<String>,
    ): PayloadJson<TrackedEntityInstanceFilter> {
        return if (dhis2VersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_38)) {
            val apiPayload = service.getTrackedEntityInstanceFilters(
                TrackedEntityInstanceFilterFields.programUid.`in`(partitionUids),
                accessDataReadFilter,
                TrackedEntityInstanceFilterFields.allFields,
                false,
            )
            apiPayload.mapItems(TrackedEntityInstanceFilterDTO::toDomain)
        } else {
            val apiPayload = service.getTrackedEntityInstanceFilters37(
                TrackedEntityInstanceFilterFields.programUid.`in`(partitionUids),
                accessDataReadFilter,
                TrackedEntityInstanceFilterFields.allFields37,
                false,
            )
            apiPayload.mapItems(TrackedEntityInstanceFilter37DTO::toDomain)
        }
    }
}
