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

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.settings.AnalyticsSettings
import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.hisp.dhis.android.core.settings.DataSetSettings
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.hisp.dhis.android.core.settings.ProgramSettings
import org.hisp.dhis.android.core.settings.SettingsAppInfo
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.hisp.dhis.android.core.settings.internal.SettingsNetworkHandler
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class SettingsNetworkHandlerImpl(
    httpServiceClient: HttpServiceClient,
) : SettingsNetworkHandler {
    private val service = SettingsService(httpServiceClient)

    override suspend fun settingsAppInfo(url: String): SettingsAppInfo {
        val apiPayload = service.settingsAppInfo(url)
        return apiPayload.toDomain()
    }

    override suspend fun generalSettings(url: String): GeneralSettings {
        val apiPayload = service.generalSettings(url)
        return apiPayload.toDomain()
    }

    override suspend fun dataSetSettings(url: String): DataSetSettings {
        val apiPayload = service.dataSetSettings(url)
        return apiPayload.toDomain()
    }

    override suspend fun programSettings(url: String): ProgramSettings {
        val apiPayload = service.programSettings(url)
        return apiPayload.toDomain()
    }

    override suspend fun synchronizationSettings(url: String): SynchronizationSettings {
        val apiPayload = service.synchronizationSettings(url)
        return apiPayload.toDomain()
    }

    override suspend fun appearanceSettings(url: String): AppearanceSettings {
        val apiPayload = service.appearanceSettings(url)
        return apiPayload.toDomain()
    }

    override suspend fun analyticsSettings(url: String): AnalyticsSettings {
        val apiPayload = service.analyticsSettings(url)
        return apiPayload.toDomain()
    }
}
