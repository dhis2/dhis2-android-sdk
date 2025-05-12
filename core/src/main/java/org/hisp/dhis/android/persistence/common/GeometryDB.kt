package org.hisp.dhis.android.persistence.common

import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry

internal data class GeometryDB(
    val geometryType: String?,
    val geometryCoordinates: String?,
) : EntityDB<Geometry?> {
    override fun toDomain(): Geometry? {

        return if (geometryType != null && geometryCoordinates != null) {
            FeatureType.valueOfFeatureType(geometryType)?.let { type ->
                Geometry.builder()
                    .type(type)
                    .coordinates(geometryCoordinates)
                    .build()
            }
        } else null
    }
}

internal fun Geometry?.toDB(): GeometryDB {
    return GeometryDB(
        geometryType = this?.type()?.geometryType,
        geometryCoordinates = this?.coordinates()
    )
}
