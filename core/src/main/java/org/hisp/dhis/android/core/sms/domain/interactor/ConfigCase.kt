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
package org.hisp.dhis.android.core.sms.domain.interactor

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxCompletable
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository

/**
 * Used to set initial data that is common for all sms sending tasks
 */
@Suppress("TooManyFunctions")
class ConfigCase(
    private val webApiRepository: WebApiRepository,
    private val localDbRepository: LocalDbRepository,
) {

    fun getSmsModuleConfig(): Single<SmsConfig> = rxSingle { getSmsModuleConfigSuspend() }

    private suspend fun getSmsModuleConfigSuspend(): SmsConfig {
        return SmsConfig(
            isModuleEnabled = localDbRepository.isModuleEnabledSuspend(),
            gateway = localDbRepository.getGatewayNumberSuspend(),
            isWaitingForResult = localDbRepository.getWaitingForResultEnabledSuspend(),
            resultSender = localDbRepository.getConfirmationSenderNumberSuspend(),
            resultWaitingTimeout = localDbRepository.getWaitingResultTimeoutSuspend(),
        )
    }

    /**
     * Set if the SDK has to wait for the result or not.
     * @param enabled
     * @return `Completable` that completes when the configuration is changed.
     */
    fun setWaitingForResultEnabled(enabled: Boolean): Completable {
        return localDbRepository.setWaitingForResultEnabled(enabled)
    }

    /**
     * Set the number to receive messages from. Messages from other senders will not be read.
     * @param number The sender number.
     * @return `Completable` that completes when the configuration is changed.
     */
    fun setConfirmationSenderNumber(number: String?): Completable {
        return if (number.isNullOrEmpty()) {
            localDbRepository.deleteConfirmationSenderNumber()
        } else {
            localDbRepository.setConfirmationSenderNumber(number)
        }
    }

    /**
     * Set the time in seconds to wait for the result.
     * @param timeoutSeconds Time in seconds.
     * @return `Completable` that completes when the configuration is changed.
     */
    fun setWaitingResultTimeout(timeoutSeconds: Int): Completable {
        return localDbRepository.setWaitingResultTimeout(timeoutSeconds)
    }

    /**
     * Set the gateway number to send the SMS. This is a required parameter before sending SMS.
     * @param gatewayNumber The gateway numberf
     * @return `Completable` that completes when the configuration is changed.
     */
    fun setGatewayNumber(gatewayNumber: String?): Completable {
        return if (gatewayNumber.isNullOrEmpty()) {
            Completable.error(IllegalArgumentException("Gateway number can't be empty"))
        } else {
            localDbRepository.setGatewayNumber(gatewayNumber)
        }
    }

    /**
     * Delete the gateway number to send the SMS.
     * @return `Completable` that completes when the configuration is changed.
     */
    fun deleteGatewayNumber(): Completable {
        return localDbRepository.deleteGatewayNumber()
    }

    /**
     * Set if SMS Module is enabled or not. It is required to enable it before using it.
     * @param enabled If the module is enabled or not
     * @return `Completable` that completes when the configuration is changed.
     */
    fun setModuleEnabled(enabled: Boolean): Completable {
        return localDbRepository.setModuleEnabled(enabled)
    }

    /**
     * Set a new MetadataDownload configuration.
     * @param metadataIdsConfig Configuration with the metadata ids.
     * @return `Completable` that completes when the configuration is changed.
     */
    fun setMetadataDownloadConfig(metadataIdsConfig: GetMetadataIdsConfig?): Completable {
        return if (metadataIdsConfig == null) {
            Completable.error(IllegalArgumentException("Received null config"))
        } else {
            localDbRepository.setMetadataDownloadConfig(metadataIdsConfig)
        }
    }

    /**
     * Method to download metadata ids. This is required before using the SMS module.
     */
    fun refreshMetadataIds(): Completable {
        return rxCompletable {
            refreshMetadataIdsCoroutines()
        }
    }

    private suspend fun refreshMetadataIdsCoroutines() {
        val enabled = localDbRepository.isModuleEnabledSuspend()

        if (enabled) {
            val config = try {
                localDbRepository.getMetadataDownloadConfig()
            } catch (ignored: Exception) {
                Log.d(TAG, "Can't read saved SMS metadata download config. Using default.")
                getDefaultMetadataDownloadConfig()
            }

            val metadata = webApiRepository.getMetadataIds(config)
            localDbRepository.setMetadataIds(metadata)
        } else {
            Log.d(TAG, "Not refreshing SMS metadata, because sms module is disabled")
        }
    }

    /**
     * Callable that triggers the method [.refreshMetadataIds].
     * @return Callable object to refresh metadata ids.
     */
    internal suspend fun refreshMetadataIdsCallable() {
        try {
            Log.d(TAG, "Started SMS metadata sync.")
            refreshMetadataIdsCoroutines()
            Log.d(TAG, "Completed SMS metadata sync.")
        } catch (ignored: Exception) {
            Log.d(TAG, "Error refreshing SMS metadata ids.")
        }
    }

    private fun getDefaultMetadataDownloadConfig(): GetMetadataIdsConfig {
        return GetMetadataIdsConfig()
    }

    class SmsConfig internal constructor(
        val isModuleEnabled: Boolean,
        val gateway: String,
        val isWaitingForResult: Boolean,
        val resultSender: String,
        val resultWaitingTimeout: Int,
    )

    companion object {
        private val TAG = ConfigCase::class.java.simpleName
    }
}
