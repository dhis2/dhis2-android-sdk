import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LatestAppVersion")
internal data class LatestAppVersionDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val downloadURL: String?,
    val version: String?
)
