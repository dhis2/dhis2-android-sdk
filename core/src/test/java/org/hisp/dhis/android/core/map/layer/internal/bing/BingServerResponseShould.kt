/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.network.bing.BingServerResponseDTO
import org.junit.Test

class BingServerResponseShould : CoreObjectShould("map/layer/bing/bing_server_response.json") {
    @Test
    override fun map_from_json_string() {
        val bingServerResponseDTO = deserialize(BingServerResponseDTO.serializer())
        assertThat(bingServerResponseDTO.resourceSets!!.size).isEqualTo(1)

        bingServerResponseDTO.resourceSets.first().let { s ->
            assertThat(s.resources.size).isEqualTo(1)

            val r = s.resources.first()
            assertThat(r.imageHeight).isEqualTo(256)
            assertThat(r.imageWidth).isEqualTo(256)
            assertThat(r.imageUrl).startsWith("https://{subdomain}.ssl.ak.dynamic.tiles.virtualearth.net/comp/")
            assertThat(r.imageUrlSubdomains.size).isEqualTo(4)
            assertThat(r.zoomMax).isEqualTo(21)
            assertThat(r.zoomMin).isEqualTo(1)
            assertThat(r.imageryProviders.size).isEqualTo(5)

            val p = r.imageryProviders.first()
            assertThat(p.attribution).isEqualTo("© 2022 Microsoft Corporation")
            assertThat(p.coverageAreas.size).isEqualTo(1)

            val c = p.coverageAreas.first()
            assertThat(c.bbox).isEqualTo(listOf(-90.0, -180.0, 90.0, 180.0))
            assertThat(c.zoomMax).isEqualTo(21)
            assertThat(c.zoomMin).isEqualTo(1)
        }

        val mapLayerList = bingServerResponseDTO.toDomain(
            BingBasemap(
                id = "bingLight",
                name = "Bing Road",
                style = "CanvasLight",
            ),
        )

        assertThat(mapLayerList.size).isEqualTo(1)
        val mapLayer = mapLayerList.first()

        assertThat(mapLayer.uid()).isEqualTo("bingLight")
        assertThat(mapLayer.name()).isEqualTo("Bing Road")
        assertThat(mapLayer.style()).isEqualTo("CanvasLight")

        assertThat(mapLayer.mapLayerPosition()).isEqualTo(MapLayerPosition.BASEMAP)
        assertThat(mapLayer.external()).isFalse()
        assertThat(mapLayer.imageUrl()).startsWith("https://{subdomain}.ssl.ak.dynamic.tiles.virtualearth.net/comp/")
        assertThat(mapLayer.subdomains()?.size).isEqualTo(4)
        assertThat(mapLayer.subdomainPlaceholder()).isEqualTo("{subdomain}")

        assertThat(mapLayer.imageryProviders()).hasSize(5)
        assertThat(mapLayer.imageryProviders()!![0].attribution()).isEqualTo("© 2022 Microsoft Corporation")
        assertThat(mapLayer.imageryProviders()!![0].coverageAreas()).hasSize(1)
        assertThat(mapLayer.imageryProviders()!![0].coverageAreas()!![0].bbox()).isEqualTo(
            listOf(
                -90.0,
                -180.0,
                90.0,
                180.0,
            ),
        )
        assertThat(mapLayer.imageryProviders()!![0].coverageAreas()!![0].zoomMax()).isEqualTo(21)
        assertThat(mapLayer.imageryProviders()!![0].coverageAreas()!![0].zoomMin()).isEqualTo(1)

        assertThat(mapLayer.mapService()).isNull()
        assertThat(mapLayer.imageFormat()).isNull()
        assertThat(mapLayer.layers()).isNull()
    }
}
