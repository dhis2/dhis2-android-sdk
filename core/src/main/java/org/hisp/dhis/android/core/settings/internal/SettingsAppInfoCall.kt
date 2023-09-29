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

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import dagger.Reusable
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.maintenance.D2Error
import java.net.HttpURLConnection
import javax.inject.Inject

@Reusable
internal class SettingsAppInfoCall @Inject constructor(
    private val settingAppService: SettingAppService,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) {
    companion object {
        const val unknown = "unknown"
    }

    suspend fun fetch(storeError: Boolean): SettingsAppVersion {
        return fetchAppVersion(storeError)
    }

    private suspend fun fetchAppVersion(storeError: Boolean): SettingsAppVersion {

        return try {

            val info = coroutineAPICallExecutor.wrap(storeError = storeError) { settingAppService.info() }
                .getOrThrow()

            SettingsAppVersion.Valid(info.dataStoreVersion(), info.androidSettingsVersion() ?: unknown)

        } catch (exception: D2Error) {
            when {
                exception.httpErrorCode() == HttpURLConnection.HTTP_NOT_FOUND ->
                    fetchV1GeneralSettings(storeError)

                exception.originalException() is InvalidFormatException ->
                    SettingsAppVersion.DataStoreEmpty

                else ->
                    throw exception
            }
        }
    }

    private suspend fun fetchV1GeneralSettings(storeError: Boolean): SettingsAppVersion {
        return try {
            coroutineAPICallExecutor.wrap(storeError = storeError) {
                settingAppService.generalSettings(SettingsAppDataStoreVersion.V1_1)
            }.getOrThrow()
            SettingsAppVersion.Valid(SettingsAppDataStoreVersion.V1_1, unknown)

        } catch (exception: D2Error) {
            when {
                exception.httpErrorCode() == HttpURLConnection.HTTP_NOT_FOUND ->
                    SettingsAppVersion.DataStoreEmpty

                exception.originalException() is InvalidFormatException ->
                    SettingsAppVersion.DataStoreEmpty

                else ->
                    throw exception
            }
        }
    }

}
