import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "FileResource",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class FileResourceDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val name: String?,
    val created: String?,
    val lastUpdated: String?,
    val contentType: String?,
    val contentLength: Int?,
    val path: String?,
    val syncState: String?,
    val domain: String?,
)
