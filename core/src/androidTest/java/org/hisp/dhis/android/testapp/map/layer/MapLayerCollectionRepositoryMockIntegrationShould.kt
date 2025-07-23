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

package org.hisp.dhis.android.testapp.map.layer

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.map.layer.ImageFormat
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.MapService
import org.hisp.dhis.android.core.map.layer.internal.microsoft.BingBasemaps
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class MapLayerCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {

    @Test
    fun filter_all() {
        val mapLayers = d2.mapsModule().mapLayers().blockingGet()

        assertThat(mapLayers.size).isEqualTo(9)
    }

    @Test
    fun filter_by_uid() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byUid().eq(BingBasemaps.list.first().id)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_name() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byName().eq(BingBasemaps.list.first().name)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_display_name() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byName().eq(BingBasemaps.list.first().name)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_code() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byCode().eq("DARK_BASEMAP")
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_external() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byExternal().isFalse
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(5)
    }

    @Test
    fun filter_by_map_layer_position() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byMapLayerPosition().eq(MapLayerPosition.BASEMAP)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(7)
    }

    @Test
    fun filter_by_style() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byStyle().eq(BingBasemaps.list.first().style)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun with_imagery_providers() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byUid().eq(BingBasemaps.list.first().id)
            .withImageryProviders()
            .blockingGet()

        assertThat(mapLayers.first().imageryProviders()).isNotEmpty()
        assertThat(mapLayers.first().imageryProviders()?.first()?.coverageAreas()?.first()?.zoomMax()).isEqualTo(21)
    }

    @Test
    fun filter_by_map_service() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byMapService().eq(MapService.WMS)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(2)
    }

    @Test
    fun filter_by_image_format() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byImageFormat().eq(ImageFormat.JPG)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_layers() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byLayers().eq("layer_test")
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            setUpClass()

            dhis2MockServer.enqueueMockResponse("settings/system_settings.json")
            dhis2MockServer.enqueueMockResponse(401)
            dhis2MockServer.enqueueMockResponse("map/layer/microsoft/bing_server_response.json")
            dhis2MockServer.enqueueMockResponse("map/layer/microsoft/bing_server_response.json")
            dhis2MockServer.enqueueMockResponse(401)
            dhis2MockServer.enqueueMockResponse("map/layer/microsoft/bing_server_response.json")
            dhis2MockServer.enqueueMockResponse("map/layer/externalmap/external_map_layers.json")

            d2.mapsModule().mapLayersDownloader().downloadMetadata().blockingAwait()
        }
    }
}
