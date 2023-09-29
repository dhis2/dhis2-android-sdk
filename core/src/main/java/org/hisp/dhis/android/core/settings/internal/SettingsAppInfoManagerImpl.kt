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
package org.hisp.dhis.android.core.settings.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SettingsAppInfoManagerImpl @Inject constructor(
    private val settingsAppInfoCall: SettingsAppInfoCall,
) : SettingsAppInfoManager {

    private var settingsAppVersion: SettingsAppVersion? = null

    private val dataStoreEmpty = D2Error.builder()
        .errorCode(D2ErrorCode.SETTINGS_APP_NOT_SUPPORTED)
        .errorDescription("The dataStore is empty")
        .build()

    override suspend fun getDataStoreVersion(): SettingsAppDataStoreVersion {
        return when (val getOrUpdateAppVersion = getOrUpdateAppVersion()) {
            is SettingsAppVersion.Valid -> getOrUpdateAppVersion.dataStore
            is SettingsAppVersion.DataStoreEmpty -> throw dataStoreEmpty
        }

    }

    override suspend fun getAppVersion(): String {
        return when (val getOrUpdateAppVersion = getOrUpdateAppVersion()) {
            is SettingsAppVersion.Valid -> getOrUpdateAppVersion.app
            is SettingsAppVersion.DataStoreEmpty -> throw dataStoreEmpty
        }

    }

    override suspend fun updateAppVersion(): SettingsAppVersion {
        return try {
            settingsAppInfoCall.fetch(false).also { settingsAppVersion = it }
        } catch (exception: D2Error) {
            SettingsAppVersion.DataStoreEmpty
        }

    }

    private suspend fun getOrUpdateAppVersion(): SettingsAppVersion {
        return settingsAppVersion
            ?: updateAppVersion()
    }
}
