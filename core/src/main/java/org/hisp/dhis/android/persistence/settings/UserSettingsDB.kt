import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserSettings")
internal data class UserSettingsDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val keyUiLocale: String?,
    val keyDbLocale: String?
)
