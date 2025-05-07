import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CustomIntent",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class CustomIntentDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val uid: String,
    val name: String?,
    val action: String?,
    val packageName: String?,
    val requestArguments: String?,
    val responseDataArgument: String?,
    val responseDataPath: String?
)
