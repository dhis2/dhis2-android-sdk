import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "MapLayer",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class MapLayerDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val name: String,
    val displayName: String,
    val external: Int?,
    val mapLayerPosition: String,
    val style: String?,
    val imageUrl: String,
    val subdomains: String?,
    val subdomainPlaceholder: String?,
    val code: String?,
    val mapService: String?,
    val imageFormat: String?,
    val layers: String?
)
