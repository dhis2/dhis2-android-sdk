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

package org.hisp.dhis.android.core.map.layer.internal.microsoft

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.hisp.dhis.android.core.D2Manager
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper
import org.hisp.dhis.android.core.arch.helpers.Result.Failure
import org.hisp.dhis.android.core.arch.helpers.Result.Success
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.internal.MapLayerHandler
import org.hisp.dhis.android.core.settings.internal.SystemSettingsNetworkHandler
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.koin.core.annotation.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
internal class MicrosoftMapsCallFactory(
    private val coroutineExecutor: CoroutineAPICallExecutor,
    private val mapLayerHandler: MapLayerHandler,
    private val versionManager: DHISVersionManager,
    private val networkHandler: SystemSettingsNetworkHandler,
    private val bingNetworkHandler: BingNetworkHandler,
    private val azureNetworkHandler: AzureNetworkHandler,
) {
    private companion object {
        private const val TIMEOUT_SECONDS = 30
    }

    suspend fun download(): List<MapLayer> {
        if (!versionManager.isGreaterOrEqualThan(DHISVersion.V2_34)) return emptyList()

        return runCatching {
            val key = coroutineExecutor.wrap(storeError = true) { networkHandler.getBingApiKey() }
                .getOrNull()?.value()
            if (key.isNullOrBlank()) return@runCatching emptyList()

            val azureLayers = downloadWithTimeout(key, AzureBasemaps.list, ::downloadAzureBasemap, true)
            val resultLayers = azureLayers.takeIf { it.isNotEmpty() }
                ?: downloadWithTimeout(key, BingBasemaps.list, ::downloadBingBasemap)

            mapLayerHandler.handleMany(resultLayers)
            resultLayers
        }.getOrDefault(emptyList())
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T> downloadWithTimeout(
        key: String,
        baseMaps: List<T>,
        fetch: suspend (String, T) -> List<MapLayer>,
        abortOnUnauthorizedFirst: Boolean = false,
    ): List<MapLayer> {
        val layers = mutableListOf<MapLayer>()

        baseMaps.forEachIndexed { index, baseMap ->
            try {
                withTimeout(TIMEOUT_SECONDS.seconds) {
                    layers += fetch(key, baseMap)
                }
            } catch (e: Exception) {
                when {
                    e is TimeoutCancellationException -> throw e
                    abortOnUnauthorizedFirst &&
                        e is D2Error && e.httpErrorCode() == HttpStatusCode.Unauthorized.value && index == 0 ->
                        return emptyList()
                }
            }
        }

        return layers
    }

    private suspend fun downloadAzureBasemap(key: String, basemap: AzureBasemap): List<MapLayer> {
        return coroutineExecutor.wrap(storeError = false) {
            azureNetworkHandler.getBaseMap(getAzureUrl(basemap.style, key), basemap, key)
        }.let { result ->
            when (result) {
                is Success -> result.value
                is Failure -> if (result.failure.httpErrorCode() == HttpStatusCode.Unauthorized.value) {
                    throw result.failure
                } else {
                    emptyList()
                }
            }
        }
    }

    private suspend fun downloadBingBasemap(key: String, basemap: BingBasemap): List<MapLayer> {
        return coroutineExecutor.wrap(storeError = false) {
            bingNetworkHandler.getBaseMap(getBingUrl(basemap.style, key), basemap)
        }.getOrNull().orEmpty()
    }

    private fun getAzureUrl(style: String, key: String) =
        if (D2Manager.isTestMode && !D2Manager.isRealIntegration) {
            "${ServerURLWrapper.serverUrl}/api/mockAzureMaps"
        } else {
            "https://atlas.microsoft.com/map/tileset" +
                "?api-version=2024-04-01" +
                "&subscription-key=$key" +
                "&tilesetId=$style"
        }

    private fun getBingUrl(style: String, key: String) =
        if (D2Manager.isTestMode && !D2Manager.isRealIntegration) {
            "${ServerURLWrapper.serverUrl}/api/mockBingMaps"
        } else {
            "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/$style" +
                "?output=json" +
                "&include=ImageryProviders" +
                "&culture=en-GB" +
                "&uriScheme=https" +
                "&key=$key"
        }
}
