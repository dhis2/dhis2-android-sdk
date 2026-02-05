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
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProviderArea
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.MapService
import org.hisp.dhis.android.core.map.layer.internal.microsoft.AzureBasemaps
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class AzureMapLayerCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {

    @Test
    fun filter_all() {
        val mapLayers = d2.mapsModule().mapLayers().blockingGet()

        assertThat(mapLayers.size).isEqualTo(10)
    }

    @Test
    fun filter_by_uid() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byUid().eq(AzureBasemaps.list.first().id)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_name() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byName().eq(AzureBasemaps.list.first().name)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_display_name() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byDisplayName().eq(AzureBasemaps.list.first().name)
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

        assertThat(mapLayers.size).isEqualTo(6)
    }

    @Test
    fun filter_by_map_layer_position() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byMapLayerPosition().eq(MapLayerPosition.BASEMAP)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(7)
    }

    @Test
    fun filter_by_overlay_position() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byMapLayerPosition().eq(MapLayerPosition.OVERLAY)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(3)
    }

    @Test
    fun verify_hybrid_layers_are_linked() {
        val hybridBasemap = AzureBasemaps.list.find { it.id == "azureHybrid" }!!
        val baseLayerUid = hybridBasemap.id + hybridBasemap.styles.first().idSuffix
        val overlayLayerUid = hybridBasemap.id + hybridBasemap.styles.last().idSuffix

        // Get the overlay layer and verify it's linked to the base layer
        val overlayLayer = d2.mapsModule().mapLayers()
            .byUid().eq(overlayLayerUid)
            .blockingGet()
            .first()

        assertThat(overlayLayer.linkedLayerUid()).isEqualTo(baseLayerUid)
        assertThat(overlayLayer.mapLayerPosition()).isEqualTo(MapLayerPosition.OVERLAY)

        // Get the base layer and verify it has no linkedLayerUid
        val baseLayer = d2.mapsModule().mapLayers()
            .byUid().eq(baseLayerUid)
            .blockingGet()
            .first()

        assertThat(baseLayer.linkedLayerUid()).isNull()
        assertThat(baseLayer.mapLayerPosition()).isEqualTo(MapLayerPosition.BASEMAP)
    }

    @Test
    fun filter_by_linked_layer_uid() {
        val hybridBasemap = AzureBasemaps.list.find { it.id == "azureHybrid" }!!
        val baseLayerUid = hybridBasemap.id + hybridBasemap.styles.first().idSuffix

        val linkedLayers = d2.mapsModule().mapLayers()
            .byLinkedLayerUid().eq(baseLayerUid)
            .blockingGet()

        assertThat(linkedLayers.size).isEqualTo(1)
    }

    @Test
    fun filter_by_style() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byStyle().eq(AzureBasemaps.list.first().styles.first().tilesetId)
            .blockingGet()

        assertThat(mapLayers.size).isEqualTo(1)
    }

    @Test
    fun azure_dark_not_present_due_to_401() {
        val azureDarkLayers = d2.mapsModule().mapLayers()
            .byUid().eq("azureDark")
            .blockingGet()

        assertThat(azureDarkLayers).isEmpty()
    }

    @Test
    fun with_imagery_providers() {
        val mapLayers = d2.mapsModule().mapLayers()
            .byUid().eq(AzureBasemaps.list.first().id)
            .withImageryProviders()
            .blockingGet()

        assertThat(mapLayers.first().imageryProviders()).isNotEmpty()
        assertThat(mapLayers.first().imageryProviders()?.first()?.coverageAreas()?.first()).isInstanceOf(
            MapLayerImageryProviderArea::class.java,
        )
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
            // azureLight (1 style - success)
            dhis2MockServer.enqueueMockResponse("map/layer/microsoft/azure_server_response.json")
            // azureDark (1 style - fails with 401, but not first so continues without Bing fallback)
            dhis2MockServer.enqueueMockResponse(401)
            // azureAerial (1 style - success)
            dhis2MockServer.enqueueMockResponse("map/layer/microsoft/azure_server_response.json")
            // azureHybrid (2 styles: imagery + hybrid.road - both success)
            dhis2MockServer.enqueueMockResponse("map/layer/microsoft/azure_server_response.json")
            dhis2MockServer.enqueueMockResponse("map/layer/microsoft/azure_server_response.json")
            // external map layers
            dhis2MockServer.enqueueMockResponse("map/layer/externalmap/external_map_layers.json")

            d2.mapsModule().mapLayersDownloader().downloadMetadata().blockingAwait()
        }
    }
}
