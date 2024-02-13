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

package org.hisp.dhis.android.core.map.layer.internal.externalmap

import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProvider
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.internal.MapLayerHandler
import org.koin.core.annotation.Singleton

@Singleton
internal class ExternalMapLayerCallFactory(
    private val mapLayerHandler: MapLayerHandler,
    val apiDownloader: APIDownloader,
    val service: ExternalMapLayerService,
) {

    suspend fun download(): List<MapLayer> {
        val mapLayers = getExternalMapLayers()
        mapLayerHandler.handleMany(mapLayers)
        return mapLayers
    }

    private suspend fun getExternalMapLayers(): List<MapLayer> {
        return service.getExternalMapLayers(
            ExternalMapLayerFields.allFields,
            ExternalMapLayerFields.mapLayerPosition.eq(MapLayerPosition.BASEMAP),
            false,
        ).items()
            .map { externalMapLayer ->
                MapLayer.builder()
                    .uid(externalMapLayer.id)
                    .name(externalMapLayer.name)
                    .displayName(externalMapLayer.displayName)
                    .code(externalMapLayer.code)
                    .mapLayerPosition(externalMapLayer.mapLayerPosition)
                    .mapService(externalMapLayer.mapService)
                    .imageFormat(externalMapLayer.imageFormat)
                    .layers(externalMapLayer.layers)
                    .external(true)
                    .imageUrl(externalMapLayer.url)
                    .imageryProviders(
                        listOf(
                            MapLayerImageryProvider.builder()
                                .mapLayer(externalMapLayer.id)
                                .attribution(externalMapLayer.attribution)
                                .build(),
                        ),
                    )
                    .build()
            }
    }
}
