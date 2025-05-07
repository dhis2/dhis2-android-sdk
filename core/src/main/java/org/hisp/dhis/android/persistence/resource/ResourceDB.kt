import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Resource")
internal data class ResourceDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val resourceType: String,
    val lastSynced: String?
)
