import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "FileResource",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class FileResourceDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val uid: String,
    val name: String?,
    val created: String?,
    val lastUpdated: String?,
    val contentType: String?,
    val contentLength: Int?,
    val path: String?,
    val syncState: String?,
    val domain: String?
)
