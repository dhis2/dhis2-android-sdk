import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "D2Error")
internal data class D2ErrorDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val resourceType: String?,
    val uid: String?,
    val url: String?,
    val errorComponent: String?,
    val errorCode: String?,
    val errorDescription: String?,
    val httpErrorCode: Int?,
    val created: String?
)
