import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SMSConfig",
    indices = [
        Index(value = ["key"], unique = true)
    ]
)
internal data class SMSConfigDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val key: String,
    val value: String?
)
