package org.hisp.dhis.android.persistence.map

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["mapLayer"]),
    ],
)
internal data class MapLayerImageryProviderDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val mapLayer: String,
    val attribution: String,
    val coverageAreas: MapLayerImageryProviderAreaDB?,
) : EntityDB<MapLayerImageryProvider> {
    override fun toDomain(): MapLayerImageryProvider {
        return MapLayerImageryProvider.builder().apply {
            mapLayer(mapLayer)
            attribution(attribution)
            coverageAreas(coverageAreas?.toDomain())
        }.build()
    }
}

internal fun MapLayerImageryProvider.toDB(): MapLayerImageryProviderDB {
    return MapLayerImageryProviderDB(
        mapLayer = mapLayer(),
        attribution = attribution(),
        coverageAreas = coverageAreas()?.toDB(),
    )
}
