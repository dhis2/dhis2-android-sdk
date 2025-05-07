import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "VisualizationDimensionItem",
    foreignKeys = [
        ForeignKey(
            entity = VisualizationDB::class,
            parentColumns = ["uid"],
            childColumns = ["visualization"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["visualization"])
    ]
)
internal data class VisualizationDimensionItemDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val visualization: String,
    val position: String,
    val dimension: String,
    val dimensionItem: String?,
    val dimensionItemType: String?
)
