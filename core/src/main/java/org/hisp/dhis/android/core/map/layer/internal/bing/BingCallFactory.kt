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

package org.hisp.dhis.android.core.map.layer.internal.bing

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.hisp.dhis.android.core.D2Manager
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProvider
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProviderArea
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.internal.MapLayerHandler
import org.hisp.dhis.android.core.settings.internal.SettingService
import org.hisp.dhis.android.core.settings.internal.SystemSettingsFields
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.koin.core.annotation.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
internal class BingCallFactory(
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val mapLayerHandler: MapLayerHandler,
    private val versionManager: DHISVersionManager,
    private val settingsService: SettingService,
    private val bingService: BingService,
) {

    @Suppress("TooGenericExceptionCaught")
    suspend fun download(): List<MapLayer> {
        return if (versionManager.isGreaterOrEqualThan(DHISVersion.V2_34)) {
            try {
                val settings = coroutineAPICallExecutor.wrap(storeError = true) {
                    settingsService.getSystemSettings(SystemSettingsFields.bingApiKey)
                }

                val mapLayers = settings.getOrNull()?.keyBingMapsApiKey
                    ?.let { key -> downloadBingBasemaps(key) }
                    ?: emptyList()

                mapLayers.also {
                    mapLayerHandler.handleMany(it)
                }
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun downloadBingBasemaps(
        bingKey: String,
    ): List<MapLayer> {
        return try {
            BingBasemaps.list.map { b ->
                try {
                    withTimeout(TIMEOUT_SECONDS.seconds) {
                        downloadBasemap(bingKey, b)
                    }
                } catch (e: Exception) {
                    when (e) {
                        is TimeoutCancellationException -> throw e
                        else -> emptyList()
                    }
                }
            }.flatten()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun downloadBasemap(
        bingkey: String,
        basemap: BingBasemap,
    ): List<MapLayer> {
        val bingResponseResult = coroutineAPICallExecutor.wrap(storeError = false) {
            bingService.getBaseMap(getUrl(basemap.style, bingkey))
        }

        return bingResponseResult.map { bingResponse ->
            bingResponse.resourceSets.firstOrNull()?.resources?.firstOrNull()?.let { resource ->
                listOf(
                    MapLayer.builder()
                        .uid(basemap.id)
                        .name(basemap.name)
                        .displayName(basemap.name)
                        .style(basemap.style)
                        .mapLayerPosition(MapLayerPosition.BASEMAP)
                        .external(false)
                        .imageUrl(resource.imageUrl)
                        .subdomains(resource.imageUrlSubdomains)
                        .subdomainPlaceholder("{subdomain}")
                        .imageryProviders(
                            resource.imageryProviders.map { i ->
                                MapLayerImageryProvider.builder()
                                    .mapLayer(basemap.id)
                                    .attribution(i.attribution)
                                    .coverageAreas(
                                        i.coverageAreas.map { ca ->
                                            MapLayerImageryProviderArea.builder()
                                                .bbox(ca.bbox)
                                                .zoomMax(ca.zoomMax)
                                                .zoomMin(ca.zoomMin)
                                                .build()
                                        },
                                    )
                                    .build()
                            },
                        )
                        .build(),
                )
            }
        }.getOrNull() ?: emptyList()
    }

    private fun getUrl(style: String, bingKey: String): String {
        return if (D2Manager.isTestMode && !D2Manager.isRealIntegration) {
            "mockBingMaps"
        } else {
            "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/$style?" +
                "output=json&include=ImageryProviders&culture=en-GB&uriScheme=https&key=$bingKey"
        }
    }

    companion object {
        private const val TIMEOUT_SECONDS = 30
    }
}
