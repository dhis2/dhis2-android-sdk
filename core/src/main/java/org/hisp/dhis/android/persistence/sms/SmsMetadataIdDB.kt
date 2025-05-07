import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SmsMetadataId")
internal data class SmsMetadataIdDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val type: String?,
    val uid: String?
)
