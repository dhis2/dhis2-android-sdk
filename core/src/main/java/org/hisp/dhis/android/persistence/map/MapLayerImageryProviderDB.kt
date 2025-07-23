package org.hisp.dhis.android.persistence.map

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.map.layer.MapLayerImageryProvider
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "MapLayerImageryProvider",
    foreignKeys = [
        ForeignKey(
            entity = MapLayerDB::class,
            parentColumns = ["uid"],
            childColumns = ["mapLayer"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["mapLayer", "attribution"],
)
internal data class MapLayerImageryProviderDB(
    val mapLayer: String,
    val attribution: String,
    val coverageAreas: MapLayerImageryProviderAreaDB?,
) : EntityDB<MapLayerImageryProvider> {

    override fun toDomain(): MapLayerImageryProvider {
        return MapLayerImageryProvider.builder()
            .mapLayer(mapLayer)
            .attribution(attribution)
            .coverageAreas(coverageAreas?.toDomain())
            .build()
    }
}

internal fun MapLayerImageryProvider.toDB(): MapLayerImageryProviderDB {
    return MapLayerImageryProviderDB(
        mapLayer = mapLayer(),
        attribution = attribution(),
        coverageAreas = coverageAreas()?.toDB(),
    )
}
