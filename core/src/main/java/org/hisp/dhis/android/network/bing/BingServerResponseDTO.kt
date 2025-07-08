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

package org.hisp.dhis.android.network.bing

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProvider
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProviderArea
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.internal.bing.BingBasemap

@Serializable
internal data class BingServerResponseDTO(
    val resourceSets: List<ResourceSetDTO>?,
) {
    fun toDomain(basemap: BingBasemap): List<MapLayer> {
        return resourceSets?.let {
            it.firstOrNull()?.resources?.firstOrNull()?.let { resource ->
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
        } ?: emptyList()
    }
}

@Serializable
internal data class ResourceSetDTO(
    val estimatedTotal: Int,
    val resources: List<ResourceDTO>,
)

@Serializable
internal data class ResourceDTO(
    val imageHeight: Int,
    val imageWidth: Int,
    val imageUrl: String,
    val imageUrlSubdomains: List<String>,
    val zoomMax: Int,
    val zoomMin: Int,
    val imageryProviders: List<ImageryProviderDTO>,
)

@Serializable
internal data class ImageryProviderDTO(
    val attribution: String,
    val coverageAreas: List<CoverageAreaDTO>,
)

@Serializable
internal data class CoverageAreaDTO(
    val bbox: List<Double>,
    val zoomMax: Int,
    val zoomMin: Int,
)
