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
package org.hisp.dhis.android.core.settings.internal

import io.reactivex.Single
import org.hisp.dhis.android.core.settings.SettingsAppInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SettingsAppInfoManagerImpl @Inject constructor(
    private val settingsAppInfoCall: SettingsAppInfoCall
) : SettingsAppInfoManager {

    private var dataStoreVersion: SettingsAppDataStoreVersion? = null
    private var appVersion: String? = null

    override fun getDataStoreVersion(): Single<SettingsAppDataStoreVersion> {
        return when {
            dataStoreVersion != null -> Single.just(dataStoreVersion)
            else -> updateAppInfo().map { it.dataStoreVersion() }
        }
    }

    override fun getAppVersion(): Single<String> {
        return when {
            appVersion != null -> Single.just(appVersion)
            else -> updateAppInfo().map { it.androidSettingsVersion() }
        }
    }

    override fun updateAppInfo(): Single<SettingsAppInfo> {
        return settingsAppInfoCall.fetch(false)
            .doOnSuccess { info ->
                dataStoreVersion = info.dataStoreVersion()
                appVersion = info.androidSettingsVersion()
            }
    }
}
