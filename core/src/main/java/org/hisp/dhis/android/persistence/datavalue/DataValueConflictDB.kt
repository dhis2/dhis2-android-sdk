import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DataValueConflict")
internal data class DataValueConflictDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val conflict: String?,
    val value: String?,
    val attributeOptionCombo: String?,
    val categoryOptionCombo: String?,
    val dataElement: String?,
    val period: String?,
    val orgUnit: String?,
    val errorCode: String?,
    val status: String?,
    val created: String?,
    val displayDescription: String?
)
