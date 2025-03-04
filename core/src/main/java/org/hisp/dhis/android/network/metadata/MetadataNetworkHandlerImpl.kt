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
package org.hisp.dhis.android.network.metadata

import android.util.Log
import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.MetadataIds
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.MetadataNetworkHandler
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.WebApiRepositoryImpl
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.koin.core.annotation.Singleton

@Singleton
internal class MetadataNetworkHandlerImpl(
    httpClient: HttpServiceClient,
    private val dhisVersionManager: DHISVersionManager,
) : MetadataNetworkHandler {
    private val service: MetadataService = MetadataService(httpClient)

    override suspend fun getMetadataFields(
        config: GetMetadataIdsConfig,
    ): MetadataIds {
        val updatedConfig = if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35) && config.users) {
            Log.i(TAG, "Version greater or equal to 2.35. Skipping users query to metadata endpoint")
            config.copy(users = false)
        } else {
            config
        }
        val metadataDTO = service.getMetadataFields(
            field(updatedConfig.dataElements),
            field(updatedConfig.categoryOptionCombos),
            field(updatedConfig.organisationUnits),
            field(updatedConfig.users),
            field(updatedConfig.trackedEntityTypes),
            field(updatedConfig.trackedEntityAttributes),
            field(updatedConfig.programs),
        )
        return metadataDTO.toDomain()
    }

    private fun field(enable: Boolean): String? {
        return if (enable) ID_FIELD else null
    }

    companion object {
        private val TAG = WebApiRepositoryImpl::class.java.simpleName
        private const val ID_FIELD = "id"
    }
}
