package org.hisp.dhis.android.persistence.visualization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem

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
    indices = [
        Index(value = ["visualization"]),
    ],
)
internal data class VisualizationDimensionItemDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val visualization: String,
    val position: String,
    val dimension: String,
    val dimensionItem: String?,
    val dimensionItemType: String?,
) {
    fun toDomain(): VisualizationDimensionItem {
        return VisualizationDimensionItem.builder()
            .id(id?.toLong())
            .visualization(visualization)
            .position(LayoutPosition.valueOf(position))
            .dimension(dimension)
            .dimensionItem(dimensionItem)
            .dimensionItemType(dimensionItemType)
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
    )
}
