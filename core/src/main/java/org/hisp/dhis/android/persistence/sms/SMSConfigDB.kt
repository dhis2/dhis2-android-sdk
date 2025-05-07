import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SMSConfig",
    indices = [
        Index(value = ["key"], unique = true),
    ],
)
internal data class SMSConfigDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val key: String,
    val value: String?,
)
