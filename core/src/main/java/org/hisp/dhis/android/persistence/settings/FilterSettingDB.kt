import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FilterSetting")
internal data class FilterSettingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val scope: String?,
    val filterType: String?,
    val uid: String?,
    val sort: Int?,
    val filter: Int?
)
