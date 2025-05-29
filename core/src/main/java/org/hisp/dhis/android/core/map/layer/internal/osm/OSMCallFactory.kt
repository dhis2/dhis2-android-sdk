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

package org.hisp.dhis.android.core.map.layer.internal.osm

import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProvider
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.internal.MapLayerHandler
import org.koin.core.annotation.Singleton

@Singleton
internal class OSMCallFactory(
    private val mapLayerHandler: MapLayerHandler,
) {

    suspend fun download(): List<MapLayer> {
        val mapLayers = getOSMBaseMaps()
        mapLayerHandler.handleMany(mapLayers)
        return mapLayers
    }

    // Function to get OSMBaseMaps
    private fun getOSMBaseMaps(): List<MapLayer> {
        return OSMBaseMaps.list.map { basemap ->
            MapLayer.builder()
                .uid(basemap.id)
                .name(basemap.name)
                .displayName(basemap.name)
                .style(basemap.style)
                .mapLayerPosition(MapLayerPosition.BASEMAP)
                .external(false)
                .imageUrl(basemap.imageUrl)
                .subdomains(basemap.subdomains)
                .subdomainPlaceholder(basemap.subdomainPlaceholder)
                .imageryProviders(
                    listOf(
                        MapLayerImageryProvider.builder()
                            .mapLayer(basemap.id)
                            .attribution(basemap.attribution)
                            .build(),
                    ),
                )
                .build()
        }
    }
}
