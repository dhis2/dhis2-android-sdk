import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "MapLayerImageryProvider",
    foreignKeys = [
        ForeignKey(
            entity = MapLayerDB::class,
            parentColumns = ["uid"],
            childColumns = ["mapLayer"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mapLayer"])
    ]
)
internal data class MapLayerImageryProviderDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val mapLayer: String,
    val attribution: String,
    val coverageAreas: String?
)
