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
package org.hisp.dhis.android.core.map.layer

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.map.layer.internal.MapLayerImagerProviderChildrenAppender
import org.hisp.dhis.android.core.map.layer.internal.MapLayerStore
import org.koin.core.annotation.Singleton

@Suppress("TooManyFunctions")
@Singleton
class MapLayerCollectionRepository internal constructor(
    store: MapLayerStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyWithUidCollectionRepositoryImpl<MapLayer, MapLayerCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope -> MapLayerCollectionRepository(store, databaseAdapter, s) },
) {

    fun byUid(): StringFilterConnector<MapLayerCollectionRepository> {
        return cf.string(MapLayerTableInfo.Columns.UID)
    }

    fun byName(): StringFilterConnector<MapLayerCollectionRepository> {
        return cf.string(MapLayerTableInfo.Columns.NAME)
    }

    fun byDisplayName(): StringFilterConnector<MapLayerCollectionRepository> {
        return cf.string(MapLayerTableInfo.Columns.DISPLAY_NAME)
    }

    fun byCode(): StringFilterConnector<MapLayerCollectionRepository> {
        return cf.string(MapLayerTableInfo.Columns.CODE)
    }

    fun byExternal(): BooleanFilterConnector<MapLayerCollectionRepository> {
        return cf.bool(MapLayerTableInfo.Columns.EXTERNAL)
    }

    fun byMapLayerPosition(): EnumFilterConnector<MapLayerCollectionRepository, MapLayerPosition> {
        return cf.enumC(MapLayerTableInfo.Columns.MAP_LAYER_POSITION)
    }

    fun byStyle(): StringFilterConnector<MapLayerCollectionRepository> {
        return cf.string(MapLayerTableInfo.Columns.STYLE)
    }

    fun byImageUrl(): StringFilterConnector<MapLayerCollectionRepository> {
        return cf.string(MapLayerTableInfo.Columns.IMAGE_URL)
    }

    fun withImageryProviders(): MapLayerCollectionRepository {
        return cf.withChild(MapLayer.IMAGERY_PROVIDERS)
    }

    fun byMapService(): EnumFilterConnector<MapLayerCollectionRepository, MapService> {
        return cf.enumC(MapLayerTableInfo.Columns.MAP_SERVICE)
    }

    fun byImageFormat(): EnumFilterConnector<MapLayerCollectionRepository, ImageFormat> {
        return cf.enumC(MapLayerTableInfo.Columns.IMAGE_FORMAT)
    }

    fun byLayers(): StringFilterConnector<MapLayerCollectionRepository> {
        return cf.string(MapLayerTableInfo.Columns.LAYERS)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<MapLayer> = mapOf(
            MapLayer.IMAGERY_PROVIDERS to ::MapLayerImagerProviderChildrenAppender,
        )
    }
}
