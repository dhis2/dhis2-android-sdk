package org.hisp.dhis.android.persistence.visualization

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "VisualizationDimensionItem",
    foreignKeys = [
        ForeignKey(
            entity = VisualizationDB::class,
            parentColumns = ["uid"],
            childColumns = ["visualization"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["visualization", "dimensionItem"]
)
internal data class VisualizationDimensionItemDB(
    val visualization: String,
    val position: String,
    val dimension: String,
    val dimensionItem: String?,
    val dimensionItemType: String?,
    val sortOrder: Int?,
) : EntityDB<VisualizationDimensionItem> {
    override fun toDomain(): VisualizationDimensionItem {
        return VisualizationDimensionItem.builder()
            .visualization(visualization)
            .position(LayoutPosition.valueOf(position))
            .dimension(dimension)
            .dimensionItem(dimensionItem)
            .dimensionItemType(dimensionItemType)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun VisualizationDimensionItem.toDB(): VisualizationDimensionItemDB {
    return VisualizationDimensionItemDB(
        visualization = visualization()!!,
        position = position()!!.name,
        dimension = dimension()!!,
        dimensionItem = dimensionItem(),
        dimensionItemType = dimensionItemType(),
        sortOrder = sortOrder(),
    )
}
