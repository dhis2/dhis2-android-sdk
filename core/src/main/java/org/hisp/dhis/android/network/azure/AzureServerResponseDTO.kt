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

package org.hisp.dhis.android.network.azure

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.map.layer.ImageFormat
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProvider
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProviderArea
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.MapService
import org.hisp.dhis.android.core.map.layer.internal.microsoft.AzureBasemap

@Serializable
internal data class AzureServerResponseDTO(
    val tileJson: String,
    val version: String,
    val tiles: List<String>,
    val minzoom: Int,
    val maxzoom: Int,
    val bounds: List<Double>,
    val name: String? = null,
    val attribution: String? = null,
    val scheme: String? = null,
) {
    fun toDomain(basemap: AzureBasemap): List<MapLayer> {
        return tiles.map { tileTemplate ->
            val coverageAreas = listOf(
                MapLayerImageryProviderArea.builder()
                    .bbox(bounds)
                    .zoomMin(minzoom)
                    .zoomMax(maxzoom)
                    .build(),
            )
            val providers = attribution?.takeIf { it.isNotBlank() }?.let {
                listOf(
                    MapLayerImageryProvider.builder()
                        .mapLayer(basemap.id)
                        .attribution(it)
                        .coverageAreas(coverageAreas)
                        .build(),
                )
            }.orEmpty()

            MapLayer.builder()
                .uid(basemap.id)
                .name(basemap.name)
                .displayName(basemap.name)
                .style(name)
                .external(false)
                .mapLayerPosition(MapLayerPosition.BASEMAP)
                .imageUrl(tileTemplate)
                .subdomains(emptyList())
                .imageryProviders(providers)
                .mapService(scheme?.let { MapService.valueOf(it) })
                .imageFormat(ImageFormat.PNG)
                .build()
        }
    }
}
