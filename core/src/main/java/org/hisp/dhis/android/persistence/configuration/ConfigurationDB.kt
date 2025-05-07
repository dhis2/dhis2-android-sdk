import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Configuration",
    indices = [
        Index(value = ["serverUrl"], unique = true)
    ]
)
internal data class ConfigurationDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val serverUrl: String
)
