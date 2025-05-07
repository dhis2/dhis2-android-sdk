import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SystemInfo")
internal data class SystemInfoDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val serverDate: String?,
    val dateFormat: String?,
    val version: String?,
    val contextPath: String?,
    val systemName: String?
)
