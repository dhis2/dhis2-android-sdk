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

package org.hisp.dhis.android.network.externalmaplayer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.map.layer.ImageFormat
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProvider
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.MapService
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.PagerDTO

@Serializable
internal data class ExternalMapLayerDTO(
    val id: String,
    val name: String,
    val displayName: String,
    val mapLayerPosition: String,
    val url: String,
    val code: String? = null,
    val mapService: String?,
    val imageFormat: String?,
    val layers: String? = null,
    val attribution: String? = null,
) {
    fun toDomain(): MapLayer {
        val external = true
        return MapLayer.builder().apply {
            uid(id)
            name(name)
            displayName(displayName)
            external(external)
            mapLayerPosition(MapLayerPosition.valueOf(mapLayerPosition))
            imageUrl(url)
            code(code)
            mapService(mapService?.let { MapService.valueOf(it) })
            imageFormat(imageFormat?.let { ImageFormat.valueOf(it) })
            layers(layers)
            imageryProviders(
                attribution?.let { attribution ->
                    listOf(
                        MapLayerImageryProvider.builder()
                            .mapLayer(id)
                            .attribution(attribution)
                            .build(),
                    )
                } ?: emptyList(),
            )
        }.build()
    }
}

@Serializable
internal class ExternalMapLayerPayload(
    override val pager: PagerDTO?,
    @SerialName("externalMapLayers") override val items: List<ExternalMapLayerDTO> = emptyList(),
) : PayloadJson<ExternalMapLayerDTO>(pager, items)
