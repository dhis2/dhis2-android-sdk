package org.hisp.dhis.android.persistence.map

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.map.layer.ImageFormat
import org.hisp.dhis.android.core.map.layer.MapLayer
import org.hisp.dhis.android.core.map.layer.MapLayerPosition
import org.hisp.dhis.android.core.map.layer.MapService
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "MapLayer",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class MapLayerDB(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int? = 0,
    val uid: String,
    val name: String,
    val displayName: String,
    val external: Boolean?,
    val mapLayerPosition: String,
    val style: String?,
    val imageUrl: String,
    val subdomains: StringListDB?,
    val subdomainPlaceholder: String?,
    val code: String?,
    val mapService: String?,
    val imageFormat: String?,
    val layers: String?,
) : EntityDB<MapLayer> {

    override fun toDomain(): MapLayer {
        return MapLayer.builder().apply {
            id(id?.toLong())
            uid(uid)
            name(name)
            displayName(displayName)
            external(external)
            mapLayerPosition(MapLayerPosition.valueOf(mapLayerPosition))
            style(style)
            imageUrl(imageUrl)
            subdomains(subdomains?.toDomain())
            subdomainPlaceholder(subdomainPlaceholder)
            code(code)
            mapService?.let { mapService(MapService.valueOf(it)) }
            imageFormat?.let { imageFormat(ImageFormat.valueOf(it)) }
            layers(layers)
        }.build()
    }
}

internal fun MapLayer.toDB(): MapLayerDB {
    return MapLayerDB(
        uid = uid(),
        name = name(),
        displayName = displayName(),
        external = external(),
        mapLayerPosition = mapLayerPosition().name,
        style = style(),
        imageUrl = imageUrl(),
        subdomains = subdomains()?.toDB(),
        subdomainPlaceholder = subdomainPlaceholder(),
        code = code(),
        mapService = mapService()?.name,
        imageFormat = imageFormat()?.name,
        layers = layers(),
    )
}
