import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserSettings")
internal data class UserSettingsDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val keyUiLocale: String?,
    val keyDbLocale: String?
)
