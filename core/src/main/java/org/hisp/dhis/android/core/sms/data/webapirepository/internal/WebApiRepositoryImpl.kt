/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.sms.data.webapirepository.internal

import android.util.Log
import io.reactivex.Single
import java.util.*
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.MetadataResponse.MetadataId
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.smscompression.models.SMSMetadata

internal class WebApiRepositoryImpl(
    private val apiService: ApiService,
    private val dhisVersionManager: DHISVersionManager
) : WebApiRepository {

    override fun getMetadataIds(config: GetMetadataIdsConfig): Single<SMSMetadata> {
        return metadataCall(config).map { response: MetadataResponse ->
            val metadata = SMSMetadata()
            // TODO Server date has not timezone. We cannot use server date because it will be consider as local and
            //  potentially could be move some hours back or forth.
            // metadata.lastSyncDate = response.system().date();
            metadata.lastSyncDate = Date()
            metadata.categoryOptionCombos = mapIds(response.categoryOptionCombos())
            metadata.dataElements = mapIds(response.dataElements())
            metadata.organisationUnits = mapIds(response.organisationUnits())
            metadata.users = mapIds(response.users())
            metadata.trackedEntityTypes = mapIds(response.trackedEntityTypes())
            metadata.trackedEntityAttributes = mapIds(response.trackedEntityAttributes())
            metadata.programs = mapIds(response.programs())
            metadata
        }
    }

    private fun mapIds(ids: List<MetadataId>?): List<SMSMetadata.ID>? {
        return ids?.let { idList ->
            idList.map { makeID(it.id()) }
        }
    }

    private fun makeID(id: String): SMSMetadata.ID {
        return SMSMetadata.ID(id)
    }

    fun metadataCall(c: GetMetadataIdsConfig): Single<MetadataResponse> {
        if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35) && c.users) {
            Log.i(TAG, "Version greater or equal to 2.35. Skipping users query to metadata endpoint")
            c.users = false
        }
        return apiService.getMetadataIds(
            `val`(c.dataElements),
            `val`(c.categoryOptionCombos),
            `val`(c.organisationUnits),
            `val`(c.users),
            `val`(c.trackedEntityTypes),
            `val`(c.trackedEntityAttributes),
            `val`(c.programs)
        )
    }

    private fun `val`(enable: Boolean): String? {
        return if (enable) ApiService.GET_IDS else null
    }

    companion object {
        private val TAG = WebApiRepositoryImpl::class.java.simpleName
    }
}
