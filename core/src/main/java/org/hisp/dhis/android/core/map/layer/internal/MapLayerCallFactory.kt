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
package org.hisp.dhis.android.core.map.layer.internal

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.internal.externalmap.ExternalMapLayerCallFactory
import org.hisp.dhis.android.core.map.layer.internal.microsoft.MicrosoftMapsCallFactory
import org.hisp.dhis.android.core.map.layer.internal.osm.OSMCallFactory
import org.koin.core.annotation.Singleton

@Singleton
internal class MapLayerCallFactory(
    private val osmCallFactory: OSMCallFactory,
    private val microsoftMapsCallFactory: MicrosoftMapsCallFactory,
    private val externalMapLayerCallFactory: ExternalMapLayerCallFactory,
    private val mapLayerCollectionCleaner: MapLayerCollectionCleaner,
) {

    suspend fun downloadMetadata(): List<MapLayer> {
        return flowOf(
            osmCallFactory.download(),
            microsoftMapsCallFactory.download(),
            externalMapLayerCallFactory.download(),
        )
            .toList()
            .flatten()
            .also { mapLayerCollectionCleaner.deleteNotPresent(it) }
    }
}
