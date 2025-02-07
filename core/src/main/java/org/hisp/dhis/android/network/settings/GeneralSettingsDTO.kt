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

package org.hisp.dhis.android.network.settings

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.settings.DataSyncPeriod
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.hisp.dhis.android.core.settings.MetadataSyncPeriod

@Serializable
internal data class GeneralSettingsDTO(
    val dataSync: String?,
    val encryptDB: Boolean,
    val lastUpdated: String?,
    val metadataSync: String?,
    val reservedValues: Int?,
    @JsonNames("numberSmsToSend") val smsGateway: String?,
    @JsonNames("numberSmsConfirmation") val smsResultSender: String?,
    val matomoID: Int?,
    val matomoURL: String?,
    val allowScreenCapture: Boolean?,
    val messageOfTheDay: String?,
    val experimentalFeatures: List<String> = emptyList(),
    val bypassDHIS2VersionCheck: Boolean?,
) {
    fun toDomain(): GeneralSettings {
        return GeneralSettings.builder()
            .dataSync(dataSync?.let { DataSyncPeriod.from(it) })
            .encryptDB(encryptDB)
            .lastUpdated(lastUpdated?.let { DateUtils.DATE_FORMAT.parse(lastUpdated) })
            .metadataSync(metadataSync?.let { MetadataSyncPeriod.from(it) })
            .reservedValues(reservedValues)
            .smsGateway(smsGateway)
            .smsResultSender(smsResultSender)
            .matomoID(matomoID)
            .matomoURL(matomoURL)
            .allowScreenCapture(allowScreenCapture)
            .messageOfTheDay(messageOfTheDay)
            .experimentalFeatures(experimentalFeatures)
            .bypassDHIS2VersionCheck(bypassDHIS2VersionCheck)
            .build()
    }
}
