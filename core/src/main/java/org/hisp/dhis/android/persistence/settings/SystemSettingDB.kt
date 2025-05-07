import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SystemSetting",
    indices = [
        Index(value = ["key"], unique = true)
    ]
)
internal data class SystemSettingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val key: String?,
    val value: String?
)
